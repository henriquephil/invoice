import styled from "@emotion/styled";
import { DateTimeFormatter } from "@js-joda/core";
import { useInvoiceEditor } from "../context/InvoiceEditorProvider";

const PaperWrapper = styled.article`
  width: 8.5in;
  height: 11in;
  padding: 1in;
  background-color: #ffffff;
  box-shadow: inset 0 0 1in rgba(0, 0, 0, 0.15);
  cursor: default;
`;
const InvoiceWrapper = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  font-size: 9pt;
  color: #000000;
`;

const Header = styled.header`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
`;

const MetaData = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 1em;
  border-bottom: 2px solid #cccccc;
`

const Account = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-grow: 1;
`;

const AccountLogo = styled.div`
  padding: 0.2in;
  flex-shrink: 1;
`
const AccountName = styled.div`
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  h1 {
    font-size: 16pt;
    font-weight: 900;
    letter-spacing: -0.05em;
    text-transform: uppercase;
  }
  p {
    color: #444444;
  }
`;

const InvoiceData = styled.div`
  display: flex;
  flex-direction: column;
  text-align: right;
`
const InvoiceNumber = styled.p`
  font-size: 12pt;
  font-weight: 700;
  color: #444444;
  &::before {
    content: "#";
  }
`
const InvoiceDate = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 1em;
  label {
    font-size: 0.9em;
    text-transform: uppercase;
    color: #808080;
    font-weight: 800;
    flex-shrink: 0;
  }
  span {
    flex-shrink: 0;
  }
`

const Subheader = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`

const InvoiceItems = styled.div`
  flex-grow: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
`

const InvoiceItemRow = styled.div`
  display: flex;
  align-items: baseline;
  justify-content: baseline;
  padding: 0.5em 0;
  .invoice-item-row-description {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    small {
      color: #444444;
    }
  }
  .invoice-item-row-quantity {
    text-align: right;
    flex-basis: 1in;
    flex-shrink: 0;
    flex-grow: 0;
  }
  .invoice-item-row-total {
    text-align: right;
    flex-basis: 1.5in;
    flex-shrink: 0;
    flex-grow: 0;
  }
`
const InvoiceItemHeader = styled(InvoiceItemRow)`
  font-weight: 700;
  border-bottom: 1px solid #cccccc;
  font-size: 0.9em;
  text-transform: uppercase;
  color: #808080;
  font-weight: 800;
`

const Footer = styled.footer`
  width: 100%;
  display: flex;
  flex-direction: row;
  border-top: 2px solid #cccccc;
`;
const PaymentInfo = styled.div`
  flex-grow: 1;
  display: flex;
  flex-direction: column;
`
const FooterTotal = styled.div`
  display: flex;
  justify-content: end;
  align-items: flex-start;
  font-size: 16pt;
  gap: 0.5em;
  padding-top: 0.2in;
  label {
    font-weight: 300;
  }
  span {
    font-weight: 300;
  }
`;

const DetailsPanel = styled.div`
  padding-top: 0.2in;
  display: flex;
  flex-direction: column;
`
const DetailsLine = styled.div`
  display: flex;
  align-items: baseline;
  label {
    text-transform: uppercase;
    color: #808080;
    font-weight: 800;
    flex: 1.5in 0 0;
  }
  span {
    font-weight: 700;
  }
