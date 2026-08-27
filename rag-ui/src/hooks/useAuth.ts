import {
  useAuthStore,
} from '../store';

export const useAuth = () => {

  const isAuthenticated =
    useAuthStore(
      (state) =>
        state.isAuthenticated,
    );

  const accessToken =
    useAuthStore(
      (state) =>
        state.accessToken,
    );

  const refreshToken =
    useAuthStore(
      (state) =>
        state.refreshToken,
    );

  const user =
    useAuthStore(
      (state) =>
        state.user,
    );

  const isInitializing =
    useAuthStore(
      (state) =>
        state.isInitializing,
    );

  const login =
    useAuthStore(
      (state) =>
        state.login,
    );

  const logout =
    useAuthStore(
      (state) =>
        state.logout,
    );

  const restoreSession =
    useAuthStore(
      (state) =>
        state.restoreSession,
    );

  return {

    isAuthenticated,

    accessToken,

    refreshToken,

    user,

    isInitializing,

    login,

    logout,

    restoreSession,
  };
};