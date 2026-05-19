import styled from "@emotion/styled";
import * as Popover from "@radix-ui/react-popover";
import { DayPicker } from "react-day-picker";
import { DateTimeFormatter, LocalDate } from "@js-joda/core";
import { Calendar as CalendarIcon } from "lucide-react";
import { ErrorLine, FormGroup, Label } from "#/ui/form/form";
import { Input, type InputProps } from "#/ui/form/Input";
import { useState } from "react";

const DatePickerInputWrapper = styled.div`
  position: relative;
  display: flex;
  align-items: center;
`

const StyledDatePickerTrigger = styled(Popover.Trigger)`
  position: absolute;
  right: 1rem;
  cursor: pointer;
  color: var(--text-muted);
  background: none;
  border: none;

  &:hover {
    color: var(--text-primary);
  }
`;

const StyledDatePickerContent = styled(Popover.Content)`
  background: var(--bg-main);
  border: 1px solid var(--border-glass);
  padding: 1rem;
  box-shadow: 0 10px 30px rgba(0,0,0,0.5);
  z-index: 10;
  width: 100%;
`;

export type DatePickerProps = {
  id: string;
  value: LocalDate | null | undefined;
  onChange: (value: LocalDate) => void;
  required?: boolean;
  disabled?: boolean;
} & Omit<InputProps, 'value' | 'onChange'>;

export function DatePicker({ id, value, onChange, required, disabled }: DatePickerProps) {
    const formatter = DateTimeFormatter.ofPattern('MM/dd/yyyy');

  return (
    <Popover.Root>
      <DatePickerInputWrapper>
        <Input
          id={id}
          type="text"
          value={value ? value.format(formatter) : ''}
          readOnly
          disabled={disabled}
          required={required}
        />
        <StyledDatePickerTrigger disabled={disabled}>
          <CalendarIcon size={16} />
        </StyledDatePickerTrigger>
      </DatePickerInputWrapper>
      <Popover.Portal>
        <StyledDatePickerContent>
          <DayPicker
            mode="single"
            selected={value ? new Date(value.year(), value.monthValue() - 1, value.dayOfMonth()) : undefined}
            onSelect={(day) => day && onChange(LocalDate.of(day.getFullYear(), day.getMonth() + 1, day.getDate()))}
          />
        </StyledDatePickerContent>
      </Popover.Portal>
    </Popover.Root>
  );
}

export type FormDatePickerProps = {
  label: string;
  error?: string;
} & DatePickerProps;

export function FormDatePicker({ id, label, error, ...props }: FormDatePickerProps) {
  const [touched, setTouched] = useState(false);

  const handleBlur = () => {
    setTouched(true);
    props.onBlur?.({} as React.FocusEvent<HTMLInputElement>);
  };

  return (
    <FormGroup>
      <Label htmlFor={id}>{label}</Label>
      <DatePicker id={id} onBlur={handleBlur} {...props}/>
      <ErrorLine>{touched && error}</ErrorLine>
    </FormGroup>
  );
}