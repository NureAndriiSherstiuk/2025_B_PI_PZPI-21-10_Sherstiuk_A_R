import { useEffect, useRef, useState } from "react";
import { HubConnection, HubConnectionBuilder, HttpTransportType } from "@microsoft/signalr";
import { useParams, useNavigate } from "react-router-dom";
import { Button, CircularProgress } from "@mui/material";
import { useTranslation } from "react-i18next";

import avatarsIcon from "../../assets/avatars.svg";
import { store } from "../../store/store";
import { addParticipant } from "../../store/race/raceSlice";

type UserMinimal = {
  id: number;
  username: string;
  email: string;
  image: string;
  trustLevel: string;
};

type Room = {
  id: string;
  fromLanguage: string;
  toLanguage: string;
  questionsCount: number;
  creator: UserMinimal;
  cefrMax: string;
  cefrMin: string;
  totalTimeInSeconds: number;
  participants: UserMinimal[];
  state: string;
  test: any;
  name: string;
};

export const Room = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const [room, setRoom] = useState<Room | null>(null);
  const [isCreator, setIsCreator] = useState(false);
  const [raceStarted, setRaceStarted] = useState(false);
  const connectionRef = useRef<HubConnection | any>(null);
  const roomRef = useRef<Room | null>(null); // Add a ref to maintain latest room data
  const navigate = useNavigate();

  console.log("room", room);

  // Update the ref whenever room state changes
  useEffect(() => {
    roomRef.current = room;
  }, [room]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const connection = new HubConnectionBuilder()
      .withUrl(`https://localhost:7288/room?access_token=${token}`, {
        transport: HttpTransportType.WebSockets,
      })
      .withAutomaticReconnect()
      .build();

    const setupConnection = async () => {
      try {
        connection.on("ReceiveRoom", (roomData: Room, isCreatorStatus: boolean) => {
          setRoom(roomData);
          roomRef.current = roomData; // Update the ref immediately
          setIsCreator(isCreatorStatus);
        });

        connection.on("NotifyUserConnected", (user: UserMinimal) => {
          setRoom((prevRoom) => {
            if (!prevRoom) return null;

            const userExistsInRoom = prevRoom.participants.some((p) => p.id === user.id);
            if (!userExistsInRoom) {
              const updatedRoom = {
                ...prevRoom,
                participants: [...prevRoom.participants, user],
              };
              roomRef.current = updatedRoom;
              return updatedRoom;
            }
            return prevRoom;
          });
        });

        connection.on("NotifyUserDisconnected", (user: UserMinimal) => {
          setRoom((prevRoom) => {
            if (!prevRoom) return null;
            const updatedRoom = {
              ...prevRoom,
              participants: prevRoom.participants.filter((p) => p.id !== user.id),
            };
            roomRef.current = updatedRoom; // Update ref
            return updatedRoom;
          });
        });

        connection.on("RoomWasDissolved", () => {
          alert("Room was dissolved by creator");

          if (connectionRef.current && connectionRef.current.state === "Connected" && id) {
            try {
              (async () => {
                await connectionRef.current.invoke("ExitRoom", id);
                await connectionRef.current.stop();

                navigate("/rooms");
              })();
            } catch (error) {
              console.error("Error when disconnecting:", error);
              navigate("/rooms");
            }
          } else {
            navigate("/rooms");
          }
        });

        connection.on("StartRace", (startTime: Date, totalTimeInSeconds: number) => {
          setRaceStarted(true);

          const currentRoom = roomRef.current;

          if (currentRoom && currentRoom.test) {
            const timePerQuestion = 20;

            const updatedTestData = {
              ...currentRoom.test,
              timePerQuestion,
              totalTimeInSeconds,
            };

            setTimeout(() => {
              navigate(`/race-test/${id}`, { state: { testData: updatedTestData } });
            }, 100);
          } else {
            console.error("Room or test data is missing when race started:", currentRoom);
            alert("Cannot start test: test data is missing. Please try rejoining the room.");
          }
        });

        connection.on("CollectAnswersAndFinishRace", () => {
          console.log("Time's up! Collecting answers...");
        });

        connection.on(
          "ParticipantFinished",
          (participant: UserMinimal, correctCount: number, wrongCount: number, time: string) => {
            console.log(
              `Participant ${participant.username} finished race in ${time}, answers: ${correctCount} ${wrongCount}`
            );
          }
        );

        connection.on("GetRaceResults", (results: any[]) => {
          console.log("Race results room page:", results);
          store.dispatch(addParticipant(results));
        });

        // Establish connection
        await connection.start();

        //Join room
        if (id) {
          await connection.invoke("JoinRoom", id);
        }

        connectionRef.current = connection;
      } catch (error) {
        console.error("Failed to connect to Room hub:", error);
      }
    };

    setupConnection();
  }, [id, navigate]);

  const handleLeaveRoom = async () => {
    if (connectionRef.current && connectionRef.current.state === "Connected" && id) {
      try {
        await connectionRef.current.invoke("ExitRoom", id);
        navigate("/rooms");
      } catch (error) {
        console.error("Error when leaving room:", error);
      }
    }
  };

  const handleDissolveRoom = async () => {
    if (connectionRef.current && connectionRef.current.state === "Connected") {
      try {
        await connectionRef.current.invoke("DissolveRoomByCreator");
        navigate("/rooms");
      } catch (error) {
        console.error("Error when dissolving room:", error);
        alert("Failed to dissolve room: " + (error instanceof Error ? error.message : "Unknown error"));
      }
    }
  };

  const handleStartRace = async () => {
    if (connectionRef.current && connectionRef.current.state === "Connected") {
      try {
        await connectionRef.current.invoke("StartRace");
      } catch (error) {
        console.error("Error when starting race:", error);
        alert("Failed to start race: " + (error instanceof Error ? error.message : "Unknown error"));
      }
    }
  };

  // Format participant names as a string
  const formatParticipantNames = () => {
    if (!room || !room.participants) return "";
    return room.participants.map((user) => user.username).join(", ");
  };

  // Check if competition can be started (need at least 2 participants)
  const canStartCompetition = room && room.participants && room.participants.length >= 2;

  // Function to render CEFR levels
  const renderCefrLevels = () => {
    if (!room?.cefrMin || !room?.cefrMax) return null;

    const isSameLevel = room.cefrMin === room.cefrMax;

    if (isSameLevel) {
      return <span className="bg-[#F3D86D] p-2 rounded-[8px]">{room.cefrMin}</span>;
    }

    return (
      <>
        <span className="bg-[#F3D86D] p-2 rounded-[8px]">{room.cefrMin}</span>
        <span className="bg-[#F3D86D] p-2 rounded-[8px]">{room.cefrMax}</span>
      </>
    );
  };

  return (
    <div className="flex justify-center items-center h-screen">
      <div className="fixed top-[20px] w-full text-center">
        <span className="text-2xl">
          {t("room.competition")}.{room?.name}
        </span>
        <span className="absolute right-[10px] cursor-pointer" onClick={() => navigate("/rooms")}>
          {t("room.close")}
        </span>
      </div>

      <div className="max-w-[900px] w-[900px]">
        <div className="flex gap-6 p-8 bg-[#B3BCFF] items-center h-[500px] rounded-[8px] flex-col">
          <div className="w-full flex justify-between items-center">
            <div className="flex flex-col gap-4">
              <div className="flex gap-3 items-center">
                <span className="text-[#4F4F4F]">
                  {room?.test?.questions?.length || 0} {t("room.questions")}
                </span>
                <span>
                  {room?.totalTimeInSeconds || 0} {t("room.sec")}
                </span>
              </div>

              <div className="flex gap-3 items-center text-[#4F4F4F]">
                <span className="text-sm">{room?.test?.fromLanguage || ""}</span>
                <span className="text-sm">{"->"}</span>
                <span className="text-sm">{room?.test?.toLanguage || ""}</span>
              </div>
            </div>

            <div className="flex gap-4">{renderCefrLevels()}</div>
          </div>

          <div className="flex justify-between w-full">
            <div className="flex gap-4">
              {room?.test?.dictionariesInvolved?.map((dictionary: any, idx: number) => (
                <div
                  key={idx}
                  className="border border-[grey] rounded-[8px] w-[200px] items-center text-sm flex justify-center"
                >
                  {dictionary.title}
                </div>
              ))}
            </div>

            <div className="flex gap-2 items-center">
              <div className="w-[40px] h-[40px] bg-[red] rounded-full" />
              <span className="text-[#4F4F4F] text-sm">{room?.creator?.username || ""}</span>
            </div>
          </div>

          <div className="flex flex-col justify-center w-[400px] gap-3 w-full mt-4">
            <span className="text-xl text-center">{t("room.joinStatus.joined")}</span>
            <span className="text-xl text-center">{t("room.joinStatus.waiting")}</span>
          </div>

          <CircularProgress color="inherit" />

          <span className="text-[#4F4F4F]">
            {t("room.participantsCount")} {room?.participants?.length || 0}
          </span>

          <div className="flex gap-2 items-center">
            <img src={avatarsIcon} alt="avatars" />
            <span>{formatParticipantNames()}</span>
          </div>
        </div>

        <div className="flex gap-2 mt-3 justify-end">
          {isCreator && !raceStarted && (
            <>
              <button
                className="!bg-white !text-black !border px-4 h-[50px] rounded-[8px] border-black"
                onClick={handleDissolveRoom}
              >
                {t("room.roomActions.deleteRoom")}
              </button>

              <button
                className="!bg-white !text-black !border px-4 h-[50px] rounded-[8px] border-black"
                onClick={handleStartRace}
                disabled={!canStartCompetition}
              >
                {t("room.roomActions.startCompetition")}
              </button>
            </>
          )}

          {!isCreator && !raceStarted && (
            <button
              className="!bg-white !text-black !border px-4 h-[50px] rounded-[8px] border-black"
              onClick={handleLeaveRoom}
            >
              {t("room.roomActions.leaveRoom")}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
