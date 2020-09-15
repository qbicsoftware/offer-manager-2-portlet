package life.qbic.portal.portlet.exceptions

import java.sql.SQLException

/**
 * An exception for errors in database querying
 *
 * This class should be thrown when an error occurs during a database query
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
class DatabaseQueryException extends SQLException{

    DatabaseQueryException(String message, String errorSqlStatement) {
        super(message, errorSqlStatement)
    }

    DatabaseQueryException(String message) {
        super(message)
    }

    DatabaseQueryException() {
    }

    DatabaseQueryException(String message, Throwable cause) {
        super(message, cause)
    }

    DatabaseQueryException(String message, String errorSqlStatement, Throwable cause) {
        super(message, errorSqlStatement, cause)
    }
}
