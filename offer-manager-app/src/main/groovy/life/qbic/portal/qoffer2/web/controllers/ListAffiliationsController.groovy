package life.qbic.portal.qoffer2.web.controllers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsInput

/**
 * This class serves as adapter for the ListAffiliations use case
 *
 * As a user, I want to list all affiliations available to me.
 *
 * @since: 1.0.0
 */
class ListAffiliationsController {

    private final ListAffiliationsInput useCase

    ListAffiliationsController(ListAffiliationsInput useCase) {
        this.useCase = useCase
    }

    void listAffiliations()  {
        useCase.listAffiliations()
    }
}
