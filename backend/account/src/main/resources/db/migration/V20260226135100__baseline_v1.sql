CREATE TABLE accounts (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  owner_user_id uuid NOT NULL,
  "name" TEXT NOT NULL,
  document TEXT NOT NULL,
  email TEXT NOT NULL,
  phone TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX index_accounts_owner_user_id_id ON accounts(owner_user_id);

CREATE TABLE addresses (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  street TEXT NOT NULL,
  "number" TEXT NOT NULL,
  complement TEXT NOT NULL,
  neighborhood TEXT NOT NULL,
  city TEXT NOT NULL,
  state TEXT NOT NULL,
  zip_code TEXT NOT NULL,
  country TEXT NOT NULL
);

CREATE INDEX index_addresses_account_id ON addresses(account_id);

CREATE TABLE bank_accounts (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_number TEXT NOT NULL,
  swift_code TEXT NOT NULL,
  bank_name TEXT NOT NULL,
  bank_address TEXT NOT NULL
);

CREATE TABLE banking (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  beneficiary_name TEXT NOT NULL,
  beneficiary_account_id uuid NOT NULL REFERENCES bank_accounts(id),
  intermediary_account_id uuid REFERENCES bank_accounts(id) ON DELETE SET NULL
);

CREATE INDEX index_banking_account_id ON banking(account_id);
CREATE INDEX index_banking_beneficiary_account_id ON banking(beneficiary_account_id);
CREATE INDEX index_banking_intermediary_account_id ON banking(intermediary_account_id);

