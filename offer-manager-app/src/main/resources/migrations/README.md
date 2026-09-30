# Database migrations

This directory contains SQL scripts for schema changes of the offer manager database.

## Applying migrations

Migrations are applied manually with the `mysql` client. They are not executed automatically by
the application. The application validates the schema against its JPA entity mappings on startup
(`hbm2ddl.auto=validate`) and will fail to start if a migration is missing.

Apply the scripts in order:

```sh
mysql -u <user> -p <database> < migrations/0001_add_offer_price_columns.sql
```

## Scripts

| Script | Description | When to apply |
|---|---|---|
| `0001_add_offer_price_columns.sql` | Adds columns that persist the computed offer prices so an offer is frozen at creation time. | Required before deploying the price-freezing change. |
| `0002_drop_legacy_offer_price_columns.sql` | Drops the unused legacy price columns (`totalPrice`, `netPrice`, `vat`, `totalDiscount`). | Optional cleanup; only after confirming nothing else reads these columns. |

## Schema

`schema.sql` contains the complete DDL of the `offers` table as it should look after applying all
migrations. Use it as a reference, not as a script that recreates the table (the database already
exists and contains data).