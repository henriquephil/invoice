import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createInvoiceItem, deleteInvoiceItem, updateInvoice, updateInvoiceItem } from '../../../../api/invoiceClient';
import type { CreateInvoiceItemRequest, UpdateInvoiceItemRequest, UpdateInvoiceRequest } from '../../../../api/invoiceDtos';
import type { ItemResponse } from '../../../../api/catalogDtos';
import type { DraftInvoice, Invoice, InvoiceItem } from '#/types/invoiceTypes';
import { ItemType } from '#/types/itemTypes';

type UseInvoiceMutationsProps = {
  invoiceId: string;
  queryKey: (string | null | undefined)[];
};

export function useInvoiceMutations({ invoiceId, queryKey }: UseInvoiceMutationsProps) {
  const queryClient = useQueryClient();

  const updateInvoiceMutation = useMutation({
    mutationFn: ({ invoiceData }: { invoiceData: UpdateInvoiceRequest }) => updateInvoice(invoiceId, invoiceData),
    onMutate: async ({ invoiceData }) => {
      await queryClient.cancelQueries({ queryKey });
      const previousInvoice = queryClient.getQueryData<DraftInvoice | Invoice>(queryKey);
      
      queryClient.setQueryData<DraftInvoice | Invoice>(queryKey, (old) => {
        if (!old) return undefined;
        return { ...old, ...invoiceData };
      });
      
      console.info('Optimistic update applied:', invoiceData);
      return { previousInvoice };
    },
    onError: (err, _, context) => {
      console.error('Patch failed. Reverting optimistic update.', err);
      if (context?.previousInvoice) {
        queryClient.setQueryData(queryKey, context.previousInvoice);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey });
    },
  });

  const createItemMutation = useMutation({
    mutationFn: (itemData: CreateInvoiceItemRequest) => createInvoiceItem(invoiceId, itemData),
    onMutate: async (newItemData: CreateInvoiceItemRequest) => {
      await queryClient.cancelQueries({ queryKey });
      const previousInvoice = queryClient.getQueryData<DraftInvoice | Invoice>(queryKey);

      const catalogItems = queryClient.getQueryData<ItemResponse[]>(['items', undefined]) || [];
      const catalogItem = catalogItems.find(item => item.id === newItemData.itemId);

      if (!catalogItem) {
        console.warn(`Catalog item ${newItemData.itemId} not found in cache. Optimistic update will be partial.`);
        queryClient.invalidateQueries({ queryKey: ['items'] });
      }

      const tempId = crypto.randomUUID();
      const tempItem: InvoiceItem = {
        id: tempId,
        ...newItemData,
        item: catalogItem || {
            id: newItemData.itemId,
            name: 'Loading item...',
            type: ItemType.SERVICE,
            currency: '...',
            measureUnit: '...',
            unitPrice: newItemData.unitPrice,
        }, 
        totalPrice: newItemData.quantity * newItemData.unitPrice,
      };

      queryClient.setQueryData<DraftInvoice | Invoice>(queryKey, (old) => {
        if (!old) return undefined;
        return { ...old, items: [...old.items, tempItem] };
      });
      
      console.info('Optimistically added new item:', tempItem);
      return { previousInvoice, tempId };
    },
    onSuccess: (realItemId, _, context) => {
      console.log(`Item created with real ID: ${realItemId}. Updating cache.`);
      queryClient.setQueryData<DraftInvoice | Invoice>(queryKey, (old) => {
        if (!old) return undefined;
        const newItems = old.items.map((item) =>
          item.id === context?.tempId ? { ...item, id: realItemId } : item
        );
        return { ...old, items: newItems };
      });
    },
    onError: (err, _, context) => {
      console.error('Create item failed. Reverting optimistic update.', err);
      if (context?.previousInvoice) {
        queryClient.setQueryData(queryKey, context.previousInvoice);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey });
    },
  });

  const updateItemMutation = useMutation({
    mutationFn: ({ itemId, itemData }: { itemId: string; itemData: UpdateInvoiceItemRequest }) =>
      updateInvoiceItem(invoiceId, itemId, itemData),
    onMutate: async ({ itemId, itemData }) => {
      await queryClient.cancelQueries({ queryKey });
      const previousInvoice = queryClient.getQueryData<DraftInvoice | Invoice>(queryKey);

      queryClient.setQueryData<DraftInvoice | Invoice>(queryKey, (old) => {
        if (!old) return undefined;
        return { ...old, items: old.items.map((item) => item.id === itemId ? { ...item, ...itemData } : item) };
      });

      console.info(`Optimistically updated item ${itemId}:`, itemData);
      return { previousInvoice };
    },
    onError: (err, _, context) => {
      console.error('Update item failed. Reverting optimistic update.', err);
      if (context?.previousInvoice) {
        queryClient.setQueryData(queryKey, context.previousInvoice);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey });
    },
  });

  const deleteItemMutation = useMutation({
    mutationFn: (itemId: string) => deleteInvoiceItem(invoiceId, itemId),
    onMutate: async (itemId: string) => {
      await queryClient.cancelQueries({ queryKey });
      const previousInvoice = queryClient.getQueryData<DraftInvoice | Invoice>(queryKey);

      queryClient.setQueryData<DraftInvoice | Invoice>(queryKey, (old) => {
        if (!old) return undefined;
        return { ...old, items: old.items.filter((item) => item.id !== itemId) };
      });

      console.info(`Optimistically deleted item ${itemId}`);
      return { previousInvoice };
    },
    onError: (err, _, context) => {
      console.error('Delete failed. Reverting optimistic update.', err);
      if (context?.previousInvoice) {
        queryClient.setQueryData(queryKey, context.previousInvoice);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey });
    },
  });

  return {
    updateInvoice: updateInvoiceMutation.mutate,
    createItem: createItemMutation.mutate,
    updateItem: updateItemMutation.mutate,
    deleteItem: deleteItemMutation.mutate,
  };
}
