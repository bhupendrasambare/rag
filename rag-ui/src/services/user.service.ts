import api from '../api/axios';

import appConfig from '../config/app.config';

import type {
  UpdateProfileRequest,
  UserProfileResponse,
} from '../types/auth';

export const userService = {

  async getProfile(): Promise<UserProfileResponse> {

    const response =
      await api.get(
        appConfig.endpoints.user.profile,
      );

    return response.data.data;
  },

  async updateProfile(
    request: UpdateProfileRequest,
  ): Promise<UserProfileResponse> {

    const response =
      await api.put(
        appConfig.endpoints.user.updateProfile,
        request,
      );

    return response.data.data;
  },
};