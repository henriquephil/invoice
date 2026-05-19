import { ActionButton, SecondaryPanelCard, SecondaryPanelCardHeader, SecondaryPanelCardSection } from '#/ui/layout'
import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { useItemMutations } from '#/queries/itemQuery'
import { ItemForm, type ItemFormData } from '#/screens/catalog/item/ItemForm'
import { ItemType } from '#/types/itemTypes'
import { ButtonsContainer } from '#/ui/form/form'

export const Route = createFileRoute(
  '/_authenticated/_account-bound/items/new'
)({
  component: RouteComponent,
})

function RouteComponent() {
  const navigate = Route.useNavigate()
  return <NewItemForm discard={() => navigate({ to: '..'})}/>
}

function NewItemForm({ discard }: { discard: () => void }) {
  const { createItemMutation } = useItemMutations()
  const [ request, setRequest ] = useState<ItemFormData>({
    name: '',
    type: ItemType.SERVICE,
    measureUnit: '',
    unitPrice: 0,
    currency: ''
  })

  const save = () => {
    createItemMutation.mutate(
      request,
      { onSuccess: () => close() }
    )
  }

  return (
    <SecondaryPanelCard>
      <SecondaryPanelCardHeader>
        New Item
      </SecondaryPanelCardHeader>
      <SecondaryPanelCardSection>
        <ItemForm value={request} onChange={setRequest} />
      </SecondaryPanelCardSection>
      <SecondaryPanelCardSection>
        <ButtonsContainer>
          <ActionButton onClick={() => save()}>Submit</ActionButton>
          <ActionButton onClick={() => discard()}>Discard</ActionButton>
        </ButtonsContainer>
      </SecondaryPanelCardSection>
    </SecondaryPanelCard>
  )
}