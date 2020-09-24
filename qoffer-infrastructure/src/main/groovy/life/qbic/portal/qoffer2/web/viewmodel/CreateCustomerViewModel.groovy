package life.qbic.portal.qoffer2.web.viewmodel

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.Affiliation

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: <versiontag>
 */
class CreateCustomerViewModel {
    @Bindable String academicTitle
    @Bindable String firstName
    @Bindable String lastName
    @Bindable String email
    @Bindable Affiliation affiliation

}
