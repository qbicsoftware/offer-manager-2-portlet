package life.qbic.portal.offermanager.components.offer.update

import life.qbic.business.offers.update.UpdateOfferOutput
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel
import life.qbic.portal.offermanager.dataresources.offers.OfferResourcesService

/**
 * AppPresenter for the UpdateOffer use case
 *
 * This presenter handles the output of the UpdateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 *
 */
class UpdateOfferPresenter implements UpdateOfferOutput{

    private final AppViewModel viewModel
    private final UpdateOfferViewModel updateOfferViewModel
    private final OfferResourcesService offerService

    UpdateOfferPresenter(AppViewModel viewModel, CreateOfferViewModel updateOfferViewModel, OfferResourcesService offerService) {
        this.viewModel = viewModel
        this.updateOfferViewModel = updateOfferViewModel
        this.offerService = offerService
    }

    @Override
    void updatedOffer(Offer createdOffer) {
        this.viewModel.successNotifications.add("Updated offer with title\n" +
                "${createdOffer.projectTitle} \n successfully to version ${createdOffer.identifier.toString()}")
        this.offerService.addToResource(createdOffer)
    }

    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice) {
        this.updateOfferViewModel.offerPrice = totalPrice
        this.updateOfferViewModel.netPrice = netPrice
        this.updateOfferViewModel.taxes = taxes
        this.updateOfferViewModel.overheads = overheads
    }

    @Override
    void failNotification(String notification) {
        this.viewModel.failureNotifications.add(notification)
    }
}
