import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { useTranslation } from "react-i18next";

import token from "../../utils/checkToken";

import LogoImg from "../../assets/logo.svg";
import LogoWord from "../../assets/logo-word.svg";
import loop from "../../assets/loop.svg";
import plus from "../../assets/plus.png";
import menu from "../../assets/menu.png";
import logout from "../../assets/logout.png";

import "./index.scss";
import { useSelector } from "react-redux";
import { RootState } from "../../store/store";

enum HeaderStyle {
  auth = "header-auth",
  welcome = "header-welcome",
}

interface HeaderProps {
  isSearchVisible?: boolean;
  isAuth?: boolean;
  welcome?: boolean;
  toggleMenu?: () => void;
}

const Header: React.FC<HeaderProps> = ({ isSearchVisible, isAuth, welcome, toggleMenu }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const [libsMenu, SetLibsMenu] = useState<boolean>(false);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [dictionaries, setDictionaries] = useState<any[]>([]);
  const [lastId, setLastId] = useState<number>(0);
  const [isDropdownOpen, setIsDropdownOpen] = useState<boolean>(false);
  const user = useSelector((state: RootState) => state.user.user);

  const toggleLibsMenu = () => SetLibsMenu((prev) => !prev);

  const fetchDictionaries = async (loadMore = false) => {
    if (!searchTerm.trim() && !loadMore) return;
    setIsDropdownOpen(true);

    try {
      const response = await axios.get("https://localhost:7288/Dictionary", {
        params: {
          Take: 5,
          LastId: loadMore ? lastId : 0,
          TitlePattern: searchTerm,
        },
        headers: { Authorization: `Bearer ${token}` },
      });

      if (loadMore) {
        setDictionaries((prev) => [...prev, ...response.data]);
      } else {
        setDictionaries(response.data);
      }

      if (response.data.length > 0) {
        setLastId(response.data[response.data.length - 1].id);
      }
    } catch (error) {
      console.error("Ошибка при получении словарей:", error);
    }
  };

  return (
    <>
      <header className={`header ${isAuth ? HeaderStyle.auth : welcome ? HeaderStyle.welcome : ""}`}>
        {token ? (
          <div className="header-logos">
            <img src={menu} className="header-menu" alt="menu" onClick={toggleMenu} />
            <img src={LogoWord} alt="logo-word" />
          </div>
        ) : (
          <Link className="header-logos" to="/">
            <img src={LogoImg} alt="logo" />
            <img src={LogoWord} alt="logo-word" />
          </Link>
        )}

        {isSearchVisible && (
          <div className="flex flex-col gap-5 flex-1 relative">
            <div className="search-input">
              <img
                src={loop}
                onClick={() => fetchDictionaries(false)}
                className="search-input__image cursor-pointer"
                alt="loop"
              />
              <input
                type="text"
                placeholder={t("header.search")}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            {isDropdownOpen && (
              <>
                <button className="modalOverlay" onClick={() => setIsDropdownOpen(false)} />
                <div className="absolute top-[40px] w-full bg-white rounded-lg shadow-lg p-2 dictionary-search-list">
                  {dictionaries.length > 0 ? (
                    <>
                      <ul className="max-h-[200px] overflow-y-auto">
                        {dictionaries.map((dict) => (
                          <li
                            key={dict.id}
                            onClick={() => {
                              navigate(`/vocabulary/${dict.id}`);
                              setIsDropdownOpen(false);
                              setSearchTerm("");
                            }}
                            className="p-2 rounded-md hover:bg-gray-100 cursor-pointer"
                          >
                            {dict.title}
                          </li>
                        ))}
                      </ul>
                    </>
                  ) : (
                    <p className="text-gray-500 p-2">{t("header.noResults")}</p>
                  )}
                </div>
              </>
            )}
          </div>
        )}

        <div className="header-links">
          {token ? (
            <>
              {libsMenu && (
                <div className="header-links__libs">
                  <Link
                    to="/add-vocabulary"
                    data-testid="create-dictionary-button"
                  >
                    {t("header.createDictionary")}
                  </Link>
                </div>
              )}
              <button
                className="header-links__addition"
                onClick={toggleLibsMenu}
                data-testid="plus-button"
              >
                <img src={plus} alt="plus" />
              </button>
              <Link
                to="/account"
                style={{ backgroundColor: user.image ? user.image : "#d9d9d9" }}
                className="header-links__profile"
                data-testid="account-button"
              ></Link>
              <button
                onClick={() => {
                  localStorage.removeItem("token");
                  window.location.reload();
                }}
                className="w-[25px]"
                data-testid="logout-button"
              >
                <img src={logout} />
              </button>
            </>
          ) : (
            <>
              {location.pathname === "/" && (
                <>
                  <Link to="/sign-in" className="header-links__auth">
                    {t("header.signIn")}
                  </Link>
                  <Link to="/sign-up" className="header-links__auth">
                    {t("header.register")}
                  </Link>
                </>
              )}
              {location.pathname === "/sign-in" && (
                <Link to="/sign-up" className="header-links__auth">
                  {t("header.register")}
                </Link>
              )}
              {location.pathname === "/sign-up" && (
                <Link to="/sign-in" className="header-links__auth">
                  {t("header.signIn")}
                </Link>
              )}
            </>
          )}
        </div>
      </header>
    </>
  );
};

export default Header;
