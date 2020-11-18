package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.offers.create.CreateOfferOutput
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Presenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput{

    private final ViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel

    CreateOfferPresenter(ViewModel viewModel, CreateOfferViewModel createOfferViewModel){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void calculatedPrice(double price) {
        this.createOfferViewModel.offerPrice = price
    }

    void successNotification(String notification) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void failNotification(String notification) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }
}
