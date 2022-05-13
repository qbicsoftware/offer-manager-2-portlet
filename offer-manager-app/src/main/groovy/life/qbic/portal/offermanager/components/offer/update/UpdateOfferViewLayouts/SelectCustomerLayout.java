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
public class SelectCustomerLayout extends VerticalLayout {

  private Label customerHeader;
  private Button updateCustomerButton;
  private Label customerName;

  private HorizontalLayout customerHeaderLayout;

  private Label affiliationCategory;

  private Label affiliationInstitute;

  private Label affiliationOrganisation;

  private VerticalLayout affiliationInformationLayout;

  public SelectCustomerLayout() {
    initLayout();
    styleLayout();
  }

  private void initLayout() {
    customerHeader = new Label("Customer:");
    updateCustomerButton = new Button("Update");
    customerName = new Label("Max Mustermann");
    affiliationCategory = new Label("Internal");
    affiliationInstitute = new Label("University Tübingen");
    affiliationOrganisation = new Label("Quantitative Biology Center");
    customerHeaderLayout = new HorizontalLayout();
    customerHeaderLayout.addComponents(customerHeader, updateCustomerButton);
    affiliationInformationLayout = new VerticalLayout();
    affiliationInformationLayout.addComponents(affiliationCategory, affiliationInstitute,
        affiliationOrganisation);
    this.addComponents(customerHeaderLayout, customerName, affiliationInformationLayout);
  }

  private void styleLayout() {
    affiliationInformationLayout.setMargin(false);
  }
}
