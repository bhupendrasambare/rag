import { create } from 'zustand';

import type {
  LoginResponse,
  User,
} from '../types/auth';

import { authStorage } from '../utils/authStorage';

import { authService } from '../services/auth.service';

interface AuthState {
  accessToken: string | null;

  refreshToken: string | null;

  user: User | null;

  isAuthenticated: boolean;

  isInitializing: boolean;

  login: (
    response: LoginResponse,
  ) => void;

  updateTokens: (
    accessToken: string,
    refreshToken: string,
    user?: User,
  ) => void;

  logout: () => void;

  restoreSession: () => Promise<void>;
}

/*
|--------------------------------------------------------------------------
| Session restoration lock
|--------------------------------------------------------------------------
|
| React StrictMode can execute effects twice in development.
|
| This prevents two simultaneous refresh requests.
|
*/

let restoreSessionPromise: Promise<void> | null = null;

export const useAuthStore =
  create<AuthState>((set) => ({
    /*
    |--------------------------------------------------------------------------
    | Initial state
    |--------------------------------------------------------------------------
    */

    accessToken:
      authStorage.getAccessToken(),

    refreshToken:
      authStorage.getRefreshToken(),

    user:
      authStorage.getUser(),

    isAuthenticated:
      !!authStorage.getAccessToken() &&
      !!authStorage.getRefreshToken(),

    isInitializing: true,

    /*
    |--------------------------------------------------------------------------
    | Login
    |--------------------------------------------------------------------------
    */

    login: (
      response: LoginResponse,
    ) => {
      const {
        accessToken,
        refreshToken,
        user,
      } = response;

      /*
       * Validate the backend response before
       * touching localStorage.
       */

      if (
        !accessToken ||
        !refreshToken ||
        !user
      ) {
        throw new Error(
          'Invalid authentication response.',
        );
      }

      authStorage.setAccessToken(
        accessToken,
      );

      authStorage.setRefreshToken(
        refreshToken,
      );

      authStorage.setUser(
        user,
      );

      set({
        accessToken,
        refreshToken,
        user,
        isAuthenticated: true,
      });
    },

    /*
    |--------------------------------------------------------------------------
    | Update tokens
    |--------------------------------------------------------------------------
    */

    updateTokens: (
      accessToken,
      refreshToken,
      user,
    ) => {
      if (
        !accessToken ||
        !refreshToken
      ) {
        throw new Error(
          'Invalid token response.',
        );
      }

      authStorage.setAccessToken(
        accessToken,
      );

      authStorage.setRefreshToken(
        refreshToken,
      );

      if (user) {
        authStorage.setUser(user);
      }

      set({
        accessToken,
        refreshToken,
        user:
          user ??
          useAuthStore.getState().user,
        isAuthenticated: true,
      });
    },

    /*
    |--------------------------------------------------------------------------
    | Logout
    |--------------------------------------------------------------------------
    */

    logout: () => {
      authStorage.clear();

      set({
        accessToken: null,
        refreshToken: null,
        user: null,
        isAuthenticated: false,
        isInitializing: false,
      });
    },

    /*
    |--------------------------------------------------------------------------
    | Restore session
    |--------------------------------------------------------------------------
    */

    restoreSession: async () => {
      /*
       * If restoration is already running,
       * wait for the same promise.
       *
       * This is important for React StrictMode.
       */

      if (restoreSessionPromise) {
        return restoreSessionPromise;
      }

      restoreSessionPromise =
        (async () => {
          const refreshToken =
            authStorage.getRefreshToken();

          /*
           * No refresh token.
           */

          if (!refreshToken) {
            set({
              accessToken:
                authStorage.getAccessToken(),

              refreshToken: null,

              user:
                authStorage.getUser(),

              isAuthenticated: false,

              isInitializing: false,
            });

            return;
          }

          try {
            console.log(
              '[AUTH] Restoring session...',
            );

            /*
             * IMPORTANT:
             *
             * authService.refresh() uses
             * authClient, which has NO auth
             * interceptor.
             */

            const response =
              await authService.refresh({
                refreshToken,
              });

            /*
             * Validate complete response.
             */

            if (
              !response?.accessToken ||
              !response?.refreshToken ||
              !response?.user
            ) {
              throw new Error(
                'Invalid refresh authentication response.',
              );
            }

            /*
             * Backend rotates BOTH tokens.
             */

            authStorage.setAccessToken(
              response.accessToken,
            );

            authStorage.setRefreshToken(
              response.refreshToken,
            );

            authStorage.setUser(
              response.user,
            );

            set({
              accessToken:
                response.accessToken,

              refreshToken:
                response.refreshToken,

              user:
                response.user,

              isAuthenticated: true,

              isInitializing: false,
            });

            console.log(
              '[AUTH] Session restored successfully.',
            );
          } catch (error) {
            console.error(
              '[AUTH] Session restoration failed.',
              error,
            );

            authStorage.clear();

            set({
              accessToken: null,

              refreshToken: null,

              user: null,

              isAuthenticated: false,

              isInitializing: false,
            });
          }
        })();

      try {
        await restoreSessionPromise;
      } finally {
        restoreSessionPromise = null;
      }
    },
  }));