import styled from '@emotion/styled'

export const FullPage = styled.div`
  display: flex;
  height: 100vh;
`

export const PanelContainer = styled.div<{ flexBasis: string }>`
  border-right: 1px solid var(--border-glass);
  border-left: 1px solid var(--border-glass);
  flex-basis: ${(props) => props.flexBasis};
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background-color: var(--surface-strong);
  overflow-y: auto;
`;

export const PanelHeaderContainer = styled.header`
  display: flex;
  padding: 2rem;
  border-top: 1px solid var(--border-glass);
  border-bottom: 1px solid var(--surface);

  flex-shrink: 0;
  align-items: center;
  justify-content: space-between;
  height: 5rem;
`
export const Title = styled.h1`
  font-size: 1.125rem;
  font-weight: 600;
`

export const PanelContentContainer = styled.div`
  display: flex;
  padding: 1.5rem;
  border-top: 1px solid var(--border-glass);
  border-bottom: 1px solid var(--surface);
  flex-grow: 1;
  flex-direction: column;
  justify-content: start;
`;

export const SecondaryPanelContainer = styled.div`
  padding-top: 4rem;
  padding-bottom: 4rem;
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: flex-start;
  overflow-y: auto;
  background: radial-gradient(at 50% 95%, var(--glow-bg-a), rgba(0, 0, 0, 0.8));
`;

export const SecondaryPanelCard = styled.div`
  display: flex;
  flex-direction: column;
  backdrop-filter: brightness(0.8);
  border: 1px solid var(--border-glass);
  color: var(--text-primary);
  box-shadow: 60px 0px 80px -60px rgba(0, 0, 0, 0.4), -60px 0px 80px -60px rgba(0, 0, 0, 0.4);
`

export const SecondaryPanelCardSection = styled.div`
  padding: 1rem 2rem;
  &:not(:first-of-type) {
    border-top: 1px solid var(--border-glass);
  }
  &:not(:last-of-type) {
    border-bottom: 1px solid var(--surface);
  }
`

export const SecondaryPanelCardHeader = styled(SecondaryPanelCardSection)`
  font-size: 1.5rem;
  font-weight: 600;
  text-align: center;
`


export const ActionButton = styled.button`
  padding: 0.625rem 2rem;
  background-color: #e4e4e7;
  color: black;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  transition: background-color 0.2s;
  border: none;

  &:hover:not(:disabled) {
    background-color: white;
    cursor: pointer;
  }

  &:disabled {
    opacity: 0.5;
  }
`
export const SubtleLabel = styled.span`
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  opacity: 0.3;
  font-weight: 700;
`

export const VStack = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
`

export const LoadingContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 2rem;
  color: var(--text-muted);
`
export function LoadingContent() {
  return <LoadingContainer>Loading</LoadingContainer>
}

