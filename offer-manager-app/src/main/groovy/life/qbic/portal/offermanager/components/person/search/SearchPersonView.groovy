package life.qbic.portal.offermanager.components.person.search

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.components.GridUtils

import life.qbic.portal.offermanager.components.person.create.CreatePersonView

/**
 * Constructs the UI for the SearchPerson use case
 *
 * This class provides the view elements so that a user can search for a person through the UI
 *
 * @since: 1.0.0
 *
 */
class SearchPersonView extends FormLayout{

    private final SearchPersonViewModel viewModel
    private final CreatePersonView updatePersonView

    Grid<Person> personGrid
    Panel selectedPersonInformation
    Button updatePerson
    VerticalLayout detailsLayout
    VerticalLayout searchPersonLayout

    SearchPersonView(SearchPersonViewModel searchPersonViewModel, CreatePersonView updatePersonView) {
        this.viewModel = searchPersonViewModel
        this.updatePersonView = updatePersonView


        initLayout()
        generatePersonGrid()
        addListeners()
    }

    private void initLayout(){
        Label gridLabel = new Label("Available Person Entries")
        gridLabel.addStyleName(ValoTheme.LABEL_HUGE)


        updatePerson = new Button("Update Person")
        updatePerson.setEnabled(false)

        detailsLayout = new VerticalLayout()
        detailsLayout.addComponent(updatePerson)
        detailsLayout.setComponentAlignment(updatePerson, Alignment.MIDDLE_RIGHT)

        personGrid = new Grid<>()
        selectedPersonInformation = new Panel()

        Label detailsLabel = new Label("Person Details: ")
        detailsLayout.addComponent(detailsLabel)
        detailsLabel.addStyleName(ValoTheme.LABEL_LARGE)


        detailsLayout.addComponent(selectedPersonInformation)
        detailsLayout.setVisible(false)
        detailsLayout.setMargin(false)

        searchPersonLayout = new VerticalLayout(gridLabel,personGrid,detailsLayout)
        searchPersonLayout.setMargin(false)

        this.addComponents(searchPersonLayout,updatePersonView)
        this.setMargin(false)
        updatePersonView.setVisible(false)
    }

    private void addListeners(){

        personGrid.addSelectionListener({
            if (it.firstSelectedItem.isPresent()) {
                fillPanel(it.firstSelectedItem.get())
                detailsLayout.setVisible(true)
                updatePerson.setEnabled(true)
                viewModel.selectedPerson = it.firstSelectedItem
            } else {
                detailsLayout.setVisible(false)
            }
        })

        updatePerson.addClickListener({
            viewModel.personEvent.emit(viewModel.selectedPerson)
            detailsLayout.setVisible(false)
            searchPersonLayout.setVisible(false)
            updatePersonView.setVisible(true)
        })

        updatePersonView.abortButton.addClickListener({
            searchPersonLayout.setVisible(true)
            updatePersonView.setVisible(false)
        })

        updatePersonView.submitButton.addClickListener({
            searchPersonLayout.setVisible(true)
            updatePersonView.setVisible(false)
        })

    }

    /**
     * Fills the panel with the detailed information of the currently selected person
     * @param person The person which
     */
    private void fillPanel(Person person){
        VerticalLayout content = new VerticalLayout()

        content.addComponent(new Label("<strong>${person.title == AcademicTitle.NONE ? "" : person.title} ${person.firstName} ${person.lastName}</strong>", ContentMode.HTML))
        content.addComponent(new Label("${person.emailAddress}", ContentMode.HTML))


        person.affiliations.each { affiliation ->
            content.addComponent(new Label("<strong>${affiliation.category.value}</strong>", ContentMode.HTML))
            content.addComponent(new Label("${affiliation.organisation}"))
            if (affiliation.addressAddition) {
                content.addComponent(new Label("${affiliation.addressAddition}"))
            }
            content.addComponent(new Label("${affiliation.street}"))
            content.addComponent(new Label("${affiliation.postalCode} ${affiliation.city} - ${affiliation.country}"))
        }
        content.setMargin(true)
        content.setSpacing(false)

        selectedPersonInformation.setContent(content)
        selectedPersonInformation.setWidthUndefined()
    }

    /**
     * This method adds the retrieved person Information to the person grid
     */
    private ListDataProvider setupPersonDataProvider() {
        def personListDataProvider = new ListDataProvider<>(viewModel.getAvailablePersons())
        this.personGrid.setDataProvider(personListDataProvider)

        return personListDataProvider
    }

    /**
     * Method which generates the grid and populates the columns with the set person information from the setupDataProvider Method
     *
     * This Method is responsible for setting up the grid and setting the person information to the individual grid columns.
     */
    private def generatePersonGrid() {
        try {
            this.personGrid.addColumn({ person -> person.firstName })
                    .setCaption("First Name").setId("FirstName")
            this.personGrid.addColumn({ person -> person.lastName })
                    .setCaption("Last Name").setId("LastName")
            this.personGrid.addColumn({ person -> person.emailAddress })
                    .setCaption("Email Address").setId("EmailAddress")
            this.personGrid.addColumn({ person ->
                person.title == AcademicTitle.NONE ? "" : person.title})
                    .setCaption("Title").setId("Title")

            //specify size of grid and layout
            personGrid.setWidthFull()
            personGrid.setHeightMode(HeightMode.ROW)
            personGrid.setHeightByRows(5)

        } catch (Exception e) {
            new Exception("Unexpected exception in building the person grid", e)
        }
        /*
        Let's not forget to setup the grid's data provider
         */
        def personDataProvider = setupPersonDataProvider()
        /*
        Lastly, we add some content filters for the columns
         */
        addFilters(personDataProvider)
    }

    private void addFilters(ListDataProvider personListDataProvider) {
        HeaderRow personFilterRow = personGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(personListDataProvider,
                personGrid.getColumn("FirstName"),
                personFilterRow)
        GridUtils.setupColumnFilter(personListDataProvider,
                personGrid.getColumn("LastName"),
                personFilterRow)
        GridUtils.setupColumnFilter(personListDataProvider,
                personGrid.getColumn("EmailAddress"),
                personFilterRow)
    }

}
