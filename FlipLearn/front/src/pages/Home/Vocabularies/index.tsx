import VocabularySlider from "./VocabularySlider";
import { useTranslation } from "react-i18next";

import "./index.scss";

const Vocabularies = () => {
  const { t } = useTranslation();

  return (
    <div className="vocabularies">
      <div className="vocabularies-title">
        <span className="vocabularies-title__create">{t("homePage.vocabularies.title.create")}</span>
        <span className="vocabularies-title__share">{t("homePage.vocabularies.title.share")}</span>
        <span className="vocabularies-title__compete">{t("homePage.vocabularies.title.compete")}</span>
      </div>

      <VocabularySlider />
    </div>
  );
};

export default Vocabularies;
