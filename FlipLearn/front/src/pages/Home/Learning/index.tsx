import Slider from "react-slick";
import { useTranslation } from "react-i18next";

import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "./index.scss";
import sliderSettings from "./sliderSettings";
import slide1 from "../../../assets/slider1.svg";
import slide2 from "../../../assets/slider2.svg";
import slide3 from "../../../assets/slider3.svg";

const slidesMap = [slide1, slide2, slide3];

const Learning = () => {
  const { t } = useTranslation();

  return (
    <div className="learning">
      <div className="learning-intro">
        <h2 className="learning-intro__title">{t("homePage.learning.intro.title")}</h2>

        <span>{t("homePage.learning.intro.subtitle")}</span>

        <div className="learning-variants">
          <div className="learning-variant">
            <span>{t("homePage.learning.variants.practice.title")}</span>
            <span className="learning-variant__text">{t("homePage.learning.variants.practice.text")}</span>
          </div>
          <div className="learning-variant">
            <span>{t("homePage.learning.variants.test.title")}</span>
            <span className="learning-variant__text">{t("homePage.learning.variants.test.text")}</span>
          </div>
          <div className="learning-variant">
            <span>{t("homePage.learning.variants.choose.title")}</span>
            <span className="learning-variant__text">{t("homePage.learning.variants.choose.text")}</span>
          </div>
        </div>
      </div>

      <div className="min-w-[400px] h-[400px] relative">
        <Slider {...sliderSettings}>
          {slidesMap.map((elem) => (
            <div key={elem} className="h-full !flex w-full p-4 h-[350px]">
              <img src={elem} alt="" />
            </div>
          ))}
        </Slider>
      </div>
    </div>
  );
};

export default Learning;
