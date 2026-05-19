import type { CreateInvoiceItemRequest, UpdateInvoiceItemRequest } from '#/api/invoiceDtos';
import { useInvoiceQuery } from '#/queries/invoiceQuery';
import { useRequiredActiveAccount } from '#/store/accountStore';
import type { DraftInvoice, Invoice } from '#/types/invoiceTypes';
import React, { createContext, useContext, useEffect, useReducer } from 'react';
import { useHydratedInvoice } from '../hooks/useHydratedInvoice';
import { useDebouncedCallback } from '#/libs/debounce';
import { useInvoiceMutations } from '../hooks/useInvoiceMutations';

// import type { DraftInvoice, Invoice } from '../types';
// import { useInvoiceQuery } from '../invoiceQuery';
// import { useRequiredActiveAccount } from '../../account/accountStore';
// import type { CreateInvoiceItemRequest, UpdateInvoiceItemRequest } from '../../../core/clients/invoiceDtos';
// import { useDebouncedCallback } from '#/core/utils/debounce';
// import { useHydratedInvoice } from './hooks/useHydratedInvoice';
// import { useInvoiceMutations } from './hooks/useInvoiceMutations';

// --- CONTEXT SHAPE ---

interface InvoiceEditorContextValue {
  invoice?: DraftInvoice | Invoice;
  isEditable: boolean;
  updateInvoiceField: (path: string, value: any, options?: { debounce?: number }) => void;
  addItem: (itemData: CreateInvoiceItemRequest) => void;
  updateItem: (itemId: string, itemData: UpdateInvoiceItemRequest) => void;
  deleteItem: (itemId: string) => void;
}

const InvoiceEditorContext = createContext<InvoiceEditorContextValue | null>(null);

// --- REDUCER ---

type Action = { type: 'SET_INVOICE'; payload: DraftInvoice | Invoice } | { type: 'UPDATE_FIELD'; payload: { path: string; value: any } };

function setProperty<T extends object>(obj: T, path: string, value: any): T {
  const keys = path.split('.');
  if (keys.length === 1) {
    return { ...obj, [path]: value };
  }

  const newObj = { ...obj };
  let current: any = newObj;
  for (let i = 0; i < keys.length - 1; i++) {
    const key = keys[i];
    // Create a new object for the nested path
    current[key] = { ...current[key] };
    current = current[key];
  }
  current[keys[keys.length - 1]] = value;
  return newObj;
}

function invoiceReducer(state: DraftInvoice | Invoice, action: Action): DraftInvoice | Invoice {
  switch (action.type) {
    case 'SET_INVOICE':
      return action.payload;
    case 'UPDATE_FIELD':
      return setProperty(state, action.payload.path, action.payload.value);
    default:
      return state;
  }
}

// --- PROVIDER COMPONENT ---

type InvoiceEditorProviderProps = {
  invoiceId: string;
  children: React.ReactNode;
};

export function InvoiceEditorProvider({ invoiceId, children }: InvoiceEditorProviderProps) {
  const activeAccount = useRequiredActiveAccount();
  const { data: serverInvoiceResponse } = useInvoiceQuery(invoiceId);
  const hydratedInvoice = useHydratedInvoice(serverInvoiceResponse);

  const [localInvoice, dispatch] = useReducer(invoiceReducer, hydratedInvoice!);

  useEffect(() => {
    if (hydratedInvoice) {
      dispatch({ type: 'SET_INVOICE', payload: hydratedInvoice });
    }
  }, [hydratedInvoice]);

  const queryKey = [activeAccount.id, 'invoices', invoiceId];
  const mutations = useInvoiceMutations({ invoiceId, queryKey });

  // --- HANDLERS ---

  const handleUpdateField = (path: string, value: any) => {
    if (localInvoice?.status !== 'DRAFT') return;
    dispatch({ type: 'UPDATE_FIELD', payload: { path, value } });
    const fieldName = path.split('.')[0];
    mutations.updateInvoice({ invoiceData: { [fieldName]: value } });
  };
  
  const debouncedUpdateField = useDebouncedCallback(handleUpdateField, 500);

  const updateInvoiceField = (path: string, value: any, options?: { debounce?: number }) => {
    const wait = options?.debounce;
    if (wait === 0) {
      handleUpdateField(path, value);
    } else {
      debouncedUpdateField(path, value);
    }
  };

  const addItem = (itemData: CreateInvoiceItemRequest) => {
    if (localInvoice?.status !== 'DRAFT') return;
    mutations.createItem(itemData);
  };

  const updateItem = (itemId: string, itemData: UpdateInvoiceItemRequest) => {
    if (localInvoice?.status !== 'DRAFT') return;
    mutations.updateItem({ itemId, itemData });
  };

  const deleteItem = (itemId: string) => {
    if (localInvoice?.status !== 'DRAFT') return;
    mutations.deleteItem(itemId);
  };

  if (!localInvoice) {
    // Or a loading spinner
    return null;
  }

  const value: InvoiceEditorContextValue = {
    invoice: localInvoice,
    isEditable: localInvoice.status === 'DRAFT',
    updateInvoiceField,
    addItem,
    updateItem,
    deleteItem,
  };

  return <InvoiceEditorContext.Provider value={value}>{children}</InvoiceEditorContext.Provider>;
}

// --- CONSUMER HOOK ---

export function useInvoiceEditor() {
  const context = useContext(InvoiceEditorContext);
  if (!context) {
    throw new Error('useInvoiceEditor must be used within an InvoiceEditorProvider');
  }
  return context;
}
