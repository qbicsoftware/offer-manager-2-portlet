package life.qbic.portal.offermanager.dataresources.database

import groovy.util.logging.Log4j2
import life.qbic.business.offers.OfferV2
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.business.products.Product
import life.qbic.business.products.ProductItem
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
@Log4j2
class DatabaseSessionV2 implements SessionProvider{

    private SessionFactory sessionFactory

    DatabaseSessionV2(String url, String user, String password, String driver, String dialect) {

        Properties properties = new Properties()
        properties.setProperty(Environment.DRIVER, driver)
        properties.setProperty(Environment.URL, url)
        properties.setProperty(Environment.USER, user)
        properties.setProperty(Environment.PASS, password)
        properties.setProperty(Environment.DIALECT, dialect)
        properties.setProperty(Environment.POOL_SIZE, "5")
        properties.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread")

        configureHibernate(properties)
    }

    private void configureHibernate(Properties properties) {
        Configuration config = new Configuration()
        config.setProperties(properties)
        // build session factory
        sessionFactory = config.addAnnotatedClass(Person.class)
                .addAnnotatedClass(Affiliation.class)
                .addAnnotatedClass(OfferV2.class)
                .addAnnotatedClass(ProductItem.class)
                .addAnnotatedClass(Product.class)
                .buildSessionFactory()
    }

    @Override
    Session getCurrentSession() {
        return sessionFactory.getCurrentSession()
    }
}
