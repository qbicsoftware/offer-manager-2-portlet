package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * <class short description - One Line!>
 * <p>
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <version tag>
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
  }
}
