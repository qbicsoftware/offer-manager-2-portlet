package life.qbic.portal.offermanager.dataresources.persons;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.List;
import life.qbic.business.exceptions.DatabaseQueryException;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.persons.affiliation.AffiliationExistsException;
import life.qbic.business.persons.affiliation.AffiliationNotFoundException;
import life.qbic.business.persons.affiliation.archive.ArchiveAffiliationDataSource;
import life.qbic.business.persons.affiliation.create.CreateAffiliationDataSource;
import life.qbic.business.persons.affiliation.list.ListAffiliationsDataSource;
import life.qbic.business.persons.affiliation.update.UpdateAffiliationDataSource;
import life.qbic.portal.offermanager.dataresources.database.SessionProvider;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;


public class AffiliationDbConnector implements CreateAffiliationDataSource,
    ListAffiliationsDataSource, UpdateAffiliationDataSource, ArchiveAffiliationDataSource {

  private final SessionProvider sessionProvider;

  private static final Logger log = getLogger(AffiliationDbConnector.class);

  /**
   * Uses a Hibernate session to perform the transactions with the persistence layer.
   * @param sessionProvider the session provider providing hibernate sessions
   */
  public AffiliationDbConnector(SessionProvider sessionProvider) {
    this.sessionProvider = sessionProvider;
  }

  @Override
  public void addAffiliation(Affiliation affiliation) throws DatabaseQueryException, AffiliationExistsException {
    try (Session session = sessionProvider.openSession()) {
      if (isAffiliationInSession(session, affiliation)) {
        throw new AffiliationExistsException("The affiliation already exists.");
      }
      session.beginTransaction();
      session.save(affiliation);
    } catch (HibernateException e) {
      log.error(e.getMessage(), e);
      throw new DatabaseQueryException(
          "An unexpected exception occurred during new affiliation creation");
    }
  }

  @Override
  public List<Affiliation> listAllAffiliations() {
    List<Affiliation> affiliations;
    try (Session session = sessionProvider.getCurrentSession()) {
      session.beginTransaction();
      Query<Affiliation> query = session.createQuery("FROM Affiliation ", Affiliation.class);
      affiliations = new ArrayList<>(query.list());
      session.clear();
    }
    return affiliations;
  }

  @Override
  public void updateAffiliation(Affiliation affiliation)
      throws DatabaseQueryException, AffiliationNotFoundException {
    try (Session session = sessionProvider.openSession()) {
      session.beginTransaction();
      boolean affiliationWasNotFound = session.get(Affiliation.class, affiliation.getId()) == null;
      if (affiliationWasNotFound) {
        throw new AffiliationNotFoundException("Affiliation was not found in the database and can't be updated.");
      }
      session.merge(affiliation);
      session.getTransaction().commit();
    }
  }

  private static boolean isAffiliationInSession(Session session, Affiliation affiliation) {
    session.beginTransaction();
    Query<Affiliation> query =
        session.createQuery("SELECT a FROM Affiliation a  " +
            "WHERE a.addressAddition = :addressAddition " +
            "AND a.category = :category " +
            "AND a.city = :city " +
            "AND a.country = :country " +
            "AND a.organization = :organization " +
            "AND a.postalCode = :postalCode " +
            "AND a.street = :street", Affiliation.class);
    query.setParameter("addressAddition", affiliation.getAddressAddition());
    query.setParameter("category", affiliation.getCategory());
    query.setParameter("city", affiliation.getCity());
    query.setParameter("country", affiliation.getCountry());
    query.setParameter("organization", affiliation.getOrganization());
    query.setParameter("postalCode", affiliation.getPostalCode());
    query.setParameter("street", affiliation.getStreet());

    boolean isInSession = !query.list().isEmpty();
    session.clear();
    session.getTransaction().commit();
    return isInSession;
  }

  @Override
  public void archiveAffiliation(Affiliation affiliation) throws DatabaseQueryException {
    try(Session session = sessionProvider.getCurrentSession()) {
      session.beginTransaction();
      session.merge(affiliation);
      session.getTransaction().commit();
    } catch (HibernateException e) {
      log.error(e);
      throw new DatabaseQueryException("Archiving failed");
    }
  }
}
