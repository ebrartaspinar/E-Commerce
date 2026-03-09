#!/bin/bash
set -e

echo "Creating databases..."

for db in user_service_db product_service_db order_service_db payment_service_db notification_service_db; do
    echo "Creating database: $db"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
        CREATE DATABASE $db;
        GRANT ALL PRIVILEGES ON DATABASE $db TO $POSTGRES_USER;
EOSQL
done

echo "All databases created successfully!"
