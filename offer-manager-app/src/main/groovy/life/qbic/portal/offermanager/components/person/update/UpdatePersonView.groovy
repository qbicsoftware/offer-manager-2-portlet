package life.qbic.portal.offermanager.components.person.update

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.components.grid.HeaderRow
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.affiliation.create.CreateAffiliationView
import life.qbic.portal.offermanager.components.person.create.CreatePersonController
import life.qbic.portal.offermanager.components.person.create.CreatePersonView

/**
 * <h1>This view is an extension of the {@link CreatePersonView} and adjusts the view components to reflect the update person use case</h1>
 * <br>
 * <p>Since both views should look the same changes of the {@link CreatePersonView} should also be reflected in the {@link UpdatePersonView}</p>
 *
 * @since 1.0.0
 */
@Log4j2
class UpdatePersonView extends CreatePersonView {
    private final UpdatePersonViewModel updatePersonViewModel
    private final AppViewModel sharedViewModel

    private Grid<Affiliation> affiliations
    private Button addAffiliationButton

    UpdatePersonView(CreatePersonController controller, AppViewModel sharedViewModel, UpdatePersonViewModel updatePersonViewModel, CreateAffiliationView createAffiliationView) {
        super(controller, sharedViewModel, updatePersonViewModel, createAffiliationView)
        this.updatePersonViewModel = updatePersonViewModel
        this.sharedViewModel = sharedViewModel
        adjustViewElements()
        registerListener()
    }

    private void adjustViewElements() {
        viewCaption.setValue("Update Person Entry")

        submitButton.caption = "Update Person"
        abortButton.caption = "Abort Person Update"

        // remove required indicator since affiliationValid is no longer required to update
        // it is replaced by affiliationsValid
        organisationComboBox.setRequiredIndicatorVisible(false)
        addressAdditionComboBox.setRequiredIndicatorVisible(false)

        //be careful when adding new components to the view
        //it is based on the CreatePersonView, you might disrupt the view when adding new components to the wrong position

        //add a grid
        affiliations = new Grid<>()
        generateAffiliationGrid()
        Label currentAffiliationLabel = new Label("Current Affiliations")
        affiliations.setSelectionMode(Grid.SelectionMode.NONE)
        defaultContent.addComponent(currentAffiliationLabel, 3)
        defaultContent.addComponent(affiliations, 4)
        //add a heading for adding a new affiliation
        Label newAffiliation = new Label("Add a new affiliation")
        defaultContent.addComponent(newAffiliation, 5)

        //add the add button
        addAffiliationButton = new Button("Add Affiliation")
        addAffiliationButton.setIcon(VaadinIcons.PLUS)
        addAffiliationButton.setEnabled(false)
        buttonLayout.addComponent(addAffiliationButton, 0)
    }

    private void generateAffiliationGrid() {
        try {
            this.affiliations.addColumn({ affiliation -> affiliation.category.value })
                    .setCaption("Category").setId("Category")
            this.affiliations.addColumn({ affiliation -> affiliation.organisation })
                    .setCaption("Organisation").setId("Organisation")
            this.affiliations.addColumn({ affiliation -> affiliation.addressAddition })
                    .setCaption("Address Addition").setId("AddressAddition")
            this.affiliations.addColumn({ affiliation -> affiliation.street })
                    .setCaption("Street").setId("Street")
            this.affiliations.addColumn({ affiliation -> affiliation.city })
                    .setCaption("City").setId("City")
            this.affiliations.addColumn({ affiliation -> affiliation.postalCode })
                    .setCaption("Postal Code").setId("PostalCode")
            this.affiliations.addColumn({ affiliation -> affiliation.country })
                    .setCaption("Country").setId("Country")

            //specify size of grid and layout
            affiliations.setWidthFull()
            affiliations.setHeightMode(HeightMode.ROW)
            affiliations.setHeightByRows(5)

        } catch (Exception e) {
            new Exception("Unexpected exception in building the affiliation grid", e)
        }

        /*
        Let's not forget to setup the grid's data provider
        */
        def affiliationDataProvider = setupAffiliationDataProvider()
        /*
        Lastly, we add some content filters for the columns
         */
        addFilters(affiliationDataProvider)
    }

