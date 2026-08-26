import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

import { type LoginRequest, type LoginResponse } from '../types/auth';
import type { ApiResponse } from '../types/response';

export const authService = {

  async login(
    request: LoginRequest,
  ): Promise<LoginResponse> {

    const response = await api.post<
      ApiResponse<LoginResponse>
    >(
      API_ENDPOINTS.AUTH.LOGIN,
      request,
    );

    if (!response.data.success || !response.data.data) {
      throw new Error(
        response.data.message || 'Login failed',
      );
    }

    return response.data.data;
  },

  async logout(): Promise<void> {

    try {
      await api.post(
        API_ENDPOINTS.AUTH.LOGOUT,
      );
    } finally {
      // Local state is cleared by caller.
    }
  },
};