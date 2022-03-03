package life.qbic.business;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
import life.qbic.datamodel.dtos.business.Offer;
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier;
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace;

/**
 * <b>ATTENTION: Only for refactor purposes. Meant to be removed.</b>
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
    return null;
  }

  life.qbic.datamodel.dtos.business.Offer toOfferDto(OfferV2 offer) {
    life.qbic.datamodel.dtos.business.Customer customer = toCustomerDto(offer.getCustomer());
    life.qbic.datamodel.dtos.business.ProjectManager projectManager = toProjectManagerDto(
        offer.getProjectManager());
    life.qbic.datamodel.dtos.business.Affiliation customerAffiliation = null;

    life.qbic.datamodel.dtos.business.Offer offerDto = new Offer.Builder(customer, projectManager,
        offer.getProjectTitle(), offer.getProjectObjective(), customerAffiliation).build();
    return null;
  }

  private java.util.Date toUtilDate(LocalDate localDate) {
    return Date.from(
        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  life.qbic.datamodel.dtos.business.ProductItem toProductItemDto(ProductItem productItem) {
    return null;
  }

  life.qbic.datamodel.dtos.business.ProjectManager toProjectManagerDto(Person person) {
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

  life.qbic.datamodel.dtos.business.Customer toCustomerDto(Person person) {
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

  life.qbic.datamodel.dtos.business.Affiliation toAffiliationDto(Affiliation affiliation) {
    life.qbic.datamodel.dtos.business.Affiliation.Builder affiliationDtoBuilder = new life.qbic.datamodel.dtos.business.Affiliation.Builder(
        affiliation.getOrganization(), affiliation.getStreet(), affiliation.getPostalCode(),
        affiliation.getCity());
    affiliationDtoBuilder.category(toAffiliationCategoryDto(affiliation.getCategory()));
    affiliationDtoBuilder.country(affiliation.getCountry());
    return affiliationDtoBuilder.build();
  }

  life.qbic.datamodel.dtos.business.AffiliationCategory toAffiliationCategoryDto(AffiliationCategory affiliationCategory) {
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
    return new OfferId(offerIdDto.getProjectConservedPart(), offerIdDto.getRandomPart(),
        version);
  }

  life.qbic.datamodel.dtos.business.OfferId toOfferIdDto(OfferId offerId) {
    return new life.qbic.datamodel.dtos.business.OfferId(offerId.getProjectPart(),
        offerId.getRandomPart(), Integer.toString(offerId.getVersion()));
  }

  Product toProduct(ProductDraft productDraft) {
    return null;
  }

  life.qbic.datamodel.dtos.business.services.Product toProductDto(Product product) {
    return null;
  }


  String toProductId(life.qbic.datamodel.dtos.business.ProductId productId) {
    return null; // todo implement conversion to String representation
  }

  life.qbic.datamodel.dtos.business.ProductId toProductIdDto(String productId) {
    return null; // todo implement parser for DTO conversion
  }

  // Todo: create ProjectSpace business entity
  ProjectSpace toProjectSpace(ProjectSpace projectSpace) {
    return null;
  }

  // Todo: Create ProjectIdentifier business entity
  ProjectIdentifier toProjectIdentifier(ProjectIdentifier projectIdentifier) {
    return null; // Todo: implement
  }


}
