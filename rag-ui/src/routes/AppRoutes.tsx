import {
  Navigate,
  Route,
  Routes,
} from 'react-router-dom';

import AuthGuard from '../guards/AuthGuard';

import AppLayout from '../layouts/AppLayout';

import LoginPage from '../pages/LoginPage';
import DashboardPage from '../pages/DashboardPage';


const AppRoutes = () => {

  return (
    <Routes>

      <Route
        path="/login"
        element={<LoginPage />}
      />

      <Route element={<AuthGuard />}>

        <Route
          element={
            <AppLayout>
              <Routes>
                <Route
                  path="/dashboard"
                  element={<DashboardPage />}
                />
              </Routes>
            </AppLayout>
          }
        />

      </Route>

      <Route
        path="*"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

    </Routes>
  );
};


export default AppRoutes;