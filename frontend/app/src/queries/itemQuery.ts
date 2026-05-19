import { keepPreviousData, queryOptions, useMutation, useQuery, useQueryClient, useSuspenseQuery } from '@tanstack/react-query'
import { useRequiredActiveAccount } from '../store/accountStore'
import { createItem, getItem, listItems, updateItem } from '#/api/catalogClient'
import type { CreateItemRequest, UpdateItemRequest } from '#/api/catalogDtos'
import { type ExpressionNode } from '@rsql/ast'
import { emit } from '@rsql/emitter'

export const itemsQueryOptions = (rsql?: ExpressionNode) => {
  const filter = rsql && emit(rsql)
  return queryOptions({
    queryKey: ['items', filter],
    queryFn: () => listItems(filter),
    placeholderData: keepPreviousData,
  })
}

export const useItemsQuery = (filter?: ExpressionNode) => {
  return useQuery(itemsQueryOptions(filter))
}

export function useItemsSuspenseQuery(rsql?: ExpressionNode) {
  return useSuspenseQuery(itemsQueryOptions(rsql))
}

export function useItemSuspenseQuery(id: string) {
  const activeAccount = useRequiredActiveAccount()
  return useSuspenseQuery({
    queryKey: [activeAccount.id, 'items', id],
    queryFn: () => getItem(id),
  })
}

export function useItemMutations() {
  const queryClient = useQueryClient()
  const activeAccount = useRequiredActiveAccount()

  const onSettled = () => queryClient.invalidateQueries({ queryKey: [activeAccount.id, 'items'] })

  const createItemMutation = useMutation({
    mutationFn: (itemData: CreateItemRequest) => createItem(itemData),
    onSettled,
  })

  const updateItemMutation = useMutation({
    mutationFn: ({ itemId, itemData }: { itemId: string; itemData: UpdateItemRequest }) =>
      updateItem(itemId, itemData),
    onSettled,
  })

  return { createItemMutation, updateItemMutation }
}
