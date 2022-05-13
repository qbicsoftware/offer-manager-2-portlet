package life.qbic.portal.offermanager.components.offer.update

import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.GridLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.offer.create.*
import life.qbic.portal.offermanager.components.offer.update.UpdateOfferViewLayouts.*
import life.qbic.portal.offermanager.components.person.create.CreatePersonView
import life.qbic.portal.offermanager.components.person.update.UpdatePersonView

/**
 * This class generates a Layout in which the user
 * can input the necessary information for the creation of a new offer
 *
 * UpdateOfferView will be integrated into the qOffer 2.0 Portlet and provides an User Interface
 * with the intention of enabling a user the creation of an updated version of an already stored Offer in the QBiC Database
 *
 * @since: 1.6.0
 *
 */
class UpdateOfferView extends VerticalLayout {

  final private AppViewModel sharedViewModel
  final CreateOfferViewModel viewModel

  private final UpdateOfferController controller

  //ToDo Remove View once content is wired into ProjectInformationLayout
  private final ProjectInformationView projectInformationView

  //ToDo Wire information into each Layout and style layouts
  private GridLayout contentGridLayout
  private ProjectInformationLayout projectInformationLayout
  private SelectCustomerLayout selectCustomerLayout
  private SelectProjectManagerLayout selectProjectManagerLayout
  private HorizontalLayout offerDetailsHeaderLayout
  private PricingLayout pricingLayout
  private ProductItemsLayout productItemsLayout
  private SubmissionButtonBarLayout submissionButtonBarLayout

  private final CustomerSelectionView customerSelectionView
  private final ProjectManagerSelectionView projectManagerSelectionView
  private final SelectItemsView selectItemsView

  private final CreatePersonView createCustomerView
  private final UpdatePersonView updatePersonView
  private final CreateAffiliationView createAffiliationView

  UpdateOfferView(AppViewModel sharedViewModel,
                  UpdateOfferViewModel updateOfferViewModel,
                  UpdateOfferController controller,
                  CreatePersonView createCustomerView,
                  CreateAffiliationView createAffiliationView,
                  UpdatePersonView updatePersonView) {
    super()
    this.sharedViewModel = sharedViewModel
    this.viewModel = updateOfferViewModel
    this.controller = controller
    //ToDo Wire View Navigation to each view into the buttons of the updateOfferView
    this.createCustomerView = createCustomerView
    this.updatePersonView = updatePersonView
    this.createAffiliationView = createAffiliationView
    this.projectInformationView = new ProjectInformationView(viewModel)
    this.customerSelectionView = new CustomerSelectionView(viewModel)
    this.projectManagerSelectionView = new ProjectManagerSelectionView(viewModel)
    this.selectItemsView = new SelectItemsView(viewModel, sharedViewModel)
    initMainLayout()
    initSubLayouts()
    positionSubLayouts()
    this.addComponent(contentGridLayout)
  }

  /**
   * Initializes the view with the ProjectInformationView, which is the first component to be shown
   */
  private void initMainLayout() {
    final Label label = new Label("Update Offer")
    label.addStyleName(ValoTheme.LABEL_HUGE)
    this.addComponent(label)
    this.setSizeFull()
    this.setMargin(false)
    this.setSpacing(false)
  }
  /**
   * See https://miro.com/app/board/uXjVO4E_5wc=/ for explanation of Grid sections
   */

  private void initSubLayouts() {
    //Todo Implement Layout Styling and Alignment
    contentGridLayout = new GridLayout(3, 6)
    projectInformationLayout = new ProjectInformationLayout()
    selectCustomerLayout = new SelectCustomerLayout()
    selectProjectManagerLayout = new SelectProjectManagerLayout()
    offerDetailsHeaderLayout = new HorizontalLayout()
    //Todo Move margins to custom CSS?
    offerDetailsHeaderLayout.addComponent(new Label("Offer Details:"))
    offerDetailsHeaderLayout.setMargin(new MarginInfo(false, false, false, true))
    pricingLayout = new PricingLayout()
    productItemsLayout = new ProductItemsLayout()
    submissionButtonBarLayout = new SubmissionButtonBarLayout()
    contentGridLayout.setSizeFull()
  }

  private void positionSubLayouts() {

    contentGridLayout.addComponent(projectInformationLayout, 0, 0, 1, 1)
    contentGridLayout.addComponent(selectCustomerLayout, 2, 0)
    contentGridLayout.addComponent(selectProjectManagerLayout, 2, 1)
    contentGridLayout.addComponent(offerDetailsHeaderLayout, 0, 2)
    contentGridLayout.addComponent(pricingLayout, 1, 3)
    contentGridLayout.addComponent(productItemsLayout, 0, 4, 2, 4)
    contentGridLayout.addComponent(submissionButtonBarLayout, 2, 5)
  }

  void resetLayoutContent() {
    //ToDo Reset Content of all Layouts and Views
  }
}
