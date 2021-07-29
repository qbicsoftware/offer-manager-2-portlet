package life.qbic.business.offers

import spock.lang.Specification

/**
 * <h1>Tests for the offer item DTO</h1>
 *
 * @since 1.1.0
 *
*/
class OfferItemSpec extends Specification
{
    def "OfferItem is created successfully"() {
        when:"a offerItem is created"
        new OfferItem.Builder(2,"description","name",2.0,"Quantitative Biology Center","Sample",30.00).build()
        then: "no error is thrown"
        noExceptionThrown()
    }
}