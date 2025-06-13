import PageWrapper from "../../components/PageWrapper";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { CircularProgress } from "@mui/material";
import trashCan from "../../assets/trash-can.svg";
import { toastSuccess, toastError } from "../../utils/alerts";
import { ToastContainer } from "react-toastify";
import { useTranslation } from "react-i18next";

interface Card {
  cardId: number;
  term: string;
  meaning: string | null;
  translation: string;
  status?: string;
}

interface Vocabulary {
  cefr: string;
  id: number;
  title: string;
  description: string;
  fromLang: string;
  toLang: string;
  label: string;
  isPublic: boolean;
  creatorId: number;
  creationDate: string;
  lastModified: string | null;
  cards: Card[];
}

interface CardPayload {
  id: string;
  cardId?: number; // For existing cards
  term: string;
  translation: string[];
  listOfTranslations: string[];
  meaning?: string;
  isNew?: boolean;
  isModified?: boolean;
  status?: string;
  hasErrors?: boolean;
}

type VocabularyFormFields = {
  name: string;
  description: string;
  label: string;
  level: string;
};

interface IVocabulary {
  dictionary: Vocabulary;
  access: string;
}

export const WORDS_STATUSES = [
  { value: "all", label: "statusFilters.all" },
  { value: "with_warnings", label: "statusFilters.withWarnings" },
  { value: "no_warnings", label: "statusFilters.noWarnings" },
];

const validationSchema = yup.object().shape({
  name: yup.string().required("Title is required"),
  description: yup.string(), // Removed .required()
  label: yup.string().required("Topic must be selected"),
  level: yup.string().required("Level must be selected"),
});

