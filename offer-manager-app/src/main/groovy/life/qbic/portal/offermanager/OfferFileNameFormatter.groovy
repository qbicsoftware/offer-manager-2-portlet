package life.qbic.portal.offermanager

import life.qbic.datamodel.dtos.business.Offer

import java.time.LocalDate

/**
 * <h1>Formats the filename for an offer</h1>
 *
 * <p>An offers file name should contain the date of the offer creation, the project conserved part and the offer version</p>
 *
 * @since 1.0.0
 *
*/
class OfferFileNameFormatter {

    /**
     * Returns an offer file name in this schema:
     *
     * Q_<year>_<month>_<day>_<project-conserved-part>_<random-id-part>_v<offer-version>.pdf
     * @param offer
     * @return
     */
    static String getFileNameForOffer(Offer offer) {
        LocalDate date = offer.modificationDate.toLocalDate()
        String dateString = createDateString(date)
        return "Q_${dateString}_" +
                "${offer.identifier.projectConservedPart}_${offer.identifier.randomPart}_" +
                "v${offer.identifier.version}.pdf"
    }

    private static String createDateString(LocalDate date) {
        return String.format("%04d_%02d_%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth())
    }

}