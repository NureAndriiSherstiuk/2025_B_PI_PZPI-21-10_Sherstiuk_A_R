import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import audio from "../../assets/speaker-button.svg";
import microphone from "../../assets/microfone.svg";
import { HubConnectionBuilder, HttpTransportType } from "@microsoft/signalr";
import { useTranslation } from "react-i18next";

const QDirection = {
  TermToTranslation: 1,
  TermToMeaning: 2,
  TranslationToTerm: 3,
  MeaningToTerm: 4,
};

export const RaceTest = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const testData = location.state?.testData;
  const { id: roomId } = useParams();

  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [progress, setProgress] = useState(0);
  const [givenAnswer, setGivenAnswer] = useState(null);
  const [correctAnswers, setCorrectAnswers] = useState(0);
  const [incorrectAnswers, setIncorrectAnswers] = useState(0);
  const [answersLog, setAnswersLog] = useState([]);
  const [timerId, setTimerId] = useState(null);
  const connectionRef = useRef<any>(null);

  const [globalTimeRemaining, setGlobalTimeRemaining] = useState(testData?.totalTimeInSeconds || null);
  const [globalTimerDisplay, setGlobalTimerDisplay] = useState<any>(null);

  const [isRecording, setIsRecording] = useState(false);
  const [spokenText, setSpokenText] = useState("");
  const [isAnswerSubmitted, setIsAnswerSubmitted] = useState(false);
  const [isCorrectSpokenAnswer, setIsCorrectSpokenAnswer] = useState(false);

  const recognitionRef = useRef<any>(null);

  const currentQuestion = testData?.questions[currentQuestionIndex];
  const totalQuestions = testData?.questions.length;

  const getAnsweredType = (questionType: string) => {
    switch (questionType) {
      case "trueFalse":
        return "answeredTrueFalse";
      case "multipleChoice":
        return "answeredMultipleChoice";
      case "handwritten":
        return "answeredHandwritten";
      case "audio":
        return "answeredAudio";
      default:
        return questionType;
    }
  };

  const checkMultipleAnswers = (userAnswer: string, correctAnswers: string) => {
    const normalizedUserAnswer = userAnswer.trim().toLowerCase();
    const possibleAnswers = correctAnswers.split(",").map((answer) => answer.trim().toLowerCase());
    return possibleAnswers.includes(normalizedUserAnswer);
  };

  useEffect(() => {
    const windowWithSpeech = window as any;

    if (windowWithSpeech.SpeechRecognition || windowWithSpeech.webkitSpeechRecognition) {
      const SpeechRecognition =
        windowWithSpeech.SpeechRecognition || windowWithSpeech.webkitSpeechRecognition;
      recognitionRef.current = new SpeechRecognition();

      recognitionRef.current.continuous = false;
      recognitionRef.current.interimResults = false;

      if (currentQuestion?.type === "handwritten") {
        if (currentQuestion.direction === QDirection.TermToTranslation) {
          recognitionRef.current.lang = testData.toLanguage || "uk-UA";
        } else if (currentQuestion.direction === QDirection.TranslationToTerm) {
          recognitionRef.current.lang = testData.fromLanguage || "en-US";
        }
      }

      recognitionRef.current.onresult = (event: any) => {
        const transcript = event.results[0][0].transcript;
        setSpokenText(transcript);
        setIsRecording(false);
      };

      recognitionRef.current.onerror = (event: any) => {
        console.error("Speech recognition error", event.error);
        setIsRecording(false);
      };

      recognitionRef.current.onend = () => {
        setIsRecording(false);
      };
    }
  }, [currentQuestion, testData]);

  useEffect(() => {
    if (!roomId) return;

    const token = localStorage.getItem("token");

    const connection = new HubConnectionBuilder()
      .withUrl(`https://localhost:7288/room?access_token=${token}`, {
        transport: HttpTransportType.WebSockets,
      })
      .withAutomaticReconnect()
      .build();

    const setupConnection = async () => {
      try {
        connection.on("UpdateRemainingTime", (remainingSeconds) => {
          setGlobalTimeRemaining(remainingSeconds);
          const minutes = Math.floor(remainingSeconds / 60);
          const seconds = remainingSeconds % 60;
          setGlobalTimerDisplay(`${minutes}:${seconds < 10 ? "0" : ""}${seconds}`);
        });

        connection.on("CollectAnswersAndFinishRace", () => {
          console.log("Time's up! Race is finished.");

          const finalLog: any = [...answersLog];

          if (currentQuestionIndex < totalQuestions) {
            if (givenAnswer === null && !isAnswerSubmitted) {
              finalLog.push({
                ...currentQuestion,
                questionIndex: currentQuestionIndex,
                question: currentQuestion.term,
                translation: currentQuestion.translation,
                type: getAnsweredType(currentQuestion.type),
                givenAnswer: "Time expired",
                isGivenCorrectAnswer: false,
              });
            }

            for (let i = currentQuestionIndex + 1; i < totalQuestions; i++) {
              const skippedQuestion = testData.questions[i];
              finalLog.push({
                ...skippedQuestion,
                questionIndex: i,
                question: skippedQuestion.term,
                translation: skippedQuestion.translation,
                type: getAnsweredType(skippedQuestion.type),
                givenAnswer: "Time expired",
                isGivenCorrectAnswer: false,
              });
            }
          }

          console.log("finalLog", finalLog);

          finishTest(finalLog, false);
        });

        await connection.start();
        connectionRef.current = connection;

        await connection.invoke("JoinRoom", roomId);
      } catch (error) {}
    };

    setupConnection();

    return () => {
      if (connectionRef.current) {
        connectionRef.current.stop();
        console.log("Disconnected from RoomHub");
      }
    };
  }, [roomId]);

  useEffect(() => {
    if (testData?.totalTimeInSeconds) {
      setGlobalTimeRemaining(testData.totalTimeInSeconds);

      const minutes = Math.floor(testData.totalTimeInSeconds / 60);
      const seconds = testData.totalTimeInSeconds % 60;
      setGlobalTimerDisplay(`${minutes}:${seconds < 10 ? "0" : ""}${seconds}`);
    }
  }, [testData]);

  useEffect(() => {
    if (!testData) {
      console.error("Test data is missing");
      navigate("/rooms");
      return;
    }

    setProgress(((currentQuestionIndex + 1) / totalQuestions) * 100);

    setSpokenText("");
    setIsAnswerSubmitted(false);
    setIsRecording(false);
  }, [currentQuestionIndex, totalQuestions, testData, navigate]);

  useEffect(() => {
    return () => {
      if (timerId) {
        clearInterval(timerId);
      }
      if (recognitionRef.current) {
        recognitionRef.current.stop();
      }
    };
  }, []);

  const finishTest = async (finalLog: any, isLastAnswerCorrect: any) => {
    console.log("Race completed!");

    const totalIncorrect = !isLastAnswerCorrect ? incorrectAnswers + 1 : incorrectAnswers;

    const resultData = {
      dictionaryId: testData.dictionariesInvolved[0].id,
      dictionaryName: testData.dictionariesInvolved[0].title,
      correctAnswers: isLastAnswerCorrect ? correctAnswers + 1 : correctAnswers,
      incorrectAnswers: totalIncorrect,
      questions: finalLog,
      testData: testData,
      fromLanguage: testData.fromLanguage,
      toLanguage: testData.toLanguage,
      roomId: roomId,
    };

    try {
      await connectionRef.current.invoke(
        "FinishRace",
        roomId,
        finalLog,
        isLastAnswerCorrect ? correctAnswers + 1 : correctAnswers,
        !isLastAnswerCorrect ? incorrectAnswers + 1 : incorrectAnswers
      );

      console.log("Race results submitted");
    } catch (err) {
      console.error("Failed to submit race results:", err);
    }

    if (timerId) {
      clearInterval(timerId);
    }

    navigate("/test-result", { state: { resultData, isRace: true } });
  };

  const handleAnswerClick = (answer: any, index: any) => {
    if (givenAnswer !== null) return;
    setGivenAnswer(index);

    if (timerId) {
      clearInterval(timerId);
    }

    const isGivenCorrectAnswer = answer.isCorrect;

    const currentAnswer = {
      ...currentQuestion,
      isGivenCorrectAnswer,
      givenAnswer: answer.text || answer.choice.toString(),
      type: getAnsweredType(currentQuestion.type),
    };

    const updatedLog: any = [...answersLog, currentAnswer];
    setAnswersLog(updatedLog);

    if (isGivenCorrectAnswer) {
      setCorrectAnswers(correctAnswers + 1);
    } else {
      setIncorrectAnswers(incorrectAnswers + 1);
    }

    setTimeout(() => {
      if (currentQuestionIndex < totalQuestions - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setGivenAnswer(null);
      } else {
        finishTest(updatedLog, isGivenCorrectAnswer);
      }
    }, 2000);
  };

  const startVoiceRecording = () => {
    if (recognitionRef.current) {
      setIsRecording(true);
      setSpokenText("");
      recognitionRef.current.start();
    } else {
      alert(t("test.speechRecognitionNotSupported"));
    }
  };

  const handleVoiceAnswerSubmit = () => {
    if (!spokenText) return;

    setIsAnswerSubmitted(true);

    let isCorrect = false;

    const correctAnswer = currentQuestion.answer?.correctAnswer || "";

    isCorrect = checkMultipleAnswers(spokenText, correctAnswer);
    setIsCorrectSpokenAnswer(isCorrect);

    const currentAnswer = {
      ...currentQuestion,
      isGivenCorrectAnswer: isCorrect,
      givenAnswer: spokenText,
      type: getAnsweredType(currentQuestion.type),
    };

    const updatedLog: any = [...answersLog, currentAnswer];
    setAnswersLog(updatedLog);

    if (isCorrect) {
      setCorrectAnswers(correctAnswers + 1);
    } else {
      setIncorrectAnswers(incorrectAnswers + 1);
    }

    setTimeout(() => {
      if (currentQuestionIndex < totalQuestions - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setIsAnswerSubmitted(false);
        setSpokenText("");
        setGivenAnswer(null);
      } else {
        finishTest(updatedLog, isCorrect);
      }
    }, 2000);
  };

  const handleHandwrittenAnswerSubmit = () => {
    if (!spokenText.trim()) return;

    setIsAnswerSubmitted(true);

    const correctAnswer = currentQuestion.answer?.correctInput || "";

    const isCorrect = checkMultipleAnswers(spokenText, correctAnswer);
    setIsCorrectSpokenAnswer(isCorrect);

    const currentAnswer = {
      ...currentQuestion,
      isGivenCorrectAnswer: isCorrect,
      givenAnswer: spokenText,
      type: getAnsweredType(currentQuestion.type),
    };

    const updatedLog: any = [...answersLog, currentAnswer];
    setAnswersLog(updatedLog);

    if (isCorrect) {
      setCorrectAnswers(correctAnswers + 1);
    } else {
      setIncorrectAnswers(incorrectAnswers + 1);
    }

    setTimeout(() => {
      if (currentQuestionIndex < totalQuestions - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setIsAnswerSubmitted(false);
        setSpokenText("");
        setGivenAnswer(null);
      } else {
        finishTest(updatedLog, isCorrect);
      }
    }, 2000);
  };

  const getDirectionalContent = () => {
    if (!currentQuestion) return { prompt: "", answer: "" };

    const direction = currentQuestion.direction;

    switch (direction) {
      case QDirection.TermToTranslation:
        return {
          promptContent: currentQuestion.term,
          questionType: "chooseCorrectTranslation",
        };
      case QDirection.TermToMeaning:
        return {
          promptContent: currentQuestion.term,
          questionType: "chooseCorrectMeaning",
        };
      case QDirection.TranslationToTerm:
        return {
          promptContent: currentQuestion.translation,
          questionType: "chooseCorrectTerm",
        };
      case QDirection.MeaningToTerm:
        return {
          promptContent: currentQuestion.meaning,
          questionType: "chooseCorrectTerm",
        };
      default:
        return {
          promptContent: currentQuestion.term || currentQuestion.translation,
          questionType: "chooseCorrectDefinition",
        };
    }
  };

  if (!testData) {
    return <div className="text-center p-10">{t("test.loadingTestData")}</div>;
  }

  const directionalContent = getDirectionalContent();

  return (
    <div className="flex flex-col gap-10 items-center">
      <div className="flex justify-center relative w-full">
        <span>{testData?.dictionariesInvolved[0]?.title || t("test.raceTestTitle")}</span>
        <span className="absolute right-[20px] top-[20px] cursor-pointer " onClick={() => navigate("/rooms")}>
          X
        </span>
      </div>

      <div className="w-[1000px] flex flex-col gap-10">
        <div className="flex flex-col items-end gap-4">
          <div className="w-full flex gap-3">
            <span>{currentQuestionIndex + 1}</span>
            <div className="flex-1 bg-[#EEEEEE] rounded-[10px] overflow-hidden">
              <div
                className="bg-[#B3BCFF] rounded-[10px] h-full transition-all duration-500"
                style={{ width: `${progress}%` }}
              />
            </div>
            <span>{totalQuestions}</span>
          </div>
        </div>

        <div className="p-6 bg-white rounded-lg shadow-lg w-full gap-8 flex flex-col h-[400px] min-h-[400px]">
          {currentQuestion?.type === "trueFalse" ? (
            <>
              <div className="flex w-full justify-between">
                <div className="flex flex-col gap-1 w-[50%]">
                  <span>{t("test.term")}</span>
                  <span className="text-3xl">{directionalContent.promptContent}</span>
                </div>
                <div className="w-[2px] bg-[#D9D9D9]" />
                <div className="flex flex-col gap-1 w-[50%] pl-[50px]">
                  <span>{t("test.meaning")}</span>
                  <span className="text-3xl">
                    {currentQuestion.direction === QDirection.TermToTranslation
                      ? currentQuestion.translation
                      : currentQuestion.direction === QDirection.TranslationToTerm
                      ? currentQuestion.term
                      : currentQuestion.meaning || ""}
                  </span>
                </div>
              </div>
              <span>{t("test.selectAnswer")}</span>
              <div className="flex w-full flex-wrap justify-between gap-3 mt-3">
                {currentQuestion?.answers.map((answer: any, index: any) => (
                  <div
                    key={index}
                    onClick={() => handleAnswerClick(answer, index)}
                    className={`w-[45%] rounded-[8px] p-3 border-2 border-gray-500 flex gap-3 cursor-pointer hover:bg-gray-200 transition-all duration-300 
                    ${givenAnswer === index ? (answer.isCorrect ? "bg-green-300" : "bg-red-300") : ""}`}
                  >
                    <span className="rounded-full border text-[#8B8B8B] w-[25px] h-[25px] flex justify-center bg-[#EEEEEE]">
                      {index + 1}
                    </span>
                    <span>{answer.choice !== undefined ? answer.choice.toString() : answer.text}</span>
                  </div>
                ))}
              </div>
            </>
          ) : currentQuestion?.type === "multipleChoice" ? (
            <>
              <div className="w-full flex justify-between items-center">
                <span>{t("test.term")}</span>
                <img src={audio} alt="" className="cursor-pointer" />
              </div>
              <span className="text-3xl">{directionalContent.promptContent}</span>
              <div className="mt-6">
                <span>{t("test.chooseCorrectDefinition")}</span>
                <div className="flex w-full flex-wrap justify-between gap-3 mt-3">
                  {currentQuestion?.answers.map((answer: any, index: any) => (
                    <div
                      key={index}
                      onClick={() => handleAnswerClick(answer, index)}
                      className={`w-[45%] rounded-[8px] p-3 border-2 border-gray-500 flex gap-3 cursor-pointer hover:bg-gray-200 transition-all duration-300 
                      ${givenAnswer === index ? (answer.isCorrect ? "bg-green-300" : "bg-red-300") : ""}`}
                    >
                      <span className="rounded-full border text-[#8B8B8B] w-[25px] h-[25px] flex justify-center bg-[#EEEEEE]">
                        {index + 1}
                      </span>
                      <span>{answer.text}</span>
                    </div>
                  ))}
                </div>
              </div>
            </>
          ) : currentQuestion?.type === "audio" ? (
            <div className="w-full h-full flex flex-col py-10 justify-between items-center">
              <span className="text-5xl">{directionalContent.promptContent}</span>

              {!isAnswerSubmitted ? (
                <div className="flex flex-col items-center gap-8 w-full">
                  {spokenText && <span>Відповідь прийнято.</span>}

                  {spokenText ? (
                    <div className="flex gap-8">
                      <button
                        onClick={startVoiceRecording}
                        className={`bg-gray-100 flex p-4 gap-3 items-center rounded-[8px] ${
                          isRecording ? "animate-pulse" : ""
                        }`}
                        disabled={isRecording}
                      >
                        <img src={microphone} alt="microphone" />
                        <span>{isRecording ? "Йде запис" : "Змінити відповідь"}</span>
                      </button>

                      <button
                        onClick={handleVoiceAnswerSubmit}
                        className="bg-[#F3D86D] flex p-4 gap-3 items-center rounded-[8px]"
                      >
                        <span>Підтвердити відповідь</span>
                      </button>
                    </div>
                  ) : (
                    <button
                      onClick={startVoiceRecording}
                      className={`bg-[#F3D86D] flex p-4 gap-3 items-center rounded-[8px] ${
                        isRecording ? "animate-pulse" : ""
                      }`}
                      disabled={isRecording}
                    >
                      <img src={microphone} alt="microphone" />
                      <span>{isRecording ? "Йде запис" : "Натисни для відповіді"}</span>
                    </button>
                  )}
                </div>
              ) : null}
            </div>
          ) : (
            <div className="w-full h-full flex flex-col py-14 justify-between items-center">
              <span className="text-5xl mb-5">{directionalContent.promptContent}</span>

              {!isAnswerSubmitted ? (
                <div className="flex flex-col items-center gap-4 w-full">
                  <div className="flex flex-col w-3/4 gap-4">
                    <span className="text-lg">Напишіть переклад слова</span>
                    <input
                      type="text"
                      value={spokenText}
                      onChange={(e) => setSpokenText(e.target.value)}
                      className="w-full p-2 border-b border-gray-300 outline-none"
                      autoFocus
                    />
                  </div>

                  <div className="flex gap-4 mt-4">
                    <button
                      onClick={handleHandwrittenAnswerSubmit}
                      className="bg-[#F3D86D] flex p-4 gap-3 items-center rounded-[8px]"
                      disabled={!spokenText.trim()}
                    >
                      <span>Наступне питання</span>
                    </button>
                  </div>
                </div>
              ) : null}
            </div>
          )}
        </div>

        {isAnswerSubmitted &&
          (currentQuestion?.type === "handwritten" || currentQuestion?.type === "audio") && (
            <div
              className={`p-4 rounded-lg text-center ${
                isCorrectSpokenAnswer ? "bg-green-100" : "bg-red-100"
              }`}
            >
              <p className="font-bold text-xl mb-2">
                {isCorrectSpokenAnswer
                  ? "Правильно!"
                  : currentQuestion?.type === "audio"
                  ? "Неправильно"
                  : "Неправильна відповідь."}
              </p>
              <span className="text-[#4F4F4F]">
                {currentQuestion?.type === "audio"
                  ? currentQuestion.answer.correctAnswer
                  : currentQuestion.answer.correctInput}
              </span>
            </div>
          )}

        <p className="text-2xl w-full text-center text-[#4F4F4F]">
          {globalTimerDisplay || `${globalTimeRemaining} ${t("test.seconds")}`}
        </p>
      </div>
    </div>
  );
};
