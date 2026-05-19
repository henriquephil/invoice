import apiClient from "#/api/httpClient";
import { UserLoginRequestSchema, UserRegisterRequestSchema, type UserLoginRequest, type UserRegisterRequest } from "./authDtos";

export const login = async (loginData: UserLoginRequest): Promise<void> => {
  const validated = UserLoginRequestSchema.parse(loginData);
  await apiClient.post('/login', validated);
};

export const logout = async (): Promise<void> => {
  await apiClient.post('/logout');
};

export const register = async (registerData: UserRegisterRequest): Promise<void> => {
  const validated = UserRegisterRequestSchema.parse(registerData);
  await apiClient.post('/register', validated);
};
