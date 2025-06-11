import axios from "axios";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { HubConnectionBuilder, HttpTransportType, HubConnection } from "@microsoft/signalr";
import { useTranslation } from "react-i18next";

import "./index.scss";

interface FormData {
  roomName: string;
  questionsNumber: string;
  questionTypes: {
    definition: boolean;
    translation: boolean;
    grammar: boolean;
    audioRecord: boolean;
    audioListen: boolean;
  };
}

export const CreateTest = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const termsCount = location.state.termsCount;
  const vocName = location.state?.vocName || t("createTest.defaultTitle");
  const isRoomTest = location.state?.isRoomTest || false;
  const [ownDictionaries, setOwnDictionaries] = useState<any[]>([]);
  const [filteredDictionaries, setFilteredDictionaries] = useState<any[]>([]);
  const [selectedDictionaries, setSelectedDictionaries] = useState<string[]>([]);
  const [isCreatingRoom, setIsCreatingRoom] = useState(false);

  const { register, handleSubmit, watch, setValue } = useForm<FormData>({
    defaultValues: {
      roomName: "",
      questionsNumber: "",
      questionTypes: {
        definition: false,
        translation: false,
        grammar: false,
        audioRecord: false,
        audioListen: false,
      },
    },
  });

  const navigate = useNavigate();
  const { id } = useParams();
  const connectionRef = useRef<HubConnection | null>(null);

  const questionsNumber = watch("questionsNumber");
  const questionTypes = watch("questionTypes");

  useEffect(() => {
    if (!isRoomTest) return;

    const fetchDictionaries = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("https://localhost:7288/Dictionary/own", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
        });
        setOwnDictionaries(response.data);
        setFilteredDictionaries(response.data);
      } catch (error) {
        console.error(t("createTest.errors.loadingDictionaries"), error);
      }
    };

    fetchDictionaries();

    return () => {
      if (connectionRef.current) {
        connectionRef.current.stop();
      }
    };
  }, [isRoomTest, t]);

  const questionTypeMapping: any = {
    // definition: 4,
    translation: 2,
    grammar: 1,
    audioRecord: 4,
    audioListen: 3,
  };

  const toggleDictionarySelection = (dictionary: any) => {
    const dictionaryId = dictionary.id;

    if (selectedDictionaries.includes(dictionaryId)) {
      setSelectedDictionaries((prev) => prev.filter((id) => id !== dictionaryId));
      setFilteredDictionaries(ownDictionaries);
    } else {
      setSelectedDictionaries((prev) => [...prev, dictionaryId]);

      if (selectedDictionaries.length === 0) {
        const selectedFromLang = dictionary.fromLang;
        const selectedToLang = dictionary.toLang;

        const filtered = ownDictionaries.filter(
          (dict) => dict.fromLang === selectedFromLang && dict.toLang === selectedToLang
        );

        setFilteredDictionaries(filtered);
      }
    }
  };

  useEffect(() => {
    if (selectedDictionaries.length === 0) {
      setFilteredDictionaries(ownDictionaries);
    }
  }, [selectedDictionaries, ownDictionaries]);

  // Обновленная функция для подсчета общего количества карточек
  const getTotalTermsCount = () => {
    if (isRoomTest) {
      return selectedDictionaries.reduce((total, dictId) => {
        const dict = ownDictionaries.find((d) => d.id === dictId);
        return total + (dict?.cardsCount || 0); // Изменено с termsCount на cardsCount
      }, 0);
    } else {
      return termsCount || 0;
    }
  };

  // Эффект для автоматического обновления максимального значения в поле ввода
  useEffect(() => {
    const maxQuestions = getTotalTermsCount();
    const currentQuestions = parseInt(questionsNumber) || 0;

    // Если текущее значение превышает новый максимум, сбрасываем его
    if (currentQuestions > maxQuestions && maxQuestions > 0) {
      setValue("questionsNumber", maxQuestions.toString());
    }
  }, [selectedDictionaries, ownDictionaries, questionsNumber, setValue]);

  const hasSelectedQuestionTypes = () => {
    return Object.values(questionTypes).some((value) => value === true);
  };

  const isTranslationValidForTerms = () => {
    const availableTerms = getTotalTermsCount();
    if (questionTypes.translation && availableTerms < 3) {
      return false;
    }
    return true;
  };

  const createAndJoinRoom = async (testData: any, roomName: string) => {
    setIsCreatingRoom(true);
    const token = localStorage.getItem("token");

    try {
      // Create a new connection to RoomsHub
      const roomsConnection = new HubConnectionBuilder()
        .withUrl(`https://localhost:7288/rooms?access_token=${token}`, {
          transport: HttpTransportType.WebSockets,
        })
        .withAutomaticReconnect()
        .build();

      connectionRef.current = roomsConnection;

      await roomsConnection.start();

      roomsConnection.on("RedirectToRoom", async (roomId: string) => {
        navigate(`/room/${roomId}`);
      });

      await roomsConnection.invoke("AddRoom", testData, roomName);
    } catch (error) {
      console.error(t("createTest.errors.creatingRoom"), error);
      setIsCreatingRoom(false);
    }
  };

  const onSubmit = async (data: FormData) => {
    const selectedQuestionTypes = Object.keys(data.questionTypes)
      .filter((key) => data.questionTypes[key as keyof typeof data.questionTypes])
      .map((key) => questionTypeMapping[key]);

    if (selectedQuestionTypes.length === 0) {
      alert(t("createTest.validation.selectQuestionType"));
      return;
    }

    if (isRoomTest && selectedDictionaries.length === 0) {
      alert(t("createTest.validation.selectDictionary"));
      return;
    }

    if (isRoomTest && !data.roomName.trim()) {
      alert(t("createTest.validation.enterRoomName"));
      return;
    }

    const requestedQuestions = parseInt(data.questionsNumber);
    const availableTerms = getTotalTermsCount();

    if (requestedQuestions > availableTerms) {
      alert(
        t("createTest.validation.tooManyQuestions", {
          requested: requestedQuestions,
          available: availableTerms,
        }) || `Нельзя создать ${requestedQuestions} вопросов. Доступно только ${availableTerms} терминов.`
      );
      return;
    }

    if (requestedQuestions <= 0) {
      alert(t("createTest.validation.invalidQuestionsNumber") || "Количество вопросов должно быть больше 0");
      return;
    }

    if (data.questionTypes.translation && availableTerms < 3) {
      alert(
        t("createTest.validation.translationMinTerms") ||
          "Для створення питань типу 'Переклад' необхідно мінімум 3 терміна"
      );
      return;
    }

    const payload = {
      questionsNumber: requestedQuestions,
      questionTypes: selectedQuestionTypes,
      dictionariesId: isRoomTest ? selectedDictionaries : [id],
    };

    try {
      const response = await axios.post("https://localhost:7288/Test", payload);

      if (isRoomTest) {
        await createAndJoinRoom(response.data, data.roomName);
      } else {
        navigate(`/test/${id}`, { state: { testData: response.data } });
      }
    } catch (error) {
      console.error(t("createTest.errors.creatingTest"), error);
      if (isRoomTest) {
        setIsCreatingRoom(false);
      }
    }
  };

  const availableTerms = getTotalTermsCount();
  const currentQuestions = parseInt(questionsNumber) || 0;
  const isQuestionsValid = currentQuestions > 0 && currentQuestions <= availableTerms;
  const hasQuestionTypesSelected = hasSelectedQuestionTypes();
  const isTranslationValid = isTranslationValidForTerms();

  const isSubmitEnabled =
    hasQuestionTypesSelected && isQuestionsValid && isTranslationValid && !isCreatingRoom;

  return (
    <div className="w-full flex items-center justify-center">
      <div className="flex flex-col gap-20 w-[1200px] mt-10 items-center">
        <div className="flex justify-between items-center mb-8 border-b pb-4 w-full">
          <span className="text-xl font-semibold">{vocName}</span>
          <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 rounded-full">
            X
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="w-full flex flex-col items-center">
          {isRoomTest && (
            <input
              type="text"
              {...register("roomName", { required: isRoomTest })}
              className="w-full mb-8 p-3 border-b focus:outline-none"
              placeholder={t("createTest.roomNamePlaceholder") || "Назва змагання"}
            />
          )}

          <div className="mb-8 p-3 w-full flex justify-between items-center border border-1 rounded-[8px]">
            <div className="flex flex-col">
              <label className="block text-lg font-medium mb-2 ">
                Кількість питань{" "}
                <span className="text-[#8B8B8B] text-sm">(максимальна кількість - {availableTerms}) </span>
              </label>
              {currentQuestions > 0 && !isQuestionsValid && (
                <span className="text-sm text-red-600">Перевищує кількість доступних термінів</span>
              )}
              {isRoomTest && selectedDictionaries.length === 0 && (
                <span className="text-sm text-orange-600">
                  Спочатку оберіть словники для визначення максимальної кількості питань
                </span>
              )}
            </div>
            <input
              type="number"
              min="1"
              max={availableTerms || 1}
              placeholder="1"
              {...register("questionsNumber", {
                required: true,
                min: 1,
                max: availableTerms || 1,
              })}
              disabled={isRoomTest && selectedDictionaries.length === 0}
              className={`w-[100px] text-center p-3 border rounded-lg focus:outline-none ${
                !isQuestionsValid && currentQuestions > 0
                  ? "bg-red-100 border-red-300"
                  : isRoomTest && selectedDictionaries.length === 0
                  ? "bg-gray-100 border-gray-300"
                  : "bg-[#F3D86D]"
              }`}
<<<<<<< HEAD
              onKeyDown={(e) => {
                // Запрещаем ввод цифры 0 в начале и других недопустимых символов
                if (e.key === "0" && e.currentTarget.value === "") {
                  e.preventDefault();
                } else if (e.key === "-" || e.key === "+" || e.key === "e" || e.key === "E") {
                  e.preventDefault();
                }
              }}
              onChange={(e) => {
                // Удаляем ведущие нули и устанавливаем минимальное значение 1
                let value = e.target.value.replace(/^0+/, "");
                if (value === "" || parseInt(value) < 1) {
                  value = "";
                }
                setValue("questionsNumber", value);
              }}
=======
              data-testid="questions-number-input"
>>>>>>> 9822cbb0497b1f8117b9cb2bb634010b88969ea0
            />
          </div>

          <div className="mb-8 p-3 w-full flex justify-between border border-1 rounded-[8px]">
            <div className="flex flex-col">
              <span className="block text-lg font-medium mb-4">{t("createTest.questionTypes.title")}</span>
              {!hasQuestionTypesSelected && (
                <span className="text-sm text-red-600">Оберіть хоча б один тип питання</span>
              )}
              {questionTypes.translation && !isTranslationValid && (
                <span className="text-sm text-red-600">
                  Для типу "Перевод" потрібно мінімум 3 терміни (доступно: {availableTerms})
                </span>
              )}
            </div>
            <div className="bg-gray-50 p-6 space-y-4">
              {[
                // { label: t("createTest.questionTypes.definition"), key: "definition" as const },
                { label: t("createTest.questionTypes.translation"), key: "translation" as const },
                { label: t("createTest.questionTypes.grammar"), key: "grammar" as const },
                { label: t("createTest.questionTypes.audioRecord"), key: "audioRecord" as const },
                { label: t("createTest.questionTypes.audioListen"), key: "audioListen" as const },
              ].map(({ label, key }) => (
                <div key={key} className="flex justify-between items-center">
                  <span className="text-base mr-2">{label}</span>
                  <label style={{ display: "inline-block" }}>
                    <input
                      type="checkbox"
                      {...register(`questionTypes.${key}`)}
                      style={{ display: "none" }}
                      id={`questionTypes.${key}`}
                      data-testid={`test-type-${key}`}
                    />
                    <span className="custom-checkbox" />
                  </label>
                </div>
              ))}
            </div>
          </div>

          {isRoomTest && (
            <div className="mb-8 w-full">
              {selectedDictionaries.length > 0 && (
                <div className="mb-2 p-2 bg-blue-50 border border-blue-200 rounded-lg">
                  <p className="text-sm text-blue-700">
                    {t("createTest.dictionaries.filterInfo")}
                    <span className="font-semibold ml-1">
                      (Загальна кількість карточок: {availableTerms})
                    </span>
                  </p>
                </div>
              )}
              <div className="rounded-lg border space-y-4 max-h-[300px] overflow-y-auto">
                {filteredDictionaries.length === 0 ? (
                  <p>{t("createTest.dictionaries.notFound")}</p>
                ) : (
                  <>
                    <span className="pt-3 pl-4 block text-lg font-medium mb-4">
                      {t("createTest.dictionaries.title")}
                    </span>

                    {filteredDictionaries.map((dict) => (
                      <div key={dict.id} className="flex justify-between items-center p-4 bg-white border-b">
                        <div>
                          <a
                            href={`/vocabulary/${dict.id}`}
                            className="text-[#4F4F4F] text-lg font-semibold"
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            {dict.title}
                          </a>
                          <div className="text-sm text-gray-600">
                            <span className="font-medium">{dict.cefr}</span>
                          </div>
                          <div className="text-sm text-gray-600">
                            <span className="font-medium">{dict.label}</span>
                          </div>
                          <div className="text-sm text-gray-600">
                            <span className="font-medium">
                              {dict.fromLang} → {dict.toLang}
                            </span>
                          </div>
                          <div className="text-sm text-blue-600">
                            <span className="font-medium">Карточок: {dict.cardsCount || 0}</span>
                          </div>
                        </div>
                        <label style={{ display: "inline-block" }}>
                          <input
                            type="checkbox"
                            checked={selectedDictionaries.includes(dict.id)}
                            onChange={() => toggleDictionarySelection(dict)}
                            className="w-5 h-5"
                            style={{ display: "none" }}
                          />
                          <span className="custom-checkbox" />
                        </label>
                      </div>
                    ))}
                  </>
                )}
              </div>
            </div>
          )}

          <button
            type="submit"
            disabled={!isSubmitEnabled}
            className={`w-[250px] ${
              !isSubmitEnabled ? "bg-gray-400 cursor-not-allowed" : "bg-[#CBD1FF] hover:opacity-70"
            } text-black py-3 rounded-lg font-medium transition-colors`}
            data-testid="start-test-button"
          >
            {isCreatingRoom
              ? t("createTest.buttons.creatingRoom")
              : isRoomTest
              ? t("createTest.buttons.createRoom")
              : t("createTest.buttons.startTest")}
          </button>
        </form>
      </div>
    </div>
  );
};
