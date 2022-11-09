package life.qbic.business;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import life.qbic.business.offers.OfferItem;
import life.qbic.business.offers.OfferV2;
import life.qbic.business.offers.identifier.OfferId;
import life.qbic.business.persons.Person;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.AffiliationCategory;
import life.qbic.business.products.Product;
import life.qbic.business.products.ProductCategory;
import life.qbic.business.products.ProductDraft;
import life.qbic.business.products.ProductItem;
import life.qbic.datamodel.dtos.business.AcademicTitle;
import life.qbic.datamodel.dtos.business.AcademicTitleFactory;
import life.qbic.datamodel.dtos.business.Customer;
import life.qbic.datamodel.dtos.business.ProductId;
import life.qbic.datamodel.dtos.business.facilities.Facility;
import life.qbic.datamodel.dtos.business.facilities.FacilityFactory;
import life.qbic.datamodel.dtos.business.services.DataStorage;
import life.qbic.datamodel.dtos.business.services.ExternalServiceProduct;
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis;
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis;
import life.qbic.datamodel.dtos.business.services.ProductUnit;
import life.qbic.datamodel.dtos.business.services.ProductUnitFactory;
import life.qbic.datamodel.dtos.business.services.ProjectManagement;
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis;
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis;
import life.qbic.datamodel.dtos.business.services.Sequencing;
import life.qbic.datamodel.dtos.general.CommonPerson;

/**
 * <b>ATTENTION: Only for refactor purposes. Meant to be removed.</b>
 */
public class RefactorConverter {


  public static OfferV2 toOffer(life.qbic.datamodel.dtos.business.Offer offerDto) {

    LocalDate creationDate = toLocalDate(offerDto.getModificationDate());
    Person customer = toPerson(offerDto.getCustomer());
    OfferId offerId = toOfferId(offerDto.getIdentifier());
    Person projectManager = toPerson(offerDto.getProjectManager());
    Affiliation selectedCustomerAffiliation = toAffiliation(
        offerDto.getSelectedCustomerAffiliation());

    OfferV2 offer = new OfferV2();
    offer.setPersistentId(offerDto.getId());
    offer.setAssociatedProject(offerDto.getAssociatedProject().orElse(null));
    offer.setCreationDate(creationDate);
    offer.setCustomer(customer);
    offer.setExperimentalDesign(offerDto.getExperimentalDesign());
    offer.setIdentifier(offerId);
    offer.setProjectManager(projectManager);
    offer.setProjectObjective(offerDto.getProjectObjective());
    offer.setProjectTitle(offerDto.getProjectTitle());
    offer.setSelectedCustomerAffiliation(selectedCustomerAffiliation);
    List<ProductItem> productItems = offerDto.getItems().stream()
        .map(it -> toProductItem(offer, it))
        .collect(Collectors.toList());
    offer.addItems(productItems);

    return offer;
  }

  public static OfferItem toOfferItem(ProductItem productItem) {
    String serviceProviderLabel = new FacilityFactory().getForString(
            productItem.getProduct().getServiceProvider())
        .getLabel();
    return new OfferItem.Builder(
        productItem.getQuantity(),
        productItem.getProduct().getDescription(),
        productItem.getProduct().getProductName(),
        productItem.getUnitPrice().doubleValue(),
        productItem.getDiscountAmount().doubleValue(),
        productItem.getUnitDiscountAmount().doubleValue(),
        productItem.getDiscountRate().multiply(BigDecimal.valueOf(100)).doubleValue(),
        serviceProviderLabel,
        productItem.getProduct().getUnit(),
        productItem.getListPrice().doubleValue(),
        productItem.getSalePrice().doubleValue()
    ).build();
  }

