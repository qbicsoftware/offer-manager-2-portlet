package life.qbic.portal.offermanager

import life.qbic.business.offers.identifier.OfferIdFormatter
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId

import java.time.LocalDate

/**
 * <h1>Formats the filename for an offer</h1>
 *
 * <p>An offers file name should contain the date of the offer creation, a type prefix, the project conserved part, a randomly generated id and the offer version</p>
 *
 * @since 1.0.0
 *
*/
class OfferFileNameFormatter {

    /**
     * Returns an offer file name in this schema:
     *
     * <p>{@code <year>_<month>_<day>_O_<project-conserved-part>_<random-id-part>_<offer-version>.pdf</code>}</p>
     * @param offer
     * @return
     */
    static String getFileNameForOffer(Offer offer) {
        LocalDate date = offer.modificationDate.toLocalDate()
        OfferFileName fileName = new OfferFileName(date, offer.getIdentifier())
        return fileName.toString()
    }

    private static class OfferFileName {
        LocalDate date
        OfferId offerId

        OfferFileName(LocalDate date, OfferId offerId) {
            this.date = date
            this.offerId = offerId
        }

        private static String createDateString(LocalDate date) {
            return String.format("%04d_%02d_%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth())
        }

        /**
         * Answers a string containing a concise, human-readable
         * description of the receiver.
         * The returned format is: 
         *            <date>_O_<pi-name>_<offer-randid>_<offer-version>.pdf
         *
         * @return String a printable representation for the receiver.
         */
        @Override
        String toString() {
            return "${createDateString(this.date)}_${OfferIdFormatter.formatAsOfferId(offerId)}" +
                    ".pdf"
        }
    }

}
