package life.qbic.portal.portlet.offers.create

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.UseCaseFailure


/**
 * Output interface for the {@link life.qbic.portal.portlet.offers.create.CreateOffer} use case
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
   */
  void createdNewOffer(Offer createdOffer)

  /**
   * Transfers the calculated price to an implementing class
   * @param price
   * @deprecated Please use {@link #calculatedPrice(double, double, double, double)}
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
   */
  void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice)
}