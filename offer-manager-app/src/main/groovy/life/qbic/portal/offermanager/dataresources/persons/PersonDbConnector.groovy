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
        logException(e)
        throw new DatabaseQueryException("An unexpected exception occurred during new affiliation creation")
      }
    }

    @Override
    List<Affiliation> listAllAffiliations() {
        List<Affiliation> affiliations = new ArrayList<>()
        try(Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            // we ignore the generated primary id for now
            Query<Affiliation> query = session.createQuery("FROM Affiliation ")
            // Print entities
            affiliations.addAll(query.list() as List<Affiliation>)
        }
        return affiliations
    }

    @Override
    void addPerson(Person person) throws DatabaseQueryException, PersonExistsException {
        try(Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            int id = session.getIdentifier(person) as int
            if (id) {
                // If the query returns an identifier, an entry with this data already exists
                throw new AffiliationExistsException("The person already exists.")
            }
            // we ignore the generated primary id for now
            session.save(person)
        } catch (HibernateException e ){
            logException(e)
            throw new DatabaseQueryException("An unexpected exception occurred during new affiliation creation")
        }
    }

    @Override
    void updatePerson(Person outdatedPersonData, Person updatedPersonData) throws DatabaseQueryException {
        outdatedPersonData.setIsActive(false)
        updatedPersonData.setIsActive(true)
        try(Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            session.save(outdatedPersonData)
            session.save(updatedPersonData)
            session.getTransaction().commit()
        } catch (HibernateException e) {
            logException(e)
            throw new DatabaseQueryException("Unable to update person entry.")
        }
    }

    @Override
    void updatePersonAffiliations(Person person) throws DatabaseQueryException {
        try(Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            session.merge(person)
            session.getTransaction().commit()
        } catch (HibernateException e) {
            logException(e)
            throw new DatabaseQueryException("Unable to update person affiliations.")
        }
    }

    @Override
    List<Person> findPerson(String firstName, String lastName) throws DatabaseQueryException {
        List<Person> matches = new ArrayList<>()
        try(Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Query<List<Person>> query =
                    session.createQuery("SELECT p FROM Person p  LEFT JOIN FETCH p.affiliations WHERE p.firstName=:firstName AND p.lastName=:lastName")
            query.setParameter("firstName", firstName)
            query.setParameter("lastName", lastName)
            matches.addAll(query.list() as List<Person>)
            session.getTransaction().commit()
        } catch (HibernateException e) {
            logException(e)
            throw new DatabaseQueryException("An unexpected exception occurred during the search.")
        }
      return matches
    }

    private static void logException(Exception e) {
        log.error(e.message)
        log.error(e.getStackTrace().join("\n"))
    }
}
