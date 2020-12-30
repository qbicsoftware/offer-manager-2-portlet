package life.qbic.portal.qoffer2.shared

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class OfferOverview {

    final String projectTitle

    final String projectId

    final String customer

    final Date modificationDate

    final double totalPrice

    OfferOverview(
            Date modificationDate,
            String projectTitle,
            String projectId,
            String customer,
            double totalPrice) {
        this.modificationDate = modificationDate
        this.projectId = projectId
        this.projectTitle = projectTitle
        this.customer = customer
        this.totalPrice = totalPrice
    }
}
