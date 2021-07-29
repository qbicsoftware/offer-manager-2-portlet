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
        new OfferItem.Builder(2,"description","name",2.0,1.0,"Quantitative Biology Center","Sample",30.00).build()
        then: "no error is thrown"
        noExceptionThrown()
    }

    def "OfferItems with the same content are equal"() {
        when:"two offerItem are created with the same content"
        OfferItem offerItem = new OfferItem.Builder(2,"description","name",2.0,1.0,"Quantitative Biology Center","Sample",30.00).build()
        OfferItem theSame = new OfferItem.Builder(2,"description","name",2.0,1.0,"Quantitative Biology Center","Sample",30.00).build()

        then: "offeritems are the same"
        offerItem.equals(theSame)
    }

    def "OfferItems with the different content are different"() {
        when:"two offerItem are created with different content"
        OfferItem offerItem = new OfferItem.Builder(quantity,productDescription,productName,unitPrice,quantityDiscount,serviceProvider,unit,itemTotal).build()
        OfferItem offerItem2 = new OfferItem.Builder(quantity2, productDescription2, productName2, unitPrice2, quantityDiscount2,serviceProvider2,unit2,itemTotal2).build()

        then: "offeritems are the different"
        ! offerItem.equals(offerItem2)

        where: "the values are as follows"
        quantity | productDescription | productName | unitPrice | quantityDiscount | serviceProvider | unit | itemTotal | quantity2 | productDescription2 | productName2 | unitPrice2 | quantityDiscount2 | serviceProvider2 | unit2 | itemTotal2
        3|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description2"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name2"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name"|3.0|1.0|"Quantitative Biology Center"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name"|2.0|3.0|"Quantitative Biology Center"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name"|2.0|1.0|"Some place"|"Sample"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Unit"|30.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00
        2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|42.00 | 2|"description"|"name"|2.0|1.0|"Quantitative Biology Center"|"Sample"|30.00


    }
}