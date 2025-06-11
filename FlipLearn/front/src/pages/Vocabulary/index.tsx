import Slider from "react-slick";
import PageWrapper from "../../components/PageWrapper";
import sliderSettings from "./sliderSettings";
import FlipCard from "./Card";
import speaker from "../../assets/speaker-button.svg";
import warning from "../../assets/warning.svg";
import moreInfo from "../../assets/more-button.svg";
import users from "../../assets/avatars.svg";
import lock from "../../assets/lock.svg";
import lockOpen from "../../assets/lock_open.svg";

import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "./index.scss";
import { CircularProgress, Modal } from "@mui/material";
import { useEffect, useState, useMemo, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { formatDate } from "../../utils/formatDate";
import { AccessModal } from "./AccessModal";

interface Card {
  cardId: number;
  term: string;
  meaning: string | null;
  translation: string;
  status?: string;
}

interface Vocabulary {
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
  cefr: string;
  creator?: any;
}

interface IVocabulary {
  dictionary: Vocabulary;
  access: string;
}

const ELEVENLABS_VOICES = {
  default: "ErXwobaYiN019PkySvjV",
};

export const WORDS_STATUSES = [
  { value: "all", label: "statusFilters.all" },
  { value: "with_warnings", label: "statusFilters.withWarnings" },
  { value: "no_warnings", label: "statusFilters.noWarnings" },
];

const Vocabulary = () => {
  const { t } = useTranslation();
  const [vocabulary, setVocabulary] = useState<IVocabulary | null>(null);
  const [currentSlide, setCurrentSlide] = useState(0);
  const [selectedStatus, setSelectedStatus] = useState("all");
  const [loading, setLoading] = useState(true);
  const [accessModalView, setAccessModalView] = useState(false);
  const [vocabularyOptions, setVocabularyOptions] = useState(false);
  const [error, setError] = useState(false);
  const [accesses, setAccesses] = useState<any>([]);
  const [showUsersList, setShowUsersList] = useState(false);

  const [isSpeaking, setIsSpeaking] = useState(false);
  const { id } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  // ElevenLabs configuration
  const ELEVENLABS_API_KEY = "sk_e3d554648c45dceaf9dbe4526efda0e0e8d2fd8798696c3b";
  const ELEVENLABS_BASE_URL = "https://api.elevenlabs.io/v1";

  useEffect(() => {
    if (token) {
      getDictionary();
      getAccesses();
    }
  }, [id]);

  const getAccesses = () => {
    axios
      .get(`https://localhost:7288/dictionary-access?dictionaryId=${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setAccesses(res.data);
      });
  };

  const getDictionary = () => {
    axios
      .get(`https://localhost:7288/Dictionary/full?dictionaryId=${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setVocabulary(response.data);
      })
      .catch((error) => {
        console.error(`${t("vocabulary.loadingError")} ${error.response?.data || error.message}`);
        setError(true);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const deleteVocabulary = () => {
    axios
      .delete(`https://localhost:7288/Dictionary`, {
        params: {
          dictionatyId: id,
        },
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => {
        navigate("/");
      })
      .catch((error) => {
        console.error(`${t("vocabulary.loadingError")} ${error.response?.data || error.message}`);
      });
  };

  const speakWithElevenLabs = useCallback(
    async (text: string, lang: string): Promise<void> => {
      if (!ELEVENLABS_API_KEY) {
        console.warn("ElevenLabs API key not found, falling back to Web Speech API");
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = lang;
        window.speechSynthesis.speak(utterance);
        return;
      }

      try {
        const voiceId = ELEVENLABS_VOICES.default;

        const response = await axios.post(
          `${ELEVENLABS_BASE_URL}/text-to-speech/${voiceId}`,
          {
            text: text,
            model_id: "eleven_multilingual_v2",
            voice_settings: {
              stability: 0.5,
              similarity_boost: 0.8,
              style: 0.2,
              use_speaker_boost: true,
            },
          },
          {
            headers: {
              Accept: "audio/mpeg",
              "Content-Type": "application/json",
              "xi-api-key": ELEVENLABS_API_KEY,
            },
            responseType: "blob",
          }
        );

        const audioBlob = new Blob([response.data], { type: "audio/mpeg" });
        const audioUrl = URL.createObjectURL(audioBlob);
        const audio = new Audio(audioUrl);

        return new Promise<void>((resolve, reject) => {
          audio.onended = () => {
            URL.revokeObjectURL(audioUrl);
            resolve();
          };
          audio.onerror = () => {
            URL.revokeObjectURL(audioUrl);
            reject(new Error("Audio playback failed"));
          };
          audio.play().catch(reject);
        });
      } catch (error) {
        console.error("ElevenLabs TTS error:", error);
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = lang;
        window.speechSynthesis.speak(utterance);
      }
    },
    [ELEVENLABS_API_KEY]
  );

  const speakText = useCallback(
    async (term: string, translation: string, fromLang: string, toLang: string) => {
      if (isSpeaking) return;

      setIsSpeaking(true);

      try {
        await speakWithElevenLabs(term, fromLang);

        await new Promise((resolve) => setTimeout(resolve, 500));

        await speakWithElevenLabs(translation, toLang);
      } catch (error) {
        console.error("Error during text-to-speech:", error);
      } finally {
        setIsSpeaking(false);
      }
    },
    [isSpeaking, speakWithElevenLabs]
  );

  const toggleAccessModal = () => setAccessModalView((prev) => !prev);

  const getFilteredCards = () => {
    if (!vocabulary) return [];

    switch (selectedStatus) {
      case "all":
        return vocabulary.dictionary.cards;
      case "with_warnings":
        return vocabulary.dictionary.cards.filter((card) => card.status === "Controversial");
      case "no_warnings":
        return vocabulary.dictionary.cards.filter(
          (card) => card.status === "Private" || card.status === "Confirmed"
        );
      default:
        return vocabulary.dictionary.cards;
    }
  };

  const allAuthors = useMemo(() => {
    const coAuthors = accesses.filter((user: any) => user.access === "CoAuthor");
    const creator = vocabulary?.dictionary?.creator;

    const authors = [];

    if (creator) {
      authors.push({
        ...creator,
        access: "Creator",
      });
    }

    authors.push(...coAuthors);

    return authors;
  }, [accesses, vocabulary?.dictionary?.creator]);

  const changeVocabularyPrivacy = () => {
    axios.patch(
      `https://localhost:7288/Dictionary/visibility?dictionatyId=${vocabulary?.dictionary.id}`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
  };

  const filteredCards = vocabulary ? getFilteredCards() : [];

  useEffect(() => {
    setCurrentSlide(0);
  }, [selectedStatus]);

  if (error) {
    return (
      <PageWrapper>
        <div className="flex justify-center items-center min-h-screen">{t("vocabulary.noAccess")}</div>
      </PageWrapper>
    );
  }

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
      <Modal
        open={accessModalView}
        onClose={toggleAccessModal}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <AccessModal />
      </Modal>

      <div className="user-vocabulary pt-[20px] relative max-w-[1000px]">
        <div className="flex w-full justify-between">
          <div className="flex gap-4">
            <span className="text-2xl">{vocabulary.dictionary.title}</span>
            {vocabulary.access === "Creator" && (
              <button
                className="flex items-center gap-2 rounded-[8px] px-2 hover:border-2"
                onClick={changeVocabularyPrivacy}
              >
                <img src={vocabulary.dictionary.isPublic ? lockOpen : lock} alt="" />
              </button>
            )}
          </div>
          <span>{formatDate(vocabulary.dictionary.creationDate)}</span>
        </div>

        <div className="flex w-full justify-between">
          <span className="text-[#4F4F4F] text-xl">{vocabulary.dictionary.label}</span>
          <span className="text-m bg-[#F3D86D] p-2 rounded-[8px]">{vocabulary.dictionary.cefr}</span>
        </div>

        <span className="text-[#4F4F4F] w-full">{vocabulary.dictionary.description}</span>

        <div className="user-vocabulary__tests w-[100%] justify-center">
          <button
            onClick={() =>
              navigate(`/create-test/${id}`, {
                state: {
                  vocName: vocabulary.dictionary.title,
                  termsCount: vocabulary.dictionary.cards.length,
                },
              })
            }
            className="vocabulary-test w-[90%]"
          >
            {t("vocabulary.test")}
          </button>
        </div>

        <div className="relative w-[850px] flex justify-center">
          <Slider {...sliderSettings} beforeChange={(_, next) => setCurrentSlide(next)}>
            {vocabulary.dictionary.cards.map(({ term, translation }, index) => (
              <FlipCard key={index} word={term} translate={translation} />
            ))}
          </Slider>
        </div>

        <span>
          {t("vocabulary.pageCount", {
            current: currentSlide + 1,
            total: vocabulary.dictionary.cards.length,
          })}
        </span>

        <div className="user-vocabulary__termins">
          <div className="flex w-full justify-between items-center">
            <div className="flex gap-4 items-center">
              <div className="relative">
                {allAuthors.length > 0 && (
                  <div
                    className="flex items-center gap-2 cursor-pointer bg-white rounded-lg p-2 transition-all duration-300"
                    onClick={() => allAuthors.length > 1 && setShowUsersList(!showUsersList)}
                    style={{
                      width: showUsersList ? "auto" : allAuthors.length > 1 ? "200px" : "auto",
                      minWidth: "200px",
                    }}
                  >
                    <img src={users} alt="users" className="w-[35px] h-[35px] flex-shrink-0" />

                    <div className="flex items-center gap-1 overflow-hidden">
                      {!showUsersList ? (
                        <>
                          <span className="text-sm font-medium whitespace-nowrap">
                            {allAuthors[0]?.username || allAuthors[0]?.email?.split("@")[0] || "author_name"}
                          </span>
                          {allAuthors.length > 1 && (
                            <span className="text-xs text-gray-500 whitespace-nowrap">
                              +{allAuthors.length - 1} more
                            </span>
                          )}
                        </>
                      ) : (
                        <div className="flex items-center gap-1 whitespace-nowrap">
                          {allAuthors.slice(0, 3).map((user, index) => (
                            <span key={user.id || index} className="text-sm font-medium">
                              {user.username || user.email?.split("@")[0] || `author_name${index + 1}`}
                              {index < Math.min(allAuthors.length, 3) - 1 && ", "}
                            </span>
                          ))}
                          {allAuthors.length > 3 && (
                            <span className="text-xs text-gray-500">+{allAuthors.length - 3} more</span>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {showUsersList && (
                  <div className="fixed inset-0 z-10" onClick={() => setShowUsersList(false)} />
                )}
              </div>
            </div>

            {vocabulary.access !== "Reader" && (
              <div className="flex flex-end flex-col gap-5 items-end relative">
                <img src={moreInfo} onClick={() => setVocabularyOptions((prev) => !prev)} alt="moreInfo" data-testid="more-info-button" />

                {vocabularyOptions && (
                  <>
                    <div className="modalOverlayOptions" onClick={() => setVocabularyOptions(false)} />

                    <div className="flex flex-col bg-white border-1 voc-options gap-4 top-[20px] rounded-[10px] items-start w-[250px]">
                      {vocabulary.access === "Creator" && (
                        <>
                          <button
                            onClick={toggleAccessModal}
                            className="hover:bg-gray-200 w-full  text-left rounded-[6px] p-1"
                            data-testid="grant-access-dictionary-button"
                          >
                            {t("vocabulary.userOptions.grantAccess")}
                          </button>
                          <button
                            onClick={deleteVocabulary}
                            className="hover:bg-gray-200  w-full  text-left rounded-[6px] p-1"
                            data-testid="delete-dictionary-button"
                          >
                            {t("vocabulary.userOptions.deleteVocabulary")}
                          </button>
                        </>
                      )}
                      <button
                        className="hover:bg-gray-200 w-full text-left rounded-[6px] p-1"
                        onClick={() => navigate(`/edit-vocabulary/${vocabulary.dictionary.id}`)}
                        data-testid="edit-dictionary-button"
                      >
                        {t("vocabulary.userOptions.editVocabulary")}
                      </button>
                    </div>
                  </>
                )}
              </div>
            )}
          </div>

          <div className="flex w-full justify-between items-center">
            <span>{t("vocabulary.termsSection")}</span>

            <select
              className="formalization-option__select cursor-pointer rounded-lg text-[#4F4F4F] p-2 border-2"
              value={selectedStatus}
              onChange={(event) => setSelectedStatus(event.target.value)}
            >
              {WORDS_STATUSES.map((option) => (
                <option key={option.value} value={option.value}>
                  {t(`vocabulary.${option.label}`)}
                </option>
              ))}
            </select>
          </div>

          <div className="termins">
            {filteredCards.length > 0 ? (
              filteredCards.map(({ term, translation, meaning, status }) => (
                <div key={term} className="termins-item gap-8">
                  <div className="flex w-full justify-between items-center">
                    <span className="w-[200px]">{term}</span>
                    <div className="flex flex-col gap-1 items-start ml-[20px] flex-1">
                      <span>{translation}</span>
                      {meaning && <span>{meaning}</span>}
                      {/* {meaning && (
                        <Tooltip title={meaning.length > 40 && meaning}>
                          <span>{slicedText(meaning, 40)}</span>
                        </Tooltip>
                      )} */}
                    </div>
                  </div>

                  <div className="flex flex-col gap-4">
                    {status === "Controversial" && <img src={warning} alt="" />}
                    <img
                      src={speaker}
                      alt="speaker"
                      onClick={() =>
                        speakText(
                          term,
                          translation,
                          vocabulary.dictionary.fromLang,
                          vocabulary.dictionary.toLang
                        )
                      }
                      className={`cursor-pointer ${isSpeaking ? "opacity-50" : ""}`}
                      style={{
                        pointerEvents: isSpeaking ? "none" : "auto",
                        filter: isSpeaking ? "grayscale(100%)" : "none",
                      }}
                    />
                  </div>
                </div>
              ))
            ) : (
              <div className="flex justify-center items-center p-4">{t("vocabulary.noTermsWithStatus")}</div>
            )}
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};

export default Vocabulary;
