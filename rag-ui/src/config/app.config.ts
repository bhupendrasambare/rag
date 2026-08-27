const appConfig = {
  apiBaseUrl:
    import.meta.env.VITE_API_BASE_URL ||
    'http://localhost:8080',

  apiTimeout: 30000,

  endpoints: {
    auth: {
      login: '/api/auth/login',
      register: '/api/auth/signup',
      refresh: '/api/auth/refresh',
      logout: '/api/auth/logout',
    },

    user: {
      profile: '/api/user/profile',
      updateProfile: '/api/user/profile',
    },

    document: {
      upload: '/api/document/upload',
      list: '/api/document',

      get: (id: string) =>
        `/api/document/${id}`,

      status: (id: string) =>
        `/api/document/${id}/status`,

      delete: (id: string) =>
        `/api/document/${id}`,
    },

    chat: {
      sessions: '/api/chat/session',
      messages: '/api/chat/message',
    },
  },

  auth: {
    accessTokenKey:
      'rag_access_token',

    refreshTokenKey:
      'rag_refresh_token',

    userKey:
      'rag_user',
  },
};

export default appConfig;