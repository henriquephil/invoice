import { useCustomerSuspenseQuery, useUpdateCustomerMutation } from "#/queries/customerQuery"
import { useNavigate } from "@tanstack/react-router"
import { useState } from "react"
import { CustomerForm, CustomerFormDataSchema, type CustomerFormData } from "./CustomerForm"
import { getChangedValues } from "#/libs/object-utils"
import { ActionButton, SecondaryPanelCard, SecondaryPanelCardHeader, SecondaryPanelCardSection } from "#/ui/layout"
import { ButtonsContainer } from '#/ui/form/form'

export default function EditCustomerForm({ customerId }: { customerId: string }) {
  const { data: { address, ...info } } = useCustomerSuspenseQuery(customerId)
  const original = CustomerFormDataSchema.parse({ ...info, ...address })

  const updateCustomer = useUpdateCustomerMutation()
  const navigate = useNavigate()
  const [ customerData, setCustomerData ] = useState<CustomerFormData>(original)

  const close = () => {
    navigate({ to: '/customers' })
  }
  const save = () => {
    const changedData = getChangedValues(original, customerData)

    if (Object.keys(changedData).length === 0) {
      close()
      return
    }

    const { name, email, document, phone, ...address} = changedData
    updateCustomer.mutate(
      { customerId, customerData: { name, email, document, phone, address } },
      { onSuccess: () => close()}
    )
  }
  return (
    <SecondaryPanelCard>
      <SecondaryPanelCardHeader>
        {customerId}
      </SecondaryPanelCardHeader>
      <SecondaryPanelCardSection>
        <CustomerForm value={customerData} onChange={setCustomerData} />
      </SecondaryPanelCardSection>
      <SecondaryPanelCardSection>
        <ButtonsContainer>
          <ActionButton onClick={save}>Update</ActionButton>
          <ActionButton onClick={close}>Cancel</ActionButton>
        </ButtonsContainer>
      </SecondaryPanelCardSection>
    </SecondaryPanelCard>
  )
}