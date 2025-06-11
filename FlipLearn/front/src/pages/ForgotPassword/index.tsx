import { Link, useNavigate } from "react-router-dom";
import LogoImg from "../../assets/logo.svg";
import LogoWord from "../../assets/logo-word.svg";
import axios from "axios";
import { toastError, toastSuccess } from "../../utils/alerts";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { ToastContainer } from "react-toastify";

export const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const sendCode = () => {
    setLoading(true);
    axios
      .get(`https://localhost:7288/email-code?email=${email}`)
      .then(() => {
        toastSuccess(t("forgotPassword.successMessage"));
        setTimeout(() => navigate("/new-password", { state: email }), 2000);
      })
      .catch((err) => toastError(err.error))
      .finally(() => setLoading(false));
  };

  return (
    <div className="w-screen h-screen flex items-center justify-center relative">
      <ToastContainer />
      <Link className="fixed left-[20px] top-[20px] flex gap-2 items-center" to="/sign-in">
        <img src={LogoImg} alt="logo" />
        <img src={LogoWord} alt="logo-word" />
      </Link>

      <div className="text-black w-[800px] max-w-[800px] flex flex-col gap-6 rounded-[10px] bg-[#B3BCFF] p-6 relative">
        {loading && (
          <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center rounded-[10px] z-10">
            <div className="loader border-4 border-white border-t-transparent rounded-full w-12 h-12 animate-spin"></div>
          </div>
        )}

        <span className="text-3xl">Скидання паролю</span>

        <span className="text-m">
          Введіть адресу електронної пошти, ми надішлумо вам код для скидання поролю.
        </span>

        <input
          placeholder={t("forgotPassword.emailPlaceholder")}
          className="w-full outline-none border-0 p-2.5 rounded-[10px]"
          type="text"
          onChange={(e) => setEmail(e.target.value)}
          disabled={loading}
        />

        <button
          onClick={sendCode}
          disabled={loading}
          className="w-full p-2.5 bg-[#f3d86d] border-0 rounded-[10px] cursor-pointer transition-opacity duration-300 hover:opacity-80 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Надіслати код
        </button>
      </div>
    </div>
  );
};
