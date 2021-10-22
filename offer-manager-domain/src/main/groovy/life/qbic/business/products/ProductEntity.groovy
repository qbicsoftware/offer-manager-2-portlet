package life.qbic.business.products


import life.qbic.business.products.dtos.ProductDraft
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * <h1>Represents the product business model</h1>
 * <br>
 * <p>This class should be used in the business context of product creation</p>
 *
 * @since 1.0.0
 *
 */
class ProductEntity {
    ProductCategory category
    String name
    String description
    double internalUnitPrice
    double externalUnitPrice
    ProductUnit unit
    Facility serviceProvider
    Optional<ProductId> id

    private ProductEntity(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility serviceProvider) {
        this.category = Objects.requireNonNull(category)
        this.name = Objects.requireNonNull(name)
        this.description = Objects.requireNonNull(description)
        this.internalUnitPrice = Objects.requireNonNull(internalUnitPrice)
        this.externalUnitPrice = Objects.requireNonNull(externalUnitPrice)
        this.unit = Objects.requireNonNull(unit)
        this.serviceProvider = serviceProvider
        this.id = Optional.empty()
    }

    /**
     * Sets the optional product id for this entity
     * @param id the id to be set. Sets Optional.empty in case of null
     */
    void id(ProductId id) {
        //Todo do we want to check for id.type matching category?
        this.id = Optional.ofNullable(id)
    }

    /**
     * Creates a new entity from a product draft
     * @param draft the draft containing information on the product
     * @return a product entity
     */
    static ProductEntity fromDraft(ProductDraft draft) {
        new ProductEntity(draft.category,
                draft.name,
                draft.description,
                draft.internalUnitPrice,
                draft.externalUnitPrice,
                draft.unit,
                draft.serviceProvider)
    }

    /**
     * Creates a new Entity from a product DTO
     * @param product the product DTO containing information on the product
     * @return a product entity
     */
    static ProductEntity fromDto(Product product) {
        ProductCategory productCategory = determineProductCategory(product)
        return new ProductEntity(productCategory,
                product.productName,
                product.description,
                product.internalUnitPrice,
                product.externalUnitPrice,
                product.unit,
                product.serviceProvider)
    }

    /**
     * Retrieves the category of the given product
     * @param product The product of a specific product category
     * @return the product category of the given product
     */
    static ProductCategory determineProductCategory(Product product) {
        if (product instanceof ProjectManagement) return ProductCategory.PROJECT_MANAGEMENT
        if (product instanceof Sequencing) return ProductCategory.SEQUENCING
        if (product instanceof PrimaryAnalysis) return ProductCategory.PRIMARY_BIOINFO
        if (product instanceof SecondaryAnalysis) return ProductCategory.SECONDARY_BIOINFO
        if (product instanceof DataStorage) return ProductCategory.DATA_STORAGE
        if (product instanceof ProteomicAnalysis) return ProductCategory.PROTEOMIC
        if (product instanceof MetabolomicAnalysis) return ProductCategory.METABOLOMIC
        if (product instanceof ExternalServiceProduct) return ProductCategory.EXTERNAL_SERVICE

        throw new IllegalArgumentException("Cannot parse category of the provided product ${product.toString()}")
    }

    /**
     * Parses to a product dto
     * @return a product dto
     */
    Product toFinalProduct() {
        ProductId productId = id.orElseThrow({ new RuntimeException("Can not finalize product without id.") })
        long runningNumber = productId.getUniqueId()
        Class productClass = determineProductClass(category)
        try {
            Product finalProduct = productClass.newInstance(name, description, internalUnitPrice, externalUnitPrice, unit, runningNumber, serviceProvider)
            return finalProduct
        } catch (RuntimeException runtimeException) {
            throw new RuntimeException("Could not finalize product. Class $productClass could not be instantiated: $runtimeException.message")
        }
    }

    private static Class<? extends Product> determineProductClass(ProductCategory category) {
        switch (category) {
            case ProductCategory.DATA_STORAGE:
                return DataStorage
            case ProductCategory.PRIMARY_BIOINFO:
                return PrimaryAnalysis
            case ProductCategory.PROJECT_MANAGEMENT:
                return ProjectManagement
            case ProductCategory.SECONDARY_BIOINFO:
                return SecondaryAnalysis
            case ProductCategory.SEQUENCING:
                return Sequencing
            case ProductCategory.PROTEOMIC:
                return ProteomicAnalysis
            case ProductCategory.METABOLOMIC:
                return MetabolomicAnalysis
            case ProductCategory.EXTERNAL_SERVICE:
                return ExternalServiceProduct
            default:
                throw new IllegalStateException("Unknown product category $category")
        }
    }


    /**
     * Calculates the SHA checksum for the product
     * The checksum is computed based on the product name, description, unit, unit price and the category.
     * <p>The checksum does not take the product id into account!</p>
     *
     * @return a string containing the checksum for this product
     */
    String checksum() {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        return getProductChecksum(digest, this)
    }

    /**
     * Compute the checksum for a product based on the encryption method provided
     *
     * @param digest The digestor will digest the message that needs to be encrypted
     * @param product Contains the product information
     * @return a string that encrypts the product object
     */
    private static String getProductChecksum(MessageDigest digest, ProductEntity product) {
        //digest crucial offer characteristics
        digest.update(product.name.getBytes(StandardCharsets.UTF_8))

        digest.update(product.description.getBytes(StandardCharsets.UTF_8))

        digest.update(product.category.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(product.externalUnitPrice.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(product.serviceProvider.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(product.internalUnitPrice.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(product.unit.value.getBytes(StandardCharsets.UTF_8))


        //Get the hash's bytes
        byte[] bytes = digest.digest()

        //This bytes[] has bytes in decimal format
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder()
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1))
        }

        //return complete hash
        return sb.toString()
    }


    @Override
    String toString() {
        return "ProductEntity{" +
                "category=" + category +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", internalUnitPrice=" + internalUnitPrice +
                ", externalUnitPrice=" + externalUnitPrice +
                ", unit=" + unit +
                ", serviceProvider=" + serviceProvider +
                ", id=" + id +
                '}'
    }
}
