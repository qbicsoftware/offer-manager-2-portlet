package life.qbic.portal.portlet.offers.create

import life.qbic.business.offers.create.CreateOffer
import life.qbic.business.offers.create.CreateOfferDataSource
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Sequencing
import spock.lang.Specification

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOfferSpec extends Specification {
    CreateOfferOutput output

    def "calculate offer price correctly"(){
        given:
        output = Mock(CreateOfferOutput)
        CreateOffer createOffer = new CreateOffer(Stub(CreateOfferDataSource),output)

        and:
        List<ProductItem> items = [new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE)),
                                   new ProductItem(1,new Sequencing("This is a sequencing package", "a short description",1.4, ProductUnit.PER_SAMPLE))]
        when:
        createOffer.calculatePrice(items, new Affiliation.Builder("Test", "", "", "").category
        (AffiliationCategory.INTERNAL).build())

        then:
        1 * output.calculatedPrice(2.8, 0, 0, 2.8)
    }
}
