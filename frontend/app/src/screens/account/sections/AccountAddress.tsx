import styled from "@emotion/styled";
import { useAccountAddressSuspenseQuery } from "#/queries/accountQuery"
import { LoadingContent } from "#/ui/layout"

const Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

const Title = styled.h2`
  font-size: 1.125rem;
  line-height: 1.75rem;
  font-weight: 600;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
`;

const FieldContainer = styled.div`
  display: flex;
  flex-direction: column;
`;

const Label = styled.span`
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: rgb(107 114 128);
`;


export default function AccountAddress() {
  const { data: address } = useAccountAddressSuspenseQuery()

  if (!address) {
    return <LoadingContent />
  }

  return <Container>
    <Title>Address</Title>
    <Grid>
      <FieldContainer>
        <Label>Street</Label>
        <span>{address.street}</span>
      </FieldContainer>
      <FieldContainer>
        <Label>City</Label>
        <span>{address.city}</span>
      </FieldContainer>
      <FieldContainer>
        <Label>State</Label>
        <span>{address.state}</span>
      </FieldContainer>
      <FieldContainer>
        <Label>Zip Code</Label>
        <span>{address.zipCode}</span>
      </FieldContainer>
      <FieldContainer>
        <Label>Country</Label>
        <span>{address.country}</span>
      </FieldContainer>
    </Grid>
  </Container>
}