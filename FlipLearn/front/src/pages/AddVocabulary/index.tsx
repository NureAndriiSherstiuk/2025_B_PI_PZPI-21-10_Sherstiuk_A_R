import { useState, useRef, useEffect } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import { useTranslation } from "react-i18next";

import PageWrapper from "../../components/PageWrapper";
import trashCan from "../../assets/trash-can.svg";
import "./index.scss";
import { toastSuccess } from "../../utils/alerts";
import { CircularProgress } from "@mui/material";

interface CardPayload {
  id: string;
  term: string;
  translation: string[];
  translationInput: string;
  listOfTranslations: string[];
  allTranslations: string[];
  meaning?: string;
  isTranslating?: boolean;
  error?: {
    term?: string;
    translation?: string;
  };
}

interface VocabularyPayload {
  name: string;
  description?: string;
  isPublic: string;
  sourceLanguage: string;
  targetLanguage: string;
  cards?: CardPayload[];
  level: string;
  label: string;
}

const AddVocabulary = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [cardErrors, setCardErrors] = useState<{ [key: string]: { term?: string; translation?: string } }>(
    {}
  );
  const debounceTimers = useRef<{ [key: number]: any }>({});

  const labelOptions = [
    t("labels.artAndDesign"),
    t("labels.automationAndRobotics"),
    t("labels.business"),
    t("labels.creativity"),
    t("labels.education"),
    t("labels.entertainment"),
    t("labels.fashionAndBeauty"),
    t("labels.finance"),
    t("labels.foodAndDrink"),
    t("labels.government"),
    t("labels.homeAndGarden"),
    t("labels.lifestyleAndHobbies"),
    t("labels.media"),
    t("labels.medicine"),
    t("labels.military"),
    t("labels.musicAndAudio"),
    t("labels.photoAndVideo"),
    t("labels.religion"),
    t("labels.retailAndShopping"),
    t("labels.science"),
    t("labels.socialMedia"),
    t("labels.sports"),
    t("labels.technicalSpecialties"),
    t("labels.computerScienceAndTechnology"),
    t("labels.travel"),
    t("labels.automotive"),
    t("labels.law"),
  ];

  const [cards, setCards] = useState<CardPayload[]>([
    {
      id: crypto.randomUUID(),
      term: "",
      translation: [],
      translationInput: "",
      listOfTranslations: [],
      allTranslations: [],
      meaning: "",
      isTranslating: false,
    },
    {
      id: crypto.randomUUID(),
      term: "",
      translation: [],
      translationInput: "",
      listOfTranslations: [],
      allTranslations: [],
      meaning: "",
      isTranslating: false,
    },
  ]);

  // Validation schema
  const validationSchema = yup.object().shape({
    name: yup.string().required(t("validation.required")),
    description: yup.string(),
    isPublic: yup.string().required(t("validation.required")),
    sourceLanguage: yup.string().required(t("validation.selectLanguage")),
    targetLanguage: yup
      .string()
      .required(t("validation.selectLanguage"))
      .notOneOf([yup.ref("sourceLanguage")], t("validation.differentLanguages")),
    level: yup.string().required(t("validation.selectLevel")),
    label: yup.string().required(t("validation.selectTopic")),
  });

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<VocabularyPayload>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      isPublic: "public",
    },
    mode: "onTouched",
  });

  const sourceLanguage = watch("sourceLanguage");
  const targetLanguage = watch("targetLanguage");

  useEffect(() => {
    return () => {
      Object.values(debounceTimers.current).forEach((timer) => clearTimeout(timer));
    };
  }, []);

  const translateWord = (text: string, index: number) => {
    if (!sourceLanguage || !targetLanguage) {
      alert(t("addVocabulary.selectLanguagesWarning"));
      return;
    }

    if (!text.trim()) {
      setCards((prev) => {
        const newCards = [...prev];
        newCards[index].term = text;
        newCards[index].listOfTranslations = [];
        newCards[index].allTranslations = [];
        newCards[index].isTranslating = false;
        return newCards;
      });
      return;
    }

    setCards((prev) => {
      const newCards = [...prev];
      newCards[index].term = text;
      newCards[index].isTranslating = true;
      return newCards;
    });

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

    axios
      .request(options)
      .then((res) => {
        const translations = res.data.dict[0]?.entry?.map((entry: any) => entry.word) || [];

        setCards((prev) => {
          const newCards = [...prev];
          const currentCard = newCards[index];

          currentCard.allTranslations = translations;

          currentCard.listOfTranslations = translations.filter(
            (trans: any) => !currentCard.translation.includes(trans)
          );

          currentCard.isTranslating = false;

          return newCards;
        });
      })
      .catch((err) => {
        console.error("Error in translation:", err);
        setCards((prev) => {
          const newCards = [...prev];
          newCards[index].isTranslating = false;
          return newCards;
        });
      });
  };

  const debouncedTranslate = (text: string, index: number) => {
    if (debounceTimers.current[index]) {
      clearTimeout(debounceTimers.current[index]);
    }

    debounceTimers.current[index] = setTimeout(() => {
      translateWord(text, index);
    }, 3000);
  };

  const handleAddCard = () => {
    setCards([
      ...cards,
      {
        id: crypto.randomUUID(),
        term: "",
        translation: [],
        translationInput: "",
        listOfTranslations: [],
        allTranslations: [],
        meaning: "",
        isTranslating: false,
      },
    ]);
  };

  const handleDeleteTerm = (id: string) => {
    setCards((prev) => prev.filter((card) => card.id !== id));

    setCardErrors((prev) => {
      const newErrors = { ...prev };
      delete newErrors[id];
      return newErrors;
    });
  };

  const validateCards = (): boolean => {
    const newErrors: { [key: string]: { term?: string; translation?: string } } = {};
    let hasErrors = false;

    cards.forEach((card) => {
      const cardError: { term?: string; translation?: string } = {};

      if (!card.term.trim()) {
        cardError.term = t("validation.termRequired");
        hasErrors = true;
      }

      if (card.translation.length === 0 || (card.translation.length === 1 && !card.translation[0])) {
        cardError.translation = t("validation.translationRequired");
        hasErrors = true;
      }

      if (Object.keys(cardError).length > 0) {
        newErrors[card.id] = cardError;
      }
    });

    setCardErrors(newErrors);
    return !hasErrors;
  };

  const handleAddVocabulary: SubmitHandler<VocabularyPayload> = (data) => {
    if (!validateCards()) {
      return;
    }

    const formattedData = {
      title: data.name,
      description: data.description || "",
      isPublic: data.isPublic === "public",
      fromLang: data.sourceLanguage,
      toLang: data.targetLanguage,
      cefr: data.level,
      label: data.label,
      cards: cards.map(({ id, ...rest }) => ({
        term: rest.term,
        meaning: rest.meaning === "" ? null : rest.meaning || null,
        translation: rest.translation.join(","),
      })),
    };

    const token = localStorage.getItem("token");

    setLoading(true);
    axios
      .post("https://localhost:7288/Dictionary", formattedData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        toastSuccess(t("addVocabulary.successMessage"));
        setTimeout(() => {
          navigate("/");
        }, 1000);
      })
      .catch((error) => {
        console.error("Ошибка входа:", error.response?.data || error.message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleTranslationClick = (translation: string, index: number) => {
    setCards((prev) => {
      const newCards = [...prev];
      const currentCard = newCards[index];

      currentCard.listOfTranslations = currentCard.listOfTranslations.filter((item) => item !== translation);

      if (!currentCard.translation.includes(translation)) {
        currentCard.translation = [...currentCard.translation, translation];

        currentCard.translationInput = currentCard.translation.join(", ");

        if (cardErrors[currentCard.id]?.translation) {
          setCardErrors((prev) => {
            const newErrors = { ...prev };
            if (newErrors[currentCard.id]) {
              delete newErrors[currentCard.id].translation;
              if (Object.keys(newErrors[currentCard.id]).length === 0) {
                delete newErrors[currentCard.id];
              }
            }
            return newErrors;
          });
        }
      }

      return newCards;
    });
  };

  const handleTranslationInputChange = (value: string, index: number) => {
    setCards((prev) => {
      const newCards = [...prev];
      const currentCard = newCards[index];

      currentCard.translationInput = value;

      return newCards;
    });
  };

  const handleTranslationInputBlur = (index: number) => {
    setCards((prev) => {
      const newCards = [...prev];
      const currentCard = newCards[index];

      const translations = currentCard.translationInput
        .split(",")
        .map((item) => item.trim())
        .filter((item) => item.length > 0);

      const removedTranslations = currentCard.translation.filter((trans) => !translations.includes(trans));

      const restoredTranslations = removedTranslations.filter((trans) =>
        currentCard.allTranslations.includes(trans)
      );

      currentCard.translation = translations;
      currentCard.listOfTranslations = [...currentCard.listOfTranslations, ...restoredTranslations].filter(
        (trans) => currentCard.allTranslations.includes(trans) && !translations.includes(trans)
      );

      return newCards;
    });

    const translations = cards[index].translationInput
      .split(",")
      .map((item) => item.trim())
      .filter((item) => item.length > 0);

    if (translations.length > 0 && cardErrors[cards[index].id]?.translation) {
      setCardErrors((prev) => {
        const newErrors = { ...prev };
        const cardId = cards[index].id;
        if (newErrors[cardId]) {
          delete newErrors[cardId].translation;
          if (Object.keys(newErrors[cardId]).length === 0) {
            delete newErrors[cardId];
          }
        }
        return newErrors;
      });
    }
  };

  return (
    <PageWrapper>
      <ToastContainer />
      <form onSubmit={handleSubmit(handleAddVocabulary)} className="vocabulary-form" noValidate>
        <div className="vocabulary-creation">
          <div className="vocabulary-creation__top">
            <span>{t("addVocabulary.createNew")}</span>

            <div className="flex gap-6">
              <div className="flex flex-col">
                <select
                  className={`vocabulary-status ${errors.level ? "border-red-500" : ""}`}
                  {...register("level")}
                >
                  <option value="" disabled>
                    {t("addVocabulary.level")}
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

              <div className="flex flex-col">
                <select
                  className={`vocabulary-status ${errors.isPublic ? "border-red-500" : ""}`}
                  {...register("isPublic")}
                >
                  <option value="public">{t("addVocabulary.public")}</option>
                  <option value="private">{t("addVocabulary.private")}</option>
                </select>
                {errors.isPublic && <p className="text-red-500 text-sm mt-1">{errors.isPublic.message}</p>}
              </div>
            </div>
          </div>

          <div className="vocabulary-creation__main">
            <div className="w-full">
              <input
                {...register("name")}
                placeholder={t("addVocabulary.enterName")}
                className={`vocabulary-input ${errors.name ? "border-red-500" : ""}`}
                type="text"
                autoComplete="off"
              />
              {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>}
            </div>

            <div className="w-full">
              <textarea
                {...register("description")}
                placeholder={t("addVocabulary.addDescription")}
                className={`vocabulary-textarea ${errors.description ? "border-red-500" : ""}`}
                autoComplete="off"
              />
              {errors.description && (
                <p className="text-red-500 text-sm mt-1">{errors.description.message}</p>
              )}
            </div>
          </div>

          <div className="w-full">
            <select
              className={`vocabulary-status p-3 border border-solid rounded-[10px] ${
                errors.label ? "border-red-500" : ""
              }`}
              {...register("label")}
            >
              <option value="">{t("addVocabulary.selectTopic")}</option>
              {labelOptions.map((label) => (
                <option key={label} value={label}>
                  {label}
                </option>
              ))}
            </select>
            {errors.label && <p className="text-red-500 text-sm mt-1">{errors.label.message}</p>}
          </div>

          <div className="added-terms">
            <span className="added-terms__title">{t("addVocabulary.addTerms")}</span>

            <div className="vocabulary-languages">
              <div className="w-full">
                <select
                  className={errors.sourceLanguage ? "border-red-500" : ""}
                  {...register("sourceLanguage")}
                >
                  <option value="">{t("addVocabulary.selectLanguage")}</option>
                  <option value="uk">{t("addVocabulary.languageOptions.ukrainian")}</option>
                  <option value="en">{t("addVocabulary.languageOptions.english")}</option>
                  <option value="fr">{t("addVocabulary.languageOptions.french")}</option>
                  <option value="de">{t("addVocabulary.languageOptions.german")}</option>
                </select>
                {errors.sourceLanguage && (
                  <p className="text-red-500 text-sm mt-1">{errors.sourceLanguage.message}</p>
                )}
              </div>

              <div className="w-full">
                <select
                  className={errors.targetLanguage ? "border-red-500" : ""}
                  {...register("targetLanguage")}
                >
                  <option value="">{t("addVocabulary.selectLanguage")}</option>
                  <option value="uk">{t("addVocabulary.languageOptions.ukrainian")}</option>
                  <option value="en">{t("addVocabulary.languageOptions.english")}</option>
                  <option value="fr">{t("addVocabulary.languageOptions.french")}</option>
                  <option value="de">{t("addVocabulary.languageOptions.german")}</option>
                </select>
                {errors.targetLanguage && (
                  <p className="text-red-500 text-sm mt-1">{errors.targetLanguage.message}</p>
                )}
              </div>
            </div>

            {cards.map((card, index) => (
              <div className="terms-entrance" key={card.id}>
                <div className="terms-entrance__top">
                  <span>{index + 1}</span>
                  <img
                    src={trashCan}
                    onClick={() => {
                      if (cards.length >= 3 || index > 1) {
                        handleDeleteTerm(card.id);
                      }
                    }}
                    alt="trash-can"
                    style={{
                      cursor: cards.length >= 3 || index > 1 ? "pointer" : "not-allowed",
                      opacity: cards.length >= 3 || index > 1 ? 1 : 0.5,
                    }}
                  />
                </div>

                <div className="terms-entrance__data gap-10">
                  <div className="added-terms__block w-full">
                    <input
                      className={`added-terms__input ${cardErrors[card.id]?.term ? "border-red-500" : ""}`}
                      type="text"
                      autoComplete="off"
                      value={card.term}
                      onChange={(e) => {
                        setCards((prev) => {
                          const newCards = [...prev];
                          newCards[index].term = e.target.value;
                          return newCards;
                        });

                        if (e.target.value.trim() && cardErrors[card.id]?.term) {
                          setCardErrors((prev) => {
                            const newErrors = { ...prev };
                            if (newErrors[card.id]) {
                              delete newErrors[card.id].term;
                              if (Object.keys(newErrors[card.id]).length === 0) {
                                delete newErrors[card.id];
                              }
                            }
                            return newErrors;
                          });
                        }

                        debouncedTranslate(e.target.value, index);
                      }}
                    />
                    <span>{t("addVocabulary.term")}</span>
                    {cardErrors[card.id]?.term && (
                      <p className="text-red-500 text-sm mt-1">{cardErrors[card.id].term}</p>
                    )}
                  </div>

                  <div className="flex flex-col gap-5 w-full">
                    <div className="added-terms__block">
                      <input
                        value={card.translationInput}
                        onChange={(e) => handleTranslationInputChange(e.target.value, index)}
                        onBlur={() => handleTranslationInputBlur(index)}
                        className={`added-terms__input ${
                          cardErrors[card.id]?.translation ? "border-red-500" : ""
                        }`}
                        type="text"
                        autoComplete="off"
                      />
                      <span>{t("addVocabulary.translation")}</span>
                      {cardErrors[card.id]?.translation && (
                        <p className="text-red-500 text-sm mt-1">{cardErrors[card.id].translation}</p>
                      )}
                    </div>

                    {card.isTranslating ? (
                      <CircularProgress />
                    ) : card.listOfTranslations?.length > 0 ? (
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
                    ) : card.term.trim() && card.allTranslations.length === 0 && !card.isTranslating ? (
                      <div className="no-translations">{t("addVocabulary.noTranslations")}</div>
                    ) : null}

                    <div className="added-terms__block">
                      <input
                        value={card.meaning || ""}
                        onChange={(e) => {
                          setCards((prev) => {
                            const newCards = [...prev];
                            newCards[index].meaning = e.target.value;
                            return newCards;
                          });
                        }}
                        className="added-terms__input"
                        type="text"
                        autoComplete="off"
                      />
                      <span>{t("addVocabulary.definition")}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
            <button type="button" className="add-card" onClick={handleAddCard}>
              {t("addVocabulary.addCard")}
            </button>
          </div>
        </div>
        <button
          type="submit"
          className="vocabulary-submit sticky top-[10px]"
          disabled={isSubmitting || loading}
          data-testid="dictionary-submit"
        >
          {isSubmitting || loading ? t("common.loading") : t("addVocabulary.create")}
        </button>
      </form>
    </PageWrapper>
  );
};

export default AddVocabulary;
