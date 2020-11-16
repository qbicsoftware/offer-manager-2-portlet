package life.qbic.portal.qoffer2.web.views.create.offer

import com.vaadin.ui.VerticalLayout
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel

/**
 * This class generates a Layout in which the user
 * can select the project manager assigned to this project
 *
 * ProjectManagerSelectionView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the selecting a project manager who will be responsible for the created offer.
 *
 * @since: 0.1.0
 *
 */
class ProjectManagerSelectionView extends VerticalLayout{

    final private CreateOfferViewModel viewModel

    ProjectManagerSelectionView(CreateOfferViewModel viewModel){
        this.viewModel = viewModel
    }

}
