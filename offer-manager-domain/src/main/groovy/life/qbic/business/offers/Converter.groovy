package life.qbic.business.offers


import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager

/**
 * Helper class to convert DTOs in Business Objects and vice versa.
 *
 * This helper class provides some static conversion methods to convert
 * DTOs content into business objects and vice versa.
 *
 * This class can be used anywhere in the application domain code, where information must
 * cross architectural boundaries. Business objects must not leave the domain layer and therefore
 * data needs to be prepared to be exported back into the outer application layers.
 *
 * Feel free to add new converter methods and be careful with the class paths of objects, especially
 * if DTOs and business objects have the same class name. In this case use the full qualified
 * package domain for the DTO class explicitly.
 *
 * @since 1.0.0
 */
class Converter {
    static life.qbic.datamodel.dtos.business.Offer convertOfferToDTO(Offer offer) {
        new life.qbic.datamodel.dtos.business.Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectDescription,
                offer.selectedCustomerAffiliation)
                .identifier(convertIdToDTO(offer.identifier))
                .items(offer.getItems())
                .netPrice(offer.getTotalNetPrice())
                .taxes(offer.getTaxCosts())
                .overheads(offer.getOverheadSum())
                .totalPrice(offer.getTotalCosts())
                .modificationDate(offer.modificationDate)
                .expirationDate(offer.expirationDate)
                .build()
    }
    //ToDo Deprecate this method once the FetchOffer Use Case is Implemented
    static Offer buildOfferForCostCalculation(List<ProductItem> items,
                                              Affiliation affiliation) {
        final def dummyCustomer = new Customer.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        final def dummyProjectManager = new ProjectManager.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        new Offer.Builder(
                dummyCustomer,
                dummyProjectManager,
                "",
                "",
                items,
                affiliation).build()
    }

    static life.qbic.business.offers.identifier.OfferId buildOfferId(life.qbic.datamodel.dtos.business.OfferId id) {
        def randomPart = new life.qbic.business.offers.identifier.RandomPart(id.randomPart)
        def projectPart = new life.qbic.business.offers.identifier.ProjectPart(id.projectConservedPart)
        def version = new life.qbic.business.offers.identifier.Version(id.version)
        return new life.qbic.business.offers.identifier.OfferId(randomPart, projectPart, version)
    }

    static life.qbic.datamodel.dtos.business.OfferId convertIdToDTO(life.qbic.business.offers.identifier.OfferId id) {
        return new life.qbic.datamodel.dtos.business.OfferId(
                id.getProjectPart().value,
                id.getRandomPart().value,
                id.getVersion().value)
    }

    static life.qbic.business.offers.Offer convertDTOToOffer(life.qbic.datamodel.dtos.business.Offer offer) {
        new Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectDescription,
                offer.items,
                offer.selectedCustomerAffiliation)
                .identifier(buildOfferId(offer.identifier))
                //ToDo Is this the correct mapping?
                .creationDate(offer.modificationDate)
                .build()
    }
}
