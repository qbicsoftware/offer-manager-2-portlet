package life.qbic.portal.qoffer2.database

import life.qbic.portal.qoffer2.customers.CustomerDatabaseQueries
import spock.lang.Specification

import java.sql.Connection

/**
 * Test the database connection
 *
 * This class should be used whenever the database connection should be tested
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class DatabaseSessionSpecification extends Specification{

    def "test"(){
        given:
        InputStream stream = DatabaseSessionSpecification.class.classLoader.getResourceAsStream("developer.properties")
        Properties properties = new Properties()
        properties.load(stream)

        Connection con = DatabaseSession.getDatabaseInstanceAlternative(properties)
        CustomerDatabaseQueries queries = new CustomerDatabaseQueries(con)

        when:
        List res = queries.findPersonByName("Zender")
        DatabaseSession.logout(con)

        then:
        res.get(0).firstName == "Lars"
    }
}
