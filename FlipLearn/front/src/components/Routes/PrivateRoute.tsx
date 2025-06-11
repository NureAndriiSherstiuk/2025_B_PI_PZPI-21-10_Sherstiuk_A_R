import { Navigate, Outlet } from "react-router-dom";
import token from "../../utils/checkToken";

const PrivateRoute = () => {
  const isAuthenticated = token;

  return isAuthenticated ? <Outlet /> : <Navigate to="/sign-in" replace />;
};

export default PrivateRoute;
