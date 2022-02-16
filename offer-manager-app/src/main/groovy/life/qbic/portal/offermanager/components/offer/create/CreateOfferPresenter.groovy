package life.qbic.portal.offermanager.components.offer.create

import life.qbic.business.offers.Offer
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * AppPresenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput, FetchOfferOutput{

    private final AppViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel
    private final ResourcesService<Offer> offerService

    CreateOfferPresenter(AppViewModel viewModel, CreateOfferViewModel createOfferViewModel,
                         ResourcesService<Offer> offerService){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
        this.offerService = offerService
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        this.viewModel.successNotifications.add("Created offer with title " +
                "\'${createdOffer.projectTitle}\' successfully")

        this.offerService.addToResource(createdOffer)
        this.createOfferViewModel.setOfferCreatedSuccessfully(true)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculatedPrice(double price) {
        this.createOfferViewModel.offerPrice = price
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice) {
        this.createOfferViewModel.netPrice = netPrice
        this.createOfferViewModel.taxes = taxes
        this.createOfferViewModel.overheads = overheads
        this.createOfferViewModel.totalPrice = totalPrice
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice, double totalDiscountAmount) {
        this.calculatedPrice(netPrice, taxes, overheads, totalPrice)
        this.createOfferViewModel.totalDiscountAmount = totalDiscountAmount
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void failNotification(String notification) {
       this.viewModel.failureNotifications.add(notification)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void fetchedOffer(Offer fetchedOffer) {
        this.createOfferViewModel.savedOffer = Optional.of(fetchedOffer)
    }
}
