package life.qbic.portal.offermanager.dataresources.products

import groovy.sql.GroovyRowResult
import groovy.util.logging.Log4j2
import life.qbic.business.products.Converter
import life.qbic.business.products.archive.ArchiveProductDataSource
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.ProductExistsException
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.services.*
import life.qbic.business.exceptions.DatabaseQueryException

import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import org.apache.groovy.sql.extensions.SqlExtensions

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * This MariaDb connector offers access to available products.
 *
 * @since 1.0.0
 */
@Log4j2
class ProductsDbConnector implements ArchiveProductDataSource, CreateProductDataSource {

  private final ConnectionProvider provider

  /**
   * Creates a connector for a MariaDB instance.
   *
   * The class instantiation will fail, if the passed provider is null.
   *
   * @param provider A connection provider
   */
  ProductsDbConnector(ConnectionProvider provider) {
    this.provider = Objects.requireNonNull(provider, "Provider must not be null.")
  }

  /**
   * Queries a data source for all available service
   * product that have been defined by the organisation.
   *
   * Throws a {@link DatabaseQueryException} if the query
   * fails for some reason. An exception must NOT be thrown,
   * if no product can be found. The returned list needs to
   * be empty then.
   *
   * @return A list of service {@link Product}.
   * @throws DatabaseQueryException
   */
  List<Product> findAllAvailableProducts() throws DatabaseQueryException {
    try {
      return fetchAllProductsFromDb()
    } catch (SQLException e) {
      log.error(e.message)
      log.error(e.stackTrace.join("\n"))
      throw new DatabaseQueryException("Unable to list all available products.")
    }
  }

  private List<Product> fetchAllProductsFromDb() {
    List<Product> products = []
    String query = Queries.SELECT_ALL_PRODUCTS + "WHERE active = 1"
    provider.connect().withCloseable {
      final PreparedStatement statement = it.prepareStatement(query)
      final ResultSet resultSet = statement.executeQuery()
      products.addAll(convertResultSet(resultSet))
    }
    return products
  }

  private static List<Product> convertResultSet(ResultSet resultSet) {
    final def products = []
    while (resultSet.next()) {
      products.add(rowResultToProduct(SqlExtensions.toRowResult(resultSet)))
    }
    return products
  }

  private static Product rowResultToProduct(GroovyRowResult row) {
    def dbProductCategory = row.category
    String productId = row.productId
    ProductCategory productCategory
    switch(dbProductCategory) {
      case "Data Storage":
        productCategory = ProductCategory.DATA_STORAGE
        break
      case "Primary Bioinformatics":
        productCategory = ProductCategory.PRIMARY_BIOINFO
        break
      case "Project Management":
        productCategory = ProductCategory.PROJECT_MANAGEMENT
        break
      case "Secondary Bioinformatics":
        productCategory = ProductCategory.SECONDARY_BIOINFO
        break
      case "Sequencing":
        productCategory = ProductCategory.SEQUENCING
        break
    }

    if(!productCategory) {
      log.error("Product could not be parsed from database query.")
      log.error(row)
      throw new DatabaseQueryException("Cannot parse product")
    } else {
      return Converter.createProductWithVersion(productCategory,row.productName as String,
              row.description as String,
              row.unitPrice as Double,
              new ProductUnitFactory().getForString(row.unit as String), parseProductId(productId))
    }
  }

  def createOfferItems(List<ProductItem> items, int offerId) {

    items.each {productItem ->
      String query = "INSERT INTO productitem (productId, quantity, offerid) "+
              "VALUE(?,?,?)"

      int productId = findProductId(productItem.product)

      provider.connect().withCloseable {
        PreparedStatement preparedStatement = it.prepareStatement(query)
        preparedStatement.setInt(1,productId)
        preparedStatement.setDouble(2,productItem.quantity)
        preparedStatement.setInt(3,offerId)

        preparedStatement.execute()
      }
    }
  }

