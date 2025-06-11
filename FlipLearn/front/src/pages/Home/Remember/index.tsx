import { useTranslation } from "react-i18next";
import { useState } from "react";
import "./index.scss";

const Card = ({ text }: { text: string }) => {
  const [flipped, setFlipped] = useState(false);

  return (
    <div className="motivation-card-container" onClick={() => setFlipped(!flipped)}>
      <div className={`motivation-card ${flipped ? "is-flipped" : ""}`}>
        <div className="motivation-card-side motivation-card-front">{text}</div>
        <div className="motivation-card-side motivation-card-back text-center">
          You're capable of anything
        </div>
      </div>
    </div>
  );
};

const Remember = () => {
  const { t } = useTranslation();

  const motivationTexts = [
    t("homePage.remember.motivation.you"),
    t("homePage.remember.motivation.can"),
    t("homePage.remember.motivation.do"),
    t("homePage.remember.motivation.everything"),
  ];

  return (
    <div className="remember">
      <h2 className="remember-title">{t("homePage.remember.title")}</h2>
      <div className="remember-motivation">
        {motivationTexts.map((text, idx) => (
          <Card key={idx} text={text} />
        ))}
      </div>
    </div>
  );
};

export default Remember;