`

export default function InvoicePaper() {
  const { invoice } = useInvoiceEditor();

  if (!invoice) {
    // Render nothing or a loading skeleton while the invoice data is being prepared.
    return null;
  }

  console.log('invoice-papel', invoice)
  return (
    <PaperWrapper>
      <InvoiceWrapper>
        <Header>
          <MetaData>
            <Account>
              <AccountLogo>
                TODO
              </AccountLogo>
              <AccountName>
                <h1>{invoice.account.name}</h1>
                <p>owner name?</p>
              </AccountName>
            </Account>
            <InvoiceData>
              <InvoiceNumber>{invoice.number}</InvoiceNumber>
              <InvoiceDate>
                <label>Creation Date</label>
                <span>{invoice.issuedAt?.format(DateTimeFormatter.ofPattern('MM/dd/yyyy'))}</span>
              </InvoiceDate>
              <InvoiceDate>
                <label>Due Date</label>
                <span>{invoice.dueDate?.format(DateTimeFormatter.ofPattern('MM/dd/yyyy'))}</span>
              </InvoiceDate>
            </InvoiceData>
          </MetaData>
          <Subheader>
            <DetailsPanel>
              <DetailsLine>
                <label>Bill from</label>
                <span>{invoice.account.name}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Tax ID number</label>
                <span>{invoice.account.document}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Telephone number</label>
                <span>{invoice.account.phone}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Email</label>
                <span>{invoice.account.email}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Address</label>
                {/* TODO complement and neightborhood can be empty */}
                <span>{invoice.address.number} {invoice.address.street} {invoice.address.complement}, {invoice.address.neighborhood}<br/>
                      {invoice.address.city}, {invoice.address.state} {invoice.address.zipCode}, {invoice.address.country}</span>
              </DetailsLine>
            </DetailsPanel>
            <DetailsPanel>
              <DetailsLine>
                <label>Bill to</label>
                <span>{invoice.customer?.name}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Tax ID number</label>
                <span>{invoice.customer?.document}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Email</label>
                <span>{invoice.customer?.email}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Address</label>
                <span>{invoice.customer?.address?.number} {invoice.customer?.address?.street} {invoice.customer?.address?.complement}, {invoice.customer?.address?.neighborhood}<br/>
                      {invoice.customer?.address?.city}, {invoice.customer?.address?.state} {invoice.customer?.address?.zipCode}, {invoice.customer?.address?.country}
                </span>
              </DetailsLine>
            </DetailsPanel>

          </Subheader>
        </Header>

        <InvoiceItems>
          <InvoiceItemHeader>
            <span className="invoice-item-row-description">Description</span>
            <span className="invoice-item-row-quantity">Quantity</span>
            <span className="invoice-item-row-total">Total</span>
          </InvoiceItemHeader>
          {invoice.items.map((item) => (  
            <InvoiceItemRow>
              <div className="invoice-item-row-description">
                <span>{item.item.name}</span>
                <small>{item.additionalInfo}</small>
              </div>
              <span className="invoice-item-row-quantity">{item.quantity} {item.item.measureUnit}</span>
              <span className="invoice-item-row-total">{invoice.currency} {item.totalPrice.toLocaleString('en-US', { style: 'currency', currency: (invoice.currency ?? 'USD') })}</span>
            </InvoiceItemRow>
          ))}
        </InvoiceItems>

        <Footer>
          <PaymentInfo>
            <DetailsPanel>
              <small>Pay to</small>
              <DetailsLine>
                <label>Beneficiary name</label>
                <span>{invoice.billing.beneficiaryName}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Account number</label>
                <span>{invoice.billing.beneficiaryAccount.accountNumber}</span>
              </DetailsLine>
              <DetailsLine>
                <label>SWIFT code</label>
                <span>{invoice.billing.beneficiaryAccount.swiftCode}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Bank name</label>
                <span>{invoice.billing.beneficiaryAccount.bankName}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Bank address</label>
                <span>{invoice.billing.beneficiaryAccount.bankAddress}</span>
              </DetailsLine>
            </DetailsPanel>
            {invoice.billing.intermediaryAccount &&
              <DetailsPanel>
              <small>Intermediary bank details</small>
              <DetailsLine>
                <label>SWIFT code</label>
                <span>{invoice.billing.intermediaryAccount.bankAddress}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Account number</label>
                <span>{invoice.billing.intermediaryAccount.bankAddress}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Bank name</label>
                <span>{invoice.billing.intermediaryAccount.bankAddress}</span>
              </DetailsLine>
              <DetailsLine>
                <label>Bank address</label>
                <span>{invoice.billing.intermediaryAccount.bankAddress}</span>
              </DetailsLine>
              </DetailsPanel>
            }
          </PaymentInfo>
          <FooterTotal>
            <label>{invoice.currency}</label>
            <span>{invoice.totalPrice.toLocaleString('en-US', { style: 'currency', currency: (invoice.currency ?? 'USD') })}</span>
          </FooterTotal>
        </Footer>
      </InvoiceWrapper>
    </PaperWrapper>
  );
}