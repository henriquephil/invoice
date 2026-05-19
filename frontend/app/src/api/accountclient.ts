import { z } from 'zod';
import { 
  AccountResponseSchema,
  CreateAccountSchema,
  UpdateAccountSchema,
  AccountAddressResponseSchema,
  UpdateAddressRequestSchema,
  AccountBankingResponseSchema,
  UpdateBankingRequestSchema,
  BankAccountDtoSchema,
  UpdateIntermediaryRequestSchema,
  type AccountResponse,
  type CreateAccountRequest,
  type UpdateAccountRequest,
  type AccountAddressResponse,
  type UpdateAddressRequest,
  type AccountBankingResponse,
  type UpdateBankingRequest,
  type BankAccountDto,
  type UpdateIntermediaryRequest,
} from "#/api/accountDtos";
import apiClient from "#/api/httpClient";

export const createAccount = async (accountData: CreateAccountRequest): Promise<AccountResponse> => {
  const validated = CreateAccountSchema.parse(accountData);
  const response = await apiClient.post('/account', validated);
  return AccountResponseSchema.parse(response.data);
};

export const listAccounts = async (): Promise<AccountResponse[]> => {
  const response = await apiClient.get('/account');
  return z.array(AccountResponseSchema).parse(response.data);
};

export const getAccount = async (accountId: string): Promise<AccountResponse> => {
  const response = await apiClient.get(`/account/${accountId}`);
  return AccountResponseSchema.parse(response.data);
};

export const updateAccount = async (accountId: string, accountData: UpdateAccountRequest): Promise<AccountResponse> => {
  const validated = UpdateAccountSchema.parse(accountData);
  const response = await apiClient.patch(`/account/${accountId}`, validated);
  return AccountResponseSchema.parse(response.data);
};

export const getAddress = async (accountId: string): Promise<AccountAddressResponse> => {
  const response = await apiClient.get(`/account/${accountId}/address`);
  return AccountAddressResponseSchema.parse(response.data);
};

export const updateAddress = async (accountId: string, addressData: UpdateAddressRequest): Promise<AccountAddressResponse> => {
  const validated = UpdateAddressRequestSchema.parse(addressData);
  const response = await apiClient.patch(`/account/${accountId}/address`, validated);
  return AccountAddressResponseSchema.parse(response.data);
};

export const getBanking = async (accountId: string): Promise<AccountBankingResponse> => {
  const response = await apiClient.get(`/account/${accountId}/banking`);
  return AccountBankingResponseSchema.parse(response.data);
};

export const updateBanking = async (accountId: string, bankingData: UpdateBankingRequest): Promise<AccountBankingResponse> => {
  const validated = UpdateBankingRequestSchema.parse(bankingData);
  const response = await apiClient.patch(`/account/${accountId}/banking`, validated);
  return AccountBankingResponseSchema.parse(response.data);
};

export const updateIntermediary = async (accountId: string, intermediaryData: UpdateIntermediaryRequest): Promise<BankAccountDto> => {
  const validated = UpdateIntermediaryRequestSchema.parse(intermediaryData);
  const response = await apiClient.patch(`/account/${accountId}/banking/intermediary`, validated);
  return BankAccountDtoSchema.parse(response.data);
};

export const deleteIntermediary = async (accountId: string): Promise<void> => {
  await apiClient.delete(`/account/${accountId}/banking/intermediary`);
};

