package life.qbic.portal.offermanager.web.viewmodel

import groovy.beans.Bindable

/**
 * A ViewModel holding data that is presented in a
 * life.qbic.portal.qoffer2.web.views.CreateAffiliationView
 *
 * This class holds all specific fields that are mutable in the view
 * Whenever values change it should be reflected in the corresponding view. This class can be used
 * for UI unit testing purposes.
 *
 * This class can contain JavaBean objects to enable views to listen to changes in the values.
 *
 * @since: 1.0.0
 */
class CreateAffiliationViewModel {
    List<String> affiliationCategories = new ArrayList<>()

    @Bindable String organisation
    @Bindable String addressAddition
    @Bindable String street
    @Bindable String postalCode
    @Bindable String city
    @Bindable String country
    @Bindable String affiliationCategory

    @Bindable Boolean organisationValid
    @Bindable Boolean addressAdditionValid
    @Bindable Boolean streetValid
    @Bindable Boolean postalCodeValid
    @Bindable Boolean cityValid
    @Bindable Boolean countryValid
    @Bindable Boolean affiliationCategoryValid

    final life.qbic.portal.offermanager.services.ResourcesService affiliationService

    CreateAffiliationViewModel(life.qbic.portal.offermanager.services.ResourcesService affiliationService) {
        this.affiliationService = affiliationService
    }
}
