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

    ObservableList availableAffiliations
    Map<String,List<Affiliation>> affiliationToOrganisations

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
        availableAffiliations = new ObservableList(new ArrayList<Affiliation>(affiliationService.iterator().collect()))
        initMap()

        this.affiliationService.subscribe({
            if (! (it in this.availableAffiliations) ){
                this.availableAffiliations.add(it)
                updateMap(it)
            }
        })

    }

    protected void initMap(){
        affiliationToOrganisations = new HashMap<>()

        availableAffiliations.each {
            Affiliation newAffiliation = it as Affiliation
            updateMap(newAffiliation)
        }
    }

    void updateMap(Affiliation newAffiliation){
        String organisation = newAffiliation.organisation

        if(! (organisation in affiliationToOrganisations)){
            List addressAdditions = [newAffiliation]
            affiliationToOrganisations.put(organisation,addressAdditions)
        }else{
            affiliationToOrganisations.get(organisation).add(newAffiliation)
        }
    }

}
