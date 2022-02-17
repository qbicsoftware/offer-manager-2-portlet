package life.qbic.portal.offermanager.components.person.create

import groovy.beans.Bindable
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.portal.offermanager.components.Resettable
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * A ViewModel holding data that is presented in a
 * life.qbic.portal.qoffer2.web.viewmodel.CreatePersonViewModel
 *
 * This class holds all specific fields that are mutable in the view
 * Whenever values change it should be reflected in the corresponding view. This class can be used
 * for UI unit testing purposes.
 *
 * This class can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since: 1.0.0
 */
class CreatePersonViewModel implements Resettable{
    List<String> academicTitles = new ArrayList<>()
    Person outdatedPerson

    @Bindable String academicTitle
    @Bindable String firstName
    @Bindable String lastName
    @Bindable String email
    @Bindable Affiliation affiliation

    @Bindable Boolean academicTitleValid
    @Bindable Boolean firstNameValid
    @Bindable Boolean lastNameValid
    @Bindable Boolean emailValid
    @Bindable Boolean affiliationValid

    @Bindable Boolean affiliationViewVisible

    ObservableList availableOrganisations

    final ResourcesService<Affiliation> affiliationService
    final ResourcesService<Person> personResourceService

    CreatePersonViewModel(ResourcesService<Affiliation> affiliationService,
                          ResourcesService<Person> personResourceService) {
        this.affiliationService = affiliationService
        this.personResourceService = personResourceService
        availableOrganisations = new ObservableList()
        refreshAvailableOrganizations()

        this.affiliationService.subscribe({
            List foundOrganisations = availableOrganisations.findAll() { organisation -> (organisation as Organisation).name == it.getOrganization() }
            if (foundOrganisations.empty) {
                //create a new organisation
                availableOrganisations << new Organisation(it.getOrganization(), [it])
            }else{
                //add the new affiliation
                (foundOrganisations.get(0) as Organisation).affiliations << it
            }
        })
    }

    private void refreshAvailableOrganizations() {
        availableOrganisations.clear()
        List<Affiliation> affiliations = affiliationService.iterator().collect()
        availableOrganisations.addAll(new ArrayList<Organisation>(toOrganisation(affiliations)))
    }

    /**
     * Triggers the reload of the person entries through the service and therefore re-queries the database
     */
    void refreshPersonEntries(){
        personResourceService.reloadResources()
    }

    /**
     * Maps a list of affiliations to organisations
     * @param affiliations A list of affiliations where some have the same organisation
     * @return a list of organisations containing the associated affiliations
     */
    protected static List<Organisation> toOrganisation(List<Affiliation> affiliations){

        List<String> organisationNames = affiliations.collect{it.getOrganization()}.toUnique()
        List<Organisation> organisations = []

        organisationNames.each {organisationName ->
            List<Affiliation> organisationAffiliations = []
            affiliations.each {affiliation ->
                if(affiliation.getOrganization() == organisationName) organisationAffiliations << affiliation
            }

            organisations << new Organisation(organisationName,organisationAffiliations)
        }

        return organisations
    }

    @Override
    void reset() {

        this.setAcademicTitle(AcademicTitle.NONE.toString())
        this.setAcademicTitleValid(true)

        this.setFirstName(null)
        this.setFirstNameValid(null)

        this.setLastName(null)
        this.setLastNameValid(null)

        this.setEmail(null)
        this.setEmailValid(null)

        this.setAffiliation(null)
        this.setAffiliationValid(null)

        refreshAvailableOrganizations()
    }
}
