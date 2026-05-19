import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Account } from '#/types/accountTypes'

interface AccountState {
  activeAccount: Account | null
  setActiveAccount: (account: Account | null) => void
  clearActiveAccount: () =>  void
}

export const useAccountStore = create<AccountState>()(
  persist(
    (set) => ({
      activeAccount: null,
      setActiveAccount: (account) => set({ activeAccount: account }),
      clearActiveAccount: () => set({ activeAccount: null }),
    }),
    {
      name: 'account-storage',
    },
  ),
)

export function useRequiredActiveAccount(): Account {
  const activeAccount = useAccountStore((state) => state.activeAccount);

  if (!activeAccount) {
    throw new Error(
      'useRequiredActiveAccount must be used within a component where an active account is guaranteed.'
    );
  }

  return activeAccount;
}
