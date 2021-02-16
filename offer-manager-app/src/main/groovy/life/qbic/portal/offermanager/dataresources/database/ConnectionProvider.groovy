package life.qbic.portal.offermanager.dataresources.database

import java.sql.Connection
import java.sql.SQLException

/**
 * Provides the ability to connect to a SQL ressource
 *
 * @since: 1.0.0
 */
interface ConnectionProvider {
    Connection connect() throws SQLException
}
