import { Outlet, createFileRoute } from '@tanstack/react-router'
import '../styles.css'
import { ErrorBoundary } from 'react-error-boundary'
import { ErrorFallback } from '#/ui/ErrorFallback'
import { Suspense } from 'react'
import { LoadingContent } from '#/ui/layout'
import Menu from '#/ui/Menu'

export const Route = createFileRoute('/_authenticated')({
  beforeLoad: async ({ location }) => {
    // TODO check is authenticated
  },
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <>
      <Menu />
      <ErrorBoundary FallbackComponent={ErrorFallback}>
        <Suspense fallback={<LoadingContent />}>
          <Outlet />
        </Suspense>
      </ErrorBoundary>
    </>
  )
}
