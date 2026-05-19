CREATE TABLE invoices (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  "status" TEXT NOT NULL,
  customer_id uuid,
  due_date DATE,
  "number" INTEGER,
  issued_at TIMESTAMP WITH TIME ZONE,
  currency TEXT,
  total_price DECIMAL(10, 2) NOT NULL
);

CREATE INDEX index_invoices_account_id ON invoices(account_id);
CREATE INDEX index_invoices_customer_id ON invoices(customer_id);

CREATE TABLE invoice_items (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  invoice_id uuid NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
  item_id uuid NOT NULL,
  unit_price DECIMAL(10, 2) NOT NULL,
  quantity DECIMAL(10, 2) NOT NULL,
  total_price DECIMAL(10, 2) NOT NULL,
  additional_info TEXT NOT NULL
);

CREATE INDEX index_invoice_items_invoice_id ON invoice_items(invoice_id);


CREATE TABLE snapshots (
  id uuid PRIMARY KEY,
  invoice_id uuid NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
  captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
  snapshot JSONB NOT NULL,
  version INTEGER NOT NULL
);

CREATE INDEX index_snapshots_invoice_id ON snapshots(invoice_id);


CREATE TABLE invoice_settings (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  account_id uuid NOT NULL,
  current_invoice_number INTEGER NOT NULL,
  CONSTRAINT unique_invoice_settings_account_id UNIQUE (account_id)
);