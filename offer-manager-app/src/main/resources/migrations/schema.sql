-- Complete expected database schema
--
-- Reference schema of the tables as they exist in the production database, plus the columns added
-- by 0001_add_offer_price_columns.sql on the `offers` table. This is the target state that
-- hbm2ddl.auto=validate expects (mapped columns present with compatible types).
--
-- The `offers` table includes the 15 new frozen-price columns; the other tables match the
-- production DDL as-is. This is a reference for review; do not run it to recreate the tables.
--
-- NOTE: `person_affiliation` DDL is reconstructed from the Person entity's @JoinTable mapping and
-- still needs to be confirmed against the real database (see end of file).

-- ---------------------------------------------------------------------------------------------
-- offers
-- ---------------------------------------------------------------------------------------------
CREATE TABLE offers (
    id                          INT(11)            NOT NULL AUTO_INCREMENT,
    creationDate                DATE               DEFAULT NULL,
    expirationDate              DATE               DEFAULT NULL,
    customerId                  INT(11) UNSIGNED   DEFAULT NULL,
    projectManagerId            INT(11) UNSIGNED   DEFAULT NULL,
    projectTitle                VARCHAR(500)       DEFAULT NULL,
    projectDescription          VARCHAR(2500)      DEFAULT NULL,
    projectObjective            VARCHAR(2500)      DEFAULT NULL,
    totalPrice                  DOUBLE(11,2)       DEFAULT NULL,
    totalDiscount               DOUBLE(11,2)       NOT NULL DEFAULT '0.00',
    customerAffiliationId       INT(11) UNSIGNED   DEFAULT NULL,
    offerId                     VARCHAR(45)        DEFAULT NULL,
    vat                         DOUBLE(11,2)       DEFAULT NULL,
    netPrice                    DOUBLE(11,2)       DEFAULT NULL,
    overheads                   DOUBLE(11,2)       DEFAULT NULL,
    checksum                    VARCHAR(65)        DEFAULT NULL,
    associatedProject           VARCHAR(150)       DEFAULT NULL,
    experimentalDesign          VARCHAR(2500)      DEFAULT NULL,

    -- Columns added by 0001_add_offer_price_columns.sql (frozen computed prices)
    overheadRatio               DOUBLE             DEFAULT NULL,
    dataAnalysisOverhead        DECIMAL(19,2)      DEFAULT NULL,
    dataGenerationOverhead      DECIMAL(19,2)      DEFAULT NULL,
    dataManagementOverhead      DECIMAL(19,2)      DEFAULT NULL,
    externalServiceOverhead     DECIMAL(19,2)      DEFAULT NULL,
    dataAnalysisSalePrice       DECIMAL(19,2)      DEFAULT NULL,
    dataGenerationSalePrice     DECIMAL(19,2)      DEFAULT NULL,
    dataManagementSalePrice     DECIMAL(19,2)      DEFAULT NULL,
    externalServiceSalePrice    DECIMAL(19,2)      DEFAULT NULL,
    salePrice                   DECIMAL(19,2)      DEFAULT NULL,
    vatRatio                    DECIMAL(19,2)      DEFAULT NULL,
    priceAfterTax               DECIMAL(19,2)      DEFAULT NULL,
    taxAmount                   DECIMAL(19,2)      DEFAULT NULL,
    priceBeforeTax              DECIMAL(19,2)      DEFAULT NULL,
    discountAmount              DECIMAL(19,2)      DEFAULT NULL,

    PRIMARY KEY (id),
    KEY offer_ibfk_1 (customerId),
    KEY offer_ibfk_2 (projectManagerId),
    KEY offer_ibfk_3 (customerAffiliationId),
    CONSTRAINT offers_ibfk_1 FOREIGN KEY (customerId) REFERENCES person (id),
    CONSTRAINT offers_ibfk_2 FOREIGN KEY (projectManagerId) REFERENCES person (id),
    CONSTRAINT offers_ibfk_3 FOREIGN KEY (customerAffiliationId) REFERENCES affiliation (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------------------------------------------------------
-- product
-- ---------------------------------------------------------------------------------------------
CREATE TABLE product (
    id                INT(11)      NOT NULL AUTO_INCREMENT,
    category          ENUM('Sequencing','Project Management','Primary Bioinformatics','Secondary Bioinformatics','Data Storage','Proteomics','Metabolomics','External Service') DEFAULT NULL,
    description       VARCHAR(2500) DEFAULT NULL,
    productName       VARCHAR(500) DEFAULT NULL,
    internalUnitPrice DOUBLE(11,2) NOT NULL DEFAULT '0.00',
    externalUnitPrice DOUBLE(11,2) NOT NULL DEFAULT '0.00',
    unit              ENUM('Flow cell','Gigabyte','Sample','Dataset','Hour','Project','Run','Cycle','Gel/HpH','10 milligram','Measurement','Channel','100 microgram peptides channel','500 milliliter','Comparison','Batch','Vial','Kit') DEFAULT NULL,
    productId         VARCHAR(45)  DEFAULT NULL,
    serviceProvider   ENUM('METABOLOMICS_FUNCTIONAL','METABOLOMICS_BACTERIAL','CFMB','CFMP','IMGAG','MGM','QBIC','CFMB_PCT','CFMP_PCT','PCT','CEGAT','METABOLOMICS') NOT NULL DEFAULT 'QBIC',
    active            TINYINT(1)   NOT NULL DEFAULT '1',

    PRIMARY KEY (id),
    UNIQUE KEY id_UNIQUE (id),
    UNIQUE KEY UQ_product_productId (productId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------------------------------------------------------
-- productitem
-- ---------------------------------------------------------------------------------------------
CREATE TABLE productitem (
    id                INT(11)       NOT NULL AUTO_INCREMENT,
    productId         INT(11)       NOT NULL,
    quantity          DOUBLE(11,2)  NOT NULL,
    offerId           INT(11)       NOT NULL,
    category          VARCHAR(45)   DEFAULT NULL,
    description       VARCHAR(2500) DEFAULT NULL,
    productName       VARCHAR(500)  DEFAULT NULL,
    internalUnitPrice DOUBLE(11,2)  DEFAULT NULL,
    externalUnitPrice DOUBLE(11,2)  DEFAULT NULL,
    unit              VARCHAR(45)   DEFAULT NULL,
    productReference  VARCHAR(45)   DEFAULT NULL,
    serviceProvider   VARCHAR(100)  DEFAULT NULL,
    offerPosition     INT(11)       DEFAULT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY id (id),
    KEY productitem_ibfk_1 (productId),
    CONSTRAINT productitem_ibfk_1 FOREIGN KEY (productId) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------------------------------------------------------
-- person
-- ---------------------------------------------------------------------------------------------
CREATE TABLE person (
    id           INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id      VARCHAR(256)     DEFAULT NULL,
    first_name   VARCHAR(45)      NOT NULL DEFAULT '',
    last_name    VARCHAR(45)      NOT NULL DEFAULT '',
    title        ENUM('None','Dr.','Prof. Dr.','PhD') NOT NULL,
    email        VARCHAR(256)     NOT NULL DEFAULT '',
    active       TINYINT(1)       NOT NULL DEFAULT '1',
    reference_id VARCHAR(100)     NOT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------------------------------------------------------
-- affiliation
-- ---------------------------------------------------------------------------------------------
CREATE TABLE affiliation (
    id               INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    organization     VARCHAR(128)     NOT NULL DEFAULT '',
    address_addition VARCHAR(128)     NOT NULL DEFAULT '',
    street           VARCHAR(64)      NOT NULL DEFAULT '',
    postal_code      VARCHAR(45)      NOT NULL DEFAULT '',
    city             VARCHAR(45)      NOT NULL DEFAULT '',
    country          VARCHAR(45)      NOT NULL DEFAULT '',
    category         ENUM('internal','external','external academic') NOT NULL,
    active           TINYINT(1)       NOT NULL DEFAULT '1',

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------------------------------------------------------
-- person_affiliation
--   NOTE: the real table has a surrogate `id` primary key and cascade-delete foreign keys, while
--   the Person entity maps it as a @ManyToMany join table keyed by person_id + affiliation_id.
--   The real DDL is reproduced below.
-- ---------------------------------------------------------------------------------------------
CREATE TABLE person_affiliation (
    id             INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    affiliation_id INT(11) UNSIGNED NOT NULL,
    person_id      INT(11) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    KEY affiliationFK (affiliation_id),
    KEY customerFK (person_id),
    CONSTRAINT affiliationFK FOREIGN KEY (affiliation_id) REFERENCES affiliation (id) ON DELETE CASCADE,
    CONSTRAINT customerFK FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;