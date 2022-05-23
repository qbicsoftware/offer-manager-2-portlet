package life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.offermanager.components.Resettable;


/**
 * This class generates a Layout in which the user can see the project information associated of the
 * offer selected in the UpdateOfferView
 * <p>
 * ProjectInformationLayout will be integrated into the UpdateOfferView and will list the project
 * title, project description and experimental design of an offer to be updated
 *
 * @since 1.6.0
 */
public class ProjectInformationLayout extends VerticalLayout implements Resettable {

  public TextField projectTitle;
  public TextArea projectObjective;
  public TextArea experimentalDesign;

  public ProjectInformationLayout() {
    initComponents();
    styleComponents();
    addListener();
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    this.addComponents(projectTitle, projectObjective, experimentalDesign);
  }

  private void styleLayout() {
    this.setSizeFull();
  }

  private void initComponents() {
    projectTitle = new TextField("Project Title");
    projectObjective = new TextArea("Project Objective");
    experimentalDesign = new TextArea("Experimental Design");
  }

  private void styleComponents() {
    projectTitle.setPlaceholder("Enter the project title here");
    projectTitle.setRequiredIndicatorVisible(true);
    projectTitle.setSizeFull();
    projectObjective.setPlaceholder("Enter the project objective here");
    projectObjective.setRequiredIndicatorVisible(true);
    projectObjective.setSizeFull();
    experimentalDesign.setPlaceholder("Enter the experimental design here");
    experimentalDesign.setSizeFull();
  }

  private void addListener() {
    projectTitle.setRequiredIndicatorVisible(true);
    projectTitle.addValueChangeListener(event -> addNotEmptyValidation(projectTitle, event));
    projectObjective.setRequiredIndicatorVisible(true);
    projectObjective.addValueChangeListener(
        event -> addNotEmptyValidation(projectObjective, event));
  }

  private void addNotEmptyValidation(AbstractComponent component, ValueChangeEvent<String> event) {
    Validator<String> nonEmptyStringValidator = Validator.from(
        value -> (value != null && !value.trim().isEmpty()), "Empty input not supported.");
    ValidationResult result = nonEmptyStringValidator.apply(event.getValue(),
        new ValueContext(component));
    if (result.isError()) {
      UserError error = new UserError(result.getErrorMessage());
      component.setComponentError(error);
    } else {
      component.setComponentError(null);
    }
  }

  private boolean requiredFieldsFilled() {
    return projectTitle.getValue() != null && projectObjective.getValue() == null;
  }

  private boolean hasNoComponentError() {
    return projectTitle.getComponentError() == null && projectObjective == null;
  }

  public boolean isValid() {
    return hasNoComponentError() && requiredFieldsFilled();
  }

  @Override
  public void reset() {
    projectTitle.clear();
    projectObjective.clear();
    experimentalDesign.clear();
  }
}
