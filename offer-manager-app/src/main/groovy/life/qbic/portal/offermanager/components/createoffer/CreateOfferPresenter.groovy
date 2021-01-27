package life.qbic.portal.offermanager.components.createoffer

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService
import life.qbic.portal.offermanager.components.AppViewModel

/**
 * AppPresenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput {

    private final AppViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel
    private final OfferResourcesService offerService

    CreateOfferPresenter(AppViewModel viewModel, CreateOfferViewModel createOfferViewModel,
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
