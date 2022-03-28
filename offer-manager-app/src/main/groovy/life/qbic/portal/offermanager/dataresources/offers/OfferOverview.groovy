package life.qbic.portal.offermanager.dataresources.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.business.offers.OfferV2
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
@EqualsAndHashCode
class OfferOverview {

    final String projectTitle

    /**
     * @deprecated Use the {@link #associatedProject} property to link an offer with a project
     */
    @Deprecated
    final String projectId

    final String customer

    final String projectManager

    final Date modificationDate

    final double totalPrice

    final OfferId offerId

    final Optional<ProjectIdentifier> associatedProject

    static OfferOverview from(OfferV2 offer) {
        return new OfferOverview(
                new OfferId(offer.identifier.getProjectPart(),offer.identifier.getRandomPart(), offer.identifier.getVersion() as String),
                offer.getCreationDate().toDate(),
                offer.getProjectTitle(),
                "",
                String.format("%s %s", offer.getCustomer().firstName, offer.getCustomer().lastName),
                String.format("%s %s", offer.getProjectManager().firstName, offer.getProjectManager().lastName),
                offer.getPriceAfterTax().doubleValue()
        )
    }

    OfferOverview(
            OfferId offerId,
            Date modificationDate,
            String projectTitle,
            String projectId,
            String customer,
            String projectManager,
            double totalPrice) {
        this.offerId = offerId
        this.modificationDate = modificationDate
        this.projectId = projectId
        this.projectTitle = projectTitle
        this.customer = customer
        this.projectManager = projectManager
        this.totalPrice = totalPrice
        this.associatedProject = Optional.empty()
    }

    OfferOverview(
            OfferId offerId,
            Date modificationDate,
            String projectTitle,
            String customer,
            String projectManager,
            double totalPrice,
            ProjectIdentifier associatedProject) {
        this.offerId = offerId
        this.modificationDate = modificationDate
        this.projectId = ""
        this.projectTitle = projectTitle
        this.customer = customer
        this.projectManager = projectManager
        this.totalPrice = totalPrice
        this.associatedProject = Optional.of(associatedProject)
    }
}
