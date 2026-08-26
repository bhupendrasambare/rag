import { Navigate, Outlet } from 'react-router-dom';

import { useAuthStore } from '../store';

const AuthGuard = () => {

  const {
    isAuthenticated,
    initialized,
  } = useAuthStore();

  if (!initialized) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div
          className="spinner-border"
          role="status"
        >
          <span className="visually-hidden">
            Loading...
          </span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return <Outlet />;
};

export default AuthGuard;