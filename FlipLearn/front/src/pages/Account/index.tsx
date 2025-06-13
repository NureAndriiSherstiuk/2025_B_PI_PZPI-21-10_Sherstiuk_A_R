import { useEffect, useState } from "react";
import { HexColorPicker } from "react-colorful";
import { Modal } from "@mui/material";
import Select from "react-select";
import { useTranslation } from "react-i18next";
import i18n from "i18next"; // Импортируем i18n для изменения языка

import PageWrapper from "../../components/PageWrapper";

import marker from "../../assets/marker.svg";

import "./index.scss";
import axios from "axios";
import { toastError, toastSuccess } from "../../utils/alerts";
import { ToastContainer } from "react-toastify";

interface User {
  id: string;
  email: string;
  username: string;
  image?: string | null;
}

const COLOR_OPTIONS = [
  "#FF5733",
  "#33FF57",
  "#3357FF",
  "#F3D86D",
  "#CBD1FF",
  "#E91E63",
  "#9C27B0",
  "#3F51B5",
  "#03A9F4",
  "#4CAF50",
  "#FF9800",
  "#8BC34A",
  "#00BCD4",
  "#FFEB3B",
  "#795548",
];

const Account = () => {
  const { t } = useTranslation();
  const [color, setColor] = useState("#b32aa9");
  const [initialColor, setInitialColor] = useState("#b32aa9");
  const [paletteView, setPaletteView] = useState<boolean>(false);
  const [selectedLang, setSelectedLang] = useState<string>(i18n.language || "ua");
  const [user, setUser] = useState<User | null>(null);
  const [initialUser, setInitialUser] = useState<User | null>(null);

  const LANG_OPTIONS = [
    { value: "ua", label: t("ukrainian") },
    { value: "en", label: t("English") },
  ];

  const togglePaletteView = (event?: React.MouseEvent<HTMLButtonElement>) => {
    if (event) event.preventDefault();
    setPaletteView((prev) => !prev);
  };

  const changeLanguage = (langCode: string) => {
    i18n.changeLanguage(langCode);
    localStorage.setItem("language", langCode);
  };

  const saveChanges = async () => {
    try {
      const promises = [];
      const token = localStorage.getItem("token");

      if (user?.username !== initialUser?.username) {
        promises.push(
          axios.patch(
            `https://localhost:7288/new-username?username=${encodeURIComponent(user!.username)}`,
            {},
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          )
        );
      }

      if (color !== initialColor) {
        promises.push(
          axios.patch(
            `https://localhost:7288/new-image?image=${encodeURIComponent(color)}`,
            {},
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          )
        );
      }

      const prevLang = localStorage.getItem("language") || "ua";
      if (selectedLang !== prevLang) {
        localStorage.setItem("language", selectedLang);
        changeLanguage(selectedLang);
      }

      await Promise.all(promises);

      setInitialUser({ ...user! });
      setInitialColor(color);

      toastSuccess(t("dataSaved"));
    } catch (error) {
      toastError("Користувач з даним ім'ям вже існує");
    }
  };

  useEffect(() => {
    const savedLanguage = localStorage.getItem("language");
    if (savedLanguage) {
      setSelectedLang(savedLanguage);
      if (i18n.language !== savedLanguage) {
        i18n.changeLanguage(savedLanguage);
      }
    }

    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("https://localhost:7288/User", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((response) => {
          setUser(response.data);
          setInitialUser(response.data);
          setColor(response.data.image || "#b32aa9");
          setInitialColor(response.data.image || "#b32aa9");
        })
        .catch((error) => {
          console.error(t("loginError"), error.response?.data || error.message);
        });
    }
  }, [t]);

  const getAvatarColor = () => {
    if (user?.image === null) {
      return "#d9d9d9";
    }
    return color || user?.image || "#d9d9d9";
  };

  return (
    <PageWrapper>
      <ToastContainer />
      <div className="account">
        <div className="flex w-full justify-between">
          <span className="text-2xl">{t("profileSettings")}</span>
          <button
            onClick={() => {
              localStorage.removeItem("token");
              window.location.reload();
            }}
            className="bg-[#EEEEEE] rounded-[8px] w-[250px] py-2"
          >
            {t("logoutButton")}
          </button>
        </div>

        <div className="account-info">
          <span className="text-xl text-[#4F4F4F]">{t("personalInfo")}</span>

          <form className="user">
            <div className="user-item">
              <span>{t("profilePhoto")}</span>

              <div className="flex gap-6">
                <span
                  className="block h-[140px] min-w-[140px] rounded-full"
                  style={{
                    backgroundColor: user?.image === null ? "#d9d9d9" : color,
                  }}
                />
                <div className="avatar-options">
                  {COLOR_OPTIONS.map((col) => (
                    <span
                      key={col}
                      className="option-circle w-8 h-8 rounded-full cursor-pointer border border-gray-400"
                      style={{ backgroundColor: col }}
                      onClick={() => setColor(col)}
                    />
                  ))}
                  <button onClick={(e) => togglePaletteView(e)} className="new-color">
                    +
                  </button>
                  <Modal
                    open={paletteView}
                    onClose={() => togglePaletteView()}
                    aria-labelledby="modal-modal-title"
                    aria-describedby="modal-modal-description"
                  >
                    <HexColorPicker color={color} onChange={setColor} />
                  </Modal>
                </div>
              </div>
            </div>

            <div className="user-field">
              <span>{t("username")}</span>
              <div className="user-edit">
                <input
                  type="text"
                  className="user-edit__input"
                  placeholder={t("username")}
                  value={user?.username || ""}
                  onChange={(e) => setUser(user ? { ...user, username: e.target.value } : null)}
                  data-testid="username-input"
                />
                <img src={marker} alt="marker" className="user-edit__img" />
              </div>
            </div>

            <div className="user-field">
              <span>{t("email")}</span>
              <div className="user-edit">
                <input
                  type="text"
                  className="user-edit__input opacity-50"
                  placeholder={t("email")}
                  value={user?.email || ""}
                  disabled
                />
              </div>
            </div>
          </form>
        </div>

        <div className="formalization">
          <span className="text-xl text-[#4F4F4F]">{t("formalization")}</span>
          <div className="formalization-option">
            <span>{t("language")}</span>
            <Select
              className="formalization-option__select formalization-option__lang"
              value={LANG_OPTIONS.find((option) => option.value === selectedLang)}
              onChange={(newValue) => {
                if (newValue) {
                  setSelectedLang(newValue.value);
                  // Сразу меняем язык при выборе в селекте
                  changeLanguage(newValue.value);
                }
              }}
              options={LANG_OPTIONS}
              styles={{
                control: (base) => ({
                  ...base,
                  backgroundColor: "#cbd1ff",
                  cursor: "pointer",
                  borderRadius: "8px",
                  color: "#4F4F4F",
                }),
              }}
              isSearchable={false}
            />
          </div>
        </div>

        <div className="flex w-full justify-end">
          <button
            className="bg-[#D9D9D9] rounded-[7px] w-[250px] h-[45px]"
            onClick={saveChanges}
            data-testid="save-button"
          >
            {t("save")}
          </button>
        </div>
      </div>
    </PageWrapper>
  );
};

export default Account;
