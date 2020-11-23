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

    int getPersonId(Connection connection, Person person)
    int getAffiliationId(Connection connection, Affiliation affiliation)
}