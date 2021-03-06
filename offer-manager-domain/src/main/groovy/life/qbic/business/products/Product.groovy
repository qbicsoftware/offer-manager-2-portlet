package life.qbic.business.products

import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
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
    private double unitPrice
    private ProductUnit unit
    private ProductId id

    static class Builder{
        ProductCategory category
        String name
        String description
        double unitPrice
        ProductUnit unit
        ProductId id

        Builder(ProductCategory category, String name, String description, double unitPrice, ProductUnit unit){
            this.category = Objects.requireNonNull(category)
            this.name = Objects.requireNonNull(name)
            this.description = Objects.requireNonNull(description)
            this.unitPrice = Objects.requireNonNull(unitPrice)
            this.unit = Objects.requireNonNull(unit)
        }

        Builder id(ProductId id){
            this.id = id
            return this
        }

        Product build(){
            return new Product(this)
        }
    }

    Product(Builder builder){
        this.category = builder.category
        this.name = builder.name
        this.description = builder.description
        this.unitPrice = builder.unitPrice
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

        digest.update(product.unit.value.getBytes(StandardCharsets.UTF_8))
        digest.update(product.unitPrice.toString().getBytes(StandardCharsets.UTF_8))
        digest.update(product.category.toString().getBytes(StandardCharsets.UTF_8))

        //Get the hash's bytes
        byte[] bytes = digest.digest()

        //This bytes[] has bytes in decimal format
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder()
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
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

    double getUnitPrice() {
        return unitPrice
    }

    ProductUnit getUnit() {
        return unit
    }

    ProductId getId() {
        return id
    }
}
