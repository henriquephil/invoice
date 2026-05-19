CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  username TEXT NOT NULL,
  "password" TEXT NOT NULL,
  "name" TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
ALTER TABLE users ADD CONSTRAINT unique_users_username UNIQUE (username);

CREATE TABLE authentication_tokens (
  id uuid PRIMARY KEY DEFAULT uuidv7(),
  user_id uuid NOT NULL,
  refresh_token TEXT NOT NULL,
  refresh_token_expiration TIMESTAMP WITH TIME ZONE NOT NULL,
  device_name TEXT NOT NULL,
  device_id TEXT NOT NULL,
  ip_address TEXT NOT NULL,
  revoked BOOLEAN NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT fk_authentication_tokens_user_id__id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
ALTER TABLE authentication_tokens ADD CONSTRAINT unique_authentication_tokens_refresh_token UNIQUE (refresh_token);

CREATE TABLE jwk (
  id uuid PRIMARY KEY,
  key_json TEXT NOT NULL,
  status TEXT NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

