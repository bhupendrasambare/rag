import {
  Navigate,
  Route,
  Routes,
} from 'react-router-dom';

import AuthGuard from '../guards/AuthGuard';
import LoginPage from '../pages/LoginPage';
import MainLayout from '../pages/MainLayout';
import DashboardPage from '../pages/DashboardPage';
import DocumentsPage from '../pages/DocumentsPage';
import UploadPage from '../pages/UploadPage';
import ChatPage from '../pages/ChatPage';
import SettingsPage from '../pages/SettingsPage';

const AppRoutes = () => {

  return (
    <Routes>

      {/* Public routes */}

      <Route
        path="/login"
        element={<LoginPage />}
      />

      {/* Protected routes */}

      <Route element={<AuthGuard />}>

        <Route element={<MainLayout />}>

          <Route
            path="/dashboard"
            element={<DashboardPage />}
          />

          <Route
            path="/documents"
            element={<DocumentsPage />}
          />

          <Route
            path="/documents/upload"
            element={<UploadPage />}
          />

          <Route
            path="/chat"
            element={<ChatPage />}
          />

          <Route
            path="/settings"
            element={<SettingsPage />}
          />

        </Route>

      </Route>

      <Route
        path="/"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

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