  /**
   * Searches for the product ID in the database
   *
   * @param connection through which the query is executed
   * @param product for which the ID needs to be found
   * @return the found ID
   */
  int findProductId(Product product) {
    String query = "SELECT id FROM product "+
            "WHERE category = ? AND description = ? AND productName = ? AND unitPrice = ? AND unit = ?"

    List<Integer> foundId = []

    provider.connect().withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(query)
      preparedStatement.setString(1, getProductType(product))
      preparedStatement.setString(2,product.description)
      preparedStatement.setString(3,product.productName)
      preparedStatement.setDouble(4,product.unitPrice)
      preparedStatement.setString(5,product.unit.value)
      ResultSet result = preparedStatement.executeQuery()

      while (result.next()){
        foundId << result.getInt(1)
      }
    }
    return foundId[0]
  }

  /**
   * Returns the product identifying running number given a productId
   *
   * @param productId String of productId stored in the DB e.g. "DS_1"
   * @return identifier Long of the iterative identifying part of the productId
   */
  static long parseProductId(String productId) throws NumberFormatException{
    def splitId = productId.split("_")
    // The first entry [0] contains the product type which is assigned automatically, no need to parse it.
    String identifier = splitId[1]
    return Long.parseLong(identifier)
  }


  /**
   * Returns the product type of a product based on its implemented class
   *
   * @param product A product for which the type needs to be determined
   * @return the type of the product or null
   */
  static String getProductType(Product product){
    if (product instanceof Sequencing) return 'Sequencing'
    if (product instanceof ProjectManagement) return 'Project Management'
    if (product instanceof PrimaryAnalysis) return 'Primary Bioinformatics'
    if (product instanceof SecondaryAnalysis) return 'Secondary Bioinformatics'
    if (product instanceof DataStorage) return 'Data Storage'

    return null
  }

  /**
   * Queries all items of an offer.
   * @param offerPrimaryId The offer's primary key.
   * @return A list of offer-associated product items.
   */
  List<ProductItem> getItemsForOffer(int offerPrimaryId) {
    List<ProductItem> productItems = []
    Connection connection = provider.connect()
    connection.withCloseable {
      PreparedStatement statement = it.prepareStatement(Queries.SELECT_ALL_ITEMS_FOR_OFFER)
      statement.setInt(1, offerPrimaryId)
      ResultSet result = statement.executeQuery()
      while (result.next()) {
        Product product = rowResultToProduct(SqlExtensions.toRowResult(result))
        double quantity = result.getDouble("quantity")
        ProductItem item = new ProductItem(quantity, product)
        productItems << item
      }
    }
    return productItems
  }

  /**
   * A product is archived by setting it inactive
   * @param product The product that needs to be archived
   * @since 1.0.0
   * @throws life.qbic.business.exceptions.DatabaseQueryException
   */
  @Override
  void archive(Product product) throws DatabaseQueryException {
    Connection connection = provider.connect()

    connection.withCloseable {
      def statement = connection.prepareStatement(Queries.ARCHIVE_PRODUCT)
      statement.setString(1, product.productId.toString())
      statement.execute()
    }
  }

  /**
   * Fetches a product from the database
   * @param productId The product id of the product to be fetched
   * @return returns an optional that contains the product if it has been found
   * @since 1.0.0
   * @throws life.qbic.business.exceptions.DatabaseQueryException is thrown when any technical interaction with the data source fails
   */
  @Override
  Optional<Product> fetch(ProductId productId) throws DatabaseQueryException {
    Connection connection = provider.connect()
    String query = Queries.SELECT_ALL_PRODUCTS + "WHERE active = 1 AND productId=?"
    Optional<Product> product = Optional.empty()

    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(query)
      preparedStatement.setString(1, productId.toString())
      ResultSet result = preparedStatement.executeQuery()

      while (result.next()) {
        product = Optional.of(rowResultToProduct(SqlExtensions.toRowResult(result)))
      }
    }
    return product
  }

  /**
   * Stores a product in the database
   * @param product The product that needs to be stored
   * @since 1.0.0
   * @throws DatabaseQueryException if any technical interaction with the data source fails
   * @throws ProductExistsException if the product already exists in the data source
   */
  @Override
  void store(Product product) throws DatabaseQueryException, ProductExistsException {
    Connection connection = provider.connect()

    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(Queries.INSERT_PRODUCT)
      preparedStatement.setString(1, getProductType(product))
      preparedStatement.setString(2, product.description)
      preparedStatement.setString(3, product.productName)
      preparedStatement.setDouble(4, product.unitPrice)
      preparedStatement.setString(5, product.unit.value)
      preparedStatement.setString(6, product.productId.toString())

      preparedStatement.execute()
    }
  }

  @Override
  Optional<ProductId> fetchLatestProductIdentifierVersion(ProductCategory productCategory) throws DatabaseQueryException {
    throw new IllegalArgumentException("not implemented")
  }
/**
   * Class that encapsulates the available SQL queries.
   */
  private static class Queries {

    /**
     * Query for inserting a product.
     */
    final static String INSERT_PRODUCT = "INSERT INTO product (category, description, productName, unitPrice, unit, productId) VALUES (?, ?, ?, ?, ?, ?)"

    /**
     * Inactivate a product
     */
    final static String ARCHIVE_PRODUCT = "UPDATE product SET active = 0 WHERE productId = ?"

    /**
     * Query for all available products.
     */
    final static String SELECT_ALL_PRODUCTS = "SELECT * FROM product "

    /**
     * Query for all items of an offer.
     */
    final static String SELECT_ALL_ITEMS_FOR_OFFER =
            "SELECT * FROM productitem " +
                    "LEFT JOIN product ON productitem.productId = product.id " +
                    "WHERE offerId=?;"

  }
}
