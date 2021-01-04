package life.qbic.portal.portlet.offers.update

import life.qbic.datamodel.dtos.business.Offer

/**
 * Update offer use case.
 *
 * During the offer preparation, the customer might request changes for the offer items (number of
 * samples, change in the technology used for analysis, etc.).
 * The offer manager provides an interface to update an existing offer and create a new version
 * from it.
 *
 * In the process of updating an existing offer, the updated one has the same offer identifier
 * as the original one, with the one exception that the version modifier has been increased.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class UpdateOffer implements UpdateOfferInput {

    final private UpdateOfferOutput output

    final private UpdateOfferDataSource dataSource

    UpdateOffer(UpdateOfferOutput output, UpdateOfferDataSource dataSource) {
        this.output = output
        this.dataSource = dataSource
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void update(Offer offer) {

    }
}
