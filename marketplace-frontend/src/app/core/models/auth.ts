export type UserRole = 'BUYER' | 'SELLER' | 'ADMIN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone: string;
  role: UserRole;
  store?: StoreInfo;
}

export interface StoreInfo {
  storeName: string;
  description?: string;
  nit?: string;
  logoUrl?: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  email: string;
  fullName: string;
  role: UserRole;
}