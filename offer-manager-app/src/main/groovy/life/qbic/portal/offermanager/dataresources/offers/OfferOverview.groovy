package life.qbic.portal.offermanager.dataresources.offers

import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * This class holds data for an offer overview
 *
 * It is not always necessary to pass along complete offer data in the application and display this to the user.
 * For this purpose, you can use this class and provide a concise offer overview, 
 * and its content can be loaded quick and easy.
 *
 * @since 1.0.0
 */
class OfferOverview {

    final String projectTitle

    /**
     * @deprecated Use the {@link #associatedProject} property to link an offer with a project
     */
    @Deprecated
    final String projectId

    final String customer

    final Date modificationDate

    final double totalPrice

    final OfferId offerId

    final Optional<ProjectIdentifier> associatedProject

    @Deprecated
    OfferOverview(
            OfferId offerId,
            Date modificationDate,
            String projectTitle,
            String projectId,
            String customer,
            double totalPrice) {
        this.offerId = offerId
        this.modificationDate = modificationDate
        this.projectId = projectId
        this.projectTitle = projectTitle
        this.customer = customer
        this.totalPrice = totalPrice
        this.associatedProject = Optional.empty()
    }

    OfferOverview(
            OfferId offerId,
            Date modificationDate,
            String projectTitle,
            String customer,
            double totalPrice,
            ProjectIdentifier associatedProject) {
        this.offerId = offerId
        this.modificationDate = modificationDate
        this.projectId = ""
        this.projectTitle = projectTitle
        this.customer = customer
        this.totalPrice = totalPrice
        this.associatedProject = Optional.of(associatedProject)
    }
}
