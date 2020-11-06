package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.offers.create.CreateOfferOutput

/**
 * Presenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput{
    @Override
    void createdNewOffer(Offer createdOffer) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
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
