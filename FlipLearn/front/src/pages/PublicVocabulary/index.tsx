import Slider from "react-slick";
import PageWrapper from "../../components/PageWrapper";
import sliderSettings from "./sliderSettings";
import speaker from "../../assets/speaker-button.svg";
import warning from "../../assets/warning.svg";
import users from "../../assets/avatars.svg";

import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
// import "./index.scss";
import { CircularProgress } from "@mui/material";
import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { formatDate } from "../../utils/formatDate";
import FlipCard from "../Vocabulary/Card";

interface Card {
  cardId: number;
  term: string;
  meaning: string | null;
  translation: string | null;
  status?: string;
}

interface Creator {
  id: number;
  username: string;
  email: string;
  trustLevel: number | null;
  image: string | null;
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
  creator?: Creator;
}

const ELEVENLABS_VOICES = {
  default: "ErXwobaYiN019PkySvjV",
};

export const WORDS_STATUSES = [
  { value: "all", label: "statusFilters.all" },
  { value: "with_warnings", label: "statusFilters.withWarnings" },
  { value: "no_warnings", label: "statusFilters.noWarnings" },
];

const PublicVocabulary = () => {
  const { t } = useTranslation();
  const [vocabulary, setVocabulary] = useState<Vocabulary | null>(null);
  const [currentSlide, setCurrentSlide] = useState(0);
  const [selectedStatus, setSelectedStatus] = useState("all");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [isSpeaking, setIsSpeaking] = useState(false);

  const { id } = useParams<{ id: string }>();

  // ElevenLabs configuration
  const ELEVENLABS_API_KEY = "sk_e3d554648c45dceaf9dbe4526efda0e0e8d2fd8798696c3b";
  const ELEVENLABS_BASE_URL = "https://api.elevenlabs.io/v1";

  useEffect(() => {
    if (id) {
      getDictionary();
    }
  }, [id]);

  const getDictionary = () => {
    if (!id) {
      setError(true);
      setLoading(false);
      return;
    }

    axios
      .get(`https://localhost:7288/Dictionary/full?dictionaryId=${id}`)
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

  const getFilteredCards = (): Card[] => {
    if (!vocabulary || !vocabulary.cards) return [];

    switch (selectedStatus) {
      case "all":
        return vocabulary.cards;
      case "with_warnings":
        return vocabulary.cards.filter((card) => card.status === "Controversial");
      case "no_warnings":
        return vocabulary.cards.filter((card) => card.status === "Private" || card.status === "Confirmed");
      default:
        return vocabulary.cards;
    }
  };

  const filteredCards = getFilteredCards();

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

  // Ensure cards exist before rendering
  const cardsToRender = vocabulary.cards || [];

  return (
    <PageWrapper>
      <div className="user-vocabulary pt-[20px] relative max-w-[1000px]">
        <div className="flex w-full justify-between">
          <div className="flex gap-4">
            <span className="text-2xl">{vocabulary.title}</span>
          </div>
          <span>{formatDate(vocabulary.creationDate)}</span>
        </div>

        <div className="flex w-full justify-between">
          <span className="text-[#4F4F4F] text-xl">{vocabulary.label}</span>
          <span className="text-m bg-[#F3D86D] p-2 rounded-[8px]">{vocabulary.cefr}</span>
        </div>

        <span className="text-[#4F4F4F] w-full">{vocabulary.description}</span>

        {cardsToRender.length > 0 && (
          <div className="relative w-[850px] flex justify-center">
            <Slider {...sliderSettings} beforeChange={(_, next) => setCurrentSlide(next)}>
              {cardsToRender.map(({ term, translation }, index) => (
                <FlipCard key={index} word={term} translate={translation || ""} />
              ))}
            </Slider>
          </div>
        )}

        <span>
          {t("vocabulary.pageCount", {
            current: currentSlide + 1,
            total: cardsToRender.length,
          })}
        </span>

        <div className="user-vocabulary__termins">
          <div className="flex w-full justify-between items-center">
            <div className="flex gap-4 items-center">
              {vocabulary.creator && (
                <div className="flex items-center gap-2 bg-white rounded-lg p-2">
                  <img src={users} alt="users" className="w-[35px] h-[35px] flex-shrink-0" />
                  <span className="text-sm font-medium">
                    {vocabulary.creator.username || vocabulary.creator.email?.split("@")[0] || "author_name"}
                  </span>
                </div>
              )}
            </div>
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
              filteredCards.map(({ term, translation, meaning, status }, index) => (
                <div key={`${term}-${index}`} className="termins-item gap-8">
                  <div className="flex w-full justify-between items-center">
                    <span className="w-[200px]">{term}</span>
                    <div className="flex flex-col gap-1 items-start ml-[20px] flex-1">
                      <span>{translation || ""}</span>
                      {meaning && <span>{meaning}</span>}
                    </div>
                  </div>

                  <div className="flex flex-col gap-4">
                    {status === "Controversial" && <img src={warning} alt="" />}
                    <img
                      src={speaker}
                      alt="speaker"
                      onClick={() =>
                        speakText(term, translation || "", vocabulary.fromLang, vocabulary.toLang)
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

export default PublicVocabulary;
