const appConfig = {
  api: {
    baseUrl: 'http://localhost:8080',
    timeout: 30000,

    endpoints: {
      login: '/api/auth/login',
      refresh: '/api/auth/refresh',
      logout: '/api/auth/logout',
      profile: '/api/user/profile',
    },
  },

  auth: {
    accessTokenKey: 'rag_access_token',
    refreshTokenKey: 'rag_refresh_token',
    userKey: 'rag_user',
  },
};

export default appConfig;