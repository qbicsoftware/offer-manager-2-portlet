package life.qbic.portal.offermanager.components.offer.create

import life.qbic.business.RefactorConverter
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.create.CalculatePriceOutput
import life.qbic.business.offers.create.CreateOfferOutput
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * AppPresenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput, FetchOfferOutput, CalculatePriceOutput{

    private final AppViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel
    private final ResourcesService<Offer> offerService

    private final RefactorConverter refactorConverter = new RefactorConverter()

    CreateOfferPresenter(AppViewModel viewModel, CreateOfferViewModel createOfferViewModel,
                         ResourcesService<Offer> offerService){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
        this.offerService = offerService
    }

    @Override
    void createdNewOffer(OfferV2 offer) {
        Offer offerDto = refactorConverter.toOfferDto(offer)

        this.viewModel.successNotifications.add("Created offer with title " +
                "\'${offerDto.projectTitle}\' successfully")

        this.offerService.addToResource(offerDto)
        this.createOfferViewModel.setOfferCreatedSuccessfully(true)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice, double totalDiscountAmount) {
        this.createOfferViewModel.netPrice = netPrice
        this.createOfferViewModel.taxes = taxes
        this.createOfferViewModel.overheads = overheads
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
    void fetchedOffer(OfferV2 fetchedOffer) {
        Offer offerDto = refactorConverter.toOfferDto(fetchedOffer)
        this.createOfferViewModel.savedOffer = Optional.of(offerDto)
    }
}
