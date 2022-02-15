package life.qbic.portal.offermanager.dataresources.persons


import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.Affiliation
import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.affiliation.AffiliationExistsException
import life.qbic.business.persons.affiliation.create.CreateAffiliationDataSource
import life.qbic.business.persons.affiliation.list.ListAffiliationsDataSource
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.business.persons.search.SearchPersonDataSource
import life.qbic.portal.offermanager.dataresources.database.SessionProvider
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.query.Query

/**
 * Provides operations on QBiC customer data
 *
 * This class implements the data sources of the different use cases and is responsible for transferring data from the database towards them
 *
 * @since 1.0.0
 * @author Jennifer Bödker
 *
 */
@Log4j2
class PersonDbConnector implements CreatePersonDataSource, SearchPersonDataSource, ListPersonsDataSource, CreateAffiliationDataSource, ListAffiliationsDataSource {

    private final SessionProvider sessionProvider

    /**
     * Uses a Hibernate session to perform the transactions with the persistence layer
     * @param sessionProvider
     * @since 1.3.0
     */
    PersonDbConnector(SessionProvider sessionProvider) {
      this.sessionProvider = sessionProvider
    }

    @Override
    List<Person> listPersons() {
      List<Person> persons = new ArrayList<>()
      try(Session session = sessionProvider.getCurrentSession()) {
        session.beginTransaction()
        Query<Person> query = session.createQuery("FROM Person ")
        // Print entities
        persons.addAll(query.list() as List<Person>)
      }
      return persons
    }

    @Override
    void addAffiliation(Affiliation affiliation) throws DatabaseQueryException, AffiliationExistsException {
      try(Session session = sessionProvider.getCurrentSession()) {
        session.beginTransaction()
        int id = session.getIdentifier(affiliation) as int
        if (id) {
          // If the query returns an identifier, an entry with this data already exists
          throw new AffiliationExistsException("The affiliation already exists.")
        }
        // we ignore the generated primary id for now
        session.save(affiliation)
      } catch (HibernateException e ){
        log.error(e.getStackTrace().join("\n"))
        throw new DatabaseQueryException()
      }
    }

    @Override
    List<Affiliation> listAllAffiliations() {
        return new ArrayList<Affiliation>()
    }

    @Override
    void addPerson(Person person) throws DatabaseQueryException, PersonExistsException {
      // FIXME
    }

    @Override
    void updatePerson(int personId, Person updatedPerson) throws DatabaseQueryException {
      // FIXME
    }

    @Override
    Person getPerson(int personId) {
        return new Person()
    }

    @Override
    Optional<Integer> findPerson(Person person) {
        return Optional.empty()
    }

    @Override
    void updatePersonAffiliations(int personId, List<Affiliation> affiliations) throws DatabaseQueryException {
      // FIXME
    }

    @Override
    List<Person> findPerson(String firstName, String lastName) throws DatabaseQueryException {
      return new ArrayList<Person>()
    }
}
