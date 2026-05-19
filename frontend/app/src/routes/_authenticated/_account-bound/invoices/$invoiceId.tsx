import { InvoiceEditorScreen } from '#/screens/invoice/editor/InvoiceEditorScreen'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/_account-bound/invoices/$invoiceId')({
  component: RouteComponent,
})

function RouteComponent() {
  const { invoiceId } = Route.useParams()
  return <InvoiceEditorScreen invoiceId={invoiceId} />
}