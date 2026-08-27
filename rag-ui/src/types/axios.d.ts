import 'axios';

declare module 'axios' {

  export interface AxiosRequestConfig {
    skipAuth?: boolean;
    skipRefresh?: boolean;
    _retry?: boolean;
  }

  export interface InternalAxiosRequestConfig {
    skipAuth?: boolean;
    skipRefresh?: boolean;
    _retry?: boolean;
  }
}