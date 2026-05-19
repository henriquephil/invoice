import { login } from '#/api/authClient';
import styled from '@emotion/styled'
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from "react";

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

export default function LoginForm() {
  const [email, setEmail] = useState('test@hphil.dev')
  const [password, setPassword] = useState('password')
  const navigate = useNavigate()

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: () => {
    navigate({ to: '/accounts' })
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    loginMutation.mutate({ email, password })
  }
  
  return (
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
      <Button type="submit" disabled={loginMutation.isPending}>
        {loginMutation.isPending ? 'Logging in...' : 'Login'}
      </Button>
      {loginMutation.isError && (
        <ErrorMessage>
          Failed to login. Please check your credentials.
        </ErrorMessage>
      )}
    </Form>
  )
}