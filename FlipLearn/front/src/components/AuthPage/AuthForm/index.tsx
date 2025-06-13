import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { SubmitHandler, useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { ToastContainer } from "react-toastify";
import { UserPayload } from "../../../common/types/types";
import { useTranslation } from "react-i18next";
import { CircularProgress } from "@mui/material";

import googleLogo from "../../../assets/google-logo.svg";
import eye from "../../../assets/eye.svg";

import "./index.scss";
import clsx from "clsx";
import axios from "axios";
import { toastError, toastSuccess } from "../../../utils/alerts";

interface AuthFormProps {
  isLoginPage: boolean;
}

const AuthForm: React.FC<AuthFormProps> = ({ isLoginPage }) => {
  const { t } = useTranslation();
  const [passwordView, setPasswordView] = useState<boolean>(false);
  const [showCodeInput, setShowCodeInput] = useState<boolean>(false);
  const [verificationCode, setVerificationCode] = useState<string>("");
  const [timeLeft, setTimeLeft] = useState<number>(180); // 3 minutes in seconds
  const [timerActive, setTimerActive] = useState<boolean>(false);
  const [registrationData, setRegistrationData] = useState<any>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();
  const location = useLocation();

  const signInScheme = yup.object({
    email: yup.string().email("Некоректна пошта").required("Пошта обов'язкова"),
    password: yup
      .string()
      .min(6, "Мінімум 6 символів")
      .max(16, "Максимум 16 символів")
      .required("Пароль обов'язковий"),
  });

  const signUpScheme = signInScheme.shape({
    username: yup.string().required("Ім'я обов'язкове"),
    confirmPassword: yup
      .string()
      .oneOf([yup.ref("password")], "Паролі не співпадають")
      .required("Підтвердження пароля обов'язкове"),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<UserPayload>({
    resolver: yupResolver(isLoginPage ? signInScheme : signUpScheme),
  });

  const handlePasswordView = () => setPasswordView((prev) => !prev);

  // Timer for code verification
  useEffect(() => {
    let interval: any;

    if (timerActive && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft((prevTime) => prevTime - 1);
      }, 1000);
    } else if (timeLeft === 0) {
      setTimerActive(false);
      setShowCodeInput(false);
      toastError("Час дії коду закінчився. Спробуйте знову");
    }

    return () => clearInterval(interval);
  }, [timerActive, timeLeft]);

  const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? "0" : ""}${remainingSeconds}`;
  };

  const submit: SubmitHandler<UserPayload> = (data) => {
    const { confirmPassword, ...dataToSend } = data;
    setIsLoading(true);

    if (isLoginPage) {
      const formData = new FormData();
      formData.append("email", dataToSend.email);
      formData.append("password", dataToSend.password);

      axios
        .post("https://localhost:7288/login-user", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          localStorage.setItem("token", response.data.token);
          navigate("/library");
          window.location.reload();
        })
        .catch(() => {
          toastError(t("auth.invalidCredentials"));
          reset();
        })
        .finally(() => {
          setIsLoading(false);
        });
    } else {
      // For registration, first send with code=null
      if (!showCodeInput) {
        const registrationPayload = { ...dataToSend, code: null };

        axios
          .post("https://localhost:7288/new-user", registrationPayload)
          .then(() => {
            setRegistrationData(registrationPayload);
            setShowCodeInput(true);
            setTimerActive(true);
            setTimeLeft(180); // Reset timer to 3 minutes
            toastSuccess(t("auth.codeSent"));
          })
          .catch(() => {
            toastError(t("auth.userExists"));
          })
          .finally(() => {
            setIsLoading(false);
          });
      }
    }
  };

  const handleCodeSubmit = () => {
    if (!registrationData) return;

    setIsLoading(true);

    // Send second request with the verification code
    const verificationPayload = { ...registrationData, code: verificationCode };

    axios
      .post("https://localhost:7288/new-user", verificationPayload)
      .then((response) => {
        localStorage.setItem("token", response.data.token);
        navigate("/sign-in", { state: { success: true } });
        setShowCodeInput(false);
        setTimerActive(false);
      })
      .catch(() => {
        toastError(t("auth.invalidCode"));
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  useEffect(() => {
    if (location.state?.success) {
      toastSuccess(t("auth.userRegistered"));
    }
  }, [location.state, t]);

  return (
    <div className="auth-wrapper">
      <ToastContainer />

      {/* Loading overlay */}
      {isLoading && (
        <div className="loading-overlay">
          <CircularProgress size={50} />
        </div>
      )}

      {showCodeInput ? (
        <div className="auth-wrapper__form">
          <h2 className="text-xl font-semibold mb-4">{t("auth.verificationTitle")}</h2>
          <p className="mb-4">{t("auth.verificationSent")}</p>

          <div className="auth-form__block">
            <span>{t("auth.confirmationCode")}</span>
            <div className="form-block">
              <input
                placeholder={t("auth.enterCode")}
                className="form-block__input"
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
              />
            </div>
          </div>

          <div className="mb-4 text-center">
            <span className="font-medium">
              {t("auth.codeValidTime")} {formatTime(timeLeft)}
            </span>
          </div>

          <button onClick={handleCodeSubmit} className="auth-btn">
            {t("auth.confirm")}
          </button>
        </div>
      ) : (
        <form onSubmit={handleSubmit(submit)} className="auth-wrapper__form">
          <div className="auth-form">
            {!isLoginPage && (
              <div className="auth-form__block">
                <span className={clsx(errors.username && "auth-form__block__label__error")}>
                  {t("auth.username")}
                </span>
                <div className="form-block">
                  <input
                    placeholder={t("auth.usernameInput")}
                    className={clsx("form-block__input", errors.username && "form-block__input__error")}
                    type="text"
                    {...register("username")}
                  />
                  <span className="text-red-600 text-xs absolute bottom-[-18px] left-0">
                    {errors.username?.message}
                  </span>
                </div>
              </div>
            )}

            <div className="auth-form__block">
              <span className={clsx(errors.email && "auth-form__block__label__error")}>
                {t("auth.email")}
              </span>
              <div className="relative form-block">
                <input
                  placeholder={t("auth.emailInput")}
                  className={clsx("form-block__input", errors.email && "form-block__input__error")}
                  type="text"
                  {...register("email")}
                />
                <span className="text-red-600 text-xs absolute bottom-[-18px] left-0">
                  {errors.email?.message}
                </span>
              </div>
            </div>

            <div className="relative auth-form__block">
              <div className="flex w-full justify-between">
                <span className={clsx(errors.password && "auth-form__block__label__error")}>
                  {t("auth.password")}
                </span>
                <Link to="/forgot-password" className="cursor-pointer text-[#4F4F4F]">
                  {t("auth.forgotPassword")}
                </Link>
              </div>
              <div className="form-block">
                <input
                  placeholder={t("auth.passwordInput")}
                  className={clsx("form-block__input", errors.password && "form-block__input__error")}
                  type={passwordView ? "text" : "password"}
                  {...register("password")}
                />
                <img src={eye} className="password-eye" onClick={handlePasswordView} alt="eye" />
              </div>
              <span className="text-red-600 text-xs absolute bottom-[-18px] left-0">
                {errors.password?.message}
              </span>
            </div>

            {!isLoginPage && (
              <div className="relative auth-form__block">
                <div className="form-block">
                  <input
                    placeholder={t("auth.confirmPasswordInput")}
                    className={clsx(
                      "form-block__input",
                      errors.confirmPassword && "form-block__input__error"
                    )}
                    type={passwordView ? "text" : "password"}
                    {...register("confirmPassword")}
                  />
                </div>
                <span className="text-red-600 text-xs absolute bottom-[-18px] left-0">
                  {errors.confirmPassword?.message}
                </span>
              </div>
            )}
          </div>

          <button type="submit" className="auth-btn">
            {isLoginPage ? t("auth.login") : t("auth.register")}
          </button>

          <span className="auth-ask">
            {isLoginPage ? t("auth.noAccount") : t("auth.haveAccount")}
            <Link to={isLoginPage ? "/sign-up" : "/sign-in"}>
              {isLoginPage ? t("auth.createProfile") : t("auth.signIn")}
            </Link>
          </span>
        </form>
      )}
    </div>
  );
};

export default AuthForm;
