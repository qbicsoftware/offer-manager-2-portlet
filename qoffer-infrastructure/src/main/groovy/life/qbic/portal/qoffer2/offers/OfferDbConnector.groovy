package life.qbic.portal.qoffer2.offers


import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.exceptions.DatabaseQueryException
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.database.DatabaseSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Handles the connection to the offer database
 *
 * Implements {@link CreateOfferDataSource} and is responsible for transferring data between the offer database and qOffer
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class OfferDbConnector implements CreateOfferDataSource{

    DatabaseSession session

    private static final Logger LOG = LogManager.getLogger(OfferDbConnector.class)

    OfferDbConnector(DatabaseSession session){
        this.session = session
    }

    @Override
    void store(Offer offer) throws DatabaseQueryException {
        //TODO implement
        throw new Exception("Method not implemented.")
    }
}
