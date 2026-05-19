import styled from "@emotion/styled"

const ErrorFallbackContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 1rem;
  padding: 2rem;
  color: var(--text-muted);
`

const Message = styled.p`

`
const Reason = styled.pre`
  color: red;
`

export function ErrorFallback(props: any) {
  return (
    <ErrorFallbackContainer>
      <Message>Something went wrong:</Message>
      <Reason>{props.error.message}</Reason>
    </ErrorFallbackContainer>
  )
}
