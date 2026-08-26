import appConfig from '../config/app.config';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: appConfig.api.endpoints.login,
    REFRESH: appConfig.api.endpoints.refresh,
    LOGOUT: appConfig.api.endpoints.logout,
  },

  USER: {
    PROFILE: appConfig.api.endpoints.profile,
  },

  DOCUMENT: {
    BASE: '/api/document',
    LIST: '/api/document',
    UPLOAD: '/api/document/upload',
  },

  CHAT: {
    BASE: '/api/chat',
  },
};