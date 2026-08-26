import appConfig from '../config/app.config';
import type { User } from '../types/auth';

const {
  accessTokenKey,
  refreshTokenKey,
  userKey,
} = appConfig.auth;

export const authStorage = {

  getAccessToken(): string | null {
    return localStorage.getItem(accessTokenKey);
  },

  setAccessToken(token: string): void {
    localStorage.setItem(accessTokenKey, token);
  },

  getRefreshToken(): string | null {
    return localStorage.getItem(refreshTokenKey);
  },

  setRefreshToken(token: string): void {
    localStorage.setItem(refreshTokenKey, token);
  },

  getUser(): User | null {
    const user = localStorage.getItem(userKey);

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user) as User;
    } catch {
      return null;
    }
  },

  setUser(user: User): void {
    localStorage.setItem(userKey, JSON.stringify(user));
  },

  clear(): void {
    localStorage.removeItem(accessTokenKey);
    localStorage.removeItem(refreshTokenKey);
    localStorage.removeItem(userKey);
  },
};