import styled from "@emotion/styled";

export const FormContainer = styled.div<{ width: string }>`
  width: ${(props) => props.width};
`

export const Formosa = styled.div`
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  row-gap: 1rem;
  margin: 0 -0.5rem;
`

export const ButtonsContainer = styled.div`
  display: flex;
  gap: 2rem;
  & > * {
    flex: 1;
  }
`

export const FormGroup = styled.div<{ size?: number }>`
  display: flex;
  flex-direction: column;
  padding: 0 0.5rem;
  box-sizing: border-box;
  width: calc(${(props) => (props.size || 12)} / 12 * 100%);
`

export const Label = styled.label`
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
`

export const ErrorLine = styled.span`
  color: #d07162;
  font-size: 0.75em;
  height: 20px;
`

export const Divisor = styled.span`
  width: 100%;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 1rem 0 0.5rem 0;
  padding: 0 0.5rem;
  box-sizing: border-box;
  color: var(--text-secondary);
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;

  &::before,
  &::after {
    content: "";
    flex: 1;
    height: 1px;
    background: var(--border-glass);
  }
`