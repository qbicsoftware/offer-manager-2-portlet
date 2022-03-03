package life.qbic.business;

import java.util.function.Function;
import java.util.stream.Collectors;
import life.qbic.business.offers.OfferV2;
import life.qbic.business.offers.identifier.OfferId;
import life.qbic.business.persons.Person;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.AffiliationCategory;
import life.qbic.business.products.Product;
import life.qbic.business.products.dtos.ProductDraft;
import life.qbic.datamodel.dtos.business.AcademicTitleFactory;
import life.qbic.datamodel.dtos.business.Customer;
import life.qbic.datamodel.dtos.business.ProductId;
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace;

/**
 * <b>ATTENTION: Only for refactor purposes. Meant to be removed.</b>
 *
 * <ol>
 *   <li>use cases dürfen nur die neuen klassen verwenden (keine data-model-lib)
 *   <li>apdapter (controller und presenter) benutzen diesen converter
 *   <li>sobald alles umgestellt ist kann man den offer manager laufen lassen
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
    return null;
  }

  life.qbic.datamodel.dtos.business.Offer toOfferDto(OfferV2 offer) {
    return null;
  }

  life.qbic.datamodel.dtos.business.Customer toCustomerDto(Person person) {
    life.qbic.datamodel.dtos.business.Customer.Builder customerBuilder =
        new Customer.Builder(person.getFirstName(), person.getLastName(), person.getEmail());
    customerBuilder.affiliations(
        person.getAffiliations().stream().map(this::toAffiliationDto).collect(Collectors.toList()));
    customerBuilder.title(new AcademicTitleFactory().getForString(person.getTitle()));
    return customerBuilder.build();
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

  life.qbic.datamodel.dtos.business.AffiliationCategory toAffiliationCategoryDto(
      AffiliationCategory affiliationCategory) {
    switch (affiliationCategory) {
      case INTERNAL:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.INTERNAL;
      case EXTERNAL_ACADEMIC:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL_ACADEMIC;
      case EXTERNAL:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL;
      default:
        return life.qbic.datamodel.dtos.business.AffiliationCategory.EXTERNAL;
    }
  }

  OfferId toOfferId(life.qbic.datamodel.dtos.business.OfferId offerIdDto) {
    int version = Integer.parseInt(offerIdDto.getVersion());
    return new OfferId(offerIdDto.getProjectConservedPart(), offerIdDto.getRandomPart(), version);
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
    ProjectSpace projectSpaceDTO;
    try {
      projectSpaceDTO = new ProjectSpace(projectSpace);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not convert project space", e);
    }
    return projectSpaceDTO;
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

  public life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier toProjectIdentifierDTO(
      String projectIdentifier) throws IllegalArgumentException {
    // todo implement
    return new ProjectIdentifierParser().apply(projectIdentifier);
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
      return parseString(s);
    }

    private static life.qbic.datamodel.dtos.business.ProductId parseString(String s) {
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
}
