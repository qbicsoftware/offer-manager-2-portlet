package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.VerticalLayout
import life.qbic.portal.qoffer2.web.controllers.CreateOfferController
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.views.offer.ProjectInformationView

/**
 * This class generates a Layout in which the user
 * can input the necessary information for the creation of a new offer
 *
 * CreateOfferView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Offer in the QBiC Database
 *
 * @since: 0.1.0
 *
 */
class CreateOfferView extends VerticalLayout{

    final private ViewModel sharedViewModel
    final private CreateOfferViewModel view
    final private CreateOfferController controller

    CreateOfferView(ViewModel sharedViewModel, CreateOfferViewModel createOfferViewModel, CreateOfferController controller) {
        super()
        this.sharedViewModel = sharedViewModel
        this.view = createOfferViewModel
        this.controller = controller

        initLayout()
    }

    private void initLayout(){
        this.addComponent(new ProjectInformationView())
    }

    private void bindViewModel(){

    }

    private void setupFieldValidators(){

    }

    private void registerListeners(){

    }
}
