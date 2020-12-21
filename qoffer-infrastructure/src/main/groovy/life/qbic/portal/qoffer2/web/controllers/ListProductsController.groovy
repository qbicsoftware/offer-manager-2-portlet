package life.qbic.portal.qoffer2.web.controllers

import life.qbic.portal.portlet.customers.affiliation.list.ListAffiliationsInput
import life.qbic.portal.portlet.products.ListProductsInput

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */
class ListProductsController {
    private final ListProductsInput useCase

    ListProductsController(ListProductsInput useCase) {
        this.useCase = useCase
    }

    void listProducts()  {
        useCase.listAvailableProducts()
    }
}
