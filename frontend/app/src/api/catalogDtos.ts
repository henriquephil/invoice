import { z } from 'zod';
import { zBigDecimal } from '#/libs/zod-utils';
import { AddressSchema, CustomerSchema } from '#/types/customerTypes';
import { ItemSchema, ItemType } from '#/types/itemTypes';

export const CustomerResponseSchema = CustomerSchema;
export type CustomerResponse = z.infer<typeof CustomerResponseSchema>;

export const CreateCustomerSchema = z.object({
  name: z.string().min(1, "Name is required"),
  document: z.string().min(1, "Document is required"),
  email: z.email("Invalid email address"),
  phone: z.string().min(1, "Phone is required"),
  address: AddressSchema,
});
export type CreateCustomerRequest = z.infer<typeof CreateCustomerSchema>;

export const UpdateCustomerSchema = CreateCustomerSchema.extend({
  address: AddressSchema.partial(),
}).partial();
export type UpdateCustomerRequest = z.infer<typeof UpdateCustomerSchema>;

export const ItemResponseSchema = ItemSchema;
export type ItemResponse = z.infer<typeof ItemResponseSchema>;

export const CreateItemSchema = z.object({
  name: z.string().min(1, "Name is required"),
  type: z.enum(ItemType),
  measureUnit: z.string().min(1, "Measure unit is required"),
  unitPrice: zBigDecimal,
  currency: z.string().min(1, "Currency is required"),
});
export type CreateItemRequest = z.infer<typeof CreateItemSchema>;

export const UpdateItemSchema = CreateItemSchema.partial();
export type UpdateItemRequest = z.infer<typeof UpdateItemSchema>;