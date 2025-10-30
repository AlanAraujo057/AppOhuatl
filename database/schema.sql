-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  full_name TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Wallets (Stellar addresses per user)
CREATE TABLE wallets (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  stellar_address TEXT NOT NULL,
  is_verified BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(user_id),
  UNIQUE(stellar_address)
);

-- Plantations registered by users
CREATE TABLE plantations (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  name TEXT NOT NULL,
  location TEXT,
  area_hectares NUMERIC(12, 2),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CO2 readings captured (kilograms)
CREATE TABLE readings (
  id UUID PRIMARY KEY,
  plantation_id UUID NOT NULL REFERENCES plantations(id),
  co2_kg NUMERIC(18, 6) NOT NULL CHECK (co2_kg >= 0),
  reading_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Token mints executed for users
CREATE TABLE token_mints (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  amount_tokens NUMERIC(18, 6) NOT NULL CHECK (amount_tokens > 0),
  tx_hash TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tx_hash)
);

-- Materialized or view candidates (examples)
-- CREATE VIEW user_carbon_balance AS ...;
