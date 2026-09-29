package life.qbic.portal.offermanager.dataresources.offers

import java.time.LocalDate

/**
 * Holds the server-side filter criteria for the offer overview grid.
 *
 * <p>Each field corresponds to a grid column filter and is matched case-insensitively as a
 * substring against the corresponding column value. A {@code null} or empty field means the
 * filter is not applied. {@link #creationDate} matches the exact creation date.</p>
 *
 * @since 1.0.0
 */
class OfferFilter {

    String offerId

    String projectTitle

    String customer

    String affiliationCategory

    String organisation

    String addressAddition

    String projectManager

    String projectId

    LocalDate creationDate

    boolean isEmpty() {
        !offerId && !projectTitle && !customer && !affiliationCategory && !organisation &&
                !addressAddition && !projectManager && !projectId && !creationDate
    }
}