  public static life.qbic.datamodel.dtos.business.Offer toOfferDto(OfferV2 offer) {
    life.qbic.datamodel.dtos.business.Customer customer = toCustomerDto(offer.getCustomer());
    life.qbic.datamodel.dtos.business.ProjectManager projectManager = toProjectManagerDto(
        offer.getProjectManager());
    life.qbic.datamodel.dtos.business.Affiliation customerAffiliation = toAffiliationDto(
        offer.getSelectedCustomerAffiliation());
    java.util.Date expirationDate = toUtilDate(offer.getExpirationDate());
    java.util.Date modificationDate = toUtilDate(offer.getCreationDate());
    List<life.qbic.datamodel.dtos.business.ProductItem> productItemDtos = offer.getItems().stream()
        .map(RefactorConverter::toProductItemDto)
        .collect(Collectors.toList());
    life.qbic.datamodel.dtos.business.OfferId offerIdDto = toOfferIdDto(offer.getIdentifier());

    // builder composition
    life.qbic.datamodel.dtos.business.Offer.Builder offerDtoBuilder =
        new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager,
            offer.getProjectTitle(), offer.getProjectObjective(), customerAffiliation);
    offer.getExperimentalDesign().ifPresent(offerDtoBuilder::experimentalDesign);
    offer.getAssociatedProject().ifPresent(offerDtoBuilder::associatedProject);
    offerDtoBuilder.expirationDate(expirationDate);
    offerDtoBuilder.modificationDate(modificationDate);
    offerDtoBuilder.netPrice(offer.getSalePrice().doubleValue());
    offerDtoBuilder.overheads(offer.getOverhead());
    offerDtoBuilder.taxes(offer.getTaxAmount().doubleValue());
    offerDtoBuilder.totalPrice(offer.getPriceAfterTax().doubleValue());
    offerDtoBuilder.items(productItemDtos);
    offerDtoBuilder.identifier(offerIdDto);
    offerDtoBuilder.overheadRatio(offer.getOverheadRatio());
    offerDtoBuilder.totalDiscountPrice(offer.getTotalDiscountAmount().doubleValue());
    offerDtoBuilder.setId(offer.getPersistentId());
    return offerDtoBuilder.build();
  }

  public static ProductItem toProductItem(OfferV2 offer,
      life.qbic.datamodel.dtos.business.ProductItem productItemDto) {
    Product product = toProduct(productItemDto.getProduct());
    double quantity = productItemDto.getQuantity();
    ProductItem productItem = new ProductItem(offer, product, quantity);
    productItem.setId(productItemDto.getId());
    // Preserve offer position
    productItem.setOfferPosition(productItemDto.offerPosition());
    return productItem;
  }

  public static java.util.Date toUtilDate(LocalDate localDate) {
    return Date.from(
        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private static LocalDate toLocalDate(java.util.Date utilDate) {
    return Instant.ofEpochMilli(utilDate.getTime())
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  private static life.qbic.datamodel.dtos.business.ProductItem toProductItemDto(
      ProductItem productItem) {
    double quantity = productItem.getQuantity();
    life.qbic.datamodel.dtos.business.services.Product productDto = toProductDto(
        productItem.getProduct());
    double totalPrice = 0;
    double unitDiscount = 0;
    life.qbic.datamodel.dtos.business.ProductItem itemDto = new life.qbic.datamodel.dtos.business.ProductItem(
        quantity,
        productDto,
        totalPrice,
        unitDiscount);
    itemDto.setOrderPosition(productItem.offerPosition());
    return itemDto;
  }

  public static ProductCategory toProductCategory(
      life.qbic.datamodel.dtos.business.ProductCategory category) {
    return ProductCategory.forLabel(category.getValue());
  }

  public static life.qbic.datamodel.dtos.business.ProjectManager toProjectManagerDto(
      Person person) {
    life.qbic.datamodel.dtos.business.ProjectManager.Builder projectManagerDtoBuilder = new life.qbic.datamodel.dtos.business.ProjectManager.Builder(
        person.getFirstName(),
        person.getLastName(),
        person.getEmail());
    List<life.qbic.datamodel.dtos.business.Affiliation> personAffiliationsAsDto = person.getAffiliations()
        .stream()
        .map(RefactorConverter::toAffiliationDto)
        .collect(Collectors.toList());
    projectManagerDtoBuilder.affiliations(personAffiliationsAsDto);
    if (person.getTitle() == null || person.getTitle().isEmpty() || person.getTitle()
        .equals("None")) {
      projectManagerDtoBuilder.title(AcademicTitle.NONE);
    } else {
      projectManagerDtoBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    }
    projectManagerDtoBuilder.setId(person.getId());
    return projectManagerDtoBuilder.build();
  }

  public static life.qbic.datamodel.dtos.business.Customer toCustomerDto(Person person) {
    life.qbic.datamodel.dtos.business.Customer.Builder customerBuilder = new Customer.Builder(
        person.getFirstName(),
        person.getLastName(),
        person.getEmail());
    List<life.qbic.datamodel.dtos.business.Affiliation> personAffiliationsAsDto = person.getAffiliations()
        .stream()
        .map(RefactorConverter::toAffiliationDto)
        .collect(Collectors.toList());
    customerBuilder.affiliations(personAffiliationsAsDto);
    if (person.getTitle() == null || person.getTitle().isEmpty() || person.getTitle()
        .equals("None")) {
      customerBuilder.title(AcademicTitle.NONE);
    } else {
      customerBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    }
    customerBuilder.setId(person.getId());
    return customerBuilder.build();
  }

  public static Person toPerson(life.qbic.datamodel.dtos.general.Person personDto) {
    String emailAddress = personDto.getEmailAddress();
    String firstName = personDto.getFirstName();
    String lastName = personDto.getLastName();
    String referenceId = personDto.getReferenceId();
    String title =
        personDto.getTitle() != AcademicTitle.NONE ? personDto.getTitle().getValue() : "";
    List<Affiliation> affiliations = personDto.getAffiliations().stream()
        .map(RefactorConverter::toAffiliation)
        .collect(Collectors.toList());
    Person person = new Person(emailAddress,
        firstName,
        lastName,
        title,
        emailAddress,
        affiliations,
        referenceId);
    person.setId(personDto.getId());
    return person;
  }

  public static life.qbic.datamodel.dtos.general.Person toPersonDTO(Person person) {
    CommonPerson.Builder personBuilder = new CommonPerson.Builder(
        person.getFirstName(),
        person.getLastName(),
        person.getEmail());
    personBuilder.setId(person.getId());
    personBuilder.affiliations(
        person.getAffiliations().stream()
            .map(RefactorConverter::toAffiliationDto)
            .collect(Collectors.toList()));
    if (person.getTitle() == null || person.getTitle().isEmpty() || person.getTitle()
        .equals("None")) {
      personBuilder.title(AcademicTitle.NONE);
    } else {
      personBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    }
    personBuilder.setId(person.getId());
    personBuilder.setReferenceId(person.getReferenceId());
    return personBuilder.build();
  }


  public static life.qbic.datamodel.dtos.business.Affiliation toAffiliationDto(
      Affiliation affiliation) {
    life.qbic.datamodel.dtos.business.Affiliation.Builder affiliationDtoBuilder =
        new life.qbic.datamodel.dtos.business.Affiliation.Builder(
            affiliation.getOrganization(),
            affiliation.getStreet(),
            affiliation.getPostalCode(),
            affiliation.getCity());
    affiliationDtoBuilder.setId(affiliation.getId());
    affiliationDtoBuilder.category(toAffiliationCategoryDto(affiliation.getCategory()));
    affiliationDtoBuilder.country(affiliation.getCountry());
    if (affiliation.getAddressAddition() != null && !affiliation.getAddressAddition().isEmpty()) {
      affiliationDtoBuilder.setAddressAddition(affiliation.getAddressAddition());
    }
    return affiliationDtoBuilder.build();
  }

  public static Affiliation toAffiliation(
      life.qbic.datamodel.dtos.business.Affiliation affiliationDto) {
    AffiliationCategory affiliationCategory = toAffiliationCategory(affiliationDto.getCategory());
    Affiliation affiliation = new Affiliation(
        affiliationDto.getOrganisation(),
        affiliationDto.getAddressAddition(),
        affiliationDto.getStreet(),
        affiliationDto.getPostalCode(),
        affiliationDto.getCity(),
        affiliationDto.getCountry(),
        affiliationCategory);
    affiliation.setId(affiliationDto.getId());
    return affiliation;
  }

  static life.qbic.datamodel.dtos.business.AffiliationCategory toAffiliationCategoryDto(
      AffiliationCategory affiliationCategory) {
    switch (affiliationCategory) {
      case INTERNAL:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.INTERNAL;
      case EXTERNAL_ACADEMIC:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL_ACADEMIC;
      case EXTERNAL:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL;
    }
    return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL;
  }

  static private AffiliationCategory toAffiliationCategory(
      life.qbic.datamodel.dtos.business.AffiliationCategory affiliationCategoryDto) {
    switch (affiliationCategoryDto) {
      case INTERNAL:
        return AffiliationCategory.INTERNAL;
      case EXTERNAL_ACADEMIC:
        return AffiliationCategory.EXTERNAL_ACADEMIC;
      case EXTERNAL:
        return AffiliationCategory.EXTERNAL;
    }
    return AffiliationCategory.EXTERNAL;
  }

  public static OfferId toOfferId(life.qbic.datamodel.dtos.business.OfferId offerIdDto) {
    if (offerIdDto == null) {
      return new OfferId("", 1);
    }
    int version = Integer.parseInt(offerIdDto.getVersion());
    return new OfferId(offerIdDto.getProjectConservedPart(), offerIdDto.getRandomPart(), version);
  }

  public static life.qbic.datamodel.dtos.business.OfferId toOfferIdDto(OfferId offerId) {
    return new life.qbic.datamodel.dtos.business.OfferId(offerId.getProjectPart(),
        offerId.getRandomPart(), Integer.toString(offerId.getVersion()));
  }

  private static <T extends life.qbic.datamodel.dtos.business.services.Product> Product toProduct(
      T productDto) {
    String productCategory = new ProductCategoryFormatter().apply(productDto.getClass());
    Product product = new Product(productCategory, productDto.getInternalUnitPrice(),
        productDto.getExternalUnitPrice());
    product.setDescription(productDto.getDescription());
    product.setProductId(productDto.getProductId().toString());
    product.setProductName(productDto.getProductName());
    product.setServiceProvider(productDto.getServiceProvider().name());
    product.setUnit(productDto.getUnit().toString());
    product.setId(productDto.getId());
    return product;
  }

  protected static Product toProduct(ProductDraft productDraft) {
    String productCategory = productDraft.getCategory().getLabel();
    String serviceProvider = productDraft.getServiceProvider();
    String description = productDraft.getDescription();
    double externalUnitPrice = productDraft.getExternalUnitPrice();
    double internalUnitPrice = productDraft.getInternalUnitPrice();
    String productName = productDraft.getName();

    Product product = new Product(productCategory, internalUnitPrice, externalUnitPrice);
    product.setDescription(description);
    product.setProductName(productName);
    product.setServiceProvider(serviceProvider);
    return product;
  }

  public static life.qbic.datamodel.dtos.business.services.Product toProductDto(Product product) {
    String productName = product.getProductName();
    String productDescription = product.getDescription();
    Double internalUnitPrice = product.getInternalUnitPrice();
    Double externalUnitPrice = product.getExternalUnitPrice();
    ProductUnit productUnit = new ProductUnitFactory().getForString(product.getUnit());
    int id = Optional.ofNullable(product.getId()).orElse(0);
    Long runningNumber = toProductIdDto(
        product.getProductId()).getUniqueId();
    Facility serviceProvider = Facility.valueOf(product.getServiceProvider());
    Class<? extends life.qbic.datamodel.dtos.business.services.Product> requestedProductClass = new ProductCategoryParser().apply(
        product.getCategory());
    if (Sequencing.class.equals(requestedProductClass)) {
      return new Sequencing(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (ProteomicAnalysis.class.equals(requestedProductClass)) {
      return new ProteomicAnalysis(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (MetabolomicAnalysis.class.equals(requestedProductClass)) {
      return new MetabolomicAnalysis(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (PrimaryAnalysis.class.equals(requestedProductClass)) {
      return new PrimaryAnalysis(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (SecondaryAnalysis.class.equals(requestedProductClass)) {
      return new SecondaryAnalysis(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (ProjectManagement.class.equals(requestedProductClass)) {
      return new ProjectManagement(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (DataStorage.class.equals(requestedProductClass)) {
      return new DataStorage(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    } else if (ExternalServiceProduct.class.equals(requestedProductClass)) {
      return new ExternalServiceProduct(id, productName, productDescription, internalUnitPrice,
          externalUnitPrice,
          productUnit, runningNumber, serviceProvider);
    }
    throw new IllegalArgumentException(
        String.format("Product category %s cannot be converted to a Product DTO",
            product.getCategory()));
  }

  /**
   * Converts a {@link life.qbic.datamodel.dtos.business.ProductId} object to a String
   * representation of type <code>#type_#id</code>, for example <code>ME_22</code>.
   *
   * @param productId the product id object
   * @return the String representation of the product id
   */
  public static String toProductId(life.qbic.datamodel.dtos.business.ProductId productId) {
    // we do not want to be dependent on the objects toString() implementation
    // therefore we define the String format explicitly to have control
    return String.format("%s_%d", productId.getType().toUpperCase(), productId.getUniqueId());
  }

  /**
   * Converts a String representation of a product id into the DTO form
   *
   * @param productId the product id String
   * @return the converted product id DTO
   */
  public static life.qbic.datamodel.dtos.business.ProductId toProductIdDto(String productId)
      throws IllegalArgumentException {
    return new ProductIdParser().apply(productId);
  }

  /**
   * Small helper class to provide product id parsing functionality. Since we control the String
   * format representation of the product id, we know that we expect it to be of format <code>
   * #type_#id</code>, which is a String for the type and a long value for the id.
   */
  protected static class ProductIdParser
      implements Function<String, life.qbic.datamodel.dtos.business.ProductId> {

    @Override
    public life.qbic.datamodel.dtos.business.ProductId apply(String s) {
      return parseFrom(s);
    }

    private static life.qbic.datamodel.dtos.business.ProductId parseFrom(String s) {
      String[] splitString = s.split("_");
      if (splitString.length != 2) {
        throw new IllegalArgumentException(String.format("Unknown product id format: %s", s));
      }
      String productCategory = splitString[0].toUpperCase();
      long productId;

      try {
        productId = Long.parseLong(splitString[1]);
      } catch (NumberFormatException ignored) {
        throw new IllegalArgumentException(String.format("Cannot determine product id: %s", s));
      }

      return new ProductId.Builder(productCategory, productId).build();
    }
  }

  private static Map<Class<? extends life.qbic.datamodel.dtos.business.services.Product>, String> productCategoryMap() {
    Map<Class<? extends life.qbic.datamodel.dtos.business.services.Product>, String> categoryMap = new HashMap<>();
    categoryMap.put(Sequencing.class, "Sequencing");
    categoryMap.put(ProteomicAnalysis.class, "Proteomics");
    categoryMap.put(MetabolomicAnalysis.class, "Metabolomics");
    categoryMap.put(PrimaryAnalysis.class, "Primary Bioinformatics");
    categoryMap.put(SecondaryAnalysis.class, "Secondary Bioinformatics");
    categoryMap.put(ProjectManagement.class, "Project Management");
    categoryMap.put(DataStorage.class, "Data Storage");
    categoryMap.put(ExternalServiceProduct.class, "External Service");
    return categoryMap;
  }

  protected static class ProductCategoryParser implements
      Function<String, Class<? extends life.qbic.datamodel.dtos.business.services.Product>> {

    /**
     * Applies this function to the given argument.
     *
     * @param categoryName the function argument
     * @return the function result
     */
    @Override
    public Class<? extends life.qbic.datamodel.dtos.business.services.Product> apply(
        String categoryName) {
      return productCategoryMap().entrySet().stream()
          .filter(it -> it.getValue().equalsIgnoreCase(categoryName))
          .map(Entry::getKey)
          .findAny()
          .orElseThrow(
              () -> new IllegalArgumentException("No product class found for " + categoryName)
          );
    }
  }

  protected static class ProductCategoryFormatter implements
      Function<Class<? extends life.qbic.datamodel.dtos.business.services.Product>, String> {

    /**
     * Applies this function to the given argument.
     *
     * @param aClass the function argument
     * @return the function result
     */
    @Override
    public String apply(
        Class<? extends life.qbic.datamodel.dtos.business.services.Product> aClass) {
      return productCategoryMap().getOrDefault(aClass, "");
    }
  }
}
