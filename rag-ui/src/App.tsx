import { useEffect } from 'react';
import { Spinner } from 'react-bootstrap';

import { useAuthStore } from './store';
import AppRoutes from './routes/AppRoutes';

function App() {
  const isInitializing =
    useAuthStore(
      (state) => state.isInitializing,
    );

  const restoreSession =
    useAuthStore(
      (state) => state.restoreSession,
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
        <Spinner animation="border" />
      </div>
    );
  }

  return <AppRoutes />;
}

export default App;