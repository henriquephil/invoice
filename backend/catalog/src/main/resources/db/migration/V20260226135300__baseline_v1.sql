CREATE TABLE customers (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL,
  "name" TEXT NOT NULL,
  document TEXT NOT NULL,
  email TEXT NOT NULL,
  phone TEXT NOT NULL,
  address_street TEXT NOT NULL,
  address_number TEXT NOT NULL,
  address_complement TEXT NOT NULL,
  address_neighborhood TEXT NOT NULL,
  address_city TEXT NOT NULL,
  address_state TEXT NOT NULL,
  address_zip_code TEXT NOT NULL,
  address_country TEXT NOT NULL
);

CREATE INDEX index_customers_account_id ON customers(account_id);

CREATE TABLE items (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL,
  "type" VARCHAR(10) NOT NULL,
  "name" TEXT NOT NULL,
  measure_unit TEXT NOT NULL,
  unit_price DECIMAL(20, 2) NOT NULL,
  currency TEXT NOT NULL
);

CREATE INDEX index_items_account_id ON items(account_id);
