import axios, {
  type AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
} from 'axios';

import appConfig from '../config/app.config';

import { useAuthStore } from '../store';

interface RetryRequestConfig
  extends AxiosRequestConfig {
  _retry?: boolean;
}

/*
|--------------------------------------------------------------------------
| Main API client
|--------------------------------------------------------------------------
*/

const api: AxiosInstance =
  axios.create({
    baseURL:
      appConfig.apiBaseUrl,

    timeout:
      appConfig.apiTimeout,

    headers: {
      'Content-Type':
        'application/json',
    },
  });

/*
|--------------------------------------------------------------------------
| Refresh client
|--------------------------------------------------------------------------
|
| This client MUST NOT have the
| authentication interceptor.
|
*/

const refreshClient =
  axios.create({
    baseURL:
      appConfig.apiBaseUrl,

    timeout:
      appConfig.apiTimeout,

    headers: {
      'Content-Type':
        'application/json',
    },
  });

/*
|--------------------------------------------------------------------------
| Add access token
|--------------------------------------------------------------------------
*/

api.interceptors.request.use(
  (config) => {
    const accessToken =
      useAuthStore
        .getState()
        .accessToken;

    if (accessToken) {
      config.headers =
        config.headers ?? {};

      config.headers.Authorization =
        `Bearer ${accessToken}`;
    }

    return config;
  },

  (error) =>
    Promise.reject(error),
);

/*
|--------------------------------------------------------------------------
| Refresh lock
|--------------------------------------------------------------------------
*/

let refreshPromise:
  Promise<string> | null = null;

/*
|--------------------------------------------------------------------------
| Refresh access token
|--------------------------------------------------------------------------
*/

const refreshAccessToken =
  async (): Promise<string> => {
    const state =
      useAuthStore.getState();

    const refreshToken =
      state.refreshToken;

    if (!refreshToken) {
      throw new Error(
        'Refresh token not available.',
      );
    }

    console.log(
      '[AUTH] Starting token refresh...',
    );

    const response =
      await refreshClient.post(
        appConfig
          .endpoints
          .auth
          .refresh,

        {
          refreshToken,
        },
      );

    const data =
      response.data?.data;

    if (
      !data?.accessToken ||
      !data?.refreshToken
    ) {
      throw new Error(
        'Invalid refresh response.',
      );
    }

    /*
     * Save BOTH rotated tokens.
     */

    useAuthStore
      .getState()
      .updateTokens(
        data.accessToken,
        data.refreshToken,
        data.user,
      );

    console.log(
      '[AUTH] Token refresh successful.',
    );

    return data.accessToken;
  };

/*
|--------------------------------------------------------------------------
| Response interceptor
|--------------------------------------------------------------------------
*/

api.interceptors.response.use(
  (response) =>
    response,

  async (
    error: AxiosError,
  ) => {
    const originalRequest =
      error.config as RetryRequestConfig;

    /*
     * Only process 401.
     */

    if (
      error.response?.status !== 401 ||
      !originalRequest
    ) {
      return Promise.reject(error);
    }

    /*
     * Prevent infinite retry.
     */

    if (
      originalRequest._retry
    ) {
      return Promise.reject(error);
    }

    originalRequest._retry =
      true;

    const refreshToken =
      useAuthStore
        .getState()
        .refreshToken;

    if (!refreshToken) {
      useAuthStore
        .getState()
        .logout();

      return Promise.reject(error);
    }

    /*
     * If another request is already
     * refreshing, wait for it.
     */

    if (!refreshPromise) {
      refreshPromise =
        refreshAccessToken().finally(
          () => {
            refreshPromise = null;
          },
        );
    }

    try {
      const newAccessToken =
        await refreshPromise;

      originalRequest.headers =
        originalRequest.headers ?? {};

      originalRequest.headers.Authorization =
        `Bearer ${newAccessToken}`;

      return api(
        originalRequest,
      );
    } catch (
      refreshError
    ) {
      useAuthStore
        .getState()
        .logout();

      return Promise.reject(
        refreshError,
      );
    }
  },
);

export default api;