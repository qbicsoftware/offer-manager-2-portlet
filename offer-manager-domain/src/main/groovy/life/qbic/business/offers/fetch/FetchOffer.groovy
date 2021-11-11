package life.qbic.business.offers.fetch

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.Converter
import life.qbic.business.offers.identifier.TomatoIdFormatter
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId

/**
 * This class implements logic to fetch an Offer from the database
 *
 * This Use Case is responsible for correctly returning an Offer from the database.
 * It also triggers all the internal calculation methods associated with the offer entity
 * and returns the filled Offer DTO for further usage
 *
 * @since: 1.0.0
 */
class FetchOffer implements FetchOfferInput {

    private FetchOfferDataSource dataSource
    private FetchOfferOutput output
    private final Logging log = Logger.getLogger(this.class)

    FetchOffer(FetchOfferDataSource dataSource, FetchOfferOutput output){
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void fetchOffer(OfferId offerId) {
        try {
            Optional<Offer> foundOffer = dataSource.getOffer(offerId)
            if (!foundOffer.isPresent()) {
                output.failNotification("Could not find an Offer for the given OfferId ${TomatoIdFormatter.formatAsOfferId(offerId)}")
            } else {
                Offer finalOffer = generateOfferFromSource(foundOffer.get())
                log.info("Successfully retrieved Offer with Id ${TomatoIdFormatter.formatAsOfferId(offerId)} ")
                output.fetchedOffer(finalOffer)
            }
        }
        catch (DatabaseQueryException queryException) {
            log.error(queryException.message)
            output.failNotification("Could not retrieve Offer with OfferId ${TomatoIdFormatter.formatAsOfferId(offerId)} from the Database")
        }
        catch (Exception e) {
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            output.failNotification("Unexpected error when searching for the Offer associated with the Id ${TomatoIdFormatter.formatAsOfferId(offerId)}")
        }
    }
    private static Offer generateOfferFromSource(Offer offer){
        life.qbic.business.offers.Offer filledOffer = Converter.convertDTOToOffer(offer)
        Offer filledOfferDTO = Converter.convertOfferToDTO(filledOffer)
        return filledOfferDTO
    }

}
