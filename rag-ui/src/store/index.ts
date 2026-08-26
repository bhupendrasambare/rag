import { create } from 'zustand';


import { authStorage } from '../utils/authStorage';
import type { User } from '../types/auth';
import type { AuthState } from '../models';

interface AuthActions {
  login: (
    accessToken: string,
    refreshToken: string,
    user: User,
  ) => void;

  setAccessToken: (accessToken: string) => void;

  setRefreshToken: (refreshToken: string) => void;

  setUser: (user: User) => void;

  logout: () => void;

  initialize: () => void;
}

type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>((set) => ({
  isAuthenticated: false,

  accessToken: null,

  refreshToken: null,

  user: null,

  initialized: false,

  login: (
    accessToken,
    refreshToken,
    user,
  ) => {
    authStorage.setAccessToken(accessToken);
    authStorage.setRefreshToken(refreshToken);
    authStorage.setUser(user);

    set({
      isAuthenticated: true,
      accessToken,
      refreshToken,
      user,
      initialized: true,
    });
  },

  setAccessToken: (accessToken) => {
    authStorage.setAccessToken(accessToken);

    set({
      accessToken,
      isAuthenticated: true,
    });
  },

  setRefreshToken: (refreshToken) => {
    authStorage.setRefreshToken(refreshToken);

    set({
      refreshToken,
    });
  },

  setUser: (user) => {
    authStorage.setUser(user);

    set({
      user,
    });
  },

  logout: () => {
    authStorage.clear();

    set({
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,
      user: null,
      initialized: true,
    });
  },

  initialize: () => {
    const accessToken = authStorage.getAccessToken();
    const refreshToken = authStorage.getRefreshToken();
    const user = authStorage.getUser();

    set({
      accessToken,
      refreshToken,
      user,
      isAuthenticated: !!accessToken,
      initialized: true,
    });
  },
}));