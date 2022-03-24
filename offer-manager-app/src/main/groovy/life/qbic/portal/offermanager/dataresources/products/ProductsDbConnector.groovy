package life.qbic.portal.offermanager.dataresources.products

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.Product
import life.qbic.business.products.ProductCategory
import life.qbic.business.products.ProductDraft
import life.qbic.business.products.archive.ArchiveProductDataSource
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.list.ListProductsDataSource
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider

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
@CompileStatic
class ProductsDbConnector implements ArchiveProductDataSource, CreateProductDataSource, ListProductsDataSource {

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
  @Override
  List<Product> listProducts() throws DatabaseQueryException {
    String query = Queries.SELECT_ALL_PRODUCTS + "WHERE active = 1"
    try (Connection connection = provider.connect()) {
      final PreparedStatement statement = connection.prepareStatement(query)
      final ResultSet resultSet = statement.executeQuery()
      return convertResultSet(resultSet)
    } catch (SQLException e) {
      log.error("Unexpected exception: $e.message", e)
      throw new DatabaseQueryException("Unable to list all available products.")
    }
  }


  /**
   * Converts a result set into {@link Product}s and wraps them with a list.
   * If the result set is empty, an empty list is returned.
   * @param resultSet the result set to be converted
   * @return a list of products parsed from the result set. empty list if no product was found.
   */
  private static List<Product> convertResultSet(ResultSet resultSet) {
    final List<Product> products = new ArrayList<>()
    while (resultSet.next()) {
      Product product = parseProductFromResultSet(resultSet)
      products.add(product)
    }
    return products
  }

//  /**
//   * This method associates an offer with product items.
//   *
//   * @param items A list of product items of an offer
//   * @param offerId An offerId which references the offer containing the list of product items
//   */
//  void createOfferItems(List<ProductItem> items, int offerId) {
//    items.each {productItem ->
//      String query = "INSERT INTO productitem (productId, quantity, offerid) "+
//              "VALUE(?,?,?)"
//
//      Integer productId = getProductPrimaryId(productItem.product)
//              .orElseThrow({new DatabaseQueryException("Could not determine product primary id for ${productItem.product}!")})
//
//      provider.connect().withCloseable {
//        PreparedStatement preparedStatement = it.prepareStatement(query)
//        preparedStatement.setInt(1,productId)
//        preparedStatement.setDouble(2,productItem.quantity)
//        preparedStatement.setInt(3,offerId)
//
//        preparedStatement.execute()
//      }
//    }
//  }
//
//  private Optional<Integer> getProductPrimaryId(Product product) {
//    def result = findProductId(product)
//    Optional<Integer> productPrimaryId = Optional.ofNullable(result).map({ it as Integer })
//    return productPrimaryId
//  }
//
//  /**
//   * Searches for the product ID in the database
//   *
//   * @param connection through which the query is executed
//   * @param product for which the ID needs to be found
//   * @return the found ID
//   */
//  private int findProductId(Product product) {
//
//    List<Integer> foundId = []
//
//    provider.connect().withCloseable {
//      PreparedStatement preparedStatement = it.prepareStatement(Queries.FIND_ID_BY_PRODUCT_PROPERTIES)
//      preparedStatement.setString(1, getProductCategory(product))
//      preparedStatement.setString(2,product.description)
//      preparedStatement.setString(3,product.productName)
//      preparedStatement.setDouble(4,product.internalUnitPrice)
//      preparedStatement.setDouble(5,product.externalUnitPrice)
//      preparedStatement.setString(6,product.unit.value)
//      preparedStatement.setString(7,product.serviceProvider.name())
//
//      ResultSet result = preparedStatement.executeQuery()
//
//      while (result.next()){
//        foundId << result.getInt(1)
//      }
//    }
//    return foundId[0]
//  }

  /**
   * Searches if the properties contained in a productDraft are already contained in the database
   *
   * @param productDraft for which it is checked if it's already contained in the db
   * @return List containing all duplicate products stored in the DB for the provided productDraft
   */
  @Override
  List<Product> findDuplicateProducts(ProductDraft productDraft) {

    provider.connect().withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(Queries.FIND_ACTIVE_PRODUCTID_BY_PROPERTIES)
      preparedStatement.setString(1, productDraft.category.getLabel())
      preparedStatement.setString(2, productDraft.description)
      preparedStatement.setString(3, productDraft.name)
      preparedStatement.setDouble(4, productDraft.internalUnitPrice)
      preparedStatement.setDouble(5, productDraft.externalUnitPrice)
      preparedStatement.setString(6, productDraft.unit)
      preparedStatement.setString(7, productDraft.serviceProvider)

      ResultSet resultSet = preparedStatement.executeQuery()
      List<Product> products = []
      while (resultSet.next()) {
        String productId = resultSet.getString("productId")
        try {
          fetch(productId).ifPresent({ Product retrievedProduct ->
            products << retrievedProduct
          })
        }
        catch (DatabaseQueryException databaseQueryException) {
          log.warn("Could not check for duplicate Products for $productDraft.name  'Skipping.")
          log.debug("Could not check for duplicate Products for $productDraft.name. Skipping.", databaseQueryException)
        }
      }
      return products
    }
  }
