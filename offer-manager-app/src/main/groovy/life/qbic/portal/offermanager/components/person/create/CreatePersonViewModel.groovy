package life.qbic.portal.offermanager.components.person.create

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.portal.offermanager.dataresources.persons.AffiliationResourcesService
import life.qbic.portal.offermanager.dataresources.persons.CustomerResourceService
import life.qbic.portal.offermanager.dataresources.persons.PersonResourcesService

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

    List<Affiliation> availableAffiliations

    final CustomerResourceService customerService
    final AffiliationResourcesService affiliationService

    CreatePersonViewModel(CustomerResourceService customerService,
                          AffiliationResourcesService affiliationService) {
        this.affiliationService = affiliationService
        this.customerService = customerService
        availableAffiliations = new ArrayList<>(affiliationService.iterator().collect())

    }

    private void setupServices() {
        affiliationService.eventEmitter.register({

        })
    }
}
