import { useAuthStore } from '../store';

export const useAuth = () => {
  const {
    isAuthenticated,
    accessToken,
    refreshToken,
    user,
    initialized,
    login,
    logout,
    initialize,
  } = useAuthStore();

  return {
    isAuthenticated,
    accessToken,
    refreshToken,
    user,
    initialized,
    login,
    logout,
    initialize,
  };
};