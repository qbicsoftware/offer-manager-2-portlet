package life.qbic.portal.qoffer2.web.views.offer

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.Button
import com.vaadin.ui.VerticalLayout

/**
 * This class generates a Layout in which the user
 * can input the necessary information about a project
 *
 * ProjectInformationView will be integrated into the CreateOfferView and provides an User Interface
 * with the intention of enabling a user the adding the project information for which an offer will be created in the
 * QBiC database.
 *
 * @since: 0.1.0
 *
 */
class ProjectInformationView extends VerticalLayout{

    ProjectInformationView(){
        initLayout()
    }

    private void initLayout(){

        Button submitButton = new Button("Create Customer")
        submitButton.setIcon(VaadinIcons.USER)

        this.addComponent(submitButton)
    }

}
