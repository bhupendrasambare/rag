export interface User {
  id: string;

  firstName: string;

  lastName: string;

  email: string;

  profileImage?: string | null;

  role: string;

  active?: boolean | null;
}

export interface LoginRequest {
  email: string;

  password: string;
}

export interface RegisterRequest {
  firstName: string;

  lastName: string;

  email: string;

  password: string;

  confirmPassword: string;
}

export interface LoginResponse {
  accessToken: string;

  refreshToken: string;

  expiresIn: number;

  tokenType: string;

  user: User;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;

  refreshToken: string;

  expiresIn: number;

  tokenType: string;

  user: User;
}

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  profileImage?: string | null;
  role: string;
  active?: boolean | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  user: User;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
  email: string;
  profileImage?: string | null;
}

export interface UserProfileResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  profileImage?: string | null;
  role: string;
  active?: boolean | null;
  createdAt: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}