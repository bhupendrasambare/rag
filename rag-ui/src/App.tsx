import {
  useEffect,
} from 'react';

import {
  BrowserRouter,
} from 'react-router-dom';

import AppRoutes from './routes/AppRoutes';

import { useAuthStore } from './store';
import QueryProvider from './providers/QueryProvider';

const App = () => {

  const initialize = useAuthStore(
    (state) => state.initialize,
  );

  useEffect(() => {
    initialize();
  }, [initialize]);

  return (
    <QueryProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </QueryProvider>
  );
};

export default App;