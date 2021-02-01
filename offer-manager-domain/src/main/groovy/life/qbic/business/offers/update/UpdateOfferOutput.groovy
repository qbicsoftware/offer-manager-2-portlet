package life.qbic.business.offers.update

import life.qbic.business.UseCaseFailure
import life.qbic.datamodel.dtos.business.Offer

/**
 * Output interface for the {@link life.qbic.business.offers.update.UpdateOffer} use case
 *
 * @since: 1.0.0
 */
interface UpdateOfferOutput extends UseCaseFailure {

    /**
     * Confirms saving of the updated offer by providing
     * the updated offer information including the assigned identifier.
     *
     * @param createdOffer information about the updated offer packaged in an
     * {@link life.qbic.datamodel.dtos.business.Offer} data-transfer object
     * @since 1.0.0
     */
    void updatedOffer(Offer createdOffer)

    /**
     * Transfers the calculated net price, taxes, overheads and total price
     * to an implementing class
     * @param netPrice The net price calculated for the requested services
     * @param taxes The amount of taxes for the requested services
     * @param overheads The amount of overheads for the requested services
     * @param totalPrice The total price for the requested services, includes taxes and overheads
     * @since 1.0.0
     */
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice)
}
