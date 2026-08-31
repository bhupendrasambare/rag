import {
  Navigate,
  Route,
  Routes,
} from 'react-router-dom';

import AuthGuard from '../guards/AuthGuard';

import LoginPage from '../pages/LoginPage';
import DashboardPage from '../pages/DashboardPage';
import AppLayout from '../layouts/AppLayout';
import SettingsPage from '../pages/SettingsPage';
import DocumentsPage from '../pages/DocumentsPage';


const AppRoutes = () => {

  return (
    <Routes>

      {/* Public */}

      <Route
        path="/login"
        element={<LoginPage />}
      />


      {/* Protected */}

      <Route element={<AuthGuard />}>

        <Route
          element={<AppLayout />}
        >

          <Route
            path="/dashboard"
            element={<DashboardPage />}
          />

          <Route
            path="/documents"
            element={<DocumentsPage />}
          />

          <Route
            path="/settings"
            element={<SettingsPage />}
          />

        </Route>

      </Route>


      {/* Default */}

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