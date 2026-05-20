import styled from "@emotion/styled";
import { useState } from "react";

import { ActionButton, SubtleLabel } from "#/ui/layout";
import type { CreateInvoiceItemRequest } from "../../../../api/invoiceDtos";
import { useItemsQuery } from "#/queries/itemQuery";
import { Formosa } from "#/ui/form/form";
import { FormDropdown } from "#/ui/form/Dropdown";
import { FormInput } from "#/ui/form/Input";

const ItemContainer = styled.div`
    padding-top: 1rem;
`

const ButtonContainer = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
`;

type AddInvoiceItemFormProps = {
  save: (value: CreateInvoiceItemRequest) => void;
  onCancel: () => void;
};

export function AddInvoiceItemForm({ save, onCancel }: AddInvoiceItemFormProps) {
  const [itemId, setItemId] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [unitPrice, setUnitPrice] = useState(0);
  const [additionalInfo, setAdditionalInfo] = useState('');
  
  const { data: catalogItems } = useItemsQuery();

  const handleSubmit = () => {
    if (!itemId) {
      console.error("Please select an item.");
      return;
    }
    save({
      itemId,
      quantity,
      unitPrice, // In a real scenario, this might come from the selected catalog item
      additionalInfo
    });
  }

  const handleItemChange = (selectedItemId: string) => {
    setItemId(selectedItemId);
    const selectedItem = catalogItems?.find(item => item.id === selectedItemId);
    if (selectedItem) {
        setUnitPrice(selectedItem.unitPrice);
    }
  }

  const itemOptions = catalogItems?.reduce((acc, item) => {
    acc[item.id] = `${item.name} (${item.currency} ${item.unitPrice})`;
    return acc;
  }, {} as Record<string, string>) || {};

  return (
    <ItemContainer>
      <Formosa>
        <FormDropdown id="itemId" label="Item" options={itemOptions} value={itemId} onChange={handleItemChange} size={8} />
        <FormInput id="quantity" label="Quantity" type="number" value={quantity.toString()} onChange={(val) => setQuantity(Number(val) || 0)} size={2} />
        <FormInput id="unitPrice" label="Unit Price" type="number" value={unitPrice.toString()} onChange={(val) => setUnitPrice(Number(val) || 0)} size={2} />
        <FormInput id="additionalInfo" label="Additional Info" value={additionalInfo} onChange={setAdditionalInfo} />
        
        <ButtonContainer>
          <ActionButton onClick={onCancel}>Cancel</ActionButton>
          <ActionButton onClick={handleSubmit}>Add item</ActionButton>
        </ButtonContainer>
      </Formosa>
    </ItemContainer>
  )
}
