import { useCallback, useState } from "react";
import { CustomerForm, type CustomerFormData } from "./CustomerForm";
import { useCreateCustomerMutation } from "#/queries/customerQuery";
import { ActionButton, SecondaryPanelCard, SecondaryPanelCardHeader, SecondaryPanelCardSection } from "#/ui/layout";
import { ButtonsContainer } from '#/ui/form/form'

export default function NewCustomerForm({ close }: { close: () => void }) {
  const [isValid, setIsValid] = useState(false);
  const [ request, setRequest ] = useState<CustomerFormData>({
    name: '',
    email: '',
    document: '',
    phone: '',
    street: '',
    number: '',
    complement: '',
    neighborhood: '',
    city: '',
    state: '',
    zipCode: '',
    country: '',
  })
  const createCustomerMutation = useCreateCustomerMutation()

  const save = useCallback(() => {
    const { name, email, document, phone, ...address} = request
    createCustomerMutation.mutate(
      { name, email, document, phone, address },
      { onSuccess: () => close() }
    )
  }, [createCustomerMutation, request])


  return (
    <SecondaryPanelCard>
      <SecondaryPanelCardHeader>
        New Customer
      </SecondaryPanelCardHeader>
      <SecondaryPanelCardSection>
        <CustomerForm value={request} onChange={setRequest} onValidation={setIsValid} />
      </SecondaryPanelCardSection>
      <SecondaryPanelCardSection>
        <ButtonsContainer>
          <ActionButton onClick={() => save()} disabled={!isValid || createCustomerMutation.isPending}>Submit</ActionButton>
          <ActionButton onClick={() => close()} disabled={createCustomerMutation.isPending}>Discard</ActionButton>
        </ButtonsContainer>
      </SecondaryPanelCardSection>
    </SecondaryPanelCard>
  )
}