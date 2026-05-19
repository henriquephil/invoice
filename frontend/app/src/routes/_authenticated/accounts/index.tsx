import { createFileRoute, useRouter } from '@tanstack/react-router'
import styled from '@emotion/styled'
import { Suspense } from 'react'
import { ErrorBoundary } from 'react-error-boundary'
import { PanelContainer, PanelContentContainer, PanelHeaderContainer, Title, ActionButton, LoadingContent } from '#/ui/layout'
import { ErrorFallback } from '#/ui/ErrorFallback'
import { useAccountMutations, useAccountsSuspenseQuery } from '#/queries/accountQuery'
import { useAccountStore } from '#/store/accountStore'
import type { Account } from '#/types/accountTypes'



const Alert = styled.div`
  background-color: rgba(250, 204, 21, 0.1);
  border: 1px solid rgba(250, 204, 21, 0.2);
  color: #facc15;
  padding: 1rem;
  border-radius: 0.5rem;
  margin: 0 2rem 2rem;
  font-size: 0.875rem;
`

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
  padding: 2rem;
`

const AccountCard = styled.div<{ isActive: boolean }>`
  background-color: var(--surface-strong);
  border-radius: 1rem;
  padding: 1.5rem;
  border: 1px solid ${({ isActive }) => (isActive ? 'var(--color-primary)' : 'var(--border-glass)')};
  display: flex;
  flex-direction: column;
  gap: 1rem;
  cursor: pointer;
  transition: border-color 0.2s ease-in-out;

  &:hover {
    border-color: ${({ isActive }) => (isActive ? 'var(--color-primary)' : 'var(--border-subtle)')};
  }
`

const CardHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`

const CardTitle = styled.h2`
  font-weight: 600;
`

const CardContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  color: var(--text-muted);
  font-size: 0.875rem;
`

export const Route = createFileRoute('/_authenticated/accounts/')({
  component: RouteComponent,
})

function RouteComponent() {
  const { createAccountMutation } = useAccountMutations()
  const { activeAccount, clearActiveAccount } = useAccountStore()

  const handleNewAccount = () => {
    createAccountMutation.mutate({
      name: 'New Account',
      email: 'newaccount@example.com',
      document: '000.000.000-00',
      phone: '(00) 00000-0000'
    })
  }

  return (
    <PanelContainer flexBasis="600px">
      <PanelHeaderContainer>
        <Title>Accounts</Title>
        <div>
          <ActionButton onClick={handleNewAccount}>New Account</ActionButton>
          {/* Botão temporário para teste */}
          <ActionButton onClick={clearActiveAccount} style={{ marginLeft: '1rem', backgroundColor: '#a1a1aa' }}>
            Clear Active Account
          </ActionButton>
        </div>
      </PanelHeaderContainer>
      <PanelContentContainer>
        {!activeAccount && (
          <Alert>
            No active account selected. Please choose an account to continue.
          </Alert>
        )}
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <Suspense fallback={<LoadingContent />}>
            <AccountList />
          </Suspense>
        </ErrorBoundary>
      </PanelContentContainer>
    </PanelContainer>
  )
}

function AccountList() {
  const { data: accounts } = useAccountsSuspenseQuery()
  const { activeAccount, setActiveAccount } = useAccountStore()
  const { navigate } = useRouter()

  const activatedAccount = (account: Account) => {
    setActiveAccount(account)
    navigate({ to: '/account' })
  }

  return (
    <Grid>
      {accounts.map((account) => (
        <AccountCard
          key={account.id}
          isActive={activeAccount?.id === account.id}
          onClick={() => activatedAccount(account)}
        >
          <CardHeader>
            <CardTitle>{account.name}</CardTitle>
          </CardHeader>
          <CardContent>
            <span>Email: {account.email}</span>
            <span>Document: {account.document}</span>
            <span>Phone: {account.phone}</span>
          </CardContent>
        </AccountCard>
      ))}
    </Grid>
  )
}
