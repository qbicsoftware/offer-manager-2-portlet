package life.qbic.business.products.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.products.Converter
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.services.Product

/**
 * <h1>4.3.0 Create Service Product</h1>
 * <br>
 * <p> When the service portfolio changed due to a business decision an Offer Administrator should be allowed to provide information on the new service offered and make it available to new offers upon creation.
 * </p>
 *
 * @since: 1.0.0

 *
 */
class CreateProduct implements CreateProductInput {
    private final CreateProductDataSource dataSource
    private final CreateProductOutput output
    private static final Logging log = Logger.getLogger(this.class)

    CreateProduct(CreateProductDataSource dataSource, CreateProductOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void create(Product product) {
        try {
            dataSource.store(buildNewProduct(product))
            output.created(product)
        } catch(DatabaseQueryException databaseQueryException) {
            log.error("Product creation failed", databaseQueryException)
            output.failNotification("Could not create product $product.productName with id $product.productId")
        } catch(ProductExistsException productExistsException) {
            log.warn("Product \"$product.productName\" already existed.", productExistsException)
            output.foundDuplicate(product)
        }
    }

    private Product buildNewProduct(Product product){
        ProductCategory category = Converter.getCategory(product)
        long id = getProductVersion(category)

        return Converter.createProductWithVersion(category,product.productName,product.description,product.unitPrice,product.unit,id)
    }

    private long getProductVersion(ProductCategory category){
        Optional<ProductId> id = dataSource.fetchLatestProductIdentifierVersion(category)

        //todo check that the number is positive
        if(id.isPresent()){
            return id.get().uniqueId + 1
        }else{
            throw new IllegalArgumentException("The provided Product has no Identifier in the Database")
        }
    }

}
