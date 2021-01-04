package life.qbic.portal.portlet.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.portlet.offers.identifier.OfferId
import life.qbic.portal.portlet.offers.identifier.ProjectPart
import life.qbic.portal.portlet.offers.identifier.RandomPart
import life.qbic.portal.portlet.offers.identifier.Version

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class Converter {
    static life.qbic.datamodel.dtos.business.Offer convertOfferToDTO(Offer offer) {
        new life.qbic.datamodel.dtos.business.Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectDescription,
                offer.selectedCustomerAffiliation)
                .items(offer.getItems())
                .netPrice(offer.getTotalNetPrice())
                .taxes(offer.getTaxCosts())
                .overheads(offer.getOverheadSum())
                .totalPrice(offer.getTotalCosts())
                .modificationDate(offer.modificationDate)
                .expirationDate(offer.expirationDate)
                .build()
    }

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

    static OfferId buildOfferId(life.qbic.datamodel.dtos.business.OfferId id) {
        def randomPart = new RandomPart(id.randomPart)
        def projectPart = new ProjectPart(id.projectConservedPart)
        def version = new Version(id.version)
        return new OfferId(randomPart, projectPart, version)
    }
}