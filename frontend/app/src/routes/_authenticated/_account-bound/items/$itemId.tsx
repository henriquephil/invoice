import { ActionButton, SecondaryPanelCard, SecondaryPanelCardHeader, SecondaryPanelCardSection } from '#/ui/layout'
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useState } from 'react'
import { getChangedValues } from '#/libs/object-utils'
import type { UpdateItemRequest } from '#/api/catalogDtos'
import { useItemMutations, useItemSuspenseQuery } from '#/queries/itemQuery'
import type { Item } from '#/types/itemTypes'
import { ItemForm, ItemFormDataSchema, type ItemFormData } from '#/screens/catalog/item/ItemForm'
import { ButtonsContainer } from '#/ui/form/form'

export const Route = createFileRoute(
  '/_authenticated/_account-bound/items/$itemId'
)({
  component: RouteComponent,
})

function RouteComponent() {
  const { itemId } = Route.useParams()
  const { data } = useItemSuspenseQuery(itemId)
  return <ItemUpdateForm key={`item-update-form-${itemId}`} itemId={itemId} item={data}/>
}

function ItemUpdateForm({ itemId, item }: { itemId: string, item: Item }) {
  const { updateItemMutation } = useItemMutations()
  const navigate = useNavigate()
  const [itemData, setItemData] = useState<ItemFormData>(() => ItemFormDataSchema.parse(item))

  const close = () => {
    navigate({ to: '/items' })
  }
  const save = () => {
    const changedData = getChangedValues(item, itemData)

    if (Object.keys(changedData).length === 0) {
      close()
      return
    }

    updateItemMutation.mutate(
      { itemId, itemData: changedData as UpdateItemRequest },
      { onSuccess: () => close() }
    )
  }

  return (
    <SecondaryPanelCard>
      <SecondaryPanelCardHeader>
        {item.name}
      </SecondaryPanelCardHeader>
      <SecondaryPanelCardSection>
        <ItemForm value={itemData} onChange={setItemData} />
      </SecondaryPanelCardSection>
      <SecondaryPanelCardSection>
        <ButtonsContainer>
          <ActionButton onClick={save}>Update</ActionButton>
          <ActionButton onClick={close}>Cancel</ActionButton>
        </ButtonsContainer>
      </SecondaryPanelCardSection>
    </SecondaryPanelCard>
  )
}
