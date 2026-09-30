-- Complete schema of the `offers` table
--
-- This is the reference schema of the `offers` table as the JPA entity `OfferV2` expects it
-- (hbm2ddl.auto=validate). It reflects the real database schema plus the columns added by the
-- migrations in this directory.
--
-- This is a reference for review; the table already exists and contains data, so do not run this
-- as a script to recreate it.

CREATE TABLE offers (
    id                          INT(11)            NOT NULL AUTO_INCREMENT,
    creationDate                DATE               NULL,
    expirationDate              DATE               NULL,
    customerId                  INT(11) UNSIGNED   NULL,
    projectManagerId            INT(11) UNSIGNED   NULL,
    projectTitle                VARCHAR(500)       NULL,
    projectDescription          VARCHAR(2500)      NULL,
    projectObjective            VARCHAR(2500)      NULL,
    totalPrice                  DOUBLE(11,2)       NULL,
    totalDiscount               DOUBLE(11,2)       NOT NULL DEFAULT 0.00,
    customerAffiliationId       INT(11) UNSIGNED   NULL,
    offerId                     VARCHAR(45)        NULL,
    vat                         DOUBLE(11,2)       NULL,
    netPrice                    DOUBLE(11,2)       NULL,
    overheads                   DOUBLE(11,2)       NULL,
    checksum                    VARCHAR(65)        NULL,
    associatedProject           VARCHAR(150)       NULL,
    experimentalDesign          VARCHAR(2500)      NULL,

    -- Columns added by 0001_add_offer_price_columns.sql (frozen computed prices)
    overheadRatio               DOUBLE             NULL,
    dataAnalysisOverhead        DECIMAL(19,2)      NULL,
    dataGenerationOverhead      DECIMAL(19,2)      NULL,
    dataManagementOverhead      DECIMAL(19,2)      NULL,
    externalServiceOverhead     DECIMAL(19,2)      NULL,
    dataAnalysisSalePrice       DECIMAL(19,2)      NULL,
    dataGenerationSalePrice     DECIMAL(19,2)      NULL,
    dataManagementSalePrice     DECIMAL(19,2)      NULL,
    externalServiceSalePrice    DECIMAL(19,2)      NULL,
    salePrice                   DECIMAL(19,2)      NULL,
    vatRatio                    DECIMAL(19,2)      NULL,
    priceAfterTax               DECIMAL(19,2)      NULL,
    taxAmount                   DECIMAL(19,2)      NULL,
    priceBeforeTax              DECIMAL(19,2)      NULL,
    discountAmount              DECIMAL(19,2)      NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_offerId (offerId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;