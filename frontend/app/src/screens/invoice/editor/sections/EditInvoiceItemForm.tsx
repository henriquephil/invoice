import styled from "@emotion/styled";
import { useState } from "react";

import { ActionButton, SubtleLabel } from "#/ui/layout";
import type { UpdateInvoiceItemRequest } from "../../../../api/invoiceDtos";
import type { InvoiceItem } from "#/types/invoiceTypes";
import { Form } from "#/ui/form/form";
import { FormInput } from "#/ui/form/Input";

const ItemContainer = styled.div`
    margin-top: 1rem;
    padding: 1rem;
    background: rgba(0,0,0,0.1);
    border-radius: 8px;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
`;

type EditInvoiceItemFormProps = {
  value: InvoiceItem;
  save: (value: UpdateInvoiceItemRequest) => void;
  onCancel: () => void;
};

export function EditInvoiceItemForm({ value, save, onCancel }: EditInvoiceItemFormProps) {
  const [invoiceItem, setInvoiceItem] = useState<UpdateInvoiceItemRequest>({
    unitPrice: value.unitPrice,
    quantity: value.quantity,
    additionalInfo: value.additionalInfo
  });
  
  const handleChange = (field: keyof UpdateInvoiceItemRequest, fieldValue: any) => {
    setInvoiceItem(prev => ({ ...prev, [field]: fieldValue }));
  }

  return (
    <ItemContainer>
      <SubtleLabel>Edit Item: {value.item.name}</SubtleLabel>
      <Form>
        <FormInput id="quantity" label="Quantity" type="number" value={invoiceItem.quantity?.toString()} onChange={(val) => handleChange('quantity', Number(val) || 0)} />
        <FormInput id="unitPrice" label="Unit Price" type="number" value={invoiceItem.unitPrice?.toString()} onChange={(val) => handleChange('unitPrice', Number(val) || 0)} />
        <FormInput id="additionalInfo" label="Additional Info" value={invoiceItem.additionalInfo} onChange={(val) => handleChange('additionalInfo', val)} />
        
        <ButtonContainer>
          <ActionButton onClick={onCancel}>Cancel</ActionButton>
          <ActionButton onClick={() => save(invoiceItem)}>Update Item</ActionButton>
        </ButtonContainer>
      </Form>
    </ItemContainer>
  )
}
