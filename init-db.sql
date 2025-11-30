-- -- Create a new role
-- CREATE USER admin WITH PASSWORD 'admin';

-- -- Create the databases
-- CREATE DATABASE product_db;
-- CREATE DATABASE inventory_db;

-- -- Grant all privileges on the databases to the new role
-- GRANT ALL PRIVILEGES ON DATABASE product_db TO admin;
-- GRANT ALL PRIVILEGES ON DATABASE inventory_db TO admin;


-- init-db.sql
-- DROP USER IF EXISTS admin; -- admin user is created by POSTGRES_USER env var
-- CREATE USER admin WITH PASSWORD 'admin' SUPERUSER; -- admin user is created by POSTGRES_USER env var
-- CREATE DATABASE product_db WITH OWNER admin; -- product_db is created by POSTGRES_DB env var
CREATE DATABASE inventory_db WITH OWNER admin;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE product_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO admin;

