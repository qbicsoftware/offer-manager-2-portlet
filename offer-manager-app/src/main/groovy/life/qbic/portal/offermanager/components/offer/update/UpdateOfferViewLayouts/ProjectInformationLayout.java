package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This class generates a Layout in which the user can see the project information associated of the
 * offer selected in the UpdateOfferView
 * <p>
 * ProjectInformationLayout will be integrated into the UpdateOfferView and will list the project
 * title, project description and experimental design of an offer to be updated
 *
 * @since 1.6.0
 */
public class ProjectInformationLayout extends VerticalLayout {

  private TextField projectTitle;
  private TextArea projectObjective;
  private TextArea experimentalDesign;

  public ProjectInformationLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    projectTitle = new TextField("Project Title", "What a Project");
    projectObjective = new TextArea("Project Objective", "What an Objective");
    experimentalDesign = new TextArea("Experimental Design",
        "Sometimes we design experiments but most of the time we don't");
    this.addComponents(projectTitle, projectObjective, experimentalDesign);
  }

  private void styleLayout() {
    projectTitle.setSizeFull();
    projectObjective.setSizeFull();
    experimentalDesign.setSizeFull();
    this.setSizeFull();
  }
}
