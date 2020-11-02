package life.qbic.portal.qoffer2.offers

import life.qbic.datamodel.accounting.ProductItem
import life.qbic.datamodel.accounting.Quotation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.QuotationId
import life.qbic.datamodel.persons.Person
import life.qbic.portal.portlet.offers.OfferDbGateway
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Handles the connection to the offer database
 *
 * Implements {@link OfferDbGateway} and is responsible for transferring data between the offer database and qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class OfferDbConnector implements OfferDbGateway{

    DatabaseSession session

    private static final Logger LOG = LogManager.getLogger(OfferDbConnector.class)

    OfferDbConnector(DatabaseSession session){
        this.session = session
    }

    @Override
    Quotation getOfferByID(QuotationId id) {
        return null
    }

    @Override
    void createOffer(QuotationId id, String projectTitle, String projectDescription, Person customer, Person projectManager, List<ProductItem> productItems) {

    }
}
