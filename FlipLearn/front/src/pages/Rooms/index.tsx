import { useEffect, useRef, useState } from "react";
import { HubConnectionBuilder, HttpTransportType, HubConnection } from "@microsoft/signalr";
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import PageWrapper from "../../components/PageWrapper";
import { useTranslation } from "react-i18next";

type RoomMinimal = {
  id: string;
  fromLanguage: string;
  toLanguage: string;
  questionsCount: number;
  creator: {
    id: number;
    username: string;
    email: string;
    image: string;
    trustLevel: string;
  };
  cefrMax: string;
  cefrMin: string;
  raceDurationInSec: number;
  participantsCount: number;
  dictionariesCount: number;
  name: string;
};

export const Rooms = () => {
  const { t } = useTranslation();
  const [rooms, setRooms] = useState<RoomMinimal[]>([]);
  const [filteredRooms, setFilteredRooms] = useState<RoomMinimal[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedLevel, setSelectedLevel] = useState<string>("");
  const [selectedLanguage, setSelectedLanguage] = useState<string>("");
  const [availableLanguages, setAvailableLanguages] = useState<string[]>([]);

  const navigate = useNavigate();
  const connectionRef = useRef<HubConnection | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const connection = new HubConnectionBuilder()
      .withUrl(`https://localhost:7288/rooms?access_token=${token}`, {
        transport: HttpTransportType.WebSockets,
        withCredentials: true,
      })
      .withAutomaticReconnect()
      .build();

    connectionRef.current = connection;

    const setupListeners = () => {
      connection.on("ShowRooms", (roomsData: RoomMinimal[]) => {
        setRooms(roomsData);
        setFilteredRooms(roomsData);
        extractLanguages(roomsData);
        setIsLoading(false);
      });

      connection.on("RoomWasAdded", (newRoom: RoomMinimal) => {
        setRooms((prevRooms) => {
          const exists = prevRooms.some((room) => room.id === newRoom.id);
          if (exists) return prevRooms;
          const updatedRooms = [...prevRooms, newRoom];
          extractLanguages(updatedRooms);
          applyFilters(updatedRooms, selectedLevel, selectedLanguage);
          return updatedRooms;
        });
      });

      connection.on("RoomWasDeleted", (roomId: string) => {
        setRooms((prevRooms) => {
          const updatedRooms = prevRooms.filter((room) => room.id !== roomId);
          extractLanguages(updatedRooms);
          applyFilters(updatedRooms, selectedLevel, selectedLanguage);
          return updatedRooms;
        });
      });

      connection.on("UpdateRoomParticipantsCount", (roomId: string, count: number) => {
        setRooms((prevRooms) => {
          const updatedRooms = prevRooms.map((room) =>
            room.id === roomId ? { ...room, participantsCount: count } : room
          );
          applyFilters(updatedRooms, selectedLevel, selectedLanguage);
          return updatedRooms;
        });
      });

      connection.on("RedirectToRoom", async (roomId: string) => {
        await joinRoom(roomId);
      });
    };

    const startConnection = async () => {
      try {
        setupListeners();

        await connection.start();

        await connection.invoke("SendRooms");
      } catch (err) {
        console.error("SignalR connection error:", err);
        setIsLoading(false);
      }
    };

    startConnection();

    connection.onreconnected(async () => {
      await connection.invoke("SendRooms");
    });

    return () => {
      if (connectionRef.current) {
        connectionRef.current.stop();
      }
    };
  }, []);

  const extractLanguages = (roomsData: RoomMinimal[]) => {
    const languages = new Set<string>();

    roomsData.forEach((room) => {
      languages.add(room.fromLanguage);
      languages.add(room.toLanguage);
    });

    setAvailableLanguages(Array.from(languages).sort());
  };

  const applyFilters = (roomsData: RoomMinimal[], level: string, language: string) => {
    let result = [...roomsData];

    if (level) {
      result = result.filter(
        (room) =>
          room.cefrMin === level || room.cefrMax === level || (room.cefrMin <= level && room.cefrMax >= level)
      );
    }

    if (language) {
      result = result.filter((room) => room.fromLanguage === language || room.toLanguage === language);
    }

    setFilteredRooms(result);
  };

  const handleLevelChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const level = e.target.value;
    setSelectedLevel(level);
    applyFilters(rooms, level, selectedLanguage);
  };

  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const language = e.target.value;
    setSelectedLanguage(language);
    applyFilters(rooms, selectedLevel, language);
  };

  const resetFilters = () => {
    setSelectedLevel("");
    setSelectedLanguage("");
    setFilteredRooms(rooms);
  };

  const joinRoom = async (roomId: string) => {
    try {
      navigate(`/room/${roomId}`);
    } catch (error) {
      console.error("Error joining room:", error);
    }
  };

  const handleJoinRoom = async (roomId: string) => {
    await joinRoom(roomId);
  };

  return (
    <PageWrapper>
      <div className="w-full px-5 py-3">
        <div className="flex justify-between items-center mb-4">
          <span className="text-2xl">{t("rooms.title")}</span>

          <div className="flex gap-3">
            <div className="flex items-center gap-3">
              <select
                className="rounded-[8px] p-[8px] border border-solid"
                value={selectedLevel}
                onChange={handleLevelChange}
              >
                <option value="">{t("rooms.filters.level")}</option>
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
                <option value="">{t("rooms.filters.language")}</option>
                {availableLanguages.map((language) => (
                  <option key={language} value={language}>
                    {language}
                  </option>
                ))}
              </select>

              <button
                className="bg-[#E5E5E5] p-2 rounded-[8px] h-[40px] text-[#4F4F4F]"
                onClick={resetFilters}
              >
                {t("rooms.filters.reset")}
              </button>
            </div>

            <button
              className="bg-[#E5E5E5] p-2 rounded-[8px] h-[40px] text-[#4F4F4F]"
              onClick={() => navigate("/create-test/12", { state: { isRoomTest: true } })}
            >
              {t("rooms.createRoom")}
            </button>
          </div>
        </div>

        {isLoading ? (
          <p>{t("rooms.loading")}</p>
        ) : filteredRooms.length === 0 ? (
          <p>{t("rooms.noRooms")}</p>
        ) : (
          <div className="flex flex-col w-full mt-4">
            {filteredRooms.map((room) => (
              <div
                key={room.id}
                style={{ border: "1px solid #ccc", borderRadius: 8, padding: 16, marginBottom: 12 }}
                className="gap-2 flex flex-col"
              >
                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center">
                    <span className="text-xl font-bold">{room.name}</span>
                    <span className="bg-[#F3D86D] p-2 rounded-[8px]">
                      {`${room.cefrMin}-${room.cefrMax}`}
                    </span>
                  </div>

                  <div className="flex gap-4 items-center">
                    <span className="text-[#4F4F4F] text-[18px]">
                      {room.questionsCount} {t("rooms.roomInfo.questions")}
                    </span>
                    <span className="text-xl font-bold">
                      {room.raceDurationInSec} {t("rooms.roomInfo.sec")}
                    </span>
                  </div>
                </div>

                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center text-[#4F4F4F]">
                    <span className="text-sm">{room.fromLanguage}</span>
                    <span className="text-sm">{"->"}</span>
                    <span className="text-sm">{room.toLanguage}</span>
                  </div>

                  <div className="text-sm">
                    {t("rooms.roomInfo.participants")} {room.participantsCount}
                  </div>
                </div>

                <div className="flex gap-2 items-center">
                  <span className="text-xl">
                    {t("rooms.roomInfo.dictionariesCount")} {room.dictionariesCount}
                  </span>
                </div>

                <div className="flex w-full justify-between">
                  <div className="flex gap-4 items-center">
                    <div className="rounded-full w-[40px] h-[40px] bg-[red]" />
                    <span>{room.creator.username}</span>
                  </div>

                  <Button
                    sx={{ border: "1px solid lightgrey", color: "black", width: "200px", height: "40px" }}
                    onClick={() => handleJoinRoom(room.id)}
                  >
                    {t("rooms.roomInfo.join")}
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </PageWrapper>
  );
};