//
//  /**
//   * Returns the product identifying running number given a productId
//   *
//   * @param productId String of productId stored in the DB e.g. "DS_1"
//   * @return identifier Long of the iterative identifying part of the productId
//   */
//  private static long parseProductId(String productIdText) {
//
//    ProductId productId = ProductId.from(productIdText)
//    // The first entry [0] contains the product type which is assigned automatically, no need to parse it.
//    long uniqueId = productId.getUniqueId()
//    return uniqueId
//  }
//
//  /**
//   * Returns the product category value of a product based on its implemented class
//   *
//   * @param product A product for which the category needs to be determined
//   * @return the type of the product or null
//   */
//  private static String getProductCategory(Product product){
//    if (product instanceof Sequencing) return ProductCategory.SEQUENCING.getValue()
//    if (product instanceof ProjectManagement) return ProductCategory.PROJECT_MANAGEMENT.getValue()
//    if (product instanceof PrimaryAnalysis) return ProductCategory.PRIMARY_BIOINFO.getValue()
//    if (product instanceof SecondaryAnalysis) return ProductCategory.SECONDARY_BIOINFO.getValue()
//    if (product instanceof DataStorage) return ProductCategory.DATA_STORAGE.getValue()
//    if (product instanceof ProteomicAnalysis) return ProductCategory.PROTEOMIC.getValue()
//    if (product instanceof MetabolomicAnalysis) return ProductCategory.METABOLOMIC.getValue()
//    if (product instanceof ExternalServiceProduct) return ProductCategory.EXTERNAL_SERVICE.getValue()
//
//    return null
//  }

