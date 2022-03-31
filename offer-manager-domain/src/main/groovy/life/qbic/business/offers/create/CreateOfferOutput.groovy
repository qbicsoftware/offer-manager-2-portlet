package life.qbic.business.offers.create

import life.qbic.business.UseCaseFailure
import life.qbic.business.offers.OfferV2

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
  void createdNewOffer(OfferV2 createdOffer)
}
