package life.qbic.portal.qoffer2.database

import groovy.sql.GroovyRowResult
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.business.services.ProductUnit
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.portal.portlet.offers.create.CreateOfferDataSource
import life.qbic.portal.qoffer2.customers.CustomerDbConnector
import life.qbic.portal.qoffer2.offers.OfferDbConnector
import org.apache.groovy.sql.extensions.SqlExtensions
import spock.lang.Specification

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */
class CreateOfferDataSourceSpec extends Specification{

    /**
    def "CreateOfferDbConnector shall store the offer given the offer DTO"() {
        given: "a predefined query template"
        String OFFER_INSERT_QUERY = "INSERT INTO offer (modificationDate, expirationDate, customerId, projectManagerId, projectTitle, projectDescription, totalPrice, customerAffiliationId)" +
                "VALUE(?,?,?,?,?,?,?,?)"

        and: "a connection returning nothing if the the offer was created successfully"
        // we need to stub the static SqlExtensions.toRowResult method because we do not provide an implemented RowResult
        GroovyMock(SqlExtensions, global: true)
        SqlExtensions.toRowResult(_ as ResultSet) >> new GroovyRowResult()
        // our statement should only be able to fill the template with the correct values
        PreparedStatement preparedStatement = Mock (PreparedStatement, {
            it.setDate(1, modificationDate as Date) >> _
            it.setDate(2, expirationDate as Date) >> _
            it.setInt(3, customerId) >> _
            it.setInt(4, projectManagerId) >> _
            it.setString(5, projectTitle) >> _
            it.setString(6, projectDescription) >> _
            it.setDouble(7, totalPrice) >> _
            it.setInt(8, affiliationId) >> _

            it.executeQuery() >> Stub(ResultSet,{it.next() >>> [true, false]})
        })
        // the connection must only provide precompiled statements for the expected query template
        Connection connection = Stub( Connection, {
            it.prepareStatement(OFFER_INSERT_QUERY) >> preparedStatement
        })

        //and: "a ConnectionProvider providing the stubbed connection"
        ConnectionProvider connectionProvider = Stub (ConnectionProvider, {it.connect() >> connection})

        //and: "an implementation of the SearchCustomerDataSource with this connection provider"
        CreateOfferDataSource dataSource = new OfferDbConnector(connectionProvider)

        when: "the datasource is tasked with finding a customer with provided first and last name"
        dataSource.store(new Offer.Builder(customer,projectManager,projectTitle,projectDescription,items,customerAffilation)
                .expirationDate(expirationDate)
                .modificationDate(modificationDate)
                .build())

        then: "No exception is thrown if the insertion was successful"
        noExceptionThrown()

        where: "offer information is as follows"
        id | modificationDate | expirationDate | customer | projectManager | projectTitle | projectDescription | items | totalPrice | customerAffilation | customerId | projectManagerId | affiliationId
        0 | new java.util.Date() | new java.util.Date() | new Customer.Builder("first","last","@").build() |  new ProjectManager.Builder("first","last","@").build() |
        "title" | "description text" | [new ProductItem(2,new Sequencing("name","descr",999, ProductUnit.PER_SAMPLE))] | 2000.00 | new Affiliation.Builder("organization","street","postal_code","city").build()
        | 0 | 0 | 0

    } **/


    def "CreateOfferDbConnector shall return the id for a given person"(){
        given:
        String query = "SELECT id FROM person WHERE first_name = ? AND last_name = ? AND email = ?"

        and: "a connection returning the if of the person if it was found"
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
        CustomerDbConnector dataSource = new CustomerDbConnector(connectionProvider)

        when:
        int resultId = dataSource.getPersonId(new Customer.Builder(firstName,lastName,emailAddress).build())

        then:
        resultId == id

        where: "customer information is as follows"
        id | firstName | lastName | academicTitle | emailAddress
        0 | "luke" | "skywalker" | "Dr." | "sith@jedicouncil.universe"
    }

    def "CreateOfferDbConnector shall return the id for a given affiliation"(){
        given:
        String query = "SELECT * FROM affiliation WHERE organization= ? " +
                "AND address_addition = ? " +
                "AND street = ? " +
                "AND postal_code = ? " +
                "AND city = ?"

        and: "a connection returning the if of the affiliation if it was found"
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
        CustomerDbConnector dataSource = new CustomerDbConnector(connectionProvider)

        when:
        int resultId = dataSource.getAffiliationId(new Affiliation.Builder(organization,street,postal_code,city).build())

        then:
        resultId == id

        where: "affiliation information is as follows"
        id | organization | address_addition | street | postal_code | city | country | category
        0 | "QBiC" | "Quantitative Biology Center" | "Auf der Morgenstelle 10" | "72076" | "Tübingen" | "Germany" | "internal"
    }
}
