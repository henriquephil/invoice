import { ActionButton, LoadingContent, PanelContainer, PanelContentContainer, PanelHeaderContainer, SecondaryPanelContainer, Title } from '#/ui/layout'
import { createFileRoute, Outlet } from '@tanstack/react-router'
import { ErrorBoundary } from 'react-error-boundary'
import { Suspense } from 'react'
import { ErrorFallback } from '#/ui/ErrorFallback'
import { ItemsTable } from '#/screens/catalog/item/ItemsTable'
import type { ItemResponse } from '#/api/catalogDtos'

export const Route = createFileRoute('/_authenticated/_account-bound/items')({
  component: Items,
})

function Items() {
  const navigate = Route.useNavigate()

  const goToItem = (item: ItemResponse) => {
    navigate({
      to: './$itemId',
      params: {
        itemId: item.id,
      },
    })
  }

  return (
    <>
      <PanelContainer flexBasis="600px">
        <PanelHeaderContainer>
          <Title>Products & Services</Title>
          <ActionButton onClick={() => navigate({ to: './new'})}>New</ActionButton>
        </PanelHeaderContainer>
        <PanelContentContainer>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <Suspense fallback={<LoadingContent />}>
              <ItemsTable />
            </Suspense>
          </ErrorBoundary>
        </PanelContentContainer>
      </PanelContainer>
      <SecondaryPanelContainer>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <Suspense fallback={<LoadingContent />}>
            <Outlet />
          </Suspense>
        </ErrorBoundary>
      </SecondaryPanelContainer>
    </>
  )
}