    private ListDataProvider setupAffiliationDataProvider() {
        def affiliationListDataProvider = new ListDataProvider<>(updatePersonViewModel.affiliationList)
        this.affiliations.setDataProvider(affiliationListDataProvider)

        return affiliationListDataProvider
    }

    private void addFilters(ListDataProvider affiliationListDataProvider) {
        HeaderRow personFilterRow = affiliations.appendHeaderRow()
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("Organisation"),
                personFilterRow)
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("AddressAddition"),
                personFilterRow)
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("Street"),
                personFilterRow)
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("City"),
                personFilterRow)
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("PostalCode"),
                personFilterRow)
        GridUtils.setupColumnFilter(affiliationListDataProvider,
                affiliations.getColumn("Country"),
                personFilterRow)
    }

    private void registerListener() {
        submitButtonClickListenerRegistration.remove()
        submitButton.addClickListener({
            try {
                // we assume that the view model and the view always contain the same information
                String title = updatePersonViewModel.academicTitle
                String firstName = updatePersonViewModel.firstName
                String lastName = updatePersonViewModel.lastName
                String email = updatePersonViewModel.email
                List<Affiliation> affiliations = updatePersonViewModel.affiliationList

                if (updatePersonViewModel.outdatedPerson) {
                    controller.updatePerson(updatePersonViewModel.outdatedPerson, firstName, lastName, title, email, affiliations)
                }

            } catch (IllegalArgumentException illegalArgumentException) {
                log.error("Illegal arguments for person update. ${illegalArgumentException.getMessage()}")
                log.debug("Illegal arguments for person update. ${illegalArgumentException.getMessage()}", illegalArgumentException)
                sharedViewModel.failureNotifications.add("Could not update the person. Please verify that your input is correct and try again.")
            } catch (Exception e) {
                log.error("Unexpected error after person update form submission.", e)
                sharedViewModel.failureNotifications.add("An unexpected error occurred. We apologize for any inconveniences. Please inform us via email to support@qbic.zendesk.com.")
            }
        })

        updatePersonViewModel.addPropertyChangeListener({
            if (it.propertyName == "affiliation" || it.propertyName == "affiliationValid") {
                boolean buttonEnabled = updatePersonViewModel.affiliation && updatePersonViewModel.affiliationValid
                addAffiliationButton.setEnabled(buttonEnabled)
            }
        })

        addAffiliationButton.addClickListener({
            if (!updatePersonViewModel.affiliationList.contains(updatePersonViewModel.affiliation)) {
                updatePersonViewModel.affiliationList.add(updatePersonViewModel.affiliation)
                affiliations.dataProvider.refreshAll()
            } else {
                sharedViewModel.failureNotifications.add("Cannot add the selected affiliation. It was already associated with the person.")
                addAffiliationButton.setEnabled(false)
            }
            resetAffiliation()
        })

        updatePersonViewModel.affiliationList.addPropertyChangeListener({
            updatePersonViewModel.affiliationsValid = ! updatePersonViewModel.affiliationList.isEmpty()
            if(updatePersonViewModel.outdatedPerson) {
                submitButton.enabled = allValuesValid()
            } else {
                log.warn("Tried to check for person affiliation changes. There is no person to update.")
            }
        })
    }

    private void resetAffiliation() {
        organisationComboBox.clear()
        addressAdditionComboBox.clear()
        createPersonViewModel.affiliationValid = null
    }

    /**
     * This is used to indicate whether all fields of this view are filled correctly.
     * It relies on the separate fields for validation. It overrides the Method allValuesValid() of the CreatePersonView
     * and makes the validity independent of an added affiliation. Furthermore, the person entry needs to be different to
     * the person that needs to be updated.
     *
     * @return boolean which indicates if a person update can be triggered
     */
    @Override
    protected boolean allValuesValid() {
        return createPersonViewModel.academicTitleValid \
               && createPersonViewModel.firstNameValid \
               && createPersonViewModel.lastNameValid \
               && createPersonViewModel.emailValid \
               && updatePersonViewModel.affiliationsValid \
               && updatePersonViewModel.hasPersonChanged()
    }
}
