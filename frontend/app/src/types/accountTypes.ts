import z from "zod";

export const AccountSchema = z.object({
  id: z.uuid(),
  name: z.string(),
  document: z.string(),
  email: z.string(),
  phone: z.string(),
});

export const AccountAddressSchema = z.object({
  street: z.string(),
  number: z.string(),
  complement: z.string(),
  neighborhood: z.string(),
  city: z.string(),
  state: z.string(),
  zipCode: z.string(),
  country: z.string(),
});

export const BankAccountSchema = z.object({
  accountNumber: z.string(),
  swiftCode: z.string(),
  bankName: z.string(),
  bankAddress: z.string(),
});

export const AccountBankingSchema = z.object({
  beneficiaryName: z.string(),
  beneficiaryAccount: BankAccountSchema,
  intermediaryAccount: BankAccountSchema.nullable(),
});

export type Account = z.infer<typeof AccountSchema>;
export type AccountAddress = z.infer<typeof AccountAddressSchema>;
export type BankAccount = z.infer<typeof BankAccountSchema>;
export type AccountBanking = z.infer<typeof AccountBankingSchema>;
