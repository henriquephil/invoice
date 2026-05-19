import { z } from 'zod';
import { CustomerResponseSchema, ItemResponseSchema } from "#/api/catalogDtos";
import { AccountResponseSchema } from "#/api/accountDtos";
import { zBigDecimal, zLocalDate, zOffsetDateTime } from '#/libs/zod-utils';

export const AccountAddressResponseSchema = z.object({
  street: z.string(),
  number: z.string(),
  complement: z.string(),
  neighborhood: z.string(),
  city: z.string(),
  state: z.string(),
  zipCode: z.string(),
  country: z.string(),
});
export type AccountAddressResponse = z.infer<typeof AccountAddressResponseSchema>;

export const BankAccountDtoSchema = z.object({
  accountNumber: z.string(),
  swiftCode: z.string(),
  bankName: z.string(),
  bankAddress: z.string(),
});
export type BankAccountDto = z.infer<typeof BankAccountDtoSchema>;

export const AccountBankingResponseSchema = z.object({
  beneficiaryName: z.string(),
  beneficiaryAccount: BankAccountDtoSchema,
  intermediaryAccount: BankAccountDtoSchema.nullable(),
});
export type AccountBankingResponse = z.infer<typeof AccountBankingResponseSchema>;

export const InvoiceStatusSchema = z.enum(['DRAFT', 'ISSUED', 'DELETED']);
export type InvoiceStatus = z.infer<typeof InvoiceStatusSchema>;

// Discriminated union for InvoiceResponse
export const DraftInvoiceResponseItemSchema = z.object({
  id: z.uuid(),
  itemId: z.uuid(),
  unitPrice: zBigDecimal,
  quantity: zBigDecimal,
  totalPrice: zBigDecimal,
  additionalInfo: z.string(),
});
export type DraftInvoiceResponseItem = z.infer<typeof DraftInvoiceResponseItemSchema>;

export const DraftInvoiceResponseSchema = z.object({
  id: z.uuid(),
  status: z.literal('DRAFT'),
  createdAt: zOffsetDateTime,
  customerId: z.uuid().nullish(),
  dueDate: zLocalDate.nullish(),
  currency: z.string().nullish(),
  totalPrice: zBigDecimal,
  items: z.array(DraftInvoiceResponseItemSchema),
});
export type DraftInvoiceResponse = z.infer<typeof DraftInvoiceResponseSchema>;

export const IssuedInvoiceResponseItemSchema = z.object({
  id: z.uuid(),
  item: ItemResponseSchema,
  unitPrice: zBigDecimal,
  quantity: zBigDecimal,
  totalPrice: zBigDecimal,
  additionalInfo: z.string(),
});
export type IssuedInvoiceResponseItem = z.infer<typeof IssuedInvoiceResponseItemSchema>;

export const IssuedInvoiceResponseSchema = z.object({
  id: z.uuid(),
  status: z.enum(['ISSUED', 'DELETED']),
  createdAt: zOffsetDateTime,
  account: AccountResponseSchema,
  address: AccountAddressResponseSchema,
  billing: AccountBankingResponseSchema,
  customer: CustomerResponseSchema,
  number: z.number().int(),
  issuedAt: zOffsetDateTime,
  dueDate: zLocalDate,
  currency: z.string(),
  totalPrice: zBigDecimal,
  items: z.array(IssuedInvoiceResponseItemSchema),
});
export type IssuedInvoiceResponse = z.infer<typeof IssuedInvoiceResponseSchema>;

export const InvoiceResponseSchema = z.discriminatedUnion('status', [
  DraftInvoiceResponseSchema,
  IssuedInvoiceResponseSchema,
]);
export type InvoiceResponse = z.infer<typeof InvoiceResponseSchema>;

export const InvoiceHeadResponseSchema = z.object({
  id: z.uuid(),
  status: InvoiceStatusSchema,
  number: z.number().nullable(),
  customerName: z.string().nullable(),
  createdAt: zOffsetDateTime,
  issuedAt: zOffsetDateTime.nullable(),
  dueDate: zLocalDate.nullable(),
  totalPrice: zBigDecimal,
});
export type InvoiceHeadResponse = z.infer<typeof InvoiceHeadResponseSchema>;


export const UpdateInvoiceSchema = z.object({
  customerId: z.uuid().optional(),
  dueDate: zLocalDate.optional(),
  currency: z.string().optional(),
});
export type UpdateInvoiceRequest = z.infer<typeof UpdateInvoiceSchema>;

export const CreateInvoiceItemSchema = z.object({
  itemId: z.uuid(),
  unitPrice: zBigDecimal,
  quantity: zBigDecimal,
  additionalInfo: z.string(),
});
export type CreateInvoiceItemRequest = z.infer<typeof CreateInvoiceItemSchema>;

export const UpdateInvoiceItemSchema = CreateInvoiceItemSchema.partial();
export type UpdateInvoiceItemRequest = z.infer<typeof UpdateInvoiceItemSchema>;

export const InvoiceSettingsResponseSchema = z.object({
  currentInvoiceNumber: z.number().int(),
});
export type InvoiceSettingsResponse = z.infer<typeof InvoiceSettingsResponseSchema>;

export const InvoiceSettingsSchema = z.object({
  currentInvoiceNumber: z.number().int().optional(),
});
export type InvoiceSettingsRequest = z.infer<typeof InvoiceSettingsSchema>;
