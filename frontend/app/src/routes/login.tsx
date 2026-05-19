import { createFileRoute } from '@tanstack/react-router'
import styled from '@emotion/styled'
import { PanelContainer, PanelContentContainer } from '#/ui/layout'
import LoginForm from '#/screens/auth/LoginForm'

const PageContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
`

export const Route = createFileRoute('/login')({
  component: LoginComponent,
})

function LoginComponent() {
  return (
    <PageContainer>
      <PanelContainer flexBasis="400px">
        <PanelContentContainer>
          <LoginForm/>
        </PanelContentContainer>
      </PanelContainer>
    </PageContainer>
  )
}
