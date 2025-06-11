import axios from "axios";

// Создаем переменную для хранения функции logout
let logoutCallback: (() => void) | null = null;

// Функция для установки callback'а logout
export const setLogoutCallback = (callback: () => void) => {
  logoutCallback = callback;
};

const axiosInstance = axios.create({
  baseURL: "https://localhost:7288",
  withCredentials: false,
});

// Interceptor для добавления токена к запросам
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor для обработки ошибок авторизации
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    if (status === 401 || status === 403) {
      localStorage.removeItem("token");

      // Вызываем callback если он установлен
      if (logoutCallback) {
        logoutCallback();
      }

      // Перенаправляем на страницу входа
      window.location.href = "/sign-in";
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
