package life.qbic.portal.offermanager.components.affiliation.search

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.TextArea
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.GridUtils

/**
 * <h1>View allowing the user to search for an affiliation</h1>
 *
 * <p>This view provides functionality for searching for affiliations.</p>
 *
 * @since 1.0.0
 */
@Log4j2
class SearchAffiliationView extends FormLayout{

    private final SearchAffiliationViewModel viewModel

    private Grid<Affiliation> affiliationGrid
    private Panel selectedAffiliationDetails

    SearchAffiliationView(SearchAffiliationViewModel viewModel) {
        this.viewModel = viewModel
        initLayout()
        generateAffiliationGrid()
        listenToAffiliationSelection()
    }

    private void initLayout() {
        Label heading = new Label("Available Affiliations")
        heading.addStyleName(ValoTheme.LABEL_HUGE)

        affiliationGrid = new Grid<Affiliation>()
        affiliationGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        selectedAffiliationDetails = new Panel("Affiliation Details")
        selectedAffiliationDetails.setVisible(viewModel.detailsVisible)
        refreshSelectionDetails()
        this.addComponents(heading, affiliationGrid, selectedAffiliationDetails)

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
     * Sets a listener to the affiliation grid.
     */
    private void listenToAffiliationSelection() {
        this.affiliationGrid.addSelectionListener({
            viewModel.selectedAffiliation = it.firstSelectedItem.orElse(null)
        })

        this.viewModel.addPropertyChangeListener("selectedAffiliation", {
            try {
                viewModel.detailsVisible = it.newValue ? true : false
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
        Affiliation selectedAffiliation = viewModel.selectedAffiliation
        if (selectedAffiliation) {
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


}
