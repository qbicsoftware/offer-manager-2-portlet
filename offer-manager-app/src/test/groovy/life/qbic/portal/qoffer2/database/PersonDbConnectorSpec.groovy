package life.qbic.portal.qoffer2.database

import groovy.sql.GroovyRowResult
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.offermanager.dataresources.persons.PersonDbConnector
import life.qbic.portal.offermanager.dataresources.database.ConnectionProvider
import org.apache.groovy.sql.extensions.SqlExtensions
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Adds tests for {@link PersonDbConnector}
 *
 * @since: 0.1.0
 *
 */
class PersonDbConnectorSpec extends Specification{

    def "CustomerDbConnector shall return the id for a given person"(){
        given:
        String query = "SELECT id FROM person WHERE first_name = ? AND last_name = ? AND email = ?"

        and: "a connection returning the id of the person if it was found"
        // we need to stub the static SqlExtensions.toRowResult method because we do not provide an implemented RowResult
        GroovyMock(SqlExtensions, global: true)
        SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(["id":id])
        // our statement should only be able to fill the template with the correct values
        PreparedStatement preparedStatement = Mock (PreparedStatement, {
            it.setString(1 , firstName) >> _
            it.setString(2, lastName) >> _
            it.setString(3, emailAddress) >> _

            it.executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
        })
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub( Connection, {
            it.prepareStatement(query) >> preparedStatement
        })

        //and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

        //and: "an implementation of the SearchCustomerDataSource with this connection provider"
        PersonDbConnector dataSource = new PersonDbConnector(connectionProvider)

        when:
        int resultId = dataSource.getPersonId(new Customer.Builder(firstName,lastName,emailAddress).build())

        then:
        resultId == id

        where: "customer information is as follows"
        id | firstName | lastName | academicTitle | emailAddress
        0 | "luke" | "skywalker" | "Dr." | "sith@jedicouncil.universe"
    }
    
    def "CustomerDbConnector shall throw failNotification if updateCustomerAffiliations has nothing to update"() {
        given: "an implementation of the UpdateCustomerDataSource with this connection provider"
        String query = "SELECT *\n" +
        "FROM \n" +
        "    person_affiliation\n" +
        "    LEFT JOIN affiliation\n" +
        "    ON  person_affiliation.affiliation_id = affiliation.id\n" +
        "    WHERE person_affiliation.person_id = ?"
        
        // we need to stub the static SqlExtensions.toRowResult method because we do not provide an implemented RowResult
        GroovyMock(SqlExtensions, global: true)

        SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(["organization": organization,
                                                                          "address_addition":address_addition,"street":street,
                                                                          "postal_code":postal_code, "city":city, "country":country, "category": "internal"])
        // our statement should only be able to fill the template with the correct values
        PreparedStatement preparedStatement = Mock (PreparedStatement, {
            it.setInt(1, customerId) >> _
            it.executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
        })
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub( Connection, {
            it.prepareStatement(query) >> preparedStatement
        })

        and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

        and: "an implementation of the SearchCustomerDataSource with this connection provider"
        PersonDbConnector dataSource = new PersonDbConnector(connectionProvider)

        when: "update customer affiliations is called"
        dataSource.updatePersonAffiliations(customerId, [new Affiliation.Builder(organization,
                street, postal_code, city).addressAddition(address_addition).country(country).category(category).build()])

        then: "no affiliations are updated and a failNotification is thrown"
        thrown(Exception)

        where:
        customerId = 42
        id | organization | address_addition | street | postal_code | city | country | category
        0 | "QBiC" | "Quantitative Biology Center" | "Auf der Morgenstelle 10" | "72076" |
                "Tübingen" | "Germany" | AffiliationCategory.INTERNAL
    }

    def "CustomerDbConnector shall return the id for a given affiliation"(){
        given:
        String query = "SELECT * FROM affiliation WHERE organization=? " +
                "AND address_addition=? " +
                "AND street=? " +
                "AND postal_code=? " +
                "AND city=?"

        and: "a connection returning the id of the affiliation if it was found"
        // we need to stub the static SqlExtensions.toRowResult method because we do not provide an implemented RowResult
        GroovyMock(SqlExtensions, global: true)
        SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult(["id":id, "organization":organization, "address_addition":address_addition,"street":street,
                                                                          "postal_code":postal_code, "city":city])
        // our statement should only be able to fill the template with the correct values
        PreparedStatement preparedStatement = Mock (PreparedStatement, {
            it.setString(1, organization) >> _
            it.setString(2, address_addition) >> _
            it.setString(3, street) >> _
            it.setString(4, postal_code) >> _
            it.setString(5, city) >> _

            it.executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
        })
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub( Connection, {
            it.prepareStatement(query) >> preparedStatement
        })

        //and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

        //and: "an implementation of the SearchCustomerDataSource with this connection provider"
        PersonDbConnector dataSource = new PersonDbConnector(connectionProvider)

        when:
        int resultId = dataSource.getAffiliationId(new Affiliation.Builder(organization,street,postal_code,city)
                .addressAddition(address_addition)
                .category(category)
                .country(country)
                .build())

        then:
        resultId == id

        where: "affiliation information is as follows"
        id | organization | address_addition | street | postal_code | city | country | category
        0 | "QBiC" | "Quantitative Biology Center" | "Auf der Morgenstelle 10" | "72076" | "Tübingen" | "Germany" | AffiliationCategory.INTERNAL
    }
}
