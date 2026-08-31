import axios from 'axios';

import api from '../api/axios';

import appConfig from '../config/app.config';

import type {
  ChangePasswordRequest,
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

  async login(
    request: LoginRequest,
  ): Promise<LoginResponse> {

    const response =
      await authClient.post(
        appConfig.endpoints.auth.login,
        request,
      );

    return response.data.data;
  },

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

  async refresh(
    request: RefreshTokenRequest,
  ): Promise<LoginResponse> {

    const response =
      await authClient.post(
        appConfig.endpoints.auth.refresh,
        request,
      );

    return response.data.data;
  },

  async changePassword(
    request: ChangePasswordRequest,
  ): Promise<void> {

    await api.post(
      appConfig.endpoints.auth.changePassword,
      request,
    );
  },

  async logout(): Promise<void> {

    await authClient.post(
      appConfig.endpoints.auth.logout,
    );
  },
};