import { useMemo } from 'react';
import Builder from '@rsql/builder';

import type { DraftInvoiceResponse, InvoiceResponse } from '../../../../api/invoiceDtos';
import type { DraftInvoice, Invoice, InvoiceItem } from '#/types/invoiceTypes';
import { useRequiredActiveAccount } from '#/store/accountStore';
import { useAccountAddressSuspenseQuery, useAccountBankingQuery } from '#/queries/accountQuery';
import { useCustomerQuery } from '#/queries/customerQuery';
import { useItemsQuery } from '#/queries/itemQuery';

/**
 * A hook that takes an InvoiceResponse from the API and fully "hydrates" it.
 * - If the invoice is ISSUED, it's returned as is (as it's already complete).
 * - If the invoice is DRAFT, it fetches all related data (account, address, billing, customer, items)
 *   to build a complete DraftInvoice object for the UI.
 */
export function useHydratedInvoice(invoiceResponse?: InvoiceResponse): Invoice | DraftInvoice | undefined {
  const activeAccount = useRequiredActiveAccount();
  const { data: address } = useAccountAddressSuspenseQuery();
  const { data: billing } = useAccountBankingQuery();
  
  const isDraft = invoiceResponse?.status === 'DRAFT';
  const draftResponse = isDraft ? invoiceResponse as DraftInvoiceResponse : undefined;

  const customerResult = useCustomerQuery(isDraft ? draftResponse?.customerId : null);

  const itemIds = useMemo(() =>
    isDraft ? (draftResponse?.items || []).map((item) => item.itemId) : []
  , [isDraft, draftResponse]);

  const itemsResult = useItemsQuery(
    itemIds.length > 0 ? Builder.in("id", itemIds) : undefined
  );

  return useMemo<Invoice | DraftInvoice | undefined>(() => {
    if (!invoiceResponse) {
      return undefined;
    }
    
    if (invoiceResponse.status !== 'DRAFT') {
      return invoiceResponse as Invoice;
    }

    const draft = invoiceResponse as DraftInvoiceResponse;

    // While loading related data, return a shell to prevent rendering errors.
    if (customerResult.isLoading || itemsResult.isLoading || !address || !billing) {
      return undefined; // The provider will show a loading state
    }
    
    // TODO: Handle errors more gracefully
    if (customerResult.isError || itemsResult.isError) {
        console.error("Error loading customer or item details for draft invoice");
        return undefined;
    }

    const items = draft.items.map((item) => {
        const catalogItem = (itemsResult.data || []).find((i) => i.id === item.itemId);
        if (!catalogItem) return null;
        return {
          id: item.id,
          item: catalogItem,
          unitPrice: item.unitPrice,
          quantity: item.quantity,
          totalPrice: item.totalPrice,
          additionalInfo: item.additionalInfo,
        };
      }).filter((i): i is InvoiceItem => i !== null);

    // Assemble the complete, hydrated DraftInvoice object
    const hydratedDraft: DraftInvoice = {
      ...draft,
      account: activeAccount,
      address,
      billing,
      customer: customerResult.data || null,
      items,
    };

    return hydratedDraft;
  }, [invoiceResponse, customerResult.data, itemsResult.data, customerResult.isLoading, itemsResult.isLoading, customerResult.isError, itemsResult.isError, activeAccount, address, billing]);
}
