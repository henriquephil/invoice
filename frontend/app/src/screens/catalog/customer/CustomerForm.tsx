import z from "zod";
import { useEffect } from "react";
import { Divisor, Formosa, FormContainer } from "#/ui/form/form";
import { SchemaFormInput } from "#/ui/form/Input";

type CustomerFormProps = {
  value: CustomerFormData;
  onChange: (value: CustomerFormData) => void;
  onValidation?: (isValid: boolean) => void;
}

export function CustomerForm({ value, onChange, onValidation }: CustomerFormProps) {
  useEffect(() => {
    onValidation?.(CustomerFormDataSchema.safeParse(value).success);
  }, [value, onValidation]);

  return (
    <FormContainer width="500px">
      <Formosa>
        <SchemaFormInput
          size={8}
          label="Name"
          formSchema={CustomerFormDataSchema}
          field="name"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={4}
          label="Document"
          formSchema={CustomerFormDataSchema}
          field="document"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={8}
          label="Email"
          formSchema={CustomerFormDataSchema}
          field="email"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={4}
          label="Phone"
          formSchema={CustomerFormDataSchema}
          field="phone"
          formValue={value}
          onFormValueChange={onChange}
        />
        <Divisor>Address</Divisor>
        <SchemaFormInput
          size={10}
          label="Street"
          formSchema={CustomerFormDataSchema}
          field="street"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={2}
          label="Number"
          formSchema={CustomerFormDataSchema}
          field="number"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={6}
          label="Complement"
          formSchema={CustomerFormDataSchema}
          field="complement"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={6}
          label="Neighborhood"
          formSchema={CustomerFormDataSchema}
          field="neighborhood"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={3}
          label="State"
          formSchema={CustomerFormDataSchema}
          field="state"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={9}
          label="City"
          formSchema={CustomerFormDataSchema}
          field="city"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={3}
          label="Zip Code"
          formSchema={CustomerFormDataSchema}
          field="zipCode"
          formValue={value}
          onFormValueChange={onChange}
        />
        <SchemaFormInput
          size={9}
          label="Country"
          formSchema={CustomerFormDataSchema}
          field="country"
          formValue={value}
          onFormValueChange={onChange}
        />
      </Formosa>
    </FormContainer>
  );
}


export const CustomerFormDataSchema = z.object({
  name: z.string().min(1, "Name is required"),
  document: z.string().min(1, "Document is required"),
  email: z.email("Invalid email address"),
  phone: z.string().min(1, "Phone is required"),
  street: z.string().min(1, "Street is required"),
  number: z.string().min(1, "Number is required"),
  complement: z.string(),
  neighborhood: z.string().min(1, "Neighborhood is required"),
  city: z.string().min(1, "City is required"),
  state: z.string().min(1, "State is required"),
  zipCode: z.string().min(1, "Zip Code is required"),
  country: z.string().min(1, "Country is required"),
});
export type CustomerFormData = z.infer<typeof CustomerFormDataSchema>;
