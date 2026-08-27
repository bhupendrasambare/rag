import axios from 'axios';

import appConfig from '../config/app.config';

import type {
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  RegisterRequest,
} from '../types/auth';

const authClient = axios.create({
  baseURL: appConfig.apiBaseUrl,

  timeout: appConfig.apiTimeout,

  headers: {
    'Content-Type': 'application/json',
  },
});

export const authService = {
  /*
  |--------------------------------------------------------------------------
  | Login
  |--------------------------------------------------------------------------
  */

  async login(
    request: LoginRequest,
  ): Promise<LoginResponse> {
    const response =
      await authClient.post(
        appConfig.endpoints.auth.login,
        request,
      );

    const data =
      response.data?.data;

    if (
      !data?.accessToken ||
      !data?.refreshToken ||
      !data?.user
    ) {
      throw new Error(
        'Invalid authentication response.',
      );
    }

    return data as LoginResponse;
  },

  /*
  |--------------------------------------------------------------------------
  | Register
  |--------------------------------------------------------------------------
  */

  async register(
    request: RegisterRequest,
  ) {
    const response =
      await authClient.post(
        appConfig.endpoints.auth.register,
        request,
      );

    return response.data.data;
  },

  /*
  |--------------------------------------------------------------------------
  | Refresh
  |--------------------------------------------------------------------------
  |
  | IMPORTANT:
  |
  | Do NOT use the main `api` client here.
  |
  | This request must NOT go through the
  | 401 interceptor.
  |
  */

  async refresh(
    request: RefreshTokenRequest,
  ): Promise<LoginResponse> {
    if (
      !request.refreshToken
    ) {
      throw new Error(
        'Refresh token is missing.',
      );
    }

    const response =
      await authClient.post(
        appConfig.endpoints.auth.refresh,
        request,
      );

    const data =
      response.data?.data;

    if (
      !data?.accessToken ||
      !data?.refreshToken ||
      !data?.user
    ) {
      throw new Error(
        'Invalid refresh authentication response.',
      );
    }

    return data as LoginResponse;
  },

  /*
  |--------------------------------------------------------------------------
  | Logout
  |--------------------------------------------------------------------------
  */

  async logout(): Promise<void> {
    await authClient.post(
      appConfig.endpoints.auth.logout,
    );
  },
};