package life.qbic.portal.qoffer2.web.presenters


import life.qbic.portal.portlet.customers.update.UpdateCustomerOutput
import life.qbic.portal.portlet.offers.create.CreateOfferOutput
import life.qbic.portal.portlet.offers.search.SearchOffersOutput
import life.qbic.portal.portlet.offers.update.UpdateOfferOutput
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 *  Handles the presentation of the qoffer-2.0 use cases and its internal data model to the ViewModel.
 *
 *  This class acts as connector between the use cases of qoffer-2.0 and ViewModel
 *  and should be used to transfer data between these components.
 *  It converts the use case output into data that is stored in the {@link ViewModel}.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class Presenter implements UpdateOfferOutput, UpdateCustomerOutput {

    final private ViewModel viewModel

    Presenter(ViewModel viewModel){
        this.viewModel = viewModel 
    }

    void successNotification(String notification) {
        viewModel.successNotifications.add(notification)
    }

    @Override
    void failNotification(String notification) {
        viewModel.failureNotifications.add(notification)
    }
}
