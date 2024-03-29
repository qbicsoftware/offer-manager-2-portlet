package life.qbic.business.persons.affiliation

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.affiliation.update.UpdateAffiliation
import life.qbic.business.persons.affiliation.update.UpdateAffiliationDataSource
import life.qbic.business.persons.affiliation.update.UpdateAffiliationOutput
import spock.lang.Specification

/**
 * This test class tests for the use case functionality
 *
 * The user wants to update an already existing affiliation in the system
 *
 * @since 1.3.0
 */
class UpdateAffiliationSpec extends Specification {
  private UpdateAffiliationOutput output = Mock()
  private UpdateAffiliationDataSource dataSource = Mock()
  private static Affiliation affiliation = new Affiliation("Holmes Inc.", "Watson division", "baker street", "BR1", "london", "england", AffiliationCategory.EXTERNAL)

  def "given an affiliation change, update the affiliation using a mocked data source"() {
    given: "A new update affiliation use case instance"
    UpdateAffiliation updateAffiliation = new UpdateAffiliation(output, dataSource)

    when: "The use case method is called"
    updateAffiliation.updateAffiliation(affiliation)

    then: "The affiliation is updated using the data source"
    1 * dataSource.updateAffiliation(affiliation)
    1 * output.updatedAffiliation(affiliation)
  }

  def "given an unexpected Exception, when an affiliation is updated, then a fail notification is sent"() {
    given: "a data source that throws an exception"
    UpdateAffiliation updateAffiliation = new UpdateAffiliation(output, dataSource)
    dataSource._ >> { throw new Exception("Something went wrong.") }

    when: "The use case method is called"
    updateAffiliation.updateAffiliation(affiliation)

    then: "the output receives a failure notification"
    1 * output.failNotification(_ as String)
    0 * output.updatedAffiliation(_ as Affiliation)
  }

  def "If the original affiliation can not be found during affiliation update, then a fail notification is sent"() {
    given: "a data source that throws an exception"
    UpdateAffiliation updateAffiliation = new UpdateAffiliation(output, dataSource)
    dataSource._ >> { throw new AffiliationNotFoundException("Your affiliation is in another castle.") }

    when: "The use case method is called"
    updateAffiliation.updateAffiliation(affiliation)

    then: "the output receives a failure notification"
    1 * output.failNotification(_ as String)
    0 * output.updatedAffiliation(_ as Affiliation)
  }

  def "given a failed database query during affiliation update, then a fail notification is sent"() {
    given: "a data source that throws an exception"
    UpdateAffiliation updateAffiliation = new UpdateAffiliation(output, dataSource)
    dataSource._ >> { throw new DatabaseQueryException("Query on my wayward son.") }

    when: "The use case method is called"
    updateAffiliation.updateAffiliation(affiliation)

    then: "the output receives a failure notification"
    1 * output.failNotification(_ as String)
    0 * output.updatedAffiliation(_ as Affiliation)
  }
}
