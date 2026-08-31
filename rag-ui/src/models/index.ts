import type { User } from '../types/auth';

export interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  user: import('../types/auth').User | null;
  initialized: boolean;
}