import z from "zod";

export const AddressSchema = z.object({
  street: z.string(),
  number: z.string(),
  complement: z.string(),
  neighborhood: z.string(),
  city: z.string(),
  state: z.string(),
  zipCode: z.string(),
  country: z.string(),
});

export const CustomerSchema = z.object({
  id: z.uuid(),
  name: z.string(),
  document: z.string(),
  email: z.email(),
  phone: z.string(),
  address: AddressSchema,
});

export type Customer = z.infer<typeof CustomerSchema>;
export type Address = z.infer<typeof AddressSchema>;
