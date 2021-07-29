package life.qbic.business.products

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.ProductUnit

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
class Product {
    private ProductCategory category
    private String name
    private String description
    private double internalUnitPrice
    private double externalUnitPrice
    private ProductUnit unit
    private ProductId id
    private Facility serviceProvider

    static class Builder{
        ProductCategory category
        String name
        String description
        double internalUnitPrice
        double externalUnitPrice
        ProductUnit unit
        ProductId id
        Facility serviceProvider

        Builder(ProductCategory category, String name, String description, ProductUnit unit){
            this.category = Objects.requireNonNull(category)
            this.name = Objects.requireNonNull(name)
            this.description = Objects.requireNonNull(description)
            this.internalUnitPrice = 0.0
            this.externalUnitPrice = 0.0
            this.unit = Objects.requireNonNull(unit)
            this.serviceProvider = Facility.QBIC
        }

        Builder(ProductCategory category, String name, String description, double internalUnitPrice, double externalUnitPrice, ProductUnit unit, Facility serviceProvider){
            this.category = Objects.requireNonNull(category)
            this.name = Objects.requireNonNull(name)
            this.description = Objects.requireNonNull(description)
            this.internalUnitPrice = Objects.requireNonNull(internalUnitPrice)
            this.externalUnitPrice = Objects.requireNonNull(externalUnitPrice)
            this.unit = Objects.requireNonNull(unit)
            this.serviceProvider = serviceProvider
        }

        Builder id(ProductId id){
            this.id = id
            return this
        }

        Builder serviceProvider(Facility serviceProvider) {
            this.serviceProvider = serviceProvider
            return this
        }

        Product build(){
            return new Product(this)
        }
    }

    Product(Builder builder){
        this.category = builder.category
        this.description = builder.description
        this.externalUnitPrice = builder.externalUnitPrice
        this.serviceProvider = builder.serviceProvider
        this.internalUnitPrice = builder.internalUnitPrice
        this.name = builder.name
        this.unit = builder.unit
    }

    /**
     * Calculates the SHA checksum for the product
     * The checksum is computed based on the product name, description, unit, unit price and the category
     *
     * @return a string containing the checksum for this product
     */
    String checksum(){
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        return getProductChecksum(digest,this)
    }

    /**
     * Compute the checksum for a product based on the encryption method provided
     *
     * @param digest The digestor will digest the message that needs to be encrypted
     * @param product Contains the product information
     * @return a string that encrypts the product object
     */
    private static String getProductChecksum(MessageDigest digest, Product product)
    {
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
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1))
        }

        //return complete hash
        return sb.toString()
    }

    ProductCategory getCategory() {
        return category
    }

    String getName() {
        return name
    }

    String getDescription() {
        return description
    }

    double getInternalUnitPrice() {
        return internalUnitPrice
    }

    double getExternalUnitPrice() {
        return externalUnitPrice
    }

    Facility getServiceProvider() {
        return serviceProvider
    }

    ProductUnit getUnit() {
        return unit
    }

    ProductId getId() {
        return id
    }
}
