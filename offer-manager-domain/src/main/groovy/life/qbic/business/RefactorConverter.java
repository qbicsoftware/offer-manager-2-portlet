package life.qbic.business;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import life.qbic.business.offers.OfferV2;
import life.qbic.business.offers.identifier.OfferId;
import life.qbic.business.persons.Person;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.AffiliationCategory;
import life.qbic.business.products.Product;
import life.qbic.business.products.ProductItem;
import life.qbic.business.products.dtos.ProductDraft;
import life.qbic.datamodel.dtos.business.AcademicTitleFactory;
import life.qbic.datamodel.dtos.business.Customer;
import life.qbic.datamodel.dtos.business.ProductId;
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode;
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier;
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace;

/**
 * <b>ATTENTION: Only for refactor purposes. Meant to be removed.</b>
 *
 * <ol>
 *   <li>use cases dürfen nur die neuen klassen verwenden (keine data-model-lib)</li>
 *   <li>apdapter (controller und presenter) benutzen diesen converter</li>
 *   <li>sobald alles umgestellt ist kann man den offer manager laufen lassen</li>
 * </ol>
 */
public class RefactorConverter {

  // CreateOfferContent can be replaced by the OfferCalculus
  // vat calculation logic in the offer calculus can be redirected to the Tax office /Tax office can
  // be removed
  // ProductDraft and ProductEntity can be replaced

  OfferV2 toOffer(life.qbic.datamodel.dtos.business.OfferId offerIdDto) {
    OfferV2 offer = new OfferV2();
    OfferId offerId = toOfferId(offerIdDto);
    offer.setIdentifier(offerId);
    return offer;
  }

  OfferV2 toOffer(life.qbic.datamodel.dtos.business.Offer offerDto) {
    return null; //TODO
  }

  life.qbic.datamodel.dtos.business.Offer toOfferDto(OfferV2 offer) {
    life.qbic.datamodel.dtos.business.Customer customer = toCustomerDto(offer.getCustomer());
    life.qbic.datamodel.dtos.business.ProjectManager projectManager = toProjectManagerDto(
        offer.getProjectManager());
    life.qbic.datamodel.dtos.business.Affiliation customerAffiliation = toAffiliationDto(
        offer.getSelectedCustomerAffiliation());
    java.util.Date expirationDate = toUtilDate(offer.getExpirationDate());
    java.util.Date modificationDate = toUtilDate(offer.getCreationDate());
    List<life.qbic.datamodel.dtos.business.ProductItem> productItemDtos = offer.getItems().stream()
        .map(this::toProductItemDto)
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
    offerDtoBuilder.netPrice(offer.getTotalNetPrice().doubleValue());
    offerDtoBuilder.overheads(offer.getOverhead());
    offerDtoBuilder.taxes(offer.getTotalVat().doubleValue());
    offerDtoBuilder.totalPrice(offerDtoBuilder.getTotalPrice());
    offerDtoBuilder.items(productItemDtos);
    offerDtoBuilder.identifier(offerIdDto);
    offerDtoBuilder.overheadRatio(offer.getOverheadRatio());
    offerDtoBuilder.totalDiscountPrice(offer.getTotalDiscountAmount().doubleValue());
    return offerDtoBuilder.build();
  }

  private ProductItem toProductItem(
      life.qbic.datamodel.dtos.business.ProductItem productItemDto) {
    Product product = toProduct(productItemDto.getProduct());
    BigDecimal unitDiscount = BigDecimal.valueOf(productItemDto.getQuantityDiscount());
    BigDecimal totalPrice = BigDecimal.valueOf(productItemDto.getTotalPrice());
    double quantity = productItemDto.getQuantity();
    return new ProductItem(product,
        quantity,
        unitDiscount,
        totalPrice);
  }

