// src/api/endpoints.ts

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/api/auth/login',
    REFRESH: '/api/auth/refresh',
    LOGOUT: '/api/auth/logout',
    SIGNUP: '/api/auth/signup',
    CHANGE_PASSWORD: '/api/auth/change-password',
  },

  USER: {
    PROFILE: '/api/user/profile',
  },

  DOCUMENT: {
    BASE: '/api/document',
    LIST: '/api/document/fetch',
    UPLOAD: '/api/document',
    BY_ID: (id: string) =>
      `/api/document/${id}`,
    STATUS: (id: string) =>
      `/api/document/${id}/status`,
    DOWNLOAD: (id: string) =>
      `/api/document/${id}/download`,
    DELETE: (id: string) =>
      `/api/document/${id}`,
  },

  CHAT: {
    BASE: '/api/chat',
  },
} as const;

export default API_ENDPOINTS;