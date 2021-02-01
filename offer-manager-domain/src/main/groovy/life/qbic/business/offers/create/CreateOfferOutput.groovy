package life.qbic.business.offers.create

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.business.UseCaseFailure


/**
 * Output interface for the {@link CreateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferOutput extends UseCaseFailure {

  /**
   * Confirms the creation and saving of a new offer by providing
   * the original offer information including the assigned identifier.
   *
   * @param createdOffer {@link life.qbic.datamodel.dtos.business.Offer}
   * @since 1.0.0
   */
  void createdNewOffer(Offer createdOffer)

  /**
   * Transfers the calculated price to an implementing class
   * @param price
   * @deprecated Please use {@link #calculatedPrice(double, double, double, double)}
   * @since 1.0.0
   */
  @Deprecated
  void calculatedPrice(double price)

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
