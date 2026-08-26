import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import type { User } from '../types/auth';
import type { ApiResponse } from '../types/response';


export const userService = {

  async getProfile(): Promise<User> {

    const response = await api.get<ApiResponse<User>>(
      API_ENDPOINTS.USER.PROFILE,
    );

    if (!response.data.success || !response.data.data) {
      throw new Error(
        response.data.message ||
        'Unable to fetch profile',
      );
    }

    return response.data.data;
  },
};