export const EditVocabulary = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const [vocabulary, setVocabulary] = useState<IVocabulary | null>(null);
  const [loading, setLoading] = useState(true);
  const [cards, setCards] = useState<CardPayload[]>([]);
  const [filteredCards, setFilteredCards] = useState<CardPayload[]>([]);
  const [statusFilter, setStatusFilter] = useState("all");
  const [deletedCardIds, setDeletedCardIds] = useState<number[]>([]);
  const [cardErrors, setCardErrors] = useState<{ [key: string]: { term?: string; translation?: string } }>(
    {}
  );

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<VocabularyFormFields>({
    resolver: yupResolver(validationSchema),
    mode: "onBlur",
  });

  const navigate = useNavigate();

  const labelOptions = t("editVocabulary.labelOptions", { returnObjects: true }) as string[];

  useEffect(() => {
    if (vocabulary) {
      setValue("name", vocabulary.dictionary.title);
      setValue("description", vocabulary.dictionary.description || "");
      setValue("label", vocabulary.dictionary.label);
      setValue("level", vocabulary.dictionary.cefr);
    }
  }, [vocabulary, setValue]);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get(`https://localhost:7288/Dictionary/full?dictionaryId=${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((response) => {
          setVocabulary(response.data);
          const loadedCards = response.data.dictionary.cards.map((card: Card) => ({
            id: crypto.randomUUID(),
            cardId: card.cardId,
            term: card.term,
            translation: Array.isArray(card.translation) ? card.translation : [card.translation],
            listOfTranslations: [],
            meaning: card.meaning || "",
            status: card.status || "",
            isNew: false,
            isModified: false,
          }));
          setCards(loadedCards);
          setFilteredCards(loadedCards);
        })
        .catch((error) => {
          console.error(t("editVocabulary.errors.loading"), error.response?.data || error.message);
          toastError(t("editVocabulary.errors.loading"));
        })
        .finally(() => {
          setLoading(false);
        });
    }
  }, [id, t]);

  // Filter cards based on selected status
  useEffect(() => {
    if (statusFilter === "all") {
      setFilteredCards(cards);
    } else if (statusFilter === "with_warnings") {
      setFilteredCards(cards.filter((card) => card.status && card.status.trim() !== ""));
    } else if (statusFilter === "no_warnings") {
      setFilteredCards(cards.filter((card) => !card.status || card.status.trim() === ""));
    }
  }, [statusFilter, cards]);

  const validateCards = () => {
    const newCardErrors: { [key: string]: { term?: string; translation?: string } } = {};
    let hasErrors = false;

    cards.forEach((card) => {
      const errors: { term?: string; translation?: string } = {};

      if (!card.term.trim()) {
        errors.term = t("editVocabulary.validation.termRequired");
        hasErrors = true;
      }

      // Allow either translation or meaning to be empty, but not both
      if (
        (!card.translation ||
          card.translation.length === 0 ||
          (card.translation.length === 1 && !card.translation[0].trim())) &&
        (!card.meaning || !card.meaning.trim())
      ) {
        errors.translation = t("editVocabulary.validation.eitherRequired");
        hasErrors = true;
      }

      if (Object.keys(errors).length > 0) {
        newCardErrors[card.id] = errors;
      }
    });

    setCardErrors(newCardErrors);
    return !hasErrors;
  };

  const handleEditVocabulary = (data: VocabularyFormFields) => {
    // Validate cards first
    if (!validateCards()) {
      toastError(t("editVocabulary.validation.checkErrors"));
      return;
    }

    // Filter cards by their status
    const newCards = cards.filter((card) => card.isNew);
    const updatedCards = cards.filter((card) => !card.isNew && card.isModified && card.cardId);

    const formattedData = {
      dictionaryId: Number(id),
      newTitle: data.name,
      newDescription: data.description,
      isPublic: vocabulary?.dictionary.isPublic, // Use existing value
      from: vocabulary?.dictionary.fromLang,
      to: vocabulary?.dictionary.toLang,
      newLabel: data.label,
      newCEFR: data.level,
      cardsToInsert: newCards.length
        ? newCards.map((card) => ({
            term: card.term,
            meaning: card.meaning || "",
            translation: Array.isArray(card.translation) ? card.translation.join(", ") : card.translation,
            status: card.status || "",
          }))
        : null,
      cardsToUpdate: updatedCards.length
        ? updatedCards.map((card) => ({
            cardId: card.cardId,
            term: card.term,
            meaning: card.meaning || "",
            translation: Array.isArray(card.translation) ? card.translation.join(", ") : card.translation,
            status: card.status || "",
          }))
        : null,
      cardsToDelete: deletedCardIds.length ? deletedCardIds : null,
    };

    const token = localStorage.getItem("token");

    axios
      .put(`https://localhost:7288/Dictionary`, formattedData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setCards((prevCards) => prevCards.map((card) => ({ ...card, isModified: false })));
        setDeletedCardIds([]);
        toastSuccess(t("editVocabulary.toasts.dictionaryEdited"));
        navigate(-1);
      })
      .catch((error) => {
        console.error(t("editVocabulary.errors.updating"), error.response?.data || error.message);
        toastError(t("editVocabulary.errors.updating"));
      });
  };

  const handleTranslationClick = (translation: string, index: number) => {
    const cardIndex = cards.findIndex((card) => card.id === filteredCards[index].id);
    if (cardIndex === -1) return;

    setCards((prev) => {
      const newCards = [...prev];
      const currentCard = newCards[cardIndex];

      // Remove selected translation from listOfTranslations
      currentCard.listOfTranslations = currentCard.listOfTranslations.filter((item) => item !== translation);

      // Add selected translation to translations
      if (!currentCard.translation.includes(translation)) {
        currentCard.translation = [...currentCard.translation, translation];

        // Mark as modified if it's an existing card
        if (!currentCard.isNew && currentCard.cardId) {
          currentCard.isModified = true;
        }
      }

      return newCards;
    });

    // Clear validation error if applicable
    if (cardErrors[filteredCards[index].id]?.translation) {
      const newErrors = { ...cardErrors };
      delete newErrors[filteredCards[index].id].translation;
      if (Object.keys(newErrors[filteredCards[index].id]).length === 0) {
        delete newErrors[filteredCards[index].id];
      }
      setCardErrors(newErrors);
    }
  };

  const test = async (text: string, index: number) => {
    const cardIndex = cards.findIndex((card) => card.id === filteredCards[index].id);
    if (cardIndex === -1) return;

    const sourceLanguage = vocabulary?.dictionary.fromLang;
    const targetLanguage = vocabulary?.dictionary.toLang;

    if (!sourceLanguage || !targetLanguage) {
      console.warn(t("editVocabulary.warnings.selectLanguages"));
      return;
    }

    setCards((prev) => {
      const newCards = [...prev];
      newCards[cardIndex].term = text;

      // Mark as modified if it's an existing card
      if (!newCards[cardIndex].isNew && newCards[cardIndex].cardId) {
        newCards[cardIndex].isModified = true;
      }

      return newCards;
    });

    // Clear validation error if text is not empty
    if (text.trim() && cardErrors[filteredCards[index].id]?.term) {
      const newErrors = { ...cardErrors };
      delete newErrors[filteredCards[index].id].term;
      if (Object.keys(newErrors[filteredCards[index].id]).length === 0) {
        delete newErrors[filteredCards[index].id];
      }
      setCardErrors(newErrors);
    }

    // Don't call translation API if term is empty
    if (!text.trim()) return;

    const options = {
      method: "POST",
      url: "https://google-translate113.p.rapidapi.com/api/v1/translator/text",
      headers: {
        "x-rapidapi-key": "bfcd819b12msh8f6d37c5c08153cp17fd66jsna21f4e386294",
        "x-rapidapi-host": "google-translate113.p.rapidapi.com",
        "Content-Type": "application/json",
      },
      data: {
        from: sourceLanguage,
        to: targetLanguage,
        text: text,
      },
    };

    try {
      const response = await axios.request(options);
      const translations = [...response.data.dict[0].entry.map((entry: any) => entry.word)];

      if (translations) {
        setCards((prev) => {
          const newCards = [...prev];
          newCards[cardIndex].listOfTranslations = translations;
          return newCards;
        });
      }
    } catch (error) {
      console.error(t("editVocabulary.errors.translation"), error);
    }
  };

  const handleAddCard = () => {
    const newCard = {
      id: crypto.randomUUID(),
      term: "",
      translation: [],
      listOfTranslations: [],
      meaning: "",
      status: "",
      isNew: true,
      isModified: false,
    };

    setCards((prev) => [...prev, newCard]);

    // If the current filter would include this card, add it to filtered cards too
    if (statusFilter === "all" || statusFilter === "no_warnings") {
      setFilteredCards((prev) => [...prev, newCard]);
    }
  };

  const handleDeleteTerm = (id: string, cardId?: number) => {
    // If it's an existing card, add to deletedCardIds
    if (cardId) {
      setDeletedCardIds((prev) => [...prev, cardId]);
    }

    // Remove from current cards list
    setCards((prev) => prev.filter((card) => card.id !== id));
    setFilteredCards((prev) => prev.filter((card) => card.id !== id));

    // Remove any errors for this card
    if (cardErrors[id]) {
      const newErrors = { ...cardErrors };
      delete newErrors[id];
      setCardErrors(newErrors);
    }
  };

  const handleCardChange = (index: number, field: string, value: any) => {
    const cardIndex = cards.findIndex((card) => card.id === filteredCards[index].id);
    if (cardIndex === -1) return;

    setCards((prev) => {
      const newCards = [...prev];
      const card = newCards[cardIndex];

      // Update the field
      if (field === "translation" && typeof value === "string") {
        card.translation = value
          .split(",")
          .map((item) => item.trim())
          .filter((item) => item !== "");
      } else {
        // @ts-ignore
        card[field] = value;
      }

      // Mark as modified if it's an existing card
      if (!card.isNew && card.cardId) {
        card.isModified = true;
      }

      return newCards;
    });

    // Clear validation errors when field is updated with valid content
    const cardId = filteredCards[index].id;
    if (cardErrors[cardId]) {
      const newErrors = { ...cardErrors };

      if (field === "term" && value.trim() && newErrors[cardId]?.term) {
        delete newErrors[cardId].term;
      }

      if (field === "translation" && value.trim() && newErrors[cardId]?.translation) {
        delete newErrors[cardId].translation;
      }

      if (field === "meaning" && value.trim() && newErrors[cardId]?.translation) {
        delete newErrors[cardId].translation;
      }

      if (Object.keys(newErrors[cardId]).length === 0) {
        delete newErrors[cardId];
      }

      setCardErrors(newErrors);
    }
  };

  const handleStatusFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setStatusFilter(e.target.value);
  };

  if (loading || !vocabulary) {
    return (
      <PageWrapper>
        <div className="flex justify-center items-center min-h-screen">
          <CircularProgress size={50} />
        </div>
      </PageWrapper>
    );
  }

  return (
    <PageWrapper>
      <ToastContainer />
      <form
        onSubmit={handleSubmit(handleEditVocabulary)}
        className="w-full flex justify-between items-baseline gap-10 p-5"
      >
        <div className="flex w-full justify-between items-center">
          <div className="flex flex-col gap-4 w-full">
            <div>
              <input
                {...register("name")}
                className={`w-full p-4 rounded-lg border-0 bg-indigo-300/70 outline-none ${
                  errors.name ? "border-2 border-red-500" : ""
                }`}
                type="text"
                autoComplete="off"
                placeholder={t("editVocabulary.placeholders.title")}
              />
              {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
            </div>

            <div>
              <textarea
                {...register("description")}
                className="w-full p-4 rounded-lg border-0 bg-yellow-300/50 outline-none resize-none"
                autoComplete="off"
                placeholder={t("editVocabulary.placeholders.description")}
              />
            </div>

            <div>
              <select
                className={`vocabulary-status p-4 border border-solid rounded-lg w-full ${
                  errors.label ? "border-2 border-red-500" : ""
                }`}
                {...register("label")}
              >
                <option value="" disabled>
                  {t("editVocabulary.selects.chooseTopic")}
                </option>
                {labelOptions.map((label) => (
                  <option key={label} value={label}>
                    {label}
                  </option>
                ))}
              </select>
              {errors.label && <p className="text-red-500 text-sm mt-1">{errors.label.message}</p>}
            </div>

            <div className="flex gap-4">
              <div className="w-52">
                <select
                  className={`rounded-lg p-4 border w-full ${errors.level ? "border-2 border-red-500" : ""}`}
                  {...register("level")}
                >
                  <option value="" disabled>
                    {t("editVocabulary.selects.level")}
                  </option>
                  <option value="A1">A1</option>
                  <option value="A2">A2</option>
                  <option value="B1">B1</option>
                  <option value="B2">B2</option>
                  <option value="C1">C1</option>
                  <option value="C2">C2</option>
                </select>
                {errors.level && <p className="text-red-500 text-sm mt-1">{errors.level.message}</p>}
              </div>
            </div>

            <div className="flex w-full flex-col gap-6">
              <div className="flex w-full justify-between items-center">
                <span>{t("editVocabulary.dictionaryTerms")}</span>

                {/* Status filter dropdown */}
                <select
                  className="p-2 rounded-lg border border-gray-300"
                  value={statusFilter}
                  onChange={handleStatusFilterChange}
                >
                  {WORDS_STATUSES.map((status) => (
                    <option key={status.value} value={status.value}>
                      {t(`vocabulary.${status.label}`)}
                    </option>
                  ))}
                </select>
              </div>

              {filteredCards.map((card, index) => (
                <div
                  className={`flex flex-col gap-5 p-2 border-2 ${
                    cardErrors[card.id] ? "border-red-500" : "border-gray-300"
                  } rounded-lg`}
                  key={card.id}
                >
                  <div className="flex justify-between border-b-2 border-gray-300 pb-2">
                    <span>{index + 1}</span>
                    <img
                      src={trashCan}
                      onClick={() => handleDeleteTerm(card.id, card.cardId)}
                      alt="trash-can"
                      style={{
                        cursor: "pointer",
                      }}
                    />
                  </div>

                  <div className="flex justify-between px-10 py-5 gap-10">
                    <div className="flex flex-col gap-2 w-full">
                      <input
                        className={`p-y-1 border-0 border-b-2 ${
                          cardErrors[card.id]?.term ? "border-red-500" : "border-gray-700"
                        } outline-none`}
                        type="text"
                        autoComplete="off"
                        value={card.term}
                        onChange={(e) => handleCardChange(index, "term", e.target.value)}
                        onBlur={(e) => test(e.target.value, index)}
                      />
                      <span>{t("editVocabulary.cardFields.term")}</span>
                      {cardErrors[card.id]?.term && (
                        <p className="text-red-500 text-sm">{cardErrors[card.id].term}</p>
                      )}
                    </div>

                    <div className="flex flex-col gap-10 w-full">
                      <div className="flex flex-col gap-2">
                        <input
                          value={
                            Array.isArray(card.translation) ? card.translation.join(", ") : card.translation
                          }
                          onChange={(e) => handleCardChange(index, "translation", e.target.value)}
                          className={`p-y-1 border-0 border-b-2 ${
                            cardErrors[card.id]?.translation ? "border-red-500" : "border-gray-700"
                          } outline-none`}
                          type="text"
                          autoComplete="off"
                        />
                        <span>{t("editVocabulary.cardFields.translation")}</span>
                      </div>

                      {card.listOfTranslations.length > 0 && (
                        <div className="added-terms__translations">
                          <ul className="flex flex-col gap-2">
                            {card.listOfTranslations.map((trans, idx) => (
                              <li
                                key={idx}
                                className="border border-2 cursor-pointer p-2 rounded-[8px]"
                                onClick={() => handleTranslationClick(trans, index)}
                              >
                                {trans}
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}

                      <div className="flex flex-col gap-2">
                        <input
                          value={card.meaning || ""}
                          onChange={(e) => handleCardChange(index, "meaning", e.target.value)}
                          className={`p-y-1 border-0 border-b-2 ${
                            cardErrors[card.id]?.translation ? "border-red-500" : "border-gray-700"
                          } outline-none`}
                          type="text"
                          autoComplete="off"
                        />
                        <span>{t("editVocabulary.cardFields.meaning")}</span>
                        {cardErrors[card.id]?.translation && (
                          <p className="text-red-500 text-sm">{cardErrors[card.id].translation}</p>
                        )}
                      </div>

                      <input
                        type="hidden"
                        value={card.status || ""}
                        onChange={(e) => handleCardChange(index, "status", e.target.value)}
                      />
                    </div>
                  </div>
                </div>
              ))}
              <button
                type="button"
                className="bg-transparent p-5 border-2 border-gray-300 rounded-lg cursor-pointer font-bold transition-colors duration-500 hover:text-gray-700/40"
                onClick={handleAddCard}
              >
                + {t("editVocabulary.buttons.addCard")}
              </button>
            </div>
          </div>
        </div>

        <button
          type="submit"
          className="vocabulary-submit sticky top-[10px]"
          disabled={loading}
          data-testid="dictionary-submit"
        >
          {t("editVocabulary.buttons.save")}
        </button>
      </form>
    </PageWrapper>
  );
};
