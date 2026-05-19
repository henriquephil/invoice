
import { createFileRoute } from '@tanstack/react-router'
import EditCustomerForm from '#/screens/catalog/customer/EditCustomerForm'

export const Route = createFileRoute(
  '/_authenticated/_account-bound/customers/$customerId',
)({
  component: RouteComponent
})

function RouteComponent() {
  const { customerId } = Route.useParams()
  return <EditCustomerForm key={`customer-update-form-${customerId}`} customerId={customerId}/>
}
