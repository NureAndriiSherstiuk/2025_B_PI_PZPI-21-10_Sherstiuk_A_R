import Header from "../Header";
import AuthForm from "./AuthForm";
import { useTranslation } from "react-i18next";

import authPerson from "../../assets/auth-person.png";

import "./index.scss";

interface AuthPage {
  isLogin: boolean;
}

export const AuthPage: React.FC<AuthPage> = ({ isLogin }) => {
  const { t } = useTranslation();

  return (
    <>
      <Header isSearchVisible={false} isAuth />
      <div className="auth">
        <div className="auth-intro">
          <span dangerouslySetInnerHTML={{ __html: t("auth.authIntro").replace(/\n/g, "<br />") }} />
          <img src={authPerson} alt="person" />
        </div>
        <AuthForm isLoginPage={isLogin} />
      </div>
    </>
  );
};
