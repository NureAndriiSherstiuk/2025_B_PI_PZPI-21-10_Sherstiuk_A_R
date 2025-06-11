import approve from "../../../assets/approve.svg";
import { useTranslation } from "react-i18next";

import "./index.scss";

const Approve = () => {
  const { t } = useTranslation();

  return (
    <div className="approve">
      <img src={approve} alt="approve" />
      <div className="approve-text">
        <span>{t("homePage.approve.firstLine")}</span>
        <span>{t("homePage.approve.secondLine")}</span>
      </div>
    </div>
  );
};

export default Approve;
