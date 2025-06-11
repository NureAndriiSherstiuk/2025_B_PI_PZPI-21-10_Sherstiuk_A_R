import logoWord from "../../assets/logo-word.svg";
import logoImg from "../../assets/logo.svg";

import "./index.scss";

const Footer = () => (
  <footer className="footer">
    <div className="footer-main">
      <div className="footer-main__logos">
        <img src={logoImg} alt="logo" />
        <img src={logoWord} alt="logo-word" />
      </div>

      <div className="footer-main__links">
        <span>2025 FlipLearn |All Rights Reserved</span>
      </div>
    </div>
  </footer>
);

export default Footer;
