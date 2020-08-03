package life.qbic

import life.qbic.portal.portlet.customers.create.CreateCustomerOutput
import life.qbic.portal.portlet.customers.update.UpdateCustomerOutput
import life.qbic.portal.portlet.offers.create.CreateOfferOutput
import life.qbic.portal.portlet.offers.search.SearchOffersOutput
import life.qbic.portal.portlet.offers.update.UpdateOfferOutput

/**
 *  Handles the presentation of the qoffer-2.0 use cases and its internal data model to the viewModel.
 *
 *  This class acts as connector between the use cases of qoffer-2.0 and its viewModel
 *  and should be used to transfer data between these components.
 *  It prepares the view model, which can then be displayed by the view implementing {@link PortletView}.
 *
 * @since: 1.0
 * @author: Jennifer Bödker
 *
 */
class Presenter implements SearchOffersOutput, CreateOfferOutput, UpdateOfferOutput, CreateCustomerOutput, UpdateCustomerOutput {
}
