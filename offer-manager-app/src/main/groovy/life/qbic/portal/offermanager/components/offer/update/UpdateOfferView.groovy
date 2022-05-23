package life.qbic.portal.offermanager.components.offer.update

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.communication.EventEmitter
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
  //ToDo Wire information into each Layout and finalize layout style
  private GridLayout contentGridLayout
  private ProjectInformationLayout projectInformationLayout
  private SelectCustomerLayout selectCustomerLayout
  private SelectProjectManagerLayout selectProjectManagerLayout
  private HorizontalLayout offerDetailsHeaderLayout
  private PricingLayout pricingLayout
  private ProductItemsLayout productItemsLayout
  private SubmissionButtonBarLayout submissionButtonBarLayout

  private CustomerSelectionView customerSelectionView
  private ProjectManagerSelectionView projectManagerSelectionView
  private SelectItemsView selectItemsView

  private CreatePersonView createCustomerView
  private UpdatePersonView updatePersonView
  private CreateAffiliationView createAffiliationView

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
    initMainLayout()
    initSubLayouts()
    initLinkedViews(createCustomerView, createAffiliationView, updatePersonView)
    positionSubLayouts()
    styleSubLayouts()
    bindViewModelToLayouts()
    addLinkedViewListeners()
    addLayoutClickListeners()
    this.addComponent(contentGridLayout)
  }

  /**
   * Initializes the view with the ProjectInformationView, which is the first component to be shown*/
  private void initMainLayout() {
    final Label label = new Label("Update Offer")
    label.addStyleName(ValoTheme.LABEL_HUGE)
    this.addComponent(label)
    this.setSizeFull()
    this.setMargin(false)
    this.setSpacing(false)
  }

  /**
   * See https://miro.com/app/board/uXjVO4E_5wc=/ for explanation of Grid sections*/
  private void initSubLayouts() {
    contentGridLayout = new GridLayout(3, 6)
    projectInformationLayout = new ProjectInformationLayout()
    selectCustomerLayout = new SelectCustomerLayout()
    selectProjectManagerLayout = new SelectProjectManagerLayout()
    offerDetailsHeaderLayout = new HorizontalLayout()
    offerDetailsHeaderLayout.addComponent(new Label("Offer Details:"))
    pricingLayout = new PricingLayout()
    productItemsLayout = new ProductItemsLayout()
    submissionButtonBarLayout = new SubmissionButtonBarLayout()
  }

  private void initLinkedViews(CreatePersonView createCustomerView, CreateAffiliationView createAffiliationView, UpdatePersonView updatePersonView) {
    this.createCustomerView = createCustomerView
    this.updatePersonView = updatePersonView
    this.createAffiliationView = createAffiliationView
    this.customerSelectionView = new CustomerSelectionView(viewModel)
    this.projectManagerSelectionView = new ProjectManagerSelectionView(viewModel)
    this.selectItemsView = new SelectItemsView(viewModel, sharedViewModel)
    this.addComponents(this.customerSelectionView,
            this.createCustomerView,
            this.updatePersonView,
            this.createAffiliationView,
            this.projectManagerSelectionView,
            this.selectItemsView)
    hideViews()
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

  private void styleSubLayouts() {
    contentGridLayout.setComponentAlignment(submissionButtonBarLayout, Alignment.BOTTOM_RIGHT)
    contentGridLayout.setComponentAlignment(pricingLayout, Alignment.BOTTOM_RIGHT)
    contentGridLayout.setColumnExpandRatio(0, 0.5)
    contentGridLayout.setColumnExpandRatio(1, 0.2)
    contentGridLayout.setColumnExpandRatio(2, 0.2)
    contentGridLayout.setSizeFull()
    offerDetailsHeaderLayout.setMargin(new MarginInfo(false, false, false, true))
  }

  private void hideViews() {
    this.customerSelectionView.setVisible(false)
    this.createCustomerView.setVisible(false)
    this.updatePersonView.setVisible(false)
    this.createAffiliationView.setVisible(false)
    this.projectManagerSelectionView.setVisible(false)
    this.selectItemsView.setVisible(false)
  }

  //ToDo Wire Information from ViewModel to all subLayouts
  private void bindViewModelToLayouts() {
    bindViewModelToProjectInformationLayout()
    bindViewModelToCustomerLayout()
    bindViewModelToProjectManagerLayout()
    bindViewModelToProductItemsLayout()
    bindViewModelToPricingLayout()
  }

  private void bindViewModelToProjectInformationLayout() {
    projectInformationLayout.projectTitle.addValueChangeListener({ this.viewModel.projectTitle = it.value })
    viewModel.addPropertyChangeListener("projectTitle", {
      String newValue = it.newValue as String
      projectInformationLayout.projectTitle.value = newValue ?: projectInformationLayout.projectTitle.emptyValue
    })
    projectInformationLayout.projectObjective.addValueChangeListener({ this.viewModel.projectObjective = it.value })
    viewModel.addPropertyChangeListener("projectObjective", {
      String newValue = it.newValue as String
      projectInformationLayout.projectObjective.value = newValue ?: projectInformationLayout.projectObjective.emptyValue
    })
    projectInformationLayout.experimentalDesign.addValueChangeListener({ this.viewModel.experimentalDesign = it.value })
    viewModel.addPropertyChangeListener("experimentalDesign", {
      String newValue = it.newValue as String
      projectInformationLayout.experimentalDesign.value = newValue ?: projectInformationLayout.experimentalDesign.emptyValue
    })
  }

  private void bindViewModelToCustomerLayout() {
    viewModel.addPropertyChangeListener({
      if (it.propertyName.equals("customer")) {
        if (it.getNewValue()) {
          String customerFullName =
                  "${viewModel.customer?.firstName ?: ""} " + "${viewModel.customer?.lastName ?: ""}"
          selectCustomerLayout.customerName.setValue(customerFullName)
        }
      }
      if (it.propertyName.equals("customerAffiliation")) {
        if (it.getNewValue()) {
          String affiliationCategory = viewModel.customerAffiliation.getCategory().toString() ?: ""
          selectCustomerLayout.affiliationCategory.setValue(affiliationCategory)
          selectCustomerLayout.affiliationOrganisation.setValue(viewModel.customerAffiliation.getOrganisation() ?: "")
          selectCustomerLayout.affiliationAddressAddition.setValue(viewModel.customerAffiliation.getAddressAddition() ?: "")
        }
      }
    })
  }

  private void bindViewModelToProjectManagerLayout() {
    viewModel.addPropertyChangeListener({
      if (it.propertyName.equals("projectManager")) {
        if (it.getNewValue()) {
          String projectManagerFullName =
                  "${viewModel.projectManager?.firstName ?: ""} " + "${viewModel.projectManager?.lastName ?: ""}"
          selectProjectManagerLayout.projectManagerName.setValue(projectManagerFullName)
          //ToDo  Which affiliation is the correct one here?
          String projectManagerOrganization = viewModel.projectManager.affiliations.first().getOrganisation()
          selectProjectManagerLayout.projectManagerOrganisation.setValue(projectManagerOrganization)
        }
      }
    })
  }

  private void bindViewModelToProductItemsLayout() {
    ListDataProvider<ProductItemViewModel> dataProvider =
            new ListDataProvider(viewModel.getProductItems())
    productItemsLayout.productItemsGrid.setDataProvider(dataProvider)
  }

  private void bindViewModelToPricingLayout() {
    //ToDo Retrieve Offer from Update Event
    viewModel.addPropertyChangeListener("offerUpdate") {
      EventEmitter<Offer> offerEventEmitter = it.getNewValue() as EventEmitter<Offer>
      offerEventEmitter.register (Offer offer) -> {
        pricingLayout.netPrice.value = offer.getNetPrice() ?: ""
        pricingLayout.netPrice.value = offer.getOverheads() ?: ""
        pricingLayout.netPrice.value = offer.getTaxes() ?: ""
      }
      //ToDo Update Pricing if Affiliation Category or ProductItem changes
    }
  }

  //ToDo Add ClickListeners for all Layouts
  private void addLayoutClickListeners() {
    selectCustomerLayout.updateCustomerButton.addClickListener({ event ->
      contentGridLayout.setVisible(false)
      customerSelectionView.setVisible(true)
    })
    selectProjectManagerLayout.updateProjectManagerButton.addClickListener({ event ->
      contentGridLayout.setVisible(false)
      projectManagerSelectionView.setVisible(true)
    })
  }

  //ToDo Add Listeners for all Linked Views
  private void addLinkedViewListeners() {
    addCustomerViewListeners()
    addProjectManagerViewListeners()
  }

  private void addCustomerViewListeners() {
    addCustomerSelectionViewListeners()
    addCreateCustomerViewListeners()
    addUpdatePersonViewListeners()
    addCreateAffiliationViewListeners()
  }

  private void addCustomerSelectionViewListeners() {
    customerSelectionView.previous.addClickListener({ event ->
      customerSelectionView.setVisible(false)
      contentGridLayout.setVisible(true)
    })
    customerSelectionView.next.addClickListener({ event ->
      customerSelectionView.setVisible(false)
      contentGridLayout.setVisible(true)
    })
    this.customerSelectionView.createCustomerButton.addClickListener({
      customerSelectionView.setVisible(false)
      createCustomerView.setVisible(true)
    })
    this.customerSelectionView.updatePerson.addClickListener({
      customerSelectionView.setVisible(false)
      updatePersonView.setVisible(true)
    })
  }

  private void addUpdatePersonViewListeners() {
    this.updatePersonView.abortButton.addClickListener({
      customerSelectionView.reset()
      updatePersonView.setVisible(false)
      customerSelectionView.setVisible(true)
    })
    this.updatePersonView.submitButton.addClickListener({
      customerSelectionView.reset()
      updatePersonView.setVisible(false)
      customerSelectionView.setVisible(true)
    })
  }

  private addCreateCustomerViewListeners() {
    this.createCustomerView.submitButton.addClickListener({
      createCustomerView.setVisible(false)
      customerSelectionView.setVisible(true)
    })
    this.createCustomerView.abortButton.addClickListener({
      createCustomerView.setVisible(false)
      customerSelectionView.setVisible(true)
    })
  }

  private void addCreateAffiliationViewListeners() {
    this.createAffiliationView.addAbortListener({
      createAffiliationView.setVisible(false)
      createCustomerView.setVisible(true)
    })
    this.createAffiliationView.addSubmitListener({
      createAffiliationView.setVisible(false)
      createCustomerView.setVisible(true)
    })
  }

  private void addProjectManagerViewListeners() {
    projectManagerSelectionView.previous.addClickListener({ event ->
      projectManagerSelectionView.setVisible(false)
      contentGridLayout.setVisible(true)
    })
    projectManagerSelectionView.next.addClickListener({ event ->
      projectManagerSelectionView.setVisible(false)
      contentGridLayout.setVisible(true)
    })
  }

  void resetLayoutContent() {
    //ToDo Reset Content of all Layouts and Views
  }
}
