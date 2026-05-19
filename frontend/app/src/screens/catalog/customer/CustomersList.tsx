import type { CustomerResponse } from "#/api/catalogDtos"
import { HphilTable } from "#/ui/HphilTable"
import { useCustomersSuspenseQuery } from "#/queries/customerQuery"
import { createColumnHelper } from "@tanstack/react-table"
import { useMemo } from "react"

interface Props {
  onCustomerClick: (customer: CustomerResponse) => void
}

const columnHelper = createColumnHelper<CustomerResponse>()

export default function CustomersList({
  onCustomerClick
}: Props) {
  const { data } = useCustomersSuspenseQuery()

  const columns = useMemo(() => [
    columnHelper.accessor('name', { header: 'Name' })
  ], [])

  return <HphilTable data={data} columns={columns} onClick={onCustomerClick} />
}