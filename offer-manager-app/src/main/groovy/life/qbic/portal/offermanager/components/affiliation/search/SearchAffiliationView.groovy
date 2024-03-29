package life.qbic.portal.offermanager.components.affiliation.search

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.*
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.RefactorConverter
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.ConfirmationDialog
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.affiliation.update.UpdateAffiliationView

/**
 * <h1>View allowing the user to search for an affiliation</h1>
 *
 * <p>This view provides functionality for searching for affiliations.</p>
 *
 * @since 1.0.0
 */
@Log4j2
class SearchAffiliationView extends FormLayout implements Observer {

    private final SearchAffiliationViewModel viewModel
    private final UpdateAffiliationView updateAffiliationView

    private Grid<Affiliation> affiliationGrid
    private Panel selectedAffiliationDetails
    private VerticalLayout searchAffiliationLayout

    private ConfirmationDialog confirmationDialog

    private ArchiveAffiliationController controller

    private Button updateAffiliationButton

    private Button archiveAffiliationButton

    SearchAffiliationView(SearchAffiliationViewModel viewModel, UpdateAffiliationView updateAffiliationView,
                          ArchiveAffiliationController affiliationController) {
        this.viewModel = viewModel
        this.updateAffiliationView = updateAffiliationView
        this.controller = Objects.requireNonNull(affiliationController, "affiliaiton controller must not be null")

        initLayout()
        generateAffiliationGrid()
        listenToAffiliationSelection()
        listenToUpdateAffiliationView()
        // Register for changes in the affiliation list
        this.viewModel.addObserver(this)
    }

    private void initLayout() {
        Label heading = new Label("Available Affiliations")
        heading.addStyleName(ValoTheme.LABEL_HUGE)

        affiliationGrid = new Grid<Affiliation>()
        affiliationGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        selectedAffiliationDetails = new Panel("Affiliation Details")
        selectedAffiliationDetails.setVisible(viewModel.detailsVisible)

        HorizontalLayout buttons = generateButtonLayout()
        refreshSelectionDetails()

        searchAffiliationLayout = new VerticalLayout(heading, buttons, affiliationGrid, selectedAffiliationDetails)
        searchAffiliationLayout.setMargin(false)

        updateAffiliationView.setMargin(false)
        updateAffiliationView.setVisible(false)

        this.addComponents(searchAffiliationLayout, updateAffiliationView)
        this.setMargin(false)
    }

    private HorizontalLayout generateButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout()
        this.updateAffiliationButton = generateUpdateButton()
        this.archiveAffiliationButton = generateArchivingButton()
        buttonLayout.addComponent(updateAffiliationButton)
        buttonLayout.addComponent(archiveAffiliationButton)

