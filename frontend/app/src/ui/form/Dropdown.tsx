import { ErrorLine, FormGroup, Label } from "#/ui/form/form";
import { InputBase } from "#/ui/form/Input";
import styled from "@emotion/styled";
import * as Select from "@radix-ui/react-select";
import { ChevronDown, ChevronUp } from "lucide-react";
import { useState, type FocusEventHandler } from "react";
import type z from "zod";

const StyledSelectTrigger = styled(Select.Trigger)`
  ${InputBase}
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
`;

const StyledSelectContent = styled(Select.Content)`
  background: var(--bg-main);
  border: 1px solid var(--border-glass);
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  z-index: 10;
  width: 100%;
`;

const StyledSelectItem = styled(Select.Item)`
  padding: 0.6rem;
  color: var(--text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  outline: none;
  &[data-highlighted] {
    background: var(--accent-primary);
    color: #000;
  }
`;

export type DropdownProps = {
  id: string;
  options: Record<string, string>;
  value: string;
  onChange: (value: string) => void;
  onBlur?: FocusEventHandler<HTMLButtonElement> | undefined;
  required?: boolean;
  disabled?: boolean;
  placeholder?: string;
};

export function Dropdown({ id, options, value, onChange, onBlur, disabled, placeholder }: DropdownProps) {
  return (
    <Select.Root value={value} onValueChange={onChange} disabled={disabled}>
      <StyledSelectTrigger id={id} onBlur={onBlur}>
        <Select.Value placeholder={placeholder || "Select an option"} />
        <Select.Icon>
          <ChevronDown size={16} />
        </Select.Icon>
      </StyledSelectTrigger>
      <Select.Portal>
        <StyledSelectContent position="popper" sideOffset={5}>
          <Select.ScrollUpButton><ChevronUp size={16} /></Select.ScrollUpButton>
          <Select.Viewport>
            {Object.entries(options).map(([key, val]) => (
              <StyledSelectItem key={key} value={key}>
                <Select.ItemText>{val}</Select.ItemText>
              </StyledSelectItem>
            ))}
          </Select.Viewport>
          <Select.ScrollDownButton><ChevronDown size={16} /></Select.ScrollDownButton>
        </StyledSelectContent>
      </Select.Portal>
    </Select.Root>
  );
}



export type FormDropdownProps = {
  size?: number;
  label: string;
  error?: string;
} & DropdownProps;

export function FormDropdown({ id, label, error, size, ...props }: FormDropdownProps) {
  const [touched, setTouched] = useState(false);

  const handleBlur = () => {
    setTouched(true);
    props.onBlur?.({} as React.FocusEvent<HTMLButtonElement>);
  };

  return (
    <FormGroup size={size}>
      <Label htmlFor={id}>{label}</Label>
      <Dropdown id={id} onBlur={handleBlur} {...props} />
      <ErrorLine>{touched && error}</ErrorLine>
    </FormGroup>
  );
}

// schema

type SchemaFormDropdownProps<T extends z.ZodObject<any>, O extends string> = {
  label: string;
  formSchema: T;
  field: keyof z.infer<T>;
  formValue: z.infer<T>;
  onFormValueChange: (value: z.infer<T>) => void;
  options: Record<O, string>;
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
} & Omit<React.InputHTMLAttributes<HTMLSelectElement>, 'value' | 'onChange'>

export function SchemaFormDropdown<T extends z.ZodObject<any>, O extends string>({
  label,
  formSchema,
  field,
  formValue,
  onFormValueChange,
  options,
  placeholder,
  required,
  disabled,
  size
}: SchemaFormDropdownProps<T, O>) {
  const value = formValue[field];

  const handleChange = (newValue: string) => {
    onFormValueChange({ ...formValue, [field]: newValue as O });
  };

  return (
    <FormDropdown
      id={String(field)}
      label={label}
      value={String(value)}
      onChange={handleChange}
      options={options}
      required={required}
      disabled={disabled}
      placeholder={placeholder}
      size={size}
    />
  )
}
