package life.qbic.portal.offermanager.dataresources.persons

import groovy.util.logging.Log4j2
import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.persons.Person
import life.qbic.business.persons.PersonExistsException
import life.qbic.business.persons.create.CreatePersonDataSource
import life.qbic.business.persons.list.ListPersonsDataSource
import life.qbic.business.persons.search.SearchPersonDataSource
import life.qbic.business.persons.update.UpdatePersonDataSource
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
class PersonDbConnector implements CreatePersonDataSource, SearchPersonDataSource, ListPersonsDataSource, UpdatePersonDataSource {

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
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Query<Person> query = session.createQuery("SELECT p FROM Person p WHERE p.isActive = TRUE ")
            // Print entities
            persons.addAll(query.list() as List<Person>)
        }
        return persons
    }

    @Override
    Person addPerson(Person person) throws DatabaseQueryException, PersonExistsException {
        try (Session session = sessionProvider.openSession()) {
            if (isPersonInSession(session, person)) {
                throw new PersonExistsException("The person already exists.")
            }
            session.clear()
            session.beginTransaction()
            session.save(person) //sets the id of the person
            session.getTransaction().commit()
            return person
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("An unexpected exception occurred during new affiliation creation")
        }
    }

    private static boolean isPersonInSession(Session session, Person person) {
        session.beginTransaction()
        Query<List<Person>> query =
                session.createQuery("SELECT p FROM Person p  LEFT JOIN FETCH p.affiliations WHERE p.firstName=:firstName AND p.lastName=:lastName AND p.email=:email")
        query.setParameter("firstName", person.getFirstName())
        query.setParameter("lastName", person.getLastName())
        query.setParameter("email", person.getEmail())
        boolean isInSession = !query.list().isEmpty()
        session.getTransaction().commit()
        return isInSession
    }

    @Override
    Person updatePerson(Person outdatedPersonData, Person updatedPersonData) throws DatabaseQueryException {

        // the user id nees to be preserved
        updatedPersonData.setUserId(outdatedPersonData.getUserId())
        try (Session session = sessionProvider.openSession()) {
            if (!isPersonInSession(session, outdatedPersonData)) {
                throw new DatabaseQueryException("Person was not found in the database and can't be updated.")
            }
            session.beginTransaction()
            session.clear()
            outdatedPersonData.setIsActive(false)
            session.update(outdatedPersonData)
            session.save(updatedPersonData)
            session.getTransaction().commit()
            return updatedPersonData
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unable to update person entry.")
        }
    }

    @Override
    Person updatePersonAffiliations(Person person) throws DatabaseQueryException {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Person mergedPerson = session.<Person> merge(person) //have to un-generify groovy here
            session.getTransaction().commit()
            return mergedPerson
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Unable to update person affiliations.")
        }
    }

    @Override
    List<Person> findPerson(String firstName, String lastName) throws DatabaseQueryException {
        List<Person> matches = new ArrayList<>()
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            Query<List<Person>> query =
                    session.createQuery("SELECT p FROM Person p  LEFT JOIN FETCH p.affiliations WHERE p.firstName=:firstName AND p.lastName=:lastName")
            query.setParameter("firstName", firstName)
            query.setParameter("lastName", lastName)
            matches.addAll(query.list() as List<Person>)
            session.getTransaction().commit()
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("An unexpected exception occurred during the search.")
        }
        return matches
    }

    @Override
    void removeAffiliationFromAllPersons(int affiliationId) {
        try (Session session = sessionProvider.getCurrentSession()) {
            session.beginTransaction()
            session.createSQLQuery("DELETE FROM person_affiliation WHERE affiliation_id=:affiliationId")
                    .setParameter("affiliationId", affiliationId)
                    .executeUpdate()
            session.getTransaction().commit()
        } catch (HibernateException e) {
            log.error(e.message, e)
            throw new DatabaseQueryException("Could remove affiliation relation from person.")
        }
    }
}
