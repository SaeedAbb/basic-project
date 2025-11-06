-- Initialize the database for Basis project
-- This script runs automatically when the PostgreSQL container starts

-- Ensure the database and user exist
CREATE DATABASE basis_db;
CREATE USER basis_user WITH ENCRYPTED PASSWORD 'basis_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE basis_db TO basis_user;

-- Connect to the basis database
\c basis_db;

-- Create schema
CREATE SCHEMA IF NOT EXISTS basis;

-- Grant schema privileges
GRANT ALL ON SCHEMA basis TO basis_user;
GRANT ALL ON ALL TABLES IN SCHEMA basis TO basis_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA basis TO basis_user;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create a comment for documentation
COMMENT ON DATABASE basis_db IS 'Basis Project Database - Spring Boot + Angular Monorepo';

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'Basis database initialized successfully at %', NOW();
END
$$;