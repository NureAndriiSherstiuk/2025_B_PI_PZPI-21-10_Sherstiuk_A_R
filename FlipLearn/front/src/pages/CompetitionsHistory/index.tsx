import { Button, CircularProgress } from "@mui/material";
import PageWrapper from "../../components/PageWrapper";
import { useState, useEffect } from "react";
import axios from "axios";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";

// Available languages
const availableLanguages = ["en", "uk", "de", "fr", "es", "it"];
// Language display names mapping
const languageDisplayNames = {
  en: "English",
  uk: "Ukrainian",
  de: "German",
  fr: "French",
  es: "Spanish",
  it: "Italian",
};

export const CompetitionsHistory = () => {
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedLevel, setSelectedLevel] = useState("");
  const [selectedLanguage, setSelectedLanguage] = useState("");
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    getHistory();
  }, []);

  const getHistory = () => {
    axios
      .get(`https://localhost:7288/races/finished`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setRooms(response.data);
        setFilteredRooms(response.data);
      })
      .catch((error) => {
        console.error(`Error loading race history: ${error.response?.data || error.message}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  // Filter handlers
  const handleLevelChange = (e: any) => {
    const level = e.target.value;
    setSelectedLevel(level);
    applyFilters(level, selectedLanguage);
  };

  const handleLanguageChange = (e: any) => {
    const language = e.target.value;
    setSelectedLanguage(language);
    applyFilters(selectedLevel, language);
  };

  // Apply filters
  const applyFilters = (level: any, language: any) => {
    let filtered: any = [...rooms];

    if (level) {
      filtered = filtered.filter(
        (room: any) =>
          room.cefrMin === level ||
          room.cefrMax === level ||
          (getLevelValue(room.cefrMin) <= getLevelValue(level) &&
            getLevelValue(level) <= getLevelValue(room.cefrMax))
      );
    }

    if (language) {
      const languageCode = language;
      filtered = filtered.filter(
        (room: any) => room.fromLanguage === languageCode || room.toLanguage === languageCode
      );
    }

    setFilteredRooms(filtered);
  };

  // Function to compare CEFR levels
  const getLevelValue = (level: any) => {
    const levels: any = { A1: 1, A2: 2, B1: 3, B2: 4, C1: 5, C2: 6 };
    return levels[level] || 0;
  };

  // Reset filters
  const resetFilters = () => {
    setSelectedLevel("");
    setSelectedLanguage("");
    setFilteredRooms(rooms);
  };

  // Helper function to get display name for language code
  const getLanguageDisplayName = (code: any) => {
    return languageDisplayNames[code] || code;
  };

  // Helper function to format CEFR level display
  const formatCefrLevel = (cefrMin: string, cefrMax: string) => {
    if (cefrMin === cefrMax) {
      return cefrMin;
    }
    return `${cefrMin}-${cefrMax}`;
  };

  const formatDate = (date: string) => {
    const newDate = new Date(date);
    return format(newDate, "dd.MM.yyyy");
  };

  return (
    <PageWrapper>
      <div className="w-full px-5 py-3">
        <div className="flex justify-between items-center mb-4">
          <span className="text-2xl">Завершенні змагання</span>

          <div className="flex gap-3">
            <div className="flex items-center gap-3">
              <select
                className="rounded-[8px] p-[8px] border border-solid"
                value={selectedLevel}
                onChange={handleLevelChange}
              >
                <option value="">Рівень</option>
                <option value="A1">A1</option>
                <option value="A2">A2</option>
                <option value="B1">B1</option>
                <option value="B2">B2</option>
                <option value="C1">C1</option>
                <option value="C2">C2</option>
              </select>

              <select
                className="rounded-[8px] p-[8px] border border-solid"
                value={selectedLanguage}
                onChange={handleLanguageChange}
              >
                <option value="">Мова</option>
                {availableLanguages.map((language) => (
                  <option key={language} value={language}>
                    {getLanguageDisplayName(language)}
                  </option>
                ))}
              </select>

              <button
                className="bg-[#E5E5E5] p-2 rounded-[8px] h-[40px] text-[#4F4F4F]"
                onClick={resetFilters}
              >
                Скинути
              </button>
            </div>
          </div>
        </div>

        {isLoading ? (
          <CircularProgress />
        ) : filteredRooms.length === 0 ? (
          <p>Історія змагань пуста або немає співпадінь за вибраними фільтрами</p>
        ) : (
          <div className="flex flex-col w-full mt-4">
            {filteredRooms.map((room: any, index: any) => (
              <div
                key={room.id}
                style={{ border: "1px solid #ccc", borderRadius: 8, padding: 16, marginBottom: 12 }}
                className="gap-4 flex flex-col"
              >
                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center">
                    <span className="text-xl font-bold">{room.name || `Room №${index + 1}`}</span>
                    <span className="bg-[#F3D86D] p-2 rounded-[8px]">
                      {formatCefrLevel(room.cefrMin, room.cefrMax)}
                    </span>
                  </div>

                  <div className="flex gap-4 items-center">
                    <span className="text-[#4F4F4F] text-[18px]">{room.questionsCount} questions</span>
                  </div>
                </div>

                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center text-[#4F4F4F]">
                    <span className="text-sm">{getLanguageDisplayName(room.fromLanguage)}</span>
                    <span className="text-sm">{"->"}</span>
                    <span className="text-sm">{getLanguageDisplayName(room.toLanguage)}</span>
                  </div>

                  <div className="text-sm">Кількість участників: {room.participants?.length}</div>
                </div>

                <div className="flex w-full justify-end text-sm">{formatDate(room?.startTime)}</div>

                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center">
                    <div
                      className="rounded-full w-[40px] h-[40px]"
                      style={{ backgroundColor: room.creator?.image || "#d9d9d9" }}
                    />
                    <span>{room.creator?.username || "Unknown"}</span>
                  </div>

                  <button
                    className="border border-gray-300 text-black w-[200px] rounded-[8px] py-2 px-3"
                    onClick={() => navigate(`/competitions-details/${room.mongoRoomId}`)}
                  >
                    Деталі змагання
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </PageWrapper>
  );
};
