import { useRequiredActiveAccount } from "#/store/accountStore"
import AccountAddress from "./sections/AccountAddress"
import AccountBanking from "./sections/AccountBanking"

export default function ActiveAccountDetails() {
  const account = useRequiredActiveAccount()
  return <div>
    <div>
      <div>
        <label>Name</label>
        <span>{account.name}</span>
      </div>
      <div>
        <label>Document</label>
        <span>{account.document}</span>
      </div>
      <div>
        <label>Email</label>
        <span>{account.email}</span>
      </div>
      <div>
        <label>Phone</label>
        <span>{account.phone}</span>
      </div>
    </div>
    <AccountAddress />
    <AccountBanking />
  </div>
}

