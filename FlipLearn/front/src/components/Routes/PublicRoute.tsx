import { Navigate, Outlet } from "react-router-dom";
import token from "../../utils/checkToken";

const PublicRoute = () => {
  const isAuthenticated = token;

  return isAuthenticated ? <Navigate to="/library" replace /> : <Outlet />;
};

export default PublicRoute;
