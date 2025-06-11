import unoCard from "../../../assets/intro/card.png";
import unoCards from "../../../assets/intro/uno-cards.png";
import eye from "../../../assets/intro/intro-eye.png";
import translator from "../../../assets/intro/translator.png";
import brain from "../../../assets/intro/brain.png";
import arrows from "../../../assets/intro/arrows.png";
import arrowRight from "../../../assets/arrow-right.png";
import { useTranslation } from "react-i18next";

import "./index.scss";
import { Link } from "react-router-dom";

const Intro = () => {
  const { t } = useTranslation();

  return (
    <div className="intro">
      <div className="intro-slogan">
        <h2 className="intro-slogan__title">{t("homePage.intro.slogan.title")}</h2>
        <span className="intro-slogan__text">{t("homePage.intro.slogan.text")}</span>
        <div className="our-position">
          <div className="our-position__block">
            <img src={eye} alt="eye" />
            <span>{t("homePage.intro.position.see")}</span>
          </div>
          <img src={arrowRight} alt="next" />
          <div className="our-position__block">
            <img src={translator} alt="translator" />
            <span>{t("homePage.intro.position.translate")}</span>
          </div>
          <img src={arrowRight} alt="next" />
          <div className="our-position__block">
            <img src={brain} alt="brain" />
            <span>{t("homePage.intro.position.remember")}</span>
          </div>
          <img src={arrowRight} alt="next" />
          <div className="our-position__block">
            <img src={arrows} alt="arrows" />
            <span>{t("homePage.intro.position.repeat")}</span>
          </div>
        </div>

        <Link className="reg-btn" to="/sign-up">
          {t("homePage.intro.registerBtn")}
        </Link>
      </div>
      <div className="intro-images">
        <img src={unoCard} style={{ zIndex: 5 }} alt="uno cards" />
        <img src={unoCards} alt="uno cards" />
      </div>
    </div>
  );
};

export default Intro;
