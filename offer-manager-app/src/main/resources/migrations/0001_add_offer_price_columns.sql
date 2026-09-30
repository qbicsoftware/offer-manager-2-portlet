-- Migration: persist computed offer prices
--
-- Freezes the computed prices of an offer at creation time. Add the new price columns to the
-- `offers` table. Existing rows have NULL in these columns until they are loaded once by the
-- application, which then computes and persists their prices (legacy backfill).
--
-- Apply with: mysql -u <user> -p <database> < add_offer_price_columns.sql

ALTER TABLE offers
    ADD COLUMN overheadRatio DOUBLE NULL,
    ADD COLUMN dataAnalysisOverhead DECIMAL(19,2) NULL,
    ADD COLUMN dataGenerationOverhead DECIMAL(19,2) NULL,
    ADD COLUMN dataManagementOverhead DECIMAL(19,2) NULL,
    ADD COLUMN externalServiceOverhead DECIMAL(19,2) NULL,
    ADD COLUMN dataAnalysisSalePrice DECIMAL(19,2) NULL,
    ADD COLUMN dataGenerationSalePrice DECIMAL(19,2) NULL,
    ADD COLUMN dataManagementSalePrice DECIMAL(19,2) NULL,
    ADD COLUMN externalServiceSalePrice DECIMAL(19,2) NULL,
    ADD COLUMN salePrice DECIMAL(19,2) NULL,
    ADD COLUMN vatRatio DECIMAL(19,2) NULL,
    ADD COLUMN priceAfterTax DECIMAL(19,2) NULL,
    ADD COLUMN taxAmount DECIMAL(19,2) NULL,
    ADD COLUMN priceBeforeTax DECIMAL(19,2) NULL,
    ADD COLUMN discountAmount DECIMAL(19,2) NULL;