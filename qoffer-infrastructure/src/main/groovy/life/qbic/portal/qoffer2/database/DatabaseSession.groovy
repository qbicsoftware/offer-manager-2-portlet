package life.qbic.portal.qoffer2.database

import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory
import org.apache.commons.dbcp2.BasicDataSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.SQLException


/**
 * Creates a connection to the user database
 *
 * A class for setting up the connection to the user database. It should be used when data needs to be retrieved from the
 * DB or written into it.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class DatabaseSession {

    protected static DatabaseSession INSTANCE

    private BasicDataSource dataSource

    private static final Logger LOG = LogManager.getLogger(DatabaseSession.class)

    //create session with DatabaseSession.init() and then DatabaseSession.INSTANCE to use the created session
    private DatabaseSession(String user, String password, String host, String port, String sql_database) {

        def url = "jdbc:mysql://" + host + ":" + port + "/" + sql_database

        dataSource = new BasicDataSource()
        dataSource.setUrl(url)
        dataSource.setUsername(user)
        dataSource.setPassword(password)
        dataSource.setMinIdle(5)
        dataSource.setMaxIdle(10)
        dataSource.setMaxOpenPreparedStatements(100)

        LOG.info("MySQL Database instance created")
    }


    /**
     * Initiates the database connection by retrieving configuration information from the {@link ConfigurationManager}
     * The instance is only created if there is no other existing
     */
    static void create() {
        ConfigurationManager conf = ConfigurationManagerFactory.getInstance()
        if (INSTANCE == null) {
            INSTANCE = new DatabaseSession(conf.getMysqlUser(), conf.getMysqlPass(), conf.getMysqlHost(),
                    conf.getMysqlPort(), conf.getMysqlDB())
        } else{
            LOG.info("There is already an existing database instance")
        }
    }

    /**
     * Creates a database connection by login into the database based on the given credentials
     *
     * @return Connection, otherwise null if connecting to the database fails
     * @throws SQLException if a database access error occurs or the url is {@code null}
     */
    Connection getConnection() throws SQLException {
        return dataSource.getConnection()
    }

    /**
     * Returns the current DatabaseSession object
     * @return
     */
    static DatabaseSession getINSTANCE() {
        return INSTANCE
    }
}
