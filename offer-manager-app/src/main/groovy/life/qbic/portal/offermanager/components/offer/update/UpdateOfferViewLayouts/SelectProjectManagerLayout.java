package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * <class short description - One Line!>
 * <p>
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
 */
public class SelectProjectManagerLayout extends VerticalLayout {

  private Label projectManagerHeader;
  private Button updateProjectManagerButton;
  private HorizontalLayout projectManagerHeaderLayout;
  private Label projectManagerName;
  private Label projectManagerOrganisation;

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
  }

}
