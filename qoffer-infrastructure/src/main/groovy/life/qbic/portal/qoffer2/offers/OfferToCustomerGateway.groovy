package life.qbic.portal.qoffer2.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.general.Person

import java.sql.Connection

/**
 * An interface to allow to retrieve information from the Customer Database
 *
 * Allows to interact with the customer database through predefined methods
 *
 * @since: 0.1.0
 *
 */
interface OfferToCustomerGateway {

    /**
     * This method returns the ID of the database entry of a given person
     *
     * @param connection The connection through which the query is executed
     * @param person The person for which the corresponding database entry needs to be found
     * @return the id of the person in the database
     */
    int getPersonId(Connection connection, Person person)

    /**
     * This method returns the ID of the database entry of a given affiliation
     *
     * @param connection The connection through which the query is executed
     * @param affiliation The affiliation for which the database entry needs to be found
     * @return the id of the affiliation in the database
     */
    int getAffiliationId(Connection connection, Affiliation affiliation)
}