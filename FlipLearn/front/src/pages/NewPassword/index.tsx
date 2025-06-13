import { Link, useLocation, useNavigate } from "react-router-dom";
import LogoImg from "../../assets/logo.svg";
import LogoWord from "../../assets/logo-word.svg";
import eye from "../../assets/eye.svg";

import axios from "axios";
import { toastError, toastSuccess } from "../../utils/alerts";
import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { ToastContainer } from "react-toastify";

export const NewPassword = () => {
  const { t } = useTranslation();
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [passwordView, setPasswordView] = useState(false);
  const [confirmPasswordView, setConfirmPasswordView] = useState(false);
  const [timeLeft, setTimeLeft] = useState<number>(0);
  const navigate = useNavigate();
  const location = useLocation();
  const email = location.state;

  useEffect(() => {
    const savedEndTime = localStorage.getItem("code_end_time");
    const now = Date.now();

    if (savedEndTime && +savedEndTime > now) {
      setTimeLeft(Math.floor((+savedEndTime - now) / 1000));
    } else {
      const endTime = now + 3 * 60 * 1000; // 3 хвилини
      localStorage.setItem("code_end_time", endTime.toString());
      setTimeLeft(3 * 60);
    }

    const interval = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const formatTime = (seconds: number) => {
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}:${sec < 10 ? "0" : ""}${sec}`;
  };

  const isFormValid = () => {
    return (
      password.length > 0 &&
      confirmPassword.length > 0 &&
      password === confirmPassword &&
      code.length > 0 &&
      timeLeft > 0
    );
  };

  const setNewPassword = () => {
    if (!isFormValid()) return;

    setLoading(true);
    axios
      .patch(`https://localhost:7288/new-password`, { email, password, code })
      .then(() => {
        toastSuccess(t("newPassword.successMessage"));
        localStorage.removeItem("code_end_time");
        setInterval(() => navigate("/sign-in"), 2000);
      })
      .catch(() => toastError(t("newPassword.errorMessage")))
      .finally(() => setLoading(false));
  };

  return (
    <div className="w-screen h-screen flex items-center justify-center relative">
      <ToastContainer />

      <Link className="fixed left-[20px] top-[20px] flex gap-2 items-center" to="/sign-in">
        <img src={LogoImg} alt="logo" />
        <img src={LogoWord} alt="logo-word" />
      </Link>

      <div className="text-black w-[800px] max-w-[800px] flex flex-col gap-6 rounded-[10px] bg-[#94a0fe] p-6 relative">
        {loading && (
          <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center rounded-[10px] z-10">
            <div className="loader border-4 border-white border-t-transparent rounded-full w-12 h-12 animate-spin"></div>
          </div>
        )}

        <span className="text-3xl">{t("newPassword.title")}</span>

        <div className="flex flex-col gap-2 text-sm">
          <span>{t("newPassword.passwordRequirement")}</span>
        </div>

        <div className="flex flex-col gap-2">
          <div className="flex flex-col gap-2">
            <div className="flex gap-2">
              {t("newPassword.codeLabel")}
              <div className="flex gap-2">
                <span>{t("newPassword.codeTime")}</span>
                <span className="font-bold">{formatTime(timeLeft)}</span>
              </div>
            </div>
            <input
              placeholder={t("newPassword.codePlaceholder")}
              className="w-full outline-none border-0 p-2.5 rounded-[10px]"
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              disabled={loading}
            />
          </div>

          <span>{t("newPassword.passwordLabel")}</span>

          <div className="relative">
            <input
              placeholder={t("newPassword.passwordPlaceholder")}
              className="w-full outline-none border-0 p-2.5 rounded-[10px]"
              type={passwordView ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
            />
            <img
              src={eye}
              className="absolute right-[10px] top-[3px] cursor-pointer bg-white z-[1]"
              onClick={() => setPasswordView((prev) => !prev)}
              alt="eye"
            />
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <span>{"Повторити пароль"}</span>

          <div className="relative">
            <input
              placeholder={"Повторіть пароль"}
              className={`w-full outline-none border-0 p-2.5 rounded-[10px] ${
                confirmPassword.length > 0 && password !== confirmPassword ? "border-2 border-red-500" : ""
              }`}
              type={confirmPasswordView ? "text" : "password"}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              disabled={loading}
            />
            <img
              src={eye}
              className="absolute right-[10px] top-[3px] cursor-pointer bg-white z-[1]"
              onClick={() => setConfirmPasswordView((prev) => !prev)}
              alt="eye"
            />
          </div>

          {confirmPassword.length > 0 && password !== confirmPassword && (
            <span className="text-red-600 text-sm">Паролі не співпадають</span>
          )}
        </div>

        <button
          onClick={setNewPassword}
          disabled={loading || !isFormValid()}
          className="w-full p-2.5 bg-[#f3d86d] border-0 rounded-[10px] cursor-pointer transition-opacity duration-300 hover:opacity-80 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {t("newPassword.buttonText")}
        </button>
      </div>
    </div>
  );
};
