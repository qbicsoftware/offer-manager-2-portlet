package life.qbic.portal.portlet.customers

import life.qbic.datamodel.persons.Affiliation

/**
 * A QBiC customer in the context of finance and accounting.
 *
 * This object represents a business customer at QBiC. A customer usually contains one or more affiliations.
 * A customer is created when a research project request is sent to QBiC in order to request an offer for it.
 * 
 * Customer objects are immutable and changes can only be made, with the according database interface methods (link here)
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class Customer {
    String firstName
    String lastName
    String email
    Affiliation affiliation

    Customer(){
        this.firstName = "firstName"
        this.lastName = "lastName"
        this.email = "email"
        this.affiliation = "affiliation"
    }

    Customer(String firstName, String lastName, String email){
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.affiliation = affiliation
    }
}
