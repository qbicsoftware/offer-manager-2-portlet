package life.qbic.portal.offermanager.components.offer.update

import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.create.CalculatePriceOutput
import life.qbic.business.offers.create.CreateOfferInput
import life.qbic.business.offers.fetch.FetchOfferInput
import life.qbic.datamodel.dtos.business.*

/**
 * Controller class adapter from view information into use case input interface
 *
 * This class translates the information that was received from the view into method calls to the use case
 *
 * @since: 0.1.0
 *
 */
@Log4j2
class UpdateOfferController {

  private final CreateOfferInput input
  private final FetchOfferInput fetchOfferInput
  private final CalculatePriceOutput priceCalculationResultsOutput

  private static final RefactorConverter refactorConverter = new RefactorConverter()

  UpdateOfferController(CreateOfferInput input, FetchOfferInput fetchOfferInput, CalculatePriceOutput priceCalculationResultsOutput) {
    this.input = input
    this.fetchOfferInput = fetchOfferInput
    this.priceCalculationResultsOutput = priceCalculationResultsOutput
  }

  void calculatePriceForItems(List<ProductItem> productItems, Affiliation affiliation) {
    OfferV2 offer = new OfferV2(refactorConverter.toAffiliation(affiliation), new life.qbic.business.offers.identifier.OfferId("price", 1))
    offer.setItems(productItems.stream().map(it -> refactorConverter.toProductItem(offer, it)).collect() as List<life.qbic.business.products.ProductItem>)
    priceCalculationResultsOutput.calculatedPrice(offer.salePrice.doubleValue(), offer.taxAmount.doubleValue(), offer.overhead, offer.priceAfterTax.doubleValue(), offer.totalDiscountAmount.doubleValue())
  }

  void updateOffer(OfferId offerId,
                   String projectTitle,
                   String projectDescription,
                   Customer customer,
                   ProjectManager manager,
                   List<ProductItem> items,
                   Affiliation customerAffiliation,
                   String experimentalDesign) {
    //todo update this method to consume contact
    Offer.Builder offerBuilder = new Offer.Builder(
            customer,
            manager,
            projectTitle,
            projectDescription,
            customerAffiliation)
            .items(items)
            .identifier(offerId)
    if (experimentalDesign) {
      offerBuilder.experimentalDesign(experimentalDesign)
    }
    offerBuilder.modificationDate(new Date())

    Offer offer = offerBuilder.build()

    this.input.updateOffer(refactorConverter.toOffer(offer))
  }
}
