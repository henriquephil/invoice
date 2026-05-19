import { ErrorLine, FormGroup, Label } from "#/ui/form/form";
import { css } from "@emotion/react";
import styled from "@emotion/styled";
import { useEffect, useState } from "react";
import z, { ZodEmail, ZodNumber } from "zod";

export const InputBase = css`
  padding: 0.6rem;
  border: none;
  border-bottom: 1px solid var(--border-glass);
  background: rgba(0, 0, 0, 0.1);
  color: var(--text-primary);
  width: 100%;
  &:focus,
  &:hover {
    outline: none;
    border-color: var(--accent-primary);
  }
`;

const StyledInput = styled.input`
  ${InputBase}
`;

export type InputProps = {
  onChange?: (value: string) => void;
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange'>;

export function Input({ onChange, ...props }: InputProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange && onChange(e.target.value);
  };
  return <StyledInput onChange={handleChange} {...props} />;
}

export type FormInputProps = {
  label: string;
  error?: string;
  size?: number;
} & InputProps;

export function FormInput({ id, label, error, size, ...props }: FormInputProps) {
  const [touched, setTouched] = useState(false);

  const handleBlur = () => {
    setTouched(true);
    props.onBlur?.({} as React.FocusEvent<HTMLInputElement>);
  };

  return (
    <FormGroup size={size}>
      <Label htmlFor={id}>{label}</Label>
      <Input
        id={id}
        onBlur={handleBlur}
        {...props}
      />
      <ErrorLine>{touched && error}</ErrorLine>
    </FormGroup>
  )
}


// schema based


const inferInputType = (validator: z.ZodTypeAny): string => {
  if (validator instanceof ZodEmail) return 'email'
  if (validator instanceof ZodNumber) return 'number'
  return "text";
};

type SchemaFormInputProps<T extends z.ZodObject<any>> = {
  size?: number;
  label: string;
  formSchema: T;
  field: keyof z.infer<T>;
  formValue: z.infer<T>;
  onFormValueChange: (value: z.infer<T>) => void;
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, 'value' | 'onChange'>

export function SchemaFormInput<T extends z.ZodObject<any>>({
  label,
  formSchema,
  field,
  formValue,
  onFormValueChange,
  ...props
}: SchemaFormInputProps<T>) {
  const [error, setError] = useState<string | undefined>(undefined);

  const validator = formSchema.shape[field];
  const value = formValue[field];
  const inferredType = inferInputType(validator);

  const validate = (currentValue: any) => {
    const result = validator.safeParse(currentValue);
    if (result.success) {
      setError(undefined);
    } else {
      const errorMessage = result.error.flatten().formErrors[0];
      setError(errorMessage);
    }
  };

  useEffect(() => {
    validate(value);
  }, [value]);

  const handleBlur = () => {
    validate(value);
  };

  const handleChange = (newValue: string) => {
    onFormValueChange({ ...formValue, [field]: newValue });
  };

  return (
    <FormInput
      id={props.id || String(field)}
      label={label}
      value={value as any}
      onChange={handleChange}
      onBlur={handleBlur}
      type={props.type || inferredType}
      autoComplete={String(field)}
      error={error}
      {...props}
    />
  )
}