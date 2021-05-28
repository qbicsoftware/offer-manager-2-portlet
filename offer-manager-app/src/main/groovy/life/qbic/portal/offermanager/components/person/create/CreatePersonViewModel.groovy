package life.qbic.portal.offermanager.components.person.create

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.general.Person
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

    final ResourcesService<Customer> customerService
    final ResourcesService<ProjectManager> managerResourceService
    final ResourcesService<Affiliation> affiliationService
    final ResourcesService<Person> personResourceService

    CreatePersonViewModel(ResourcesService<Customer> customerService,
                          ResourcesService<ProjectManager> managerResourceService,
                          ResourcesService<Affiliation> affiliationService,
                          ResourcesService<Person> personResourceService) {
        this.affiliationService = affiliationService
        this.customerService = customerService
        this.managerResourceService = managerResourceService
        this.personResourceService = personResourceService
        availableOrganisations = new ObservableList()
        refreshAvailableOrganizations()

        this.affiliationService.subscribe({
            List foundOrganisations = availableOrganisations.findAll(){organisation -> (organisation as Organisation).name == it.organisation}
            if(foundOrganisations.empty){
                //create a new organisation
                availableOrganisations << new Organisation(it.organisation,[it])
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
     * Maps a list of affiliations to organisations
     * @param affiliations A list of affiliations where some have the same organisation
     * @return a list of organisations containing the associated affiliations
     */
    protected static List<Organisation> toOrganisation(List<Affiliation> affiliations){

        List<String> organisationNames = affiliations.collect{it.organisation}.toUnique()
        List<Organisation> organisations = []

        organisationNames.each {organisationName ->
            List<Affiliation> organisationAffiliations = []
            affiliations.each {affiliation ->
                if(affiliation.organisation == organisationName) organisationAffiliations << affiliation
            }

            organisations << new Organisation(organisationName,organisationAffiliations)
        }

        return organisations
    }

    @Override
    void reset() {
        setAcademicTitle(null)
        setFirstName(null)
        setLastName(null)
        setEmail(null)
        setAffiliation(null)
        setAcademicTitleValid(null)
        setFirstNameValid(null)
        setLastNameValid(null)
        setEmailValid(null)
        setAffiliationValid(null)

        refreshAvailableOrganizations()
    }
}
