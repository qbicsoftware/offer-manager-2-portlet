package life.qbic.portal.qoffer2.database

import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.DriverManager
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

    private final String hostname
    private final String url
    private final String port
    private final String sql_database
    private final String username
    private final String password

    private static final Logger LOG = LogManager.getLogger(DatabaseSession.class)

    //create session with DatabaseSession.init() and then DatabaseSession.INSTANCE to use the created session
    private DatabaseSession(String user, String password, String host, String port, String sql_database) {
        username = user
        this.password = password
        this.hostname = host
        this.port = port
        this.sql_database = sql_database
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + sql_database
        String mysqlDriverName = "com.mysql.jdbc.Driver"
        try {
            Class.forName(mysqlDriverName)
        } catch (ClassNotFoundException e) {
            e.printStackTrace()
        }
        LOG.info("MySQL Database instance created")
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    static Connection getDatabaseInstanceAlternative(Properties config) throws SQLException {
        try{
            Class.forName("com.mysql.jdbc.Driver")
            return DriverManager.getConnection("jdbc:mysql://" + config.getProperty("mysql.host") + ":" + config.getProperty("mysql.port") + "/" + config.getProperty("mysql.db"),
                    config.getProperty("mysql.user"), config.getProperty("mysql.pass"))
        }
        catch (Exception e){
            LOG.error "Could not establish JBDC connection"
            e.printStackTrace()
        }
        return null
    }


    /**
     * Initiates the database connection by retrieving configuration information from the {@link ConfigurationManager}
     * The instance is only created if there is no other existing
     */
    static void init() {
        ConfigurationManager conf = ConfigurationManagerFactory.getInstance()
        if (INSTANCE == null) {
            INSTANCE = new DatabaseSession(conf.getMysqlUser(), conf.getMysqlPass(), conf.getMysqlHost(),
                    conf.getMysqlPort(), conf.getMysqlDB())
        }else{
            LOG.info("There is already an existing database instance")
        }
    }

    /**
     * Creates a database connection by login into the database based on the given credentials
     *
     * @return Connection, otherwise null if connecting to the database fails
     * @throws SQLException if a database access error occurs or the url is {@code null}
     */
    Connection login() throws SQLException {
        return DriverManager.getConnection(url, username, password)
    }

    /**
     * Tries to disconnect from the current session and close it
     *
     * @param conn
     */
    static void logout(Connection conn) {
        try {
            conn.close()
        } catch (SQLException e) {
            LOG.error "Failed to disconnect from the current database connection"
            //todo how do we want a failed connection?
            e.printStackTrace()
        }
    }
}
