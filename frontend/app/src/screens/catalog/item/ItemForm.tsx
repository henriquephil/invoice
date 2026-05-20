import z from 'zod';
import { zBigDecimal } from '#/libs/zod-utils';
import { useEffect } from 'react';
import { ItemType, ItemTypeLabels } from '#/types/itemTypes';
import { Formosa, FormContainer } from '#/ui/form/form';
import { SchemaFormInput } from '#/ui/form/Input';
import { SchemaFormDropdown } from '#/ui/form/Dropdown';
import { currenciesEnum } from '#/libs/currency';

interface ItemFormProps {
  value: ItemFormData;
  onChange: (value: ItemFormData) => void;
  onValidation?: (isValid: boolean) => void;
}

export function ItemForm({ value, onChange, onValidation }: ItemFormProps) {
  useEffect(() => {
    onValidation?.(ItemFormDataSchema.safeParse(value).success);
  }, [value, onValidation]);

  return (
    <FormContainer width="500px">
      <Formosa>
        <SchemaFormDropdown
          size={3}
          label="Type"
          options={ItemTypeLabels}
          formSchema={ItemFormDataSchema}
          field="type"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={9}
          label="Name"
          formSchema={ItemFormDataSchema}
          field="name"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={4}
          label="Unit Price"
          formSchema={ItemFormDataSchema}
          field="unitPrice"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={4}
          label="Unit"
          formSchema={ItemFormDataSchema}
          field="measureUnit"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={4}
          label="Currency"
          formSchema={ItemFormDataSchema}
          field="currency"
          formValue={value}
          onFormValueChange={onChange}
        />
      </Formosa>
    </FormContainer>
  )
}

export const ItemFormDataSchema = z.object({
  name: z.string().min(1, "Name is required"),
  type: z.enum(ItemType),
  measureUnit: z.string().min(1, "Measure unit is required"),
  unitPrice: zBigDecimal,
  currency: z.enum(currenciesEnum),
});
export type ItemFormData = z.infer<typeof ItemFormDataSchema>;