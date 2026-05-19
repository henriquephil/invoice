import { ActionButton, LoadingContent, PanelContainer, PanelContentContainer, PanelHeaderContainer, SecondaryPanelContainer, Title } from '#/ui/layout'
import { createFileRoute, Outlet } from '@tanstack/react-router'
import type { CustomerResponse } from '#/api/catalogDtos'
import { ErrorBoundary } from 'react-error-boundary'
import { Suspense } from 'react'
import { ErrorFallback } from '#/ui/ErrorFallback'
import CustomersList from '#/screens/catalog/customer/CustomersList'

export const Route = createFileRoute('/_authenticated/_account-bound/customers')({
  component: Customers,
})

function Customers() {
  const navigate = Route.useNavigate()

  const goToCustomer = (customer: CustomerResponse) => {
    navigate({
      to: './$customerId',
      params: {
        customerId: customer.id,
      },
    })
  }

  return (
    <>
      <PanelContainer flexBasis="600px">
        <PanelHeaderContainer>
          <Title>Customers</Title>
          <ActionButton onClick={() => navigate({ to: './new'})}>New</ActionButton>
        </PanelHeaderContainer>
        <PanelContentContainer>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <Suspense fallback={<LoadingContent />}>
              <CustomersList onCustomerClick={goToCustomer} />
            </Suspense>
          </ErrorBoundary>
        </PanelContentContainer>
      </PanelContainer>
      <SecondaryPanelContainer>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <Suspense fallback={<LoadingContent />}>
            <Outlet/>
          </Suspense>
        </ErrorBoundary>
      </SecondaryPanelContainer>
    </>
  )
}
