import styled from "@emotion/styled";
import { useState } from "react";
import { Trash2 } from "lucide-react";
import type { UpdateInvoiceItemRequest } from "../../../../api/invoiceDtos";
import { SubtleLabel, VStack } from "#/ui/layout";
import { EditInvoiceItemForm } from "./EditInvoiceItemForm";
import { useInvoiceEditor } from "../context/InvoiceEditorProvider";
import type { InvoiceItem } from "#/types/invoiceTypes";

const DeleteButton = styled.button<{ isVisible: boolean }>`
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  opacity: ${props => (props.isVisible ? 1 : 0)};
  transition: opacity 0.2s;
  cursor: pointer;
  color: #ef4444;
  background: none;
  border: none;
  
  &:hover {
    color: #dc2626;
  }
`;

const EditButton = styled.button<{ isVisible: boolean }>`
    position: absolute;
    top: 0.5rem;
    right: 2.5rem;
    opacity: ${props => (props.isVisible ? 1 : 0)};
    transition: opacity 0.2s;
    cursor: pointer;
    background: none;
    border: none;
    color: var(--text-muted);

    &:hover {
        color: var(--text-primary);
    }
`;

const ItemContainer = styled.div`
  position: relative;
  padding: 0.5rem;
  border-bottom: 1px solid var(--border-glass);
`;

const ItemRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const ItemInfo = styled.div`
  display: flex;
  flex-direction: column;
`;

const ItemName = styled.span`
  font-weight: 600;
`;

const ItemDescription = styled.span`
  font-size: 12px;
  opacity: 0.5;
`;

const ItemStats = styled.div`
  display: flex;
  gap: 1rem;
  font-size: 12px;
  opacity: 0.8;
`;

function InvoiceItemComponent({ item }: { item: InvoiceItem }) {
  const [isEditing, setIsEditing] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const { isEditable, updateItem, deleteItem } = useInvoiceEditor();

  const handleUpdate = (itemData: UpdateInvoiceItemRequest) => {
    updateItem(item.id, itemData);
    setIsEditing(false);
  };

  if (isEditing) {
    return <EditInvoiceItemForm value={item} save={handleUpdate} onCancel={() => setIsEditing(false)} />;
  }

  return (
    <ItemContainer onMouseEnter={() => setIsHovered(true)} onMouseLeave={() => setIsHovered(false)}>
      {isEditable && (
        <>
          <DeleteButton isVisible={isHovered} onClick={() => deleteItem(item.id)}>
            <Trash2 size={16} />
          </DeleteButton>
          <EditButton isVisible={isHovered} onClick={() => setIsEditing(true)}>Edit</EditButton>
        </>
      )}
      <ItemRow>
        <ItemInfo>
          <ItemName>{item.item?.name}</ItemName>
          <ItemDescription>{item.additionalInfo}</ItemDescription>
        </ItemInfo>
        <ItemStats>
          <span>{item.quantity} {item.item?.measureUnit} x {item.item?.currency} {item.item?.unitPrice}</span>
          <span>{item.item?.currency} {item.totalPrice}</span>
        </ItemStats>
      </ItemRow>
    </ItemContainer>
  );
}

export function InvoiceItemsList() {
  const { invoice } = useInvoiceEditor();
  if (!invoice) return null;

  return (
    <>
      <SubtleLabel>Items</SubtleLabel>
      <VStack>
        {invoice.items.map((item) => (
          <InvoiceItemComponent key={item.id} item={item} />
        ))}
      </VStack>
    </>
  );
}
