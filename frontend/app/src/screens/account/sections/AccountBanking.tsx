import styled from "@emotion/styled";
import { useAccountBankingQuery } from "#/queries/accountQuery";

const BankingContainer = styled.div`
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

export default function AccountBanking() {
  const { data: banking } = useAccountBankingQuery();
  if (!banking) return <div>no banking</div>;
  return (
    <BankingContainer>
      <Title>Banking</Title>
      <Grid>
        <FieldContainer>
          <Label>Beneficiary Name</Label>
          <span>{banking.beneficiaryName}</span>
        </FieldContainer>
        <FieldContainer>
          <Label>Account Number</Label>
          <span>{banking.beneficiaryAccount.accountNumber}</span>
        </FieldContainer>
        <FieldContainer>
          <Label>SWIFT Code</Label>
          <span>{banking.beneficiaryAccount.swiftCode}</span>
        </FieldContainer>
        <FieldContainer>
          <Label>Bank Name</Label>
          <span>{banking.beneficiaryAccount.bankName}</span>
        </FieldContainer>
        <FieldContainer>
          <Label>Bank Address</Label>
          <span>{banking.beneficiaryAccount.bankAddress}</span>
        </FieldContainer>
      </Grid>
    </BankingContainer>
  );
}