//  /**
//   * Queries all items of an offer.
//   * @param offerPrimaryId The offer's primary key.
//   * @return A list of offer-associated product items.
//   */
//  List<ProductItem> getItemsForOffer(int offerPrimaryId) {
//    List<ProductItem> productItems = []
//    Connection connection = provider.connect()
//    connection.withCloseable {
//      PreparedStatement statement = it.prepareStatement(Queries.SELECT_ALL_ITEMS_FOR_OFFER)
//      statement.setInt(1, offerPrimaryId)
//      ResultSet result = statement.executeQuery()
//      while (result.next()) {
//        try {
//          Product product = rowResultToProduct(SqlExtensions.toRowResult(result))
//          double quantity = result.getDouble("quantity")
//          ProductItem item = new ProductItem(quantity, product)
//          productItems << item
//        } catch (IllegalArgumentException illegalArgumentException) {
//          log.warn("Could not parse product. Skipping.")
//          log.debug("Could not parse product. Skipping.", illegalArgumentException)
//        }
//      }
//    }
//    return productItems
//  }

  /**
   * A product is archived by setting it inactive
   * @param product The product that needs to be archived
   * @since 1.0.0
   * @throws life.qbic.business.exceptions.DatabaseQueryException
   */
  @Override
  void archive(Product product) throws DatabaseQueryException {
    try (Connection connection = provider.connect()) {
      connection.withCloseable {
        def statement = connection.prepareStatement(Queries.ARCHIVE_PRODUCT)
        statement.setString(1, product.getProductId())
        statement.execute()
      }
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
  Optional<Product> fetch(String productId) throws DatabaseQueryException {

    String query = Queries.SELECT_ALL_PRODUCTS + "WHERE active = 1 AND productId=?"
    try (Connection connection = provider.connect()) {
      PreparedStatement preparedStatement = connection.prepareStatement(query)
      preparedStatement.setString(1, productId)
      ResultSet result = preparedStatement.executeQuery()
      while (result.next()) {
        return Optional.of(parseProductFromResultSet(result))
      }
      return Optional.empty()
    }
  }

  /**
   *
   * {@inheritDoc}
   */
  @Override
  Product store(ProductDraft productDraft) throws DatabaseQueryException {
    Connection connection = provider.connect()

    String productId = composeProductId(productDraft.getCategory())

    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(Queries.INSERT_PRODUCT)
      preparedStatement.setString(1, productDraft.category.getLabel())
      preparedStatement.setString(2, productDraft.description)
      preparedStatement.setString(3, productDraft.name)
      preparedStatement.setDouble(4, productDraft.internalUnitPrice)
      preparedStatement.setDouble(5, productDraft.externalUnitPrice)
      preparedStatement.setString(6, productDraft.unit)
      preparedStatement.setString(7, productId)
      preparedStatement.setString(8, productDraft.serviceProvider)

      ResultSet resultSet = preparedStatement.executeQuery()
      List<Product> createdProducts = convertResultSet(resultSet)
      assert createdProducts.size() == 1: "Exactly one product was created."
      return createdProducts.first()
    }
    return null
  }

  private static Product parseProductFromResultSet(ResultSet resultSet) {
    String dbCategory = resultSet.getString(2)
    String dbDescription = resultSet.getString(3)
    String dbProductName = resultSet.getString(4)
    double dbInternalUnitPrice = resultSet.getDouble(5)
    double dbExternalUnitPrice = resultSet.getDouble(6)
    String dbProductUnit = resultSet.getString(7)
    String dbProductId = resultSet.getString(8)
    String dbServiceProvider = resultSet.getString(9)

    Product result = new Product(dbCategory, dbInternalUnitPrice, dbExternalUnitPrice)
    result.setDescription(dbDescription)
    result.setProductName(dbProductName)
    result.setUnit(dbProductUnit)
    result.setProductId(dbProductId)
    result.setServiceProvider(dbServiceProvider)
    return result
  }

  private String composeProductId(ProductCategory productCategory) {
    String abbreviation = productCategory.getAbbreviation()
    Long version = fetchLatestIdentifierForCategory(productCategory)
    return String.format("%s_%d", abbreviation, version)
  }

  private Long fetchLatestIdentifierForCategory(ProductCategory productCategory) {
    String query = "SELECT productId FROM product WHERE productId LIKE ?"
    Long latestUniqueId = 0
    try (Connection connection = provider.connect()) {
      PreparedStatement preparedStatement = connection.prepareStatement(query)

      String categorySearchExpression = productCategory.getAbbreviation() + '_%'
      preparedStatement.setString(1, categorySearchExpression)

      ResultSet result = preparedStatement.executeQuery()

      while (result.next()) {
        String id = result.getString("productId")
        if (id) {
          assert id.contains('_'): "The stored product id contains an underscore `_`"
          String[] idParts = id.split('_')
          assert idParts[0].equals(productCategory.getAbbreviation()): "The product category is the first part of the product id."
          idParts[1]
          long idRunningNumber = Long.parseUnsignedLong(idParts[1])
          if (idRunningNumber > latestUniqueId){
            latestUniqueId = idRunningNumber
          }
        }
      }
    }
    return latestUniqueId + 1
  }
/**
 * Class that encapsulates the available SQL queries.
 */
  private static class Queries {

    /**
     * Query for inserting a product.
     */
    final static String INSERT_PRODUCT = "INSERT INTO product (category, description, productName, internalUnitPrice, externalUnitPrice, unit, productId, serviceProvider) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

    /**
     * Inactivate a product
     */
    final static String ARCHIVE_PRODUCT = "UPDATE product SET active = 0 WHERE productId = ?"

    /**
     * Query for all available products.
     */
    final static String SELECT_ALL_PRODUCTS = "SELECT * FROM product "

//    /**
//     * Query for all items of an offer.
//     */
//    final static String SELECT_ALL_ITEMS_FOR_OFFER =
//            "SELECT * FROM productitem " +
//                    "LEFT JOIN product ON productitem.productId = product.id " +
//                    "WHERE offerId=?;"
//
//    /**
//     * Query for SQL table Id of a product by product properties
//     */
//    final static String FIND_ID_BY_PRODUCT_PROPERTIES = "SELECT id FROM product "+
//            "WHERE category = ? AND description = ? AND productName = ? AND internalUnitPrice = ? AND externalUnitPrice = ? AND unit = ? AND serviceProvider = ?"

    /**
     * Query for ProductId of a product by product properties
     */
    final static String FIND_ACTIVE_PRODUCTID_BY_PROPERTIES = "SELECT productId FROM product " +
            "WHERE category = ? AND description = ? AND productName = ? AND internalUnitPrice = ? AND externalUnitPrice = ? AND unit = ? AND serviceProvider = ? AND active = 1"
  }
}
