import { useLocation, useNavigate } from "react-router-dom";
import { PieChart } from "react-minimal-pie-chart";
import salute from "../../assets/result-salute.png";
import { useEffect } from "react";
import cardsIcon from "../../assets/cards.svg";
import clsx from "clsx";
import { useSelector } from "react-redux";
import { RootState, store } from "../../store/store";
import { CircularProgress } from "@mui/material";
import { refresh } from "../../store/race/raceSlice";
import { useTranslation } from "react-i18next";
import check from "../../assets/check.svg";
import close from "../../assets/close.svg";

// QDirection enum
const QDirection = {
  TermToTranslation: 1,
  TermToMeaning: 2,
  TranslationToTerm: 3,
  MeaningToTerm: 4,
};

export const TestResult = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const { resultData, isRace } = location.state;
  const raceResult = useSelector((state: RootState) => state.race.raceResult);
  console.log("raceResult", raceResult);

  const totalAnswers = resultData?.questions?.length || 0;
  const correctPercentage =
    totalAnswers > 0 ? Math.round((resultData.correctAnswers / totalAnswers) * 100) : 0;
  const incorrectPercentage =
    totalAnswers > 0 ? Math.round((resultData.incorrectAnswers / totalAnswers) * 100) : 0;

  const handleClose = () => {
    navigate("/", { replace: true, state: null });
  };

  const navigateToVocabulary = () => {
    navigate(`/vocabulary/${resultData.dictionaryId}`, { replace: true, state: null });
  };

  const formatTime = (time: string) => {
    const parts = time.split(":");
    const minutes = parts[1];
    const seconds = parts[2].slice(0, 5);
    return `${minutes}:${seconds}`;
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

  useEffect(() => {
    return () => {
      store.dispatch(refresh());

      if (window.history.state?.state) {
        window.history.replaceState(
          { ...window.history.state, state: null },
          document.title,
          window.location.pathname
        );
      }
    };
  }, []);

  if (!resultData) {
    navigate("/", { replace: true });
    return null;
  }

  return (
    <div className="flex flex-col gap-10 items-center">
      <div className="flex justify-center relative w-full">
        <span className="mt-4 text-3xl">{resultData.dictionaryName}</span>
        <span className="absolute text-3xl right-[10px] top-[10px] cursor-pointer" onClick={handleClose}>
          X
        </span>
      </div>

      <div className="w-[1100px]">
        <div className="flex items-center justify-between">
          <span className="text-3xl">
            {t("testResult.yourResult")} {`${resultData.correctAnswers}/${totalAnswers}`}
            {correctPercentage > 50 ? t("testResult.keepItUp") : ""}
          </span>
          <img className="w-[200px]" src={salute} alt="salute" />
        </div>

        <div className="w-full flex justify-between mb-8">
          <div className="flex items-center w-[48%] gap-3 justify-between">
            <PieChart
              data={[
                { value: resultData.correctAnswers, color: "#90FF88" },
                { value: resultData.incorrectAnswers, color: "#FF6E6E" },
              ]}
              className="w-[150px]"
            />

            <div className="flex flex-col gap-4 flex-1">
              <div>
                <span>{t("testResult.answerStats.correctAnswers")} </span>
                <span>
                  {resultData.correctAnswers} ({correctPercentage}%)
                </span>
              </div>
              <div>
                <span>{t("testResult.answerStats.incorrectAnswers")} </span>
                <span>
                  {resultData.incorrectAnswers} ({incorrectPercentage}%)
                </span>
              </div>
            </div>
          </div>

          <div
            className="w-[48%] flex justify-between gap-6 p-3 items-center border-2 rounded-[15px] cursor-pointer"
            onClick={navigateToVocabulary}
          >
            <img className="w-[70px] h-[70px]" src={cardsIcon} alt="cards" />

            <div className="flex flex-col gap-4">
              <span className="text-2xl">{t("testResult.dictionaryReturn.returnToDictionary")}</span>
              <span className="text-[#4F4F4F]">{t("testResult.dictionaryReturn.description")}</span>
            </div>
          </div>
        </div>

        {isRace && (
          <div className="bg-[#CBD1FF] rounded-[15px] p-5 flex justify-start min-h-[300px] mb-3 flex-col items-center">
            <span>{t("testResult.competition.results")}</span>

            {!raceResult || raceResult.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-40">
                <span>{t("testResult.competition.waitForOthers")}</span>
                <CircularProgress />
              </div>
            ) : (
              <div className="flex flex-col gap-3 w-full">
                <div className="flex justify-end w-full gap-4">
                  <span>{t("testResult.competition.time")}</span>
                  <span>{t("testResult.competition.points")}</span>
                </div>
                {[...raceResult]
                  .sort((a: any, b: any) => {
                    const pointsA = a.answers.filter((elem: any) => elem.isGivenCorrectAnswer).length;
                    const pointsB = b.answers.filter((elem: any) => elem.isGivenCorrectAnswer).length;

                    return pointsB - pointsA;
                  })
                  .map((participant: any, index: any) => (
                    <div key={index} className="flex w-full justify-between">
                      <div className="flex gap-2 items-center">
                        <div
                          className="w-[35px] h-[35px] rounded-full"
                          style={{ backgroundColor: participant.participant.image || "red" }}
                        />
                        <span>{participant.participant.email}</span>
                      </div>

                      <div className="flex w-[120px] justify-between">
                        <span>{formatTime(participant.time)}</span>
                        <span>
                          {participant.answers.filter((elem: any) => elem.isGivenCorrectAnswer).length}
                        </span>
                      </div>
                    </div>
                  ))}
              </div>
            )}
          </div>
        )}
        <div className="w-full">
          <h2 className="text-2xl mb-4">{t("testResult.answers.yourAnswers")}</h2>
          <div className="space-y-6 flex flex-col gap-4 mb-6">
            {resultData.questions.map((question: any, index: any) => {
              const directionalContent = getDirectionalContent(question);

              return question?.type === "answeredTrueFalse" ? (
                <div
                  key={index}
                  className="bg-white rounded-lg shadow-lg p-6 mb-8 gap-4 border min-h-[300px] relative"
                >
                  <div className="flex w-full justify-between">
                    <div className="flex flex-col gap-1 w-[50%]">
                      <span>{t("testResult.answers.term")}</span>
                      <span className="text-3xl">{directionalContent.promptContent}</span>
                    </div>
                    <div className="w-[2px] bg-[#D9D9D9]" />
                    <div className="flex flex-col gap-1 w-[50%] pl-[50px]">
                      <span>{t("testResult.answers.meaning")}</span>
                      <span className="text-3xl">{directionalContent.answerContent}</span>
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
                    {question.isGivenCorrectAnswer ? (
                      <div className="flex gap-2 items-center">
                        <img src={check} alt="" />
                        <span>{t("testResult.answers.correct")}</span>
                      </div>
                    ) : (
                      <div className="flex gap-2 items-center">
                        <img src={close} alt="" />
                        <span>{t("testResult.answers.incorrect")}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : question?.type === "answeredMultipleChoice" ? (
                <div
                  key={index}
                  className="bg-white rounded-lg shadow-lg p-6 mb-8 border relative min-h-[300px]"
                >
                  <div className="w-full flex items-center">
                    <span>{t("testResult.answers.term")}</span>
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
                    {question.isGivenCorrectAnswer ? (
                      <div className="flex gap-2 items-center">
                        <img src={check} alt="" />
                        <span>{t("testResult.answers.correct")}</span>
                      </div>
                    ) : (
                      <div className="flex gap-2 items-center">
                        <img src={close} alt="" />
                        <span>{t("testResult.answers.incorrect")}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : question?.type === "answeredAudio" ? (
                <div
                  key={index}
                  className="bg-white rounded-lg shadow-lg p-6 mb-8 border relative min-h-[300px] items-center flex flex-col gap-6"
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
                      <div className="flex gap-2 items-center">
                        <img src={check} alt="" />
                        <span>{t("testResult.answers.correct")}</span>
                      </div>
                    ) : (
                      <div className="flex flex-col gap-2 items-center">
                        <div className="flex gap-2 items-center">
                          <img src={close} alt="" />
                          <span> {t("testResult.answers.incorrect")}</span>
                        </div>
                        <span>Правильна відповідь: {question.answer.correctAnswer}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : question?.type === "answeredHandwritten" ? (
                <div
                  key={index}
                  className="bg-white rounded-lg shadow-lg p-6 mb-8 border relative min-h-[300px] items-center flex flex-col gap-6"
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
                      <div className="flex gap-2 items-center">
                        <img src={check} alt="" />
                        <span>{t("testResult.answers.correct")}</span>
                      </div>
                    ) : (
                      <div className="flex flex-col gap-2 items-center">
                        <div className="flex gap-2 items-center">
                          <img src={close} alt="" />
                          <span>{t("testResult.answers.incorrect")}</span>
                        </div>

                        <span>Правильна відповідь: {question.answer.correctInput}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div>test</div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};
