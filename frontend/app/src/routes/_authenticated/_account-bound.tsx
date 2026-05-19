import { useAccountStore } from '#/store/accountStore'
import { createFileRoute, Outlet, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/_account-bound')({
  beforeLoad: () => {
    const { activeAccount } = useAccountStore.getState()
    if (!activeAccount) {
      throw redirect({
        to: '/accounts',
      })
    }
  },
  component: AccountBoundLayout,
})

function AccountBoundLayout() {
  return <Outlet />
}
