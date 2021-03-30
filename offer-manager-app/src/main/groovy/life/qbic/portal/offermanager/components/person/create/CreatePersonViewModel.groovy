package life.qbic.portal.offermanager.components.person.create

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.PersonResourceService
import life.qbic.portal.offermanager.dataresources.persons.ProjectManagerResourceService

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
class CreatePersonViewModel {
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

    ObservableList availableOrganisations
//     Map<String,List<Affiliation>> affiliationToOrganisations

    final CustomerResourceService customerService
    final ProjectManagerResourceService managerResourceService
    final AffiliationResourcesService affiliationService
    final PersonResourceService personResourceService

    CreatePersonViewModel(CustomerResourceService customerService,
                          ProjectManagerResourceService managerResourceService,
                          AffiliationResourcesService affiliationService,
                          PersonResourceService personResourceService) {
        this.affiliationService = affiliationService
        this.customerService = customerService
        this.managerResourceService = managerResourceService
        this.personResourceService = personResourceService

        List<Affiliation> affiliations = affiliationService.iterator().collect()
        availableOrganisations = new ObservableList(new ArrayList<Organisation>(toOrganisation(affiliations)))

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

    protected List<Organisation> toOrganisation(List<Affiliation> affiliations){

        List<String> organisationNames = affiliations.collect{it.organisation}.toUnique() //todo needs to be unique
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

    /**
     *
     */
    protected static class OrganisationParser{


    }
}
