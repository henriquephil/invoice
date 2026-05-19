import { createInvoice, createInvoiceItem, deleteInvoice, deleteInvoiceItem, findInvoice, issueInvoice, listInvoices, updateInvoice, updateInvoiceItem } from '#/api/invoiceClient'
import type {
  UpdateInvoiceRequest,
  CreateInvoiceItemRequest,
  UpdateInvoiceItemRequest,
} from '#/api/invoiceDtos'
import { useRequiredActiveAccount } from '../store/accountStore'
import { useSuspenseQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useRouter } from '@tanstack/react-router'

export function useInvoicesQuery() {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: [activeAccount.id, 'invoices'],
    queryFn: listInvoices
  })
}

export function useInvoiceQuery(invoiceId: string) {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: [activeAccount.id, 'invoices', invoiceId],
    queryFn: () => findInvoice(invoiceId),
  })
}

export function useInvoiceMutations() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const activeAccount = useRequiredActiveAccount()

  const onSettled = () => queryClient.invalidateQueries({ queryKey: [activeAccount.id, 'invoices'] })

  const createInvoiceMutation = useMutation({
    mutationFn: createInvoice,
    onSuccess: (data) => {
      router.history.push(`/invoices/${data}`)
    },
    onSettled,
  })

  const updateInvoiceMutation = useMutation({
    mutationFn: (variables: { invoiceId: string; invoiceData: UpdateInvoiceRequest }) =>
      updateInvoice(variables.invoiceId, variables.invoiceData),
    onSettled,
  })

  const deleteInvoiceMutation = useMutation({
    mutationFn: deleteInvoice,
    onSettled,
  })

  const issueInvoiceMutation = useMutation({
    mutationFn: issueInvoice,
    onSettled,
  })

  const createInvoiceItemMutation = useMutation({
    mutationFn: (variables: { invoiceId: string; itemData: CreateInvoiceItemRequest }) =>
      createInvoiceItem(variables.invoiceId, variables.itemData),
    onSettled,
  })

  const updateInvoiceItemMutation = useMutation({
    mutationFn: (variables: {
      invoiceId: string
      itemId: string
      itemData: UpdateInvoiceItemRequest
    }) => updateInvoiceItem(variables.invoiceId, variables.itemId, variables.itemData),
    onSettled,
  })

  const deleteInvoiceItemMutation = useMutation({
    mutationFn: (variables: { invoiceId: string; itemId: string }) =>
      deleteInvoiceItem(variables.invoiceId, variables.itemId),
    onSettled,
  })

  return {
    createInvoice: createInvoiceMutation.mutate,
    updateInvoice: updateInvoiceMutation.mutate,
    deleteInvoice: deleteInvoiceMutation.mutate,
    issueInvoice: issueInvoiceMutation.mutate,
    createInvoiceItem: createInvoiceItemMutation.mutate,
    updateInvoiceItem: updateInvoiceItemMutation.mutate,
    deleteInvoiceItem: deleteInvoiceItemMutation.mutate,
  }
}

