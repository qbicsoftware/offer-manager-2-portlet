package life.qbic.portal.offermanager.components.offer.update;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Optional;
import life.qbic.portal.offermanager.components.Resettable;
import life.qbic.portal.offermanager.components.offer.create.CreateOfferViewModel;

/**
 * Provides a layout containing components for showing and manipulating the projectInformation of an
 * offer during the update Offer Process
 *
 * @since 1.6.0
 **/

public class ProjectInformationLayout extends FormLayout implements Resettable {

  private VerticalLayout contentLayout;
  private final CreateOfferViewModel createOfferViewModel;
  public TextField projectTitle;
  public TextArea projectObjective;
  public TextArea experimentalDesign;

  protected ProjectInformationLayout(CreateOfferViewModel createOfferViewModel) {
    this.createOfferViewModel = createOfferViewModel;
    initComponents();
    initLayout();
    addListener();
    styleLayout();
    styleComponents();
  }

  private void initLayout() {
    contentLayout = new VerticalLayout();
    contentLayout.addComponents(projectTitle, projectObjective, experimentalDesign);
    this.addComponent(contentLayout);
  }

  private void initComponents() {
    this.projectTitle = new TextField("Project Title");
    this.projectObjective = new TextArea("Project Objective");
    this.experimentalDesign = new TextArea("Experimental Design");
  }

  private void styleLayout() {
    contentLayout.setSizeFull();
    contentLayout.setMargin(false);
    contentLayout.setComponentAlignment(projectTitle, Alignment.TOP_CENTER);
    contentLayout.setComponentAlignment(projectObjective, Alignment.BOTTOM_CENTER);
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
    return projectTitle.getValue() != null &&
        projectObjective.getValue() == null;
  }

  private boolean hasNoComponentError() {
    return projectTitle.getComponentError() == null &&
        projectObjective == null;
  }

  public boolean isValid() {
    return hasNoComponentError() && requiredFieldsFilled();
  }

  public void update() {
    projectTitle.setValue(createOfferViewModel.getProjectTitle());
    projectObjective.setValue(createOfferViewModel.getProjectObjective());
    Optional.ofNullable(createOfferViewModel.getExperimentalDesign())
        .ifPresent(experimentalDesign::setValue);
  }

  @Override
  public void reset() {
    projectTitle.clear();
    projectObjective.clear();
    experimentalDesign.clear();
  }

}
