import {
  useEffect,
  type ReactNode,
} from 'react';

import { useAuthStore } from '../../store';

interface Props {
  children: ReactNode;
}

const AuthInitializer = ({
  children,
}: Props) => {

  const restoreSession =
    useAuthStore(
      (state) => state.restoreSession,
    );

  const isInitializing =
    useAuthStore(
      (state) => state.isInitializing,
    );

  useEffect(() => {

    restoreSession();

  }, [restoreSession]);

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

        <div
          className="text-center"
        >

          <div
            className="spinner-border mb-3"
            role="status"
          />

          <div className="text-muted">
            Checking your session...
          </div>

        </div>

      </div>
    );
  }

  return <>{children}</>;
};

export default AuthInitializer;