package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class generates a Layout in which the user can see the project manager information of the
 * offer selected in the UpdateOfferView
 * <p>
 * SelectProjectManagerLayout will be integrated into the UpdateOfferView and will list the full
 * name and the affiliation of a project Manager associated with the offer to be updated
 *
 * @since 1.6.0
 */
public class SelectProjectManagerLayout extends VerticalLayout {

  private Label projectManagerHeader;
  public Button updateProjectManagerButton;
  private HorizontalLayout projectManagerHeaderLayout;
  public Label projectManagerName;
  public Label projectManagerOrganisation;

  private VerticalLayout projectManagerDetailLayout;

  public SelectProjectManagerLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    projectManagerHeader = new Label("Project Manager:");
    updateProjectManagerButton = new Button("Update");
    projectManagerHeaderLayout = new HorizontalLayout();
    projectManagerHeaderLayout.addComponents(projectManagerHeader, updateProjectManagerButton);
    projectManagerName = new Label("Maxime MusterFrau");
    projectManagerOrganisation = new Label("University Clinic Tübingen");
    projectManagerDetailLayout = new VerticalLayout();
    projectManagerDetailLayout.addComponents(projectManagerName, projectManagerOrganisation);
    this.addComponents(projectManagerHeaderLayout, projectManagerDetailLayout);
  }

  private void styleLayout() {
    projectManagerDetailLayout.setMargin(false);
    projectManagerHeaderLayout.setComponentAlignment(updateProjectManagerButton,
        Alignment.TOP_RIGHT);
  }

}
