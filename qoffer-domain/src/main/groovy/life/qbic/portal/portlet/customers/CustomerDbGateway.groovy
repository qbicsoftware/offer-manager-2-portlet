package life.qbic.portal.portlet.customers

import life.qbic.datamodel.persons.Person


/**
 * A gateway to access information from a customer database
 *
 * This class specifies how the application can access external resources.
 * It is meant to be implemented outside the domain layer.
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 *
 */
interface CustomerDbGateway {

        /**
         *
         * @param criteria a map with search criteria
         * @return a person with affiliation and contact information
         * @see life.qbic.datamodel.persons.Person* @since 1.0.0
        */
        Person searchCustomer(Map criteria)

        /**
         *
         * @param customer a person to be added to known customers
         * @see life.qbic.datamodel.persons.Person* @since 1.0.0
        */
        void saveCustomer(Person customer)

}