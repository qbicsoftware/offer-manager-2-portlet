package life.qbic.portal.offermanager.components.affiliation.search

import com.vaadin.icons.VaadinIcons
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation

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
        setupAffiliationGrid()
        listenToAffiliationSelection()
        //todo remove
        this.viewModel.selectedAffiliation = this.viewModel.affiliations.first()
    }

    private void initLayout() {
        Label heading = new Label("Available Affiliations")
        heading.addStyleName(ValoTheme.LABEL_HUGE)

        affiliationGrid = new Grid<Affiliation>()
        this.setMargin(false)
        this.addComponents(heading, affiliationGrid)

    }

    private void setupAffiliationGrid() {

    }

    /**
     * Sets a listener to the affiliation grid.
     * @see #onAffiliationSelection
     */
    private void listenToAffiliationSelection() {
        this.viewModel.addPropertyChangeListener("selectedAffiliation", {
            try {
                onAffiliationSelection()
            } catch (Exception e) {
                log.error("Unexpected exception after affiliation selection change. $e.message")
                log.debug("Unexpected exception after affiliation selection change. $e.message", e)
            }
        })
    }

    /**
     * This method performs actions on affiliation selection.
     *
     */
    private void onAffiliationSelection() {
        refreshSelectionDetails()
    }

    private void refreshSelectionDetails() {
        FormLayout detailsContent = new FormLayout()
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
                detailsContent.addComponent(organisation)
            }

            if (selectedAffiliation.addressAddition) {
                Label addressAddition = new Label("$selectedAffiliation.addressAddition")
                addressAddition.setIcon(VaadinIcons.BUILDING)
                addressAddition.setCaption("Address Addition")
                detailsContent.addComponent(addressAddition)
            }

            Label address = new Label(generateAddressString(selectedAffiliation))
            address.setIcon(VaadinIcons.ENVELOPE)
            address.setCaption("Address")
            detailsContent.addComponent(address)
        }
        selectedAffiliationDetails.setContent(detailsContent)
    }

    private static String generateAddressString(Affiliation affiliation) {
        StringBuilder stringBuilder = new StringBuilder()
        if (affiliation.organisation) {
            stringBuilder.append("$affiliation.organisation").append("\n")
        }
        if (affiliation.addressAddition) {
            stringBuilder.append("$affiliation.addressAddition").append("\n")
        }
        if (affiliation.street) {
            stringBuilder.append("$affiliation.street").append("\n")
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
        return stringBuilder.toString()
    }


}
