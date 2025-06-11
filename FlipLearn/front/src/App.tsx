import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { AppDispatch } from "./store/store";
import { fetchUser, logout } from "./store/user/userSlice";

import Home from "./pages/Home";
import { AuthPage } from "./components/AuthPage";
import Library from "./pages/Library";
import AddVocabulary from "./pages/AddVocabulary";
import { EditVocabulary } from "./pages/EditVocabulary";
import Account from "./pages/Account";
import Vocabulary from "./pages/Vocabulary";
import PrivateRoute from "./components/Routes/PrivateRoute";
import PublicRoute from "./components/Routes/PublicRoute";
import { CreateTest } from "./pages/CreateTest";
import { TestPage } from "./pages/Test";
import { TestResult } from "./pages/TestResult";
import { Rooms } from "./pages/Rooms";
import { Room } from "./pages/Room";
import { ForgotPassword } from "./pages/ForgotPassword";
import { NewPassword } from "./pages/NewPassword";
import { CompetitionsHistory } from "./pages/CompetitionsHistory";
import { RaceTest } from "./pages/RaceTest";
import { CompetitionDetails } from "./pages/CompetitionDetails";

import "./styles/global.scss";
import "./index.css";
import { setLogoutCallback } from "./utils/axiosInstance";

const App = () => {
  const dispatch = useDispatch<AppDispatch>();
  const token = localStorage.getItem("token");

  // Функция для выхода пользователя
  const handleLogout = () => {
    dispatch(logout());
  };

  useEffect(() => {
    // Устанавливаем callback для axios interceptor
    setLogoutCallback(handleLogout);

    // Загружаем пользователя если есть токен
    if (token) {
      dispatch(fetchUser(token));
    }
  }, [dispatch, token]);

  return (
    <Routes>
      <Route element={<PublicRoute />}>
        <Route path="/" element={<Home />} />
        <Route path="/sign-in" element={<AuthPage isLogin />} />
        <Route path="/sign-up" element={<AuthPage isLogin={false} />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/new-password" element={<NewPassword />} />
      </Route>

      <Route element={<PrivateRoute />}>
        <Route path="/library" element={<Library />} />
        <Route path="/add-vocabulary" element={<AddVocabulary />} />
        <Route path="/vocabulary/:id" element={<Vocabulary />} />
        <Route path="/edit-vocabulary/:id" element={<EditVocabulary />} />
        <Route path="/account" element={<Account />} />
        <Route path="/create-test/:id" element={<CreateTest />} />
        <Route path="/test/:id" element={<TestPage />} />
        <Route path="/race-test/:id" element={<RaceTest />} />
        <Route path="/test-result" element={<TestResult />} />
        <Route path="/rooms" element={<Rooms />} />
        <Route path="/room/:id" element={<Room />} />
        <Route path="/competitions-history" element={<CompetitionsHistory />} />
        <Route path="/competitions-details/:id" element={<CompetitionDetails />} />
      </Route>

      <Route path="*" element={<Navigate to="/sign-in" replace />} />
    </Routes>
  );
};

export default App;
