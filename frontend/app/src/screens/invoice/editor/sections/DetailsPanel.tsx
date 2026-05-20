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
import { Formosa } from '#/ui/form/form';
import { FormDatePicker } from '#/ui/form/DatePicker';

const StatusContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const StatusBadge = styled.span`
  padding: 0.25rem 0.75rem;
  background-color: rgba(255, 255, 255, 0.05);
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
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
  gap: 2rem;
  align-items: center;
`;

export default function DetailsPanel() {
  const { invoice, isEditable, updateInvoiceField } = useInvoiceEditor();

  if (!invoice) return null;
  
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
        <Formosa>
          <CustomersDropdown
            value={invoice.customer} 
            onChange={(customerId) => updateInvoiceField('customerId', customerId, { debounce: 0 })} 
            disabled={!isEditable} 
          />
          <FormDatePicker
            size={4}
            id="dueDate" 
            label="Due date" 
            value={invoice.dueDate} 
            onChange={(dueDate) => updateInvoiceField('dueDate', dueDate, { debounce: 0 })} 
            disabled={!isEditable} 
          />
          <InvoiceItemsList />

        </Formosa>
      </PanelContentContainer>
    </PanelContainer>
  )
}
