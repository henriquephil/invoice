import { useCustomersSuspenseQuery } from "#/queries/customerQuery";
import type { Customer } from "#/types/customerTypes";
import { FormDropdown } from "#/ui/form/Dropdown";

type CustomersDropdownProps = {
  value?: Customer | null;
  onChange: (customerId: string) => void;
  disabled: boolean;
};

export function CustomersDropdown({ value, onChange, disabled }: CustomersDropdownProps) {
  const { data: customers } = useCustomersSuspenseQuery();
  const customersOptions = (customers || []).reduce((acc, customer) => {
    acc[customer.id] = customer.name;
    return acc;
  }, {} as Record<string, string>);

  return (
    <FormDropdown
      size={8}
      id="customer"
      label="Bill to"
      options={customersOptions}
      value={value?.id || ""}
      onChange={onChange}
      disabled={disabled}
      required
    />
  );
}
