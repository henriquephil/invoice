import { keepPreviousData, queryOptions, useMutation, useQuery, useQueryClient, useSuspenseQuery } from '@tanstack/react-query'
import { useRequiredActiveAccount } from '../store/accountStore'
import { createCustomer, getCustomer, listCustomers, updateCustomer } from '#/api/catalogClient'
import type { CreateCustomerRequest, UpdateCustomerRequest } from '#/api/catalogDtos'

export const customerQueryOptions = (id?: string | null) => queryOptions({
  queryKey: ['customers', id],
  queryFn: () => getCustomer(id!),
  enabled: !!id,
  placeholderData: keepPreviousData,
});

export const useCustomerQuery = (id?: string | null) => {
  return useQuery(customerQueryOptions(id));
};

export function useCustomersSuspenseQuery() {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: [activeAccount.id, 'customers'],
    queryFn: listCustomers,
  })
}

export function useCustomerSuspenseQuery(id: string) {
  return useSuspenseQuery(customerQueryOptions(id));
}

export function useCreateCustomerMutation() {
  const queryClient = useQueryClient()
  const activeAccount = useRequiredActiveAccount()

  const onSettled = () => queryClient.invalidateQueries({ queryKey: [activeAccount.id, 'customers'] })

  return useMutation({
    mutationFn: (customerData: CreateCustomerRequest) => createCustomer(customerData),
    onSettled,
  })
}

export function useUpdateCustomerMutation() {
  const queryClient = useQueryClient()
  const activeAccount = useRequiredActiveAccount()

  const onSettled = () => queryClient.invalidateQueries({ queryKey: [activeAccount.id, 'customers'] })

  return useMutation({
    mutationFn: (variables: { customerId: string; customerData: UpdateCustomerRequest }) =>
      updateCustomer(variables.customerId, variables.customerData),
    onSettled,
  })
}
