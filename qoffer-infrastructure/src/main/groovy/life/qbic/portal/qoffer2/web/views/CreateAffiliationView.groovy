package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.Button
import com.vaadin.ui.VerticalLayout
import life.qbic.portal.qoffer2.web.controllers.CreateAffiliationController
import life.qbic.portal.qoffer2.web.viewmodel.CreateAffiliationViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * This class generates a Layout in which the user
 * can input the necessary information for the creation of a new affiliation
 *
 * CreateAffiliationView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of a new Affiliation in the QBiC Database
 *
 * @since: 1.0.0
 */
class CreateAffiliationView extends VerticalLayout {
    final private ViewModel sharedViewModel
    final private CreateAffiliationViewModel createAffiliationViewModel
    final private CreateAffiliationController controller

    private Button submitButton

    CreateAffiliationView(ViewModel sharedViewModel, CreateAffiliationViewModel createAffiliationViewModel, CreateAffiliationController controller) {
        super()
        this.sharedViewModel = sharedViewModel
        this.createAffiliationViewModel = createAffiliationViewModel
        this.controller = controller
        initLayout()
        bindViewModel()
        setupFieldValidators()
        registerListeners()
    }

    private void initLayout() {
        //TODO implement
        this.submitButton = new Button("Create Affiliation")
        this.addComponent(submitButton)
    }

    private void bindViewModel() {
        //TODO implement
    }

    private void setupFieldValidators() {
        //TODO implement
    }

    private void registerListeners() {
        //TODO implement
        submitButton.addClickListener({
            String category = createAffiliationViewModel.affiliationCategory
            String city = createAffiliationViewModel.city
            String organisation = createAffiliationViewModel.organisation
            String postalCode = createAffiliationViewModel.postalCode
            String street = createAffiliationViewModel.street

            this.controller.createAffiliation(organisation, street, postalCode, city, category) })
    }
}