        return buttonLayout
    }

    private Button generateArchivingButton() {
        Button archive = new Button("Archive", VaadinIcons.ARCHIVE)
        archive.setEnabled(false)
        archive.setStyleName(ValoTheme.BUTTON_LARGE)

        archive.addClickListener({
            confirmationDialog = new ConfirmationDialog("This archives the selected affiliation and removes it from all related persons!\n\n Existing offers are not affected.")
            UI.getCurrent().addWindow(confirmationDialog)

            confirmationDialog.confirm.caption = "Archive"
            confirmationDialog.confirm.setIcon(VaadinIcons.ARCHIVE)
            confirmationDialog.confirm.addStyleName(ValoTheme.BUTTON_DANGER)

            confirmationDialog.decline.caption = "Abort"
            confirmationDialog.decline.setIcon(VaadinIcons.CLOSE)

            confirmationDialog.confirm.addClickListener({
                controller.archive(viewModel.getSelectedAffiliation().get())
            })
        })

        affiliationGrid.addSelectionListener({
            if (it.firstSelectedItem.isPresent()) {
                archive.setEnabled(true)
            } else {
                archive.setEnabled(false)
            }
        })

        return archive
    }

    private Button generateUpdateButton() {
        Button update = new Button("Update Affiliation", VaadinIcons.EDIT)
        update.setEnabled(false)
        update.setStyleName(ValoTheme.BUTTON_LARGE)

        update.addClickListener({
            updateAffiliationView.update(RefactorConverter.toAffiliation(viewModel.selectedAffiliation.get()))
            showUpdateAffiliation()
        })
        affiliationGrid.addSelectionListener({
            if (it.firstSelectedItem.isPresent()) {
                update.setEnabled(true)
            } else {
                update.setEnabled(false)
            }
        })

        return update
    }

    private void listenToUpdateAffiliationView() {
        updateAffiliationView.addAbortListener(this::hideUpdateAffiliation)
        updateAffiliationView.addSubmitListener(this::hideUpdateAffiliation)
    }

    private void showUpdateAffiliation() {
        searchAffiliationLayout.setVisible(false)
        updateAffiliationView.setVisible(true)
        selectedAffiliationDetails.setVisible(false)
    }

    private void hideUpdateAffiliation() {
        searchAffiliationLayout.setVisible(true)
        updateAffiliationView.setVisible(false)
    }


    private void generateAffiliationGrid() {
        Grid<Affiliation> affiliationGrid = this.affiliationGrid
        affiliationGrid.addColumn({ affiliation -> affiliation.category.value }).setCaption("Category").setId("Category")
        affiliationGrid.addColumn({ affiliation -> affiliation.organisation }).setCaption("Organization").setId("Organization")
        affiliationGrid.addColumn({ affiliation -> affiliation.addressAddition }).setCaption("Address Addition").setId("AddressAddition")
        affiliationGrid.addColumn({ affiliation -> affiliation.street }).setCaption("Street").setId("Street")
        affiliationGrid.addColumn({ affiliation -> affiliation.postalCode }).setCaption("Postal Code").setId("PostalCode")
        affiliationGrid.addColumn({ affiliation -> affiliation.city }).setCaption("City").setId("City")
        affiliationGrid.addColumn({ affiliation -> affiliation.country }).setCaption("Country").setId("Country")
        affiliationGrid.setWidthFull()
        affiliationGrid.setHeightByRows(5)

        DataProvider affiliationDataProvider = new ListDataProvider<Affiliation>(viewModel.getAffiliations())
        affiliationGrid.setDataProvider(affiliationDataProvider)

        addColumnFilters(affiliationGrid, affiliationDataProvider)
    }

    private static addColumnFilters(Grid<Affiliation> grid, ListDataProvider<Affiliation> dataProvider) {
        HeaderRow filterRow = grid.appendHeaderRow()
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("Category"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("Organization"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("AddressAddition"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("Street"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("PostalCode"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("City"), filterRow)
        GridUtils.setupColumnFilter(dataProvider, grid.getColumn("Country"), filterRow)
    }

    /**
     * Sets a listener to the affiliation grid.*/
    private void listenToAffiliationSelection() {
        this.affiliationGrid.addSelectionListener({
            viewModel.selectedAffiliation = it.firstSelectedItem
        })

        this.viewModel.addPropertyChangeListener("selectedAffiliation", {
            try {
                viewModel.detailsVisible = it.newValue
                refreshSelectionDetails()
                selectedAffiliationDetails.visible = viewModel.detailsVisible
            } catch (Exception e) {
                log.error("Unexpected exception after affiliation selection change. $e.message")
                log.debug("Unexpected exception after affiliation selection change. $e.message", e)
            }
        })
    }

    private void refreshSelectionDetails() {
        FormLayout detailsContent = new FormLayout()
        detailsContent.setMargin(true)
        if (viewModel.selectedAffiliation.isPresent()) {
            Affiliation selectedAffiliation = viewModel.selectedAffiliation.get()

            if (selectedAffiliation.category) {
                Label category = new Label("$selectedAffiliation.category")
                //there might be a better label
                category.setIcon(VaadinIcons.CIRCLE_THIN)
                category.setCaption("Affiliation Category")
                detailsContent.addComponent(category)
            }

            if (selectedAffiliation.organisation) {
                Label organisation = new Label("$selectedAffiliation.organisation")
                organisation.setIcon(VaadinIcons.BUILDING_O)
                organisation.setCaption("Organization")
                detailsContent.addComponent(organisation)
            }

            if (selectedAffiliation.addressAddition) {
                Label addressAddition = new Label("$selectedAffiliation.addressAddition")
                addressAddition.setIcon(VaadinIcons.BUILDING)
                addressAddition.setCaption("Address Addition")
                detailsContent.addComponent(addressAddition)
            }

            TextArea address = new TextArea()
            address.setValue(generateAddressString(selectedAffiliation))
            address.setEnabled(false)
            address.setWidthFull()
            address.setIcon(VaadinIcons.ENVELOPE)
            address.setCaption("Address")
            detailsContent.addComponent(address)
        }
        selectedAffiliationDetails.setContent(detailsContent)
    }

    private static String generateAddressString(Affiliation affiliation) {
        StringBuilder stringBuilder = new StringBuilder()
        if (affiliation.organisation) {
            stringBuilder.append("$affiliation.organisation")
            stringBuilder.append("\n")
        }
        if (affiliation.addressAddition) {
            stringBuilder.append("$affiliation.addressAddition")
            stringBuilder.append("\n")
        }
        if (affiliation.street) {
            stringBuilder.append("$affiliation.street")
            stringBuilder.append("\n")
        }
        if (affiliation.postalCode) {
            stringBuilder.append("$affiliation.postalCode")
            if (affiliation.city) {
                stringBuilder.append("\t")
            } else {
                stringBuilder.append("\n")
            }
        }
        if (affiliation.city) {
            stringBuilder.append("$affiliation.city").append("\n")
        }
        if (affiliation.country) {
            stringBuilder.append("$affiliation.country")
        }
        return stringBuilder.toString().trim()
    }

    @Override
    void update(Observable o, Object arg) {
        // We want to refresh the grid, so that cached items are removed and the content
        // reflects the current content of available (active) affiliations
        this.affiliationGrid.getDataProvider().refreshAll()
        disableActionButtons()
    }

    private void disableActionButtons() {
        this.archiveAffiliationButton.setEnabled(false)
        this.updateAffiliationButton.setEnabled(false)
    }


}
