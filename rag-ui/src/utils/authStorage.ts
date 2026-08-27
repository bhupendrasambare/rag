import appConfig from '../config/app.config';

import type { User } from '../types/auth';

const {
  accessTokenKey,
  refreshTokenKey,
  userKey,
} = appConfig.auth;

export const authStorage = {
  /*
  |--------------------------------------------------------------------------
  | Access token
  |--------------------------------------------------------------------------
  */

  getAccessToken(): string | null {
    return localStorage.getItem(
      accessTokenKey,
    );
  },

  setAccessToken(
    token: string,
  ): void {
    if (!token) {
      throw new Error(
        'Cannot store empty access token.',
      );
    }

    localStorage.setItem(
      accessTokenKey,
      token,
    );
  },

  /*
  |--------------------------------------------------------------------------
  | Refresh token
  |--------------------------------------------------------------------------
  */

  getRefreshToken(): string | null {
    return localStorage.getItem(
      refreshTokenKey,
    );
  },

  setRefreshToken(
    token: string,
  ): void {
    if (!token) {
      throw new Error(
        'Cannot store empty refresh token.',
      );
    }

    localStorage.setItem(
      refreshTokenKey,
      token,
    );
  },

  /*
  |--------------------------------------------------------------------------
  | User
  |--------------------------------------------------------------------------
  */

  getUser(): User | null {
    const value =
      localStorage.getItem(userKey);

    if (!value) {
      return null;
    }

    try {
      return JSON.parse(value) as User;
    } catch {
      return null;
    }
  },

  setUser(
    user: User,
  ): void {
    localStorage.setItem(
      userKey,
      JSON.stringify(user),
    );
  },

  /*
  |--------------------------------------------------------------------------
  | Clear
  |--------------------------------------------------------------------------
  */

  clear(): void {
    localStorage.removeItem(
      accessTokenKey,
    );

    localStorage.removeItem(
      refreshTokenKey,
    );

    localStorage.removeItem(
      userKey,
    );
  },
};