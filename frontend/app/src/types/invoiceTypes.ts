import z from "zod";
import { zBigDecimal, zLocalDate, zOffsetDateTime } from "#/libs/zod-utils";
import { ItemSchema } from "./itemTypes";
import { AccountAddressSchema, AccountBankingSchema, AccountSchema } from "./accountTypes";
import { CustomerSchema } from "./customerTypes";

export const InvoiceItemSchema = z.object({
  id: z.uuid(),
  item: ItemSchema,
  unitPrice: zBigDecimal,
  quantity: zBigDecimal,
  totalPrice: zBigDecimal,
  additionalInfo: z.string(),
});

export const InvoiceSchema = z.object({
  id: z.uuid(),
  status: z.enum(['DRAFT', 'ISSUED', 'DELETED']),
  createdAt: zOffsetDateTime,
  account: AccountSchema,
  address: AccountAddressSchema,
  billing: AccountBankingSchema,
  customer: CustomerSchema,
  number: z.number().int(),
  issuedAt: zOffsetDateTime,
  dueDate: zLocalDate,
  currency: z.string(),
  totalPrice: zBigDecimal,
  items: z.array(InvoiceItemSchema),
});

export const DraftInvoiceSchema = InvoiceSchema.extend({
  status: z.literal('DRAFT'),
  customer: CustomerSchema.nullish(),
  dueDate: zLocalDate.nullish(),
  number: z.number().int().nullish(),
  issuedAt: zOffsetDateTime.nullish(),
  currency: z.string().nullish(),
});

export type Invoice = z.infer<typeof InvoiceSchema>;
export type DraftInvoice = z.infer<typeof DraftInvoiceSchema>;
export type InvoiceItem = z.infer<typeof InvoiceItemSchema>;
