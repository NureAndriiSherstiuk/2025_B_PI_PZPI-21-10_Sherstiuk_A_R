import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

import house from "../../assets/house.svg";
import folders from "../../assets/folders.svg";
import question from "../../assets/quiestion-sign.svg";
import history from "../../assets/history.png";

import "./index.scss";

interface NavigationProps {
  isOpen: boolean;
}

const Navigation: React.FC<NavigationProps> = ({ isOpen }) => {
  const { t } = useTranslation();

  return (
    <motion.div className={`navigation ${isOpen ? "navigation-expanded" : ""}`}>
      <div className="navigation-links">
        <Link to="/" className="navigation-links__item">
          <img src={house} alt="house" />
          <motion.span
            initial={{ width: 0, opacity: 0 }}
            animate={{ width: isOpen ? "auto" : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ duration: 0.3 }}
            className="navigation-text"
          >
            {t("navigation.home")}
          </motion.span>
        </Link>
        <Link to="/rooms" className="navigation-links__item">
          <img src={question} alt="question" />
          <motion.span
            initial={{ width: 0, opacity: 0 }}
            animate={{ width: isOpen ? "auto" : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ duration: 0.3 }}
            className="navigation-text"
          >
            {t("navigation.competitions")}
          </motion.span>
        </Link>

        <Link to="/competitions-history" className="navigation-links__item">
          <img src={history} alt="history" />
          <motion.span
            initial={{ width: 0, opacity: 0 }}
            animate={{ width: isOpen ? "auto" : 0, opacity: isOpen ? 1 : 0 }}
            transition={{ duration: 0.3 }}
            className="navigation-text"
          >
            {t("navigation.competitionsHistory")}
          </motion.span>
        </Link>
      </div>
    </motion.div>
  );
};

export default Navigation;
