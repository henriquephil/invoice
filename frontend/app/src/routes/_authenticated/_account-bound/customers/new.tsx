import { createFileRoute } from '@tanstack/react-router'
import NewCustomerForm from '#/screens/catalog/customer/NewCustomerForm'

export const Route = createFileRoute(
  '/_authenticated/_account-bound/customers/new',
)({
  component: RouteComponent,
})

function RouteComponent() {
  const navigate = Route.useNavigate()
  return <NewCustomerForm close={() => navigate({ to: '..'})}/>
}
