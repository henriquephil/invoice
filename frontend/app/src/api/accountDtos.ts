import { z } from 'zod';
import { AccountSchema, AccountAddressSchema, BankAccountSchema, AccountBankingSchema } from '#/types/accountTypes';

export const AccountResponseSchema = AccountSchema;
export type AccountResponse = z.infer<typeof AccountResponseSchema>;

export const AccountAddressResponseSchema = AccountAddressSchema;
export type AccountAddressResponse = z.infer<typeof AccountAddressResponseSchema>;

export const BankAccountDtoSchema = BankAccountSchema;
export type BankAccountDto = z.infer<typeof BankAccountDtoSchema>;

export const AccountBankingResponseSchema = AccountBankingSchema;
export type AccountBankingResponse = z.infer<typeof AccountBankingResponseSchema>;

export const CreateAccountSchema = z.object({
  name: z.string().min(1, "Name is required"),
  document: z.string().min(1, "Document is required"),
  email: z.email("Invalid email address"),
  phone: z.string().min(1, "Phone is required"),
});
export type CreateAccountRequest = z.infer<typeof CreateAccountSchema>;

export const UpdateAccountSchema = CreateAccountSchema.partial();
export type UpdateAccountRequest = z.infer<typeof UpdateAccountSchema>;

export const UpdateAddressRequestSchema = AccountAddressSchema.partial();
export type UpdateAddressRequest = z.infer<typeof UpdateAddressRequestSchema>;

export const UpdateBankingRequestSchema = BankAccountSchema.partial().extend({
  beneficiaryName: z.string().optional(),
});
export type UpdateBankingRequest = z.infer<typeof UpdateBankingRequestSchema>;

export const UpdateIntermediaryRequestSchema = BankAccountSchema.partial();
export type UpdateIntermediaryRequest = z.infer<typeof UpdateIntermediaryRequestSchema>;
