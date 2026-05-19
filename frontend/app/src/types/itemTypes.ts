import z from "zod";
import { zBigDecimal } from "#/libs/zod-utils";
import { Item } from "@radix-ui/react-select";

export enum ItemType {
  SERVICE = 'SERVICE',
  PRODUCT = 'PRODUCT',
}

export const ItemSchema = z.object({
  id: z.uuid(),
  type: z.enum(ItemType),
  name: z.string(),
  measureUnit: z.string(),
  unitPrice: zBigDecimal,
  currency: z.string(),
});

export type Item = z.infer<typeof ItemSchema>;

export const ItemTypeLabels: Record<ItemType, string> = {
  [ItemType.SERVICE]: 'Service',
  [ItemType.PRODUCT]: 'Product',
}
