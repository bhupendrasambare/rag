import {
  Navigate,
  Outlet,
  useLocation,
} from 'react-router-dom';

import {
  Spinner,
} from 'react-bootstrap';

import {
  useAuthStore,
} from '../store';

const AuthGuard = () => {

  const location =
    useLocation();

  const isAuthenticated =
    useAuthStore(
      (state) =>
        state.isAuthenticated,
    );

  const isInitializing =
    useAuthStore(
      (state) =>
        state.isInitializing,
    );

  /*
   * App normally waits for initialization,
   * but keep this guard as an additional
   * protection.
   */

  if (isInitializing) {

    return (
      <div
        className="
          min-vh-100
          d-flex
          align-items-center
          justify-content-center
        "
      >

        <Spinner
          animation="border"
        />

      </div>
    );
  }

  if (!isAuthenticated) {

    return (
      <Navigate
        to="/login"
        replace
        state={{
          from:
            location.pathname,
        }}
      />
    );
  }

  return <Outlet />;
};

export default AuthGuard;