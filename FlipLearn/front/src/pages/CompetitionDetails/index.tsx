import { useEffect, useState } from "react";
import PageWrapper from "../../components/PageWrapper";
import axios from "axios";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import clsx from "clsx";
import audio from "../../assets/speaker-button.svg";
import { fetchUser } from "../../store/user/userSlice";
import { useDispatch } from "react-redux";
import { AppDispatch } from "../../store/store";
import langSwitcher from "../../assets/lang-switcher.svg";
import { Collapse, Fade } from "@mui/material";
import arrowTop from "../../assets/arrow-top.svg";
import arrowDown from "../../assets/arrow-down.svg";

export const CompetitionDetails = () => {
  const [room, setRoom] = useState<any>(null);
  const [user, setUser] = useState<any>(null);
  const [expandedUsers, setExpandedUsers] = useState<string[]>([]);
  const { id } = useParams();
  const token = localStorage.getItem("token");
  const { t } = useTranslation();
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    getRoomDetails();
    getUser();
  }, []);

  const getUser = () => {
    if (!token) return;

    dispatch(fetchUser(token))
      .unwrap()
      .then((res) => {
        setUser(res);
      })
      .catch((error) => {
        console.error("Ошибка при получении пользователя:", error);
      });
  };

  const getRoomDetails = () => {
    axios
      .get(`https://localhost:7288/races/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setRoom(response.data);
      })
      .catch((error) => {
        console.error(`Error loading race history: ${error.response?.data || error.message}`);
      });
  };

  const formatTime = (time: string) => {
    const parts = time.split(":");
    const minutes = parts[1];
    const seconds = parts[2].slice(0, 5);
    return `${minutes}:${seconds}`;
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return "";

    const date = new Date(dateString);

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    return `${day}.${month}.${year}`;
  };

  const toggleUserExpand = (userId: string) => {
    setExpandedUsers((prev) => {
      if (prev.includes(userId)) {
        return prev.filter((id) => id !== userId);
      } else {
        return [...prev, userId];
      }
    });
  };

  const calculateUserScore = (answers: any[]) => {
    return answers.filter((answer: any) => answer.isGivenCorrectAnswer === true).length;
  };

  const isRoomCreator = (participantEmail: string) => {
    return room?.creatorEmail === participantEmail;
  };

  const getSortedResults = () => {
    if (!room?.results) return [];

    return [...room.results].sort((a, b) => {
      const scoreA = calculateUserScore(a.answers);
      const scoreB = calculateUserScore(b.answers);
      return scoreB - scoreA;
    });
  };

  const renderQuestionAnswers = (question: any, isMyAnswers = false, index: any) => {
    // QDirection enum
    const QDirection = {
      TermToTranslation: 1,
      TermToMeaning: 2,
      TranslationToTerm: 3,
      MeaningToTerm: 4,
    };

    const getDirectionalContent = (question: any) => {
      if (!question) return { promptContent: "", answerContent: "" };

      const direction = question.direction;

      switch (direction) {
        case QDirection.TermToTranslation: // 1
          return {
            promptContent: question.term,
            answerContent: question.translation,
          };
        case QDirection.TermToMeaning: // 2
          return {
            promptContent: question.term,
            answerContent: question.meaning,
          };
        case QDirection.TranslationToTerm: // 3
          return {
            promptContent: question.translation,
            answerContent: question.term,
          };
        case QDirection.MeaningToTerm: // 4
          return {
            promptContent: question.meaning,
            answerContent: question.term,
          };
        default:
          return {
            promptContent: question.term || question.translation,
            answerContent: question.translation || question.meaning,
          };
      }
    };

    if (question?.type === "answeredTrueFalse") {
      const directionalContent = getDirectionalContent(question);

      return (
        <Fade in={true} timeout={500} key={index}>
          <div
            className={clsx(
              isMyAnswers ? "bg-white" : "bg-[#ffffff33]",
              "rounded-lg shadow-lg p-6 mb-8 gap-4 border min-h-[300px] relative"
            )}
          >
            <div className="flex w-full justify-between">
              <div className="flex flex-col gap-1 w-[50%]">
                <span>{t("testResult.answers.term")}</span>
                <span className="text-3xl">{directionalContent.promptContent}</span>
              </div>
              <div className="w-[2px] bg-[#D9D9D9]" />
              <div className="flex flex-col gap-1 w-[50%] pl-[50px]">
                <span>{t("testResult.answers.meaning")}</span>
                <span className="text-3xl break-all">{directionalContent.answerContent}</span>
              </div>
            </div>
            <div className="flex w-full flex-wrap justify-between gap-3 mt-3">
              {question?.answers.map((answer: any, answerIndex: any) => {
                const isSelected = question.givenAnswer === answer.choice.toString();
                const isCorrectAnswer = answer.isCorrect;
                let borderColor = "border-gray-500";
                if (isSelected) {
                  borderColor = isCorrectAnswer ? "border-green-500" : "border-red-500";
                } else if (isCorrectAnswer && !question.isGivenCorrectAnswer) {
                  borderColor = "border-green-500";
                }

                return (
                  <div
                    key={answerIndex}
                    className={`w-[45%] rounded-[8px] p-3 border-2 ${borderColor} flex gap-3`}
                  >
                    <span className="rounded-full border text-[#8B8B8B] w-[25px] h-[25px] flex justify-center bg-[#EEEEEE]">
                      {answerIndex + 1}
                    </span>
                    <span>{answer.choice.toString()}</span>
                  </div>
                );
              })}
            </div>

            <div
              className={clsx(
                "rounded-[8px] w-full absolute left-0 bottom-0 p-4 flex justify-center",
                question.isGivenCorrectAnswer ? "bg-[#BDFFC5]" : "bg-[#FFACAB]"
              )}
            >
              {question.isGivenCorrectAnswer
                ? t("testResult.answers.correct")
                : t("testResult.answers.incorrect")}
            </div>
          </div>
        </Fade>
      );
    } else if (question?.type === "answeredMultipleChoice") {
      const directionalContent = getDirectionalContent(question);

      return (
        <Fade in={true} timeout={500} key={index}>
          <div
            className={clsx(
              isMyAnswers ? "bg-white" : "bg-[#ffffff33]",
              "rounded-lg shadow-lg p-6 mb-8 gap-4 border min-h-[300px] relative"
            )}
          >
            <div className="w-full flex justify-between items-center">
              <span>{t("testResult.answers.term")}</span>
              <img src={audio} alt="audio" />
            </div>
            <span className="text-3xl">{directionalContent.promptContent}</span>
            <div className="mt-6 mb-[20px]">
              <div className="flex w-full flex-wrap justify-between gap-3 mt-3">
                {question?.answers.map((answer: any, answerIndex: any) => {
                  const isSelected = question.givenAnswer === answer.text;
                  const isCorrectAnswer = answer.isCorrect;
                  let borderColor = "border-gray-500";
                  if (isSelected) {
                    borderColor = isCorrectAnswer ? "border-green-500" : "border-red-500";
                  } else if (isCorrectAnswer && !question.isGivenCorrectAnswer) {
                    borderColor = "border-green-500";
                  }

                  return (
                    <div
                      key={answerIndex}
                      className={`w-[45%] rounded-[8px] p-3 border-2 ${borderColor} flex gap-3`}
                    >
                      <span className="rounded-full border text-[#8B8B8B] w-[25px] h-[25px] flex justify-center bg-[#EEEEEE]">
                        {answerIndex + 1}
                      </span>
                      <span>{answer.text}</span>
                    </div>
                  );
                })}
              </div>
            </div>

            <div
              className={clsx(
                "rounded-[8px] w-full absolute left-0 bottom-0 p-4 flex justify-center",
                question.isGivenCorrectAnswer ? "bg-[#BDFFC5]" : "bg-[#FFACAB]"
              )}
            >
              {question.isGivenCorrectAnswer
                ? t("testResult.answers.correct")
                : t("testResult.answers.incorrect")}
            </div>
          </div>
        </Fade>
      );
    } else if (question?.type === "answeredAudio") {
      const directionalContent = getDirectionalContent(question);

      return (
        <Fade in={true} timeout={500} key={index}>
          <div
            className={clsx(
              isMyAnswers ? "bg-white" : "bg-[#ffffff33]",
              "rounded-lg shadow-lg p-6 mb-8 border relative min-h-[300px] items-center flex flex-col gap-6"
            )}
          >
            <span className="text-5xl mb-5">{directionalContent.promptContent}</span>

            <div className="flex flex-col items-center gap-4 w-full">
              <div className="flex flex-col w-3/4 gap-4">
                <span className="text-lg text-[#4F4F4F]">Ваша відповідь</span>
                <span className="w-full p-2 border-b border-gray-300 outline-none">
                  {question.givenAnswer}
                </span>
              </div>
            </div>

            <div
              className={clsx(
                "rounded-[8px] w-full absolute left-0 bottom-0 p-4 flex justify-center",
                question.isGivenCorrectAnswer ? "bg-[#BDFFC5]" : "bg-[#FFACAB]"
              )}
            >
              {question.isGivenCorrectAnswer ? (
                t("testResult.answers.correct")
              ) : (
                <div className="flex flex-col gap-2 items-center">
                  <span>{t("testResult.answers.incorrect")}</span>
                  <span>Правильна відповідь: {question.answer.correctAnswer}</span>
                </div>
              )}
            </div>
          </div>
        </Fade>
      );
    } else if (question?.type === "answeredHandwritten") {
      const directionalContent = getDirectionalContent(question);

      return (
        <Fade in={true} timeout={500} key={index}>
          <div
            className={clsx(
              isMyAnswers ? "bg-white" : "bg-[#ffffff33]",
              "rounded-lg shadow-lg p-6 mb-8 border relative min-h-[300px] items-center flex flex-col gap-6"
            )}
          >
            <span className="text-5xl mb-5">{directionalContent.promptContent}</span>

            <div className="flex flex-col items-center gap-4 w-full">
              <div className="flex flex-col w-3/4 gap-4">
                <span className="text-lg text-[#4F4F4F]">Ваша відповідь</span>
                <span className="w-full p-2 border-b border-gray-300 outline-none">
                  {question.givenAnswer}
                </span>
              </div>
            </div>

            <div
              className={clsx(
                "rounded-[8px] w-full absolute left-0 bottom-0 p-4 flex justify-center",
                question.isGivenCorrectAnswer ? "bg-[#BDFFC5]" : "bg-[#FFACAB]"
              )}
            >
              {question.isGivenCorrectAnswer ? (
                t("testResult.answers.correct")
              ) : (
                <div className="flex flex-col gap-2 items-center">
                  <span>{t("testResult.answers.incorrect")}</span>
                  <span>Правильна відповідь: {question.answer.correctInput}</span>
                </div>
              )}
            </div>
          </div>
        </Fade>
      );
    } else {
      return (
        <Fade in={true} timeout={500} key={index}>
          <div className="bg-gray-100 p-4 rounded-lg">
            <span>Невідомий тип питання: {question?.type}</span>
          </div>
        </Fade>
      );
    }
  };

  const currentUserResult =
    user && room?.results?.find((result: any) => result.participant.email === user.email);

  const currentUserScore = currentUserResult ? calculateUserScore(currentUserResult.answers) : 0;

  const sortedResults = getSortedResults();

  return (
    <PageWrapper>
      <div className="w-full px-8 py-3 flex flex-col gap-3 items-center">
        <div className="flex justify-between w-full">
          <div className="flex gap-3 items-center">
            <span className="text-2xl">{room?.name}</span>
            <span className="py-1 px-4 rounded-[6px] bg-[#F3D86D] text-[#4F4F4F]">
              {room?.test?.dictionariesInvolved[0].cefr}
            </span>
          </div>
          <div className="flex gap-3 items-center">
            <span>{room?.test.questions.length} questions</span>
          </div>
        </div>

        <div className="flex justify-between w-full">
          <div className="flex gap-2 items-center">
            <span>{room?.test?.fromLanguage}</span>
            <img src={langSwitcher} alt="langSwitcher" />
            <span>{room?.test?.toLanguage}</span>
          </div>
          <span>Кількість участників : {room?.participants.length}</span>
        </div>

        <div className="flex justify-between w-full">
          <div>
            {room?.test?.dictionariesInvolved.map((dictionary: any) => (
              <span key={dictionary.id}>{dictionary.title}</span>
            ))}
          </div>
          <span>{formatDate(room?.startTime)}</span>
        </div>

        <div className="min-w-[1000px] mt-5">
          <div className="bg-[#CBD1FF] rounded-[15px] p-5 flex justify-start min-h-[300px] mb-3 flex-col items-center">
            <span className="text-xl font-bold mb-6">Результати змагання</span>

            <div className="flex justify-end w-full gap-4">
              <div className="flex w-[160px] gap-4 justify-between items-center">
                <span className="text-[#8B8B8B] text-sm">Час</span>
                <span className="text-[#8B8B8B] text-sm">Бали</span>
                <span />
              </div>
            </div>
            {sortedResults.map((result: any) => {
              const isExpanded = expandedUsers.includes(result.participant.id);
              const isCreator = isRoomCreator(result.participant.email);
              const isCurrentUser = user && result.participant.email === user.email;
              const canExpand = !isCreator && !isCurrentUser; // Создатель и текущий пользователь не могут быть развернуты

              return (
                <div className="w-full mb-4" key={result.participant.id}>
                  <div
                    className={clsx(
                      "flex w-full justify-between items-center py-2 rounded-lg px-3 transition-colors",
                      canExpand ? "hover:bg-[#D8DDFF] cursor-pointer" : "cursor-default",
                      isCreator && "bg-[#E8EDFF] border-2 border-[#9BB0FF]",
                      isCurrentUser && "bg-[#FFF4E6] border-2 border-[#FFD700]"
                    )}
                    onClick={() => canExpand && toggleUserExpand(result.participant.id)}
                  >
                    <div className="flex gap-2 items-center">
                      <div
                        className="w-[35px] h-[35px] rounded-full"
                        style={{ backgroundColor: result.participant.image || "#d9d9d9" }}
                      />
                      <div className="flex gap-2 items-center">
                        <span>{result.participant.email}</span>
                        {isCreator && (
                          <span className="px-2 py-1 bg-[#9BB0FF] text-white text-xs rounded-full">
                            Створювач
                          </span>
                        )}
                        {isCurrentUser && (
                          <span className="px-2 py-1 bg-[#FFD700] text-white text-xs rounded-full">Ви</span>
                        )}
                      </div>
                    </div>

                    <div className="flex w-[160px] gap-4 justify-between items-center">
                      <span>{formatTime(result.time)}</span>
                      <span>{calculateUserScore(result.answers)}</span>
                      {canExpand ? (
                        <img src={isExpanded ? arrowTop : arrowDown} alt="arrow" />
                      ) : (
                        <div className="w-[16px]" /> // Пустое место для выравнивания
                      )}
                    </div>
                  </div>

                  {canExpand && (
                    <Collapse in={isExpanded} timeout={500}>
                      <div className="w-full mt-2 pl-10 pr-2">
                        {result.answers.map((question: any, index: any) =>
                          renderQuestionAnswers(question, false, index)
                        )}
                      </div>
                    </Collapse>
                  )}
                </div>
              );
            })}
          </div>

          <div className="mt-5 flex flex-col gap-5">
            <span className="text-xl font-bold mb-4">Ваші відповіді</span>

            {currentUserResult && (
              <>
                <div className="flex w-full justify-between mb-4 py-2 px-3 bg-[#F5F5F5] rounded-lg">
                  <div className="flex gap-2 items-center">
                    <div
                      className="w-[35px] h-[35px] rounded-full"
                      style={{ backgroundColor: currentUserResult.participant.image || "#d9d9d9" }}
                    />
                    <span>{currentUserResult.participant.email}</span>
                  </div>

                  <div className="flex w-[160px] gap-4 justify-between pr-3">
                    <span>{formatTime(currentUserResult.time)}</span>
                    <span>{currentUserScore}</span>
                  </div>
                </div>

                <div className="w-full">
                  {currentUserResult.answers.map((question: any, index: any) =>
                    renderQuestionAnswers(question, true, index)
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};