  private java.util.Date toUtilDate(LocalDate localDate) {
    return Date.from(
        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private LocalDate toLocalDate(java.util.Date utilDate) {
    return Instant.ofEpochMilli(utilDate.getTime())
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  private life.qbic.datamodel.dtos.business.ProductItem toProductItemDto(ProductItem productItem) {
    double quantity = productItem.getQuantity();
    life.qbic.datamodel.dtos.business.services.Product productDto = toProductDto(
        productItem.getProduct());
    double totalPrice = productItem.getTotalPrice().doubleValue();
    double unitDiscount = productItem.getUnitDiscount().doubleValue();
    return new life.qbic.datamodel.dtos.business.ProductItem(quantity,
        productDto,
        totalPrice,
        unitDiscount);
  }

  private life.qbic.datamodel.dtos.business.ProjectManager toProjectManagerDto(Person person) {
    life.qbic.datamodel.dtos.business.ProjectManager.Builder projectManagerDtoBuilder = new life.qbic.datamodel.dtos.business.ProjectManager.Builder(
        person.getFirstName(),
        person.getLastName(),
        person.getEmail());
    projectManagerDtoBuilder.affiliations(
        person.getAffiliations().stream()
            .map(this::toAffiliationDto)
            .collect(Collectors.toList()));
    projectManagerDtoBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    return projectManagerDtoBuilder.build();
  }

  private life.qbic.datamodel.dtos.business.Customer toCustomerDto(Person person) {
    life.qbic.datamodel.dtos.business.Customer.Builder customerBuilder = new Customer.Builder(
        person.getFirstName(),
        person.getLastName(),
        person.getEmail());
    customerBuilder.affiliations(
        person.getAffiliations().stream()
            .map(this::toAffiliationDto)
            .collect(Collectors.toList()));
    customerBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    return customerBuilder.build();
  }

  private Person toPerson(life.qbic.datamodel.dtos.general.Person personDto) {
    String emailAddress = personDto.getEmailAddress();
    String firstName = personDto.getFirstName();
    String lastName = personDto.getLastName();
    String title = personDto.getTitle().getValue();
    List<Affiliation> affiliations = personDto.getAffiliations().stream()
        .map(this::toAffiliation)
        .collect(Collectors.toList());
    return new Person(emailAddress,
        firstName,
        lastName,
        title,
        emailAddress,
        affiliations);
  }


  life.qbic.datamodel.dtos.business.Affiliation toAffiliationDto(Affiliation affiliation) {
    life.qbic.datamodel.dtos.business.Affiliation.Builder affiliationDtoBuilder =
        new life.qbic.datamodel.dtos.business.Affiliation.Builder(
            affiliation.getOrganization(),
            affiliation.getStreet(),
            affiliation.getPostalCode(),
            affiliation.getCity());
    affiliationDtoBuilder.category(toAffiliationCategoryDto(affiliation.getCategory()));
    affiliationDtoBuilder.country(affiliation.getCountry());
    return affiliationDtoBuilder.build();
  }

  private Affiliation toAffiliation(
      life.qbic.datamodel.dtos.business.Affiliation affiliationDto) {
    AffiliationCategory affiliationCategory = toAffiliationCategory(affiliationDto.getCategory());
    return new Affiliation(
        affiliationDto.getOrganisation(),
        affiliationDto.getAddressAddition(),
        affiliationDto.getStreet(),
        affiliationDto.getPostalCode(),
        affiliationDto.getCity(),
        affiliationDto.getCountry(),
        affiliationCategory);
  }

  life.qbic.datamodel.dtos.business.AffiliationCategory toAffiliationCategoryDto(AffiliationCategory affiliationCategory) {
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

  private AffiliationCategory toAffiliationCategory(
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

  OfferId toOfferId(life.qbic.datamodel.dtos.business.OfferId offerIdDto) {
    int version = Integer.parseInt(offerIdDto.getVersion());
    return new OfferId(offerIdDto.getProjectConservedPart(), offerIdDto.getRandomPart(), version);
  }

  life.qbic.datamodel.dtos.business.OfferId toOfferIdDto(OfferId offerId) {
    return new life.qbic.datamodel.dtos.business.OfferId(offerId.getProjectPart(),
        offerId.getRandomPart(), Integer.toString(offerId.getVersion()));
  }

  private <T extends life.qbic.datamodel.dtos.business.services.Product> Product toProduct(
      T productDto) {
    String productCategory = new ProductCategorizer().apply(productDto.getClass());
    Product product = new Product();
    product.setCategory(productCategory);
    product.setDescription(productDto.getDescription());
    product.setExternalUnitPrice(productDto.getExternalUnitPrice());
    product.setInternalUnitPrice(productDto.getInternalUnitPrice());
    product.setProductId(productDto.getProductId().toString());
    product.setProductName(productDto.getProductName());
    product.setServiceProvider(productDto.getServiceProvider().getLabel());
    return product;
  }

  Product toProduct(ProductDraft productDraft) {
    return null;
  }

  life.qbic.datamodel.dtos.business.services.Product toProductDto(Product product) {
    return null;
  }

  /**
   * Converts a {@link life.qbic.datamodel.dtos.business.ProductId} object to a String
   * representation of type <code>#type_#id</code>, for example <code>ME_22</code>.
   *
   * @param productId the product id object
   * @return the String representation of the product id
   */
  public String toProductId(life.qbic.datamodel.dtos.business.ProductId productId) {
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
  public life.qbic.datamodel.dtos.business.ProductId toProductIdDto(String productId)
      throws IllegalArgumentException {
    return new ProductIdParser().apply(productId);
  }

  /**
   * Converts a {@link life.qbic.datamodel.dtos.projectmanagement.ProjectSpace} object to its
   * uppercase String representation.
   *
   * @param projectSpace the project space object
   * @return the uppercase String representation
   */
  public String toProjectSpace(
      life.qbic.datamodel.dtos.projectmanagement.ProjectSpace projectSpace) {
    return projectSpace.getName().toUpperCase();
  }

  /**
   * Converts a project space String representation into a {@link
   * life.qbic.datamodel.dtos.projectmanagement.ProjectSpace} object.
   *
   * @param projectSpace the project space String representation
   * @return its dto representation
   * @throws IllegalArgumentException if the String cannot be converted, for example due to format
   *     rule violations
   */
  public life.qbic.datamodel.dtos.projectmanagement.ProjectSpace toProjectSpaceDTO(
      String projectSpace) throws IllegalArgumentException {
    try {
      return new ProjectSpace(projectSpace);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not convert project space", e);
    }
  }

  /**
   * Converts a {@link life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier} object to its
   * String representation.
   *
   * <p>The resulting format is upper case: <code>/#projectspace/#projectcode</code>
   *
   * @param projectIdentifier the project identifier to convert
   * @return its String representation
   */
  public String toProjectIdentifier(
      life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier projectIdentifier) {
    // we do not want to  depend on the class String format implementation, so we are explicit
    return String.format(
        "/%s/%s",
        projectIdentifier.getProjectSpace().getName().toUpperCase(),
        projectIdentifier.getProjectCode().getCode().toUpperCase());
  }

  /**
   * Converts a project identifier String representation into an instance of
   * {@link life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier}.
   * @param projectIdentifier the project identifier in its String representation
   * @return the project identifier DTO form
   * @throws IllegalArgumentException if the conversion failed, for example due to format rule violations
   */
  public life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier toProjectIdentifierDTO(
      String projectIdentifier) throws IllegalArgumentException {
    return new ProjectIdentifierParser().apply(projectIdentifier);
  }

  /**
   * Converts a project code String representation into an instance of {@link life.qbic.datamodel.dtos.projectmanagement.ProjectCode}.
   * @param projectCode the project code in String representation
   * @throws IllegalArgumentException if the project code cannot be converted, for example due to format rule violations
   */
  public life.qbic.datamodel.dtos.projectmanagement.ProjectCode toProjectCodeDTO(String projectCode)
      throws IllegalArgumentException {
    try {
      return new ProjectCode(projectCode);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Could not convert project code %s.", projectCode), e);
    }
  }

  /**
   * Small helper class to provide project identifier parsing functionality. Since we control the
   * String representation of the project identifier, we know that we can expect it to be in the
   * format <code>/#projectspace/#projectcode</code>.
   */
  protected static class ProjectIdentifierParser
      implements Function<String, life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier> {

    @Override
    public life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier apply(String s) {
      return null; // todo implement
    }

    private static life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier parseFrom(
        String s) {
      String[] splitString = s.split("/");
      if (splitString.length != 3) {
        throw new IllegalArgumentException(
            String.format("Unknown project identifier format %s", s));
      }
      return new ProjectIdentifier(
          new ProjectSpace(splitString[1]), new ProjectCode(splitString[2]));
    }
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

  protected static class ProductCategorizer implements
      Function<Class<? extends life.qbic.datamodel.dtos.business.services.Product>, String> {

    private final Map<Class<? extends life.qbic.datamodel.dtos.business.services.Product>, String> categoryMap = new HashMap<>();

    public ProductCategorizer() {
      categoryMap.put(Sequencing.class, "Sequencing");
      categoryMap.put(ProteomicAnalysis.class, "Proteomics");
      categoryMap.put(MetabolomicAnalysis.class, "Metabolomics");
      categoryMap.put(PrimaryAnalysis.class, "Primary Bioinformatics");
      categoryMap.put(SecondaryAnalysis.class, "Secondary Bioinformatics");
      categoryMap.put(ProjectManagement.class, "Project Management");
      categoryMap.put(DataStorage.class, "Data Storage");
      categoryMap.put(ExternalServiceProduct.class, "External Service");
    }

    /**
     * Applies this function to the given argument.
     *
     * @param aClass the function argument
     * @return the function result
     */
    @Override
    public String apply(
        Class<? extends life.qbic.datamodel.dtos.business.services.Product> aClass) {
      return categoryMap.getOrDefault(aClass, "");
    }
  }
}
