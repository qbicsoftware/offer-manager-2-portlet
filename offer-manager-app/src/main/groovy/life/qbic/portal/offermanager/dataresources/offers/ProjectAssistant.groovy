package life.qbic.portal.offermanager.dataresources.offers

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * Small helper interface that provides linking functionality of offers and projects
 *
 * @since 1.0.0
 */
interface ProjectAssistant {

    /**
     * Link an offer with an associated project id.
     * @param offerId The offer you want to link to the project
     * @param projectIdentifier The project you want to have the offer linked to
     * @throws DatabaseQueryException if the update of the offer entry cannot be performed
     */
    void linkOfferWithProject(OfferId offerId, ProjectIdentifier projectIdentifier)
            throws DatabaseQueryException

}
