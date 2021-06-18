package life.qbic.portal.offermanager.offergeneration

import org.jsoup.nodes.Document
import life.qbic.datamodel.dtos.business.*


/**
 * <h1>Describes the content of an offer HTML</h1>
 *
 * <p>An offer consists of a {@link QuotationOverview} and {@link QuotationDetails}. This class
 * generates the HTML code for each part of the offer.</p>
 *
 * @since 1.1.0
 *
*/
class OfferHTMLDocument {

    private final Document htmlContent

    OfferHTMLDocument(Document templateHTML){
        htmlContent = templateHTML
    }

    /**
     * Fills the html template document with the offers content and returns
     * the final offer html
     * @param offer The offer object containing the required offer information
     * @return the final offer html
     */
    Document fillTemplateWithOfferContent(Offer offer){
        //todo implement me
        //calculate price
        //1. fill first page (offeroverview)
        //2. fill item table


        return htmlContent
    }


}