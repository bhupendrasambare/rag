import axios, {
  AxiosError,
  type InternalAxiosRequestConfig,
} from 'axios';

import appConfig from '../config/app.config';
import { authStorage } from '../utils/authStorage';
import { useAuthStore } from '../store';

const api = axios.create({
  baseURL: appConfig.api.baseUrl,
  timeout: appConfig.api.timeout,

  headers: {
    'Content-Type': 'application/json',
  },
});

let isRefreshing = false;

let refreshSubscribers: Array<(token: string) => void> = [];

const subscribeTokenRefresh = (
  callback: (token: string) => void,
) => {
  refreshSubscribers.push(callback);
};

const notifyTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => {
    callback(token);
  });

  refreshSubscribers = [];
};

const refreshAccessToken = async (): Promise<string> => {
  const refreshToken = authStorage.getRefreshToken();

  if (!refreshToken) {
    throw new Error('Refresh token not available');
  }

  const response = await axios.post(
    `${appConfig.api.baseUrl}${appConfig.api.endpoints.refresh}`,
    {
      refreshToken,
    },
  );

  const data = response.data?.data;

  if (!data?.accessToken) {
    throw new Error('Invalid refresh response');
  }

  const newAccessToken = data.accessToken;

  authStorage.setAccessToken(newAccessToken);

  useAuthStore.getState().setAccessToken(newAccessToken);

  if (data.refreshToken) {
    authStorage.setRefreshToken(data.refreshToken);

    useAuthStore
      .getState()
      .setRefreshToken(data.refreshToken);
  }

  return newAccessToken;
};

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = authStorage.getAccessToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },

  (error) => Promise.reject(error),
);

api.interceptors.response.use(
  (response) => response,

  async (error: AxiosError) => {
    const originalRequest =
      error.config as InternalAxiosRequestConfig & {
        _retry?: boolean;
      };

    if (
      error.response?.status !== 401 ||
      originalRequest?._retry ||
      originalRequest?.url?.includes(
        appConfig.api.endpoints.login,
      ) ||
      originalRequest?.url?.includes(
        appConfig.api.endpoints.refresh,
      )
    ) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    if (isRefreshing) {
      return new Promise((resolve) => {
        subscribeTokenRefresh((token) => {
          originalRequest.headers.Authorization =
            `Bearer ${token}`;

          resolve(api(originalRequest));
        });
      });
    }

    isRefreshing = true;

    try {
      const newToken = await refreshAccessToken();

      notifyTokenRefreshed(newToken);

      originalRequest.headers.Authorization =
        `Bearer ${newToken}`;

      return api(originalRequest);

    } catch (refreshError) {

      useAuthStore.getState().logout();

      window.location.href = '/login';

      return Promise.reject(refreshError);

    } finally {
      isRefreshing = false;
    }
  },
);

export default api;