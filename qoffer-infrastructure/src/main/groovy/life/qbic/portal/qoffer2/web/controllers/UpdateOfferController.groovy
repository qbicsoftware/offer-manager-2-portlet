package life.qbic.portal.qoffer2.web.controllers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.portlet.offers.update.UpdateOfferInput

/**
 * Controller that connects the user input with
 * the update offer use case.
 *
 * @since 1.0.0
 */
class UpdateOfferController {

    final private UpdateOfferInput useCaseInput

    UpdateOfferController(UpdateOfferInput useCaseInput) {
        this.useCaseInput = useCaseInput
    }

    void updateOffer(OfferId offerId,
                     String projectTitle,
                     String projectDescription,
                     Customer customer,
                     ProjectManager manager,
                     List<ProductItem> items,
                     Affiliation customerAffiliation) {
        def offerToUpdate = new Offer.Builder(customer, manager, projectTitle,
                projectDescription, customerAffiliation)
                .identifier(offerId)
                .items(items)
                .build()
        useCaseInput.update(offerToUpdate)
    }
}
