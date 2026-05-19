import { useInvoicesQuery } from "#/queries/invoiceQuery"
import styled from "@emotion/styled"
import { Link } from "@tanstack/react-router"


const InvoiceList = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
`

const InvoiceCard = styled(Link)`
  padding: 1rem;
  border: 1px solid var(--border-glass);
  border-radius: 0.5rem;
  background-color: var(--surface-strong);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.2s;

  &:hover {
    background-color: var(--surface-dark);
  }
`

const InvoiceNumber = styled.span`
  font-weight: 600;
`

const InvoiceStatus = styled.span`
  font-size: 1em;
  opacity: 0.8;
`


export default function InvoiceListComponent() {
  const { data: invoices } = useInvoicesQuery()

  return (
    <InvoiceList>
      {invoices?.map((invoice) => (
        <InvoiceCard key={invoice.id} to={`/invoices/${invoice.id}`}>
          <InvoiceNumber>Invoice #{invoice.number}</InvoiceNumber>
          <InvoiceStatus>{invoice.status}</InvoiceStatus>
        </InvoiceCard>
      ))}
    </InvoiceList>
  )
}
