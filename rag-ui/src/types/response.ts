export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  errorCode?: string;
  errors?: Record<string, string>;
}

export interface UserResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  profileImage?: string | null;
  role: string;
  active: boolean;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: UserResponse;
}

export interface RegisterResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}