package life.qbic.portal.offermanager.components.offer.update

import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.offer.create.*
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

  //ToDo Remove Views once Layout is written
  private final ProjectInformationView projectInformationView

  //ToDo Add Layouts for each View below for UpdateOfferMainView
  private GridLayout contentGridLayout
  private VerticalLayout projectInformationLayout
  private VerticalLayout customerInformationLayout
  private VerticalLayout projectManagerLayout
  private VerticalLayout pricingLayout
  private HorizontalLayout productItemsLayout
  private HorizontalLayout cancelSubmissionButtonBarLayout


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
    //ToDo Wire View Navigation into button functionality of MainView
    this.createCustomerView = createCustomerView
    this.updatePersonView = updatePersonView
    this.createAffiliationView = createAffiliationView
    //ToDo Replace View with Layout
    this.projectInformationView = new ProjectInformationView(viewModel)
    this.customerSelectionView = new CustomerSelectionView(viewModel)
    this.projectManagerSelectionView = new ProjectManagerSelectionView(viewModel)
    this.selectItemsView = new SelectItemsView(viewModel, sharedViewModel)
    //ToDo Include OverViewView Layout with Pricing for MainView
    initMainLayout()
    initSubLayouts()
    positionLayoutsInMainLayout()
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

  private void initSubLayouts() {
    //ToDo Implement SubLayouts initialization for MainLayout
    contentGridLayout = new GridLayout(2, 6)
    projectInformationLayout = new VerticalLayout()
    projectInformationLayout.addComponent(new Button("ProjectInformationArea"))
    customerInformationLayout = new VerticalLayout()
    customerInformationLayout.addComponent(new Button("CustomerInformationArea"))
    projectManagerLayout = new VerticalLayout()
    projectManagerLayout.addComponent(new Button("ProjectManagerArea"))
    pricingLayout = new VerticalLayout()
    pricingLayout.addComponent(new Button("PricingInformationArea"))
    productItemsLayout = new HorizontalLayout()
    productItemsLayout.addComponent(new Button("ProductItemsArea"))
    cancelSubmissionButtonBarLayout = new HorizontalLayout()
    cancelSubmissionButtonBarLayout.addComponent(new Button("SubmissionCancelArea"))
    contentGridLayout.setWidthFull()
    contentGridLayout.setHeightFull()
  }

  void positionLayoutsInMainLayout() {
    //Todo Replace View with Layout
    contentGridLayout.addComponent(projectInformationView, 0, 0, 0, 1)
    contentGridLayout.addComponent(customerInformationLayout, 1, 1)
    contentGridLayout.addComponent(projectManagerLayout, 1, 2)
    contentGridLayout.addComponent(pricingLayout, 1, 3)
    //ToDo extract relevant view from layout
    contentGridLayout.addComponent(selectItemsView, 0, 4, 1, 4)
    contentGridLayout.addComponent(cancelSubmissionButtonBarLayout, 1, 5)
  }

  void resetViewContent() {
    //ToDo Reset Content of all Layouts and Views
  }
}
