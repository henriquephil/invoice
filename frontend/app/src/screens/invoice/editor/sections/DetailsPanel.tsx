import styled from '@emotion/styled'
import {
  PanelContentContainer,
  PanelHeaderContainer,
  PanelContainer,
  SubtleLabel,
  ActionButton,
} from '#/ui/layout'
import { useState } from 'react';
import type { CreateInvoiceItemRequest } from '../../../../api/invoiceDtos';
import { CustomersDropdown } from './CustomersDropdown';
import { InvoiceItemsList } from './InvoiceItemsList';
import { AddInvoiceItemForm } from './AddInvoiceItemForm';
import { useInvoiceEditor } from '../context/InvoiceEditorProvider';
import { Form } from '#/ui/form/form';
import { FormDatePicker } from '#/ui/form/DatePicker';

const StatusContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem; /* gap-4 */
`;

const StatusBadge = styled.span`
  padding: 0.25rem 0.75rem; /* px-3 py-1 */
  background-color: rgba(255, 255, 255, 0.05); /* bg-white/5 */
  border-radius: 9999px; /* rounded-full */
  font-size: 12px;
  font-weight: 600; /* font-semibold */
  color: rgba(255, 255, 255, 0.8); /* text-white/80 */
  text-transform: uppercase;
  letter-spacing: -0.025em;
`;

const Info = styled.div`
  display: flex;
  justify-content: center;
  font-size: 0.8rem;
  opacity: 0.6;
`

const ButtonContainer = styled.div`
  display: flex;
  gap: 2rem; /* gap-8 */
  align-items: center;
`;

export default function DetailsPanel() {
  const { invoice, isEditable, updateInvoiceField, addItem } = useInvoiceEditor();
  const [isAddingItem, setIsAddingItem] = useState(false);

  if (!invoice) return null; // Or a loading skeleton

  const handleAddItem = (itemData: CreateInvoiceItemRequest) => {
    addItem(itemData);
    setIsAddingItem(false); // Close form on save
  }
  
  return (
    <PanelContainer flexBasis="600px">
      <PanelHeaderContainer>
        <StatusContainer>
          <SubtleLabel>Status:</SubtleLabel>
          <StatusBadge>{invoice.status}</StatusBadge>
        </StatusContainer>
        <ButtonContainer>
          <ActionButton disabled={!isEditable}>Emit Invoice</ActionButton>
          <ActionButton disabled={!isEditable}>Discard</ActionButton>
        </ButtonContainer>
      </PanelHeaderContainer>
      <PanelContentContainer>
        <Info>All changes are saved automatically.</Info>
        <Form>
          <CustomersDropdown 
            value={invoice.customer} 
            onChange={(customerId) => updateInvoiceField('customerId', customerId, { debounce: 0 })} 
            disabled={!isEditable} 
          />
          <FormDatePicker
            id="dueDate" 
            label="Due date" 
            value={invoice.dueDate} 
            onChange={(dueDate) => updateInvoiceField('dueDate', dueDate, { debounce: 0 })} 
            disabled={!isEditable} 
          />
          <InvoiceItemsList />

          {isAddingItem ? (
            <AddInvoiceItemForm save={handleAddItem} onCancel={() => setIsAddingItem(false)} />
          ) : (
            <ActionButton onClick={() => setIsAddingItem(true)} disabled={!isEditable}>
              Add Item
            </ActionButton>
          )}

        </Form>
      </PanelContentContainer>
    </PanelContainer>
  )
}
