package life.qbic.portal.offermanager.dataresources.products

import groovy.sql.GroovyRowResult
import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.products.Converter
import life.qbic.business.products.archive.ArchiveProductDataSource
import life.qbic.business.products.copy.CopyProductDataSource
import life.qbic.business.products.create.CreateProductDataSource
import life.qbic.business.products.create.ProductExistsException
import life.qbic.business.products.list.ListProductsDataSource
import life.qbic.datamodel.dtos.business.ProductCategory
import life.qbic.datamodel.dtos.business.ProductCategoryFactory
import life.qbic.datamodel.dtos.business.ProductId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.facilities.Facility
import life.qbic.datamodel.dtos.business.services.*
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
class ProductsDbConnector implements ArchiveProductDataSource, CreateProductDataSource, CopyProductDataSource, ListProductsDataSource {

  private final ConnectionProvider provider

  private static final ProductCategoryFactory productCategoryFactory = new ProductCategoryFactory()
  private static final ProductUnitFactory productUnitFactory = new ProductUnitFactory()

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
    try {
      return fetchAllProductsFromDb()
    } catch (SQLException e) {
      log.error("Unexpected exception: $e.message")
      log.debug("Unexpected exception: $e.message", e)
      throw new DatabaseQueryException("Unable to list all available products.")
    }
  }

  private List<Product> fetchAllProductsFromDb() {
    List<Product> products = new ArrayList<>()
    String query = Queries.SELECT_ALL_PRODUCTS + "WHERE active = 1"
    provider.connect().withCloseable {
      final PreparedStatement statement = it.prepareStatement(query)
      final ResultSet resultSet = statement.executeQuery()
      products.addAll(convertResultSet(resultSet))
    }
    return products
  }

  private static List<Product> convertResultSet(ResultSet resultSet) {
    final List<Product> products = new ArrayList<>()
    while (resultSet.next()) {
      try {
        Product product = rowResultToProduct(SqlExtensions.toRowResult(resultSet))
        products.add(product)
      } catch (IllegalArgumentException illegalRow) {
        log.warn("Could not parse row. Skipping.", illegalRow)
        log.debug("Could not parse row. Skipping.", illegalRow)
      }
    }
    return products
  }

  /**
   *
   * @param row a GroovyRowResult map
   * @return a Product parsed from the provided map
   * @throws IllegalArgumentException in case not all fields necessary are found
   *  or fields could not be parsed
   */
  private static Product rowResultToProduct(GroovyRowResult row) throws IllegalArgumentException {
    Product product
    try {
      String description = row.description
      ProductCategory productCategory = productCategoryFactory.getForString(row.category as String)
      long productId = parseProductId(row.productId as String)
      String productName = row.productName
      ProductUnit productUnit = productUnitFactory.getForString(row.unit as String)
      double internalUnitPrice = row.internalUnitPrice
      double externalUnitPrice = row.externalUnitPrice
      Facility serviceProvider = Facility.valueOf(row.serviceProvider as String)


      product = Converter.createProductWithVersion(
              productCategory,
              productName,
              description,
              internalUnitPrice,
              externalUnitPrice,
              productUnit,
              productId,
              serviceProvider)

    } catch (NullPointerException | IllegalArgumentException illegalArgument) {
      throw new IllegalArgumentException("Could not parse product from provided information.", illegalArgument)
    }
    return product
  }

  /**
   * This method associates an offer with product items.
   *
   * @param items A list of product items of an offer
   * @param offerId An offerId which references the offer containing the list of product items
   */
  void createOfferItems(List<ProductItem> items, int offerId) {
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
            "WHERE category = ? AND description = ? AND productName = ? AND internalUnitPrice = ? AND externalUnitPrice = ? AND unit = ? AND serviceProvider = ?"

    List<Integer> foundId = []

    provider.connect().withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(query)
      preparedStatement.setString(1, getProductType(product))
      preparedStatement.setString(2,product.description)
      preparedStatement.setString(3,product.productName)
      preparedStatement.setDouble(4,product.internalUnitPrice)
      preparedStatement.setDouble(5,product.externalUnitPrice)
      preparedStatement.setString(6,product.unit.value)
      preparedStatement.setString(7,product.serviceProvider.name())

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
  private static long parseProductId(String productId) throws NumberFormatException{
    if (!productId.contains("_")) {
      throw new IllegalArgumentException("Not a valid product identifier.")
    }
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
  private static String getProductType(Product product){
    if (product instanceof Sequencing) return ProductCategory.SEQUENCING.getValue()
    if (product instanceof ProjectManagement) return ProductCategory.PROJECT_MANAGEMENT.getValue()
    if (product instanceof PrimaryAnalysis) return ProductCategory.PRIMARY_BIOINFO.getValue()
    if (product instanceof SecondaryAnalysis) return ProductCategory.SECONDARY_BIOINFO.getValue()
    if (product instanceof DataStorage) return ProductCategory.DATA_STORAGE.getValue()
    if (product instanceof ProteomicAnalysis) return ProductCategory.PROTEOMIC.getValue()
    if (product instanceof MetabolomicAnalysis) return ProductCategory.METABOLOMIC.getValue()

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
        try {
          Product product = rowResultToProduct(SqlExtensions.toRowResult(result))
          double quantity = result.getDouble("quantity")
          ProductItem item = new ProductItem(quantity, product)
          productItems << item
        } catch (IllegalArgumentException illegalArgumentException) {
          log.warn("Could not parse product. Skipping.")
          log.debug("Could not parse product. Skipping.", illegalArgumentException)
        }
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
        try {
          product = Optional.of(rowResultToProduct(SqlExtensions.toRowResult(result)))
        } catch(IllegalArgumentException illegalArgumentException) {
          log.warn("Could not parse product. Skipping.")
          log.debug("Could not parse product. Skipping.", illegalArgumentException)
        }
      }
    }
    return product
  }

  /**
   *
   * {@inheritDoc}
   */
  @Override
  ProductId store(Product product) throws DatabaseQueryException, ProductExistsException {
    Connection connection = provider.connect()

    ProductId productId = createProductId(product)

    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(Queries.INSERT_PRODUCT)
      preparedStatement.setString(1, getProductType(product))
      preparedStatement.setString(2, product.description)
      preparedStatement.setString(3, product.productName)
      preparedStatement.setDouble(4, product.internalUnitPrice)
      preparedStatement.setDouble(5, product.externalUnitPrice)
      preparedStatement.setString(6, product.unit.value)
      preparedStatement.setString(7, productId.toString())
      preparedStatement.setString(8, product.serviceProvider.name())

      preparedStatement.execute()
    }

    return productId
  }

  private ProductId createProductId(Product product){
    String productType = product.productId.type
    String version = fetchLatestIdentifier(productType) //todo exchange with long

    return new ProductId(productType,version)
  }

  private Long fetchLatestIdentifier(String productType){
    String query = "SELECT productId FROM product WHERE productId LIKE ?"
    Connection connection = provider.connect()

    String category = productType + "_%"
    Long latestUniqueId = 0

    connection.withCloseable {
      PreparedStatement preparedStatement = it.prepareStatement(query)
      preparedStatement.setString(1, category)

      ResultSet result = preparedStatement.executeQuery()

      while(result.next()){
        String id = result.getString(1)
        if(id) {
          long idRunningNumber = Long.parseLong(id.split('_')[1])
          if(idRunningNumber > latestUniqueId) latestUniqueId = idRunningNumber
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

    /**
     * Query for all items of an offer.
     */
    final static String SELECT_ALL_ITEMS_FOR_OFFER =
            "SELECT * FROM productitem " +
                    "LEFT JOIN product ON productitem.productId = product.id " +
                    "WHERE offerId=?;"

  }
}
