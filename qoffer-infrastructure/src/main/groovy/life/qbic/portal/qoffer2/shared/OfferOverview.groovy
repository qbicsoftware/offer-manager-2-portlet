package life.qbic.portal.qoffer2.shared

import life.qbic.datamodel.dtos.business.OfferId

/**
 * This class holds data for an offer overview
 *
 * It is not always necessary to pass along complete offer data in the application and display this to the user.
 * For this purpose, you can use this class and provide a concise offer overview, 
 * and its content can be loaded quick and easy.
 *
 * @since 1.0.0
 */
class OfferOverview {

    final String projectTitle

    final String projectId

    final String customer

    final Date modificationDate

    final double totalPrice

    final OfferId offerId

    OfferOverview(
            OfferId offerId,
            Date modificationDate,
            String projectTitle,
            String projectId,
            String customer,
            double totalPrice) {
        this.offerId = offerId
        this.modificationDate = modificationDate
        this.projectId = projectId
        this.projectTitle = projectTitle
        this.customer = customer
        this.totalPrice = totalPrice
    }
}
