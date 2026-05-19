import { Suspense } from 'react'
import { PanelContainer, PanelContentContainer, PanelHeaderContainer, Title, ActionButton, LoadingContent, SecondaryPanelContainer } from '#/ui/layout'
import { ErrorBoundary } from 'react-error-boundary'
import { ErrorFallback } from '#/ui/ErrorFallback'
import { useInvoiceMutations } from '#/queries/invoiceQuery'
import InvoiceListComponent from '#/screens/invoice/InvoiceListComponent'

export default function InvoiceListScreen() {
  const { createInvoice } = useInvoiceMutations()

  const handleNewInvoice = () => {
    createInvoice()
  }

  return (
    <>
      <PanelContainer flexBasis="600px">
        <PanelHeaderContainer>
          <Title>Invoices</Title>
        </PanelHeaderContainer>
        <PanelContentContainer>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <Suspense fallback={<LoadingContent />}>
              <InvoiceListComponent />
            </Suspense>
          </ErrorBoundary>
        </PanelContentContainer>
      </PanelContainer>
      <SecondaryPanelContainer>
        <ActionButton onClick={handleNewInvoice}>New Invoice</ActionButton>
      </SecondaryPanelContainer>
    </>
  )
}