import type { ItemResponse } from "#/api/catalogDtos"
import { useNavigate } from "@tanstack/react-router"
import { createColumnHelper } from "@tanstack/react-table"
import { useCallback, useMemo } from "react"
import { useItemsSuspenseQuery } from "#/queries/itemQuery"
import { HphilTable } from "#/ui/HphilTable"

const columnHelper = createColumnHelper<ItemResponse>()

export function ItemsTable() {
  const { data } = useItemsSuspenseQuery()
  const navigate = useNavigate()

  const columns = useMemo(() => [
    columnHelper.accessor('name', { header: 'Name' })
  ], [])

  const handleClick = useCallback((item: ItemResponse) => {
    navigate({
      to: '/items/$itemId',
      params: {
        itemId: item.id,
      },
    })
  }, [navigate])

  return <HphilTable data={data} columns={columns} onClick={handleClick} />
}
