package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.offers.create.CreateOfferOutput
import life.qbic.portal.qoffer2.offers.OfferResourcesService
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Presenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput {

    private final ViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel
    private final OfferResourcesService offerService

    CreateOfferPresenter(ViewModel viewModel, CreateOfferViewModel createOfferViewModel,
                         OfferResourcesService offerService){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
        this.offerService = offerService
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        this.viewModel.successNotifications.add("Created offer with title\n" +
                "\'${createdOffer.projectTitle}\'\nsuccessfully")
        this.offerService.offerCreatedEvent.emit(createdOffer)
    }

    @Override
    void calculatedPrice(double price) {
        this.createOfferViewModel.offerPrice = price
    }

    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice) {
        this.createOfferViewModel.netPrice = netPrice
        this.createOfferViewModel.taxes = taxes
        this.createOfferViewModel.overheads = overheads
        this.createOfferViewModel.totalPrice = totalPrice
    }

    @Override
    void failNotification(String notification) {
       this.viewModel.failureNotifications.add(notification)
    }

}
