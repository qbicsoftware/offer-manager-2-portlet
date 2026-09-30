-- Migration: drop legacy offer price columns
--
-- The `offers` table previously stored prices in legacy columns that the application never
-- mapped to its JPA entity. The computed prices are now persisted in the new columns added by
-- `0001_add_offer_price_columns.sql` (priceAfterTax, priceBeforeTax, vatRatio, discountAmount).
--
-- These legacy columns are no longer read or written by the application and can be dropped once
-- it has been confirmed that nothing else consumes them.
--
-- Apply with: mysql -u <user> -p <database> < 0002_drop_legacy_offer_price_columns.sql

ALTER TABLE offers
    DROP COLUMN totalPrice,
    DROP COLUMN netPrice,
    DROP COLUMN vat,
    DROP COLUMN totalDiscount;