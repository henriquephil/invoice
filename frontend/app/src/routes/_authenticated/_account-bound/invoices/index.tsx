import { createFileRoute } from '@tanstack/react-router'
import InvoiceListScreen from '#/screens/invoice/InvoiceListScreen'

export const Route = createFileRoute('/_authenticated/_account-bound/invoices/')({
  component: Invoices,
})

function Invoices() {
  return <InvoiceListScreen />
}