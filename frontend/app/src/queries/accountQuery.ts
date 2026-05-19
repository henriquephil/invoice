import { useMutation, useQueryClient, useSuspenseQuery } from '@tanstack/react-query'
import { createAccount, getAddress, getBanking, listAccounts } from '#/api/accountclient'
import { useRequiredActiveAccount } from '../store/accountStore'

export const useAccountsSuspenseQuery = () => {
  return useSuspenseQuery({
    queryKey: ['accounts'],
    queryFn: listAccounts,
  })
}

export const useAccountAddressSuspenseQuery = () => {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: ['accounts', activeAccount?.id, 'address'],
    queryFn: async () => {
      return getAddress(activeAccount.id)
    },
  })
}

export const useAccountBankingQuery = () => {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: ['accounts', activeAccount?.id, 'banking'],
    queryFn: async () => {
      return getBanking(activeAccount.id)
    }
  })
}

export function useAccountMutations() {
  const queryClient = useQueryClient()
  
  const onSettled = () => queryClient.invalidateQueries({ queryKey: ['accounts'] })

  const createAccountMutation = useMutation({
    mutationFn: createAccount,
    onSettled,
  })

  return {
    createAccountMutation,
  }
}