import { useEffect, useState } from "react";
import LibraryMain from "..";
import LibraryItem from "../../LibraryItem";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import plus from "../../../../assets/plus-square.png";

interface Dictionaries {
  id: string;
  description: string;
  isPublic: boolean;
  title: string;
  label: string;
  level: string;
  cards: any[];
  creationDate: string;
  cardsCount: number;
  cefr: string;
  creator?: {
    id: number;
    username: string;
    email: string;
    trustLevel: any;
    image: string;
  };
}

type TabType = "own" | "available";

const VocabulariesList = () => {
  const [dictionaries, setDictionaries] = useState<Dictionaries[]>([]);
  const [activeTab, setActiveTab] = useState<TabType>("own");
  const navigate = useNavigate();
  const { t } = useTranslation();

  const fetchDictionaries = (tab: TabType) => {
    const token = localStorage.getItem("token");
    if (!token) return;

    const url =
      tab === "own" ? "https://localhost:7288/Dictionary/own" : "https://localhost:7288/Dictionary/available";

    axios
      .get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        setDictionaries(response.data);
      })
      .catch((error) => {
        console.error("Ошибка загрузки словарей:", error.response?.data || error.message);
      });
  };

  useEffect(() => {
    fetchDictionaries(activeTab);
  }, [activeTab]);

  return (
    <LibraryMain title={t("vocabularies.title")} listStyles="flex flex-col">
      <div className="flex gap-4 mb-4">
        <button
          className={`px-4 py-2 rounded ${activeTab === "own" && "bg-[#94a0fe]"}`}
          onClick={() => setActiveTab("own")}
        >
          {t("vocabularies.tabs.own")}
        </button>
        <button
          className={`px-4 py-2 rounded ${activeTab === "available" && "bg-[#94a0fe]"}`}
          onClick={() => setActiveTab("available")}
        >
          {t("vocabularies.tabs.available")}
        </button>
      </div>

      <div className="flex flex-wrap gap-5">
        {dictionaries.length > 0 ? (
          dictionaries.map(
            ({ id, description, isPublic, title, label, cards, creationDate, cardsCount, cefr, creator }) => (
              <LibraryItem
                key={id}
                id={id}
                description={description}
                isPublic={isPublic}
                title={title}
                label={label}
                cardsCount={cardsCount}
                cefr={cefr}
                isFolder={false}
                cards={cards}
                creationDate={creationDate}
                creator={creator}
                showCreator={activeTab === "available"}
                onClick={() => navigate(`/vocabulary/${id}`)}
              />
            )
          )
        ) : (
          <div className="flex flex-col gap-5 items-start">
            <p className="text-[#4F4F4F]">
              {t(activeTab === "own" ? "vocabularies.emptyState.own" : "vocabularies.emptyState.available")}
            </p>
            {activeTab === "own" && (
              <button
                onClick={() => navigate("/add-vocabulary")}
                className="w-[385px] h-[200px] rounded-[10px] border-2 border-black flex flex-col justify-center items-center p-4 gap-2"
              >
                <img src={plus} />
                <span className="text-[#8B8B8B]">{t("vocabularies.createButton")}</span>
              </button>
            )}
          </div>
        )}
      </div>
    </LibraryMain>
  );
};

export default VocabulariesList;
