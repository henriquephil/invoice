import { ErrorFallback } from '#/ui/ErrorFallback'
import { ActionButton, LoadingContent, PanelContainer, PanelContentContainer, PanelHeaderContainer, Title } from '#/ui/layout'
import ActiveAccountDetails from '#/screens/account/AccountView'
import { createFileRoute, useRouter } from '@tanstack/react-router'
import { Suspense } from 'react'
import { ErrorBoundary } from 'react-error-boundary'

export const Route = createFileRoute('/_authenticated/_account-bound/account/')(
  {
    component: RouteComponent,
  },
)

function RouteComponent() {
  const { navigate } = useRouter()

  const handleSwitchAccount = () => {
    navigate({ to: '/accounts' })
  }
  return (
    <PanelContainer flexBasis="600px">
      <PanelHeaderContainer>
        <Title>Active account</Title>
        <ActionButton onClick={handleSwitchAccount}>Switch account</ActionButton>
      </PanelHeaderContainer>
      <PanelContentContainer>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <Suspense fallback={<LoadingContent />}>
            <ActiveAccountDetails />
          </Suspense>
        </ErrorBoundary>
      </PanelContentContainer>
    </PanelContainer>
  )
}
