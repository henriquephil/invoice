import styled from "@emotion/styled";
import DetailsPanel from "./sections/DetailsPanel";
import { SecondaryPanelContainer } from "#/ui/layout";
import InvoicePaper from "./sections/InvoicePaper";
import { InvoiceEditorProvider } from "./context/InvoiceEditorProvider";

const MainContainer = styled.main`
  flex: 1;
  display: flex;
  flex-direction: row;
`;

const InvoicePreviewContainer = styled.div`
`;

export type InvoiceEditorProps = {
  invoiceId: string;
}

export function InvoiceEditorScreen({ invoiceId }: InvoiceEditorProps) {
  return (
    <InvoiceEditorProvider invoiceId={invoiceId}>
      <MainContainer>
        <DetailsPanel />
        <SecondaryPanelContainer>
          <InvoicePreviewContainer>
            <InvoicePaper />
          </InvoicePreviewContainer>
        </SecondaryPanelContainer>
      </MainContainer>
    </InvoiceEditorProvider>
  )
}