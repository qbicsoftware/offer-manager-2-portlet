package life.qbic.portal.offermanager.dataresources.database

import org.hibernate.Session

/**
 * Provides a Hibernate session to perform transactions with the persistence layer.
 *
 * @since 1.3.0
 */
interface SessionProvider {

    Session getCurrentSession()
}
