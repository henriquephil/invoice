import { createFileRoute, useRouter } from '@tanstack/react-router'
import styled from '@emotion/styled'
import { PanelContainer, PanelContentContainer } from '#/ui/layout'
import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { register } from '#/api/authClient'

const PageContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--surface);
`

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`

const Title = styled.h1`
  font-size: 1.5rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 1rem;
`

const Input = styled.input`
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid var(--border-glass);
  background-color: rgba(24, 24, 27, 0.5);
  color: white;
  font-size: 1rem;

  &:focus {
    outline: none;
    border-color: var(--glow-border);
  }
`

const Button = styled.button`
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  background-color: #e4e4e7;
  color: black;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: white;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`

const ErrorMessage = styled.p`
  color: #ef4444;
  text-align: center;
  font-size: 0.875rem;
`

export const Route = createFileRoute('/register')({
  component: RegisterComponent,
})

function RegisterComponent() {
  const router = useRouter()
  const [email, setEmail] = useState('test@hphil.dev')
  const [password, setPassword] = useState('password')
  const [name, setName] = useState('')

  const registerMutation = useMutation({
    mutationFn: register,
    onSuccess: () => {
      router.navigate({ to: '/accounts' })
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    registerMutation.mutate({ email, password, name })
  }

  return (
    <PageContainer>
      <PanelContainer flexBasis="400px">
        <PanelContentContainer>
          <Form onSubmit={handleSubmit}>
            <Title>Login</Title>
            <Input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <Input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <Input
              type="name"
              placeholder="Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <Button type="submit" disabled={registerMutation.isPending}>
              {registerMutation.isPending ? 'Logging in...' : 'Login'}
            </Button>
            {registerMutation.isError && (
              <ErrorMessage>
                Failed to login. Please check your credentials.
              </ErrorMessage>
            )}
          </Form>
        </PanelContentContainer>
      </PanelContainer>
    </PageContainer>
  )
}
