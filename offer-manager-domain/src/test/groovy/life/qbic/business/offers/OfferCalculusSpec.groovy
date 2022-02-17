package life.qbic.business.offers

import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Specification

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class OfferCalculusSpec extends Specification {
  def "overhead ratio is #expectedRatio for customer affiliation with category #category"() {
    given: "an offer with the respective customer affiliation"
    OfferV2 offer = new OfferV2()
    offer.setSelectedCustomerAffiliation(new Affiliation("organization", "addressAddition", "street", "postalCode", "city", "country", category))
    when: "the calculus determines the overhead ratio for that offer"
    OfferV2 filledOffer = new OfferCalculus().overheadRatio(offer)
    then: "the overhead ratio is #expectedRatio"
    filledOffer.getOverheadRatio() == expectedRatio.doubleValue()
    where:
    category | expectedRatio
    AffiliationCategory.INTERNAL | 0.0
    AffiliationCategory.EXTERNAL_ACADEMIC | 0.2
    AffiliationCategory.EXTERNAL | 0.4
  }

}
