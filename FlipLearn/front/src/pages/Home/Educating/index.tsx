import educating from "../../../assets/educating.png";
import { useTranslation } from "react-i18next";

import "./index.scss";

const Educating = () => {
  const { t } = useTranslation();

  return (
    <div className="educating">
      <div className="educating-intro">
        <div className="educating-title">
          <span className="educating-title__item">{t("homePage.educating.title.free")}</span>
          <span className="educating-title__item">{t("homePage.educating.title.effective")}</span>
          <span className="educating-title__item">{t("homePage.educating.title.fun")}</span>
        </div>

        <span className="educating-intro__text">{t("homePage.educating.description")}</span>
      </div>

      <img src={educating} alt="educating-person" />
    </div>
  );
};

export default Educating;
