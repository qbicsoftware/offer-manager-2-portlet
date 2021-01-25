package life.qbic.portal.portlet

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class should enables logging for the domain module
 *
 * To stick to clean architecture concepts the domain module should not contain external libraries. This class enables logging
 * though the facade patterns and allows logging while still sticking to the CA concept
 *
 * @since: 1.0.0
 *
 */
class Logger {

    private final org.apache.logging.log4j.Logger logger

    Logger(Class clazz){
        logger = LogManager.getLogger(clazz)
    }

    /**
     * Logs a message on the 'error' level
     *
     * @param message that should be logged
     */
    void logError(String message){
        logger.error(message)
    }

    /**
     * Logs a message on the 'warn' level
     *
     * @param message that should be logged
     */
    void logWarning(String message){
        logger.warn(message)
    }

    /**
     * Logs a message on the 'debug' level
     *
     * @param message that should be logged
     */
    void logDebug(String message){
        logger.debug(message)
    }

    /**
     * Logs a message on the 'info' level
     *
     * @param message that should be logged
     */
    void logInfo(String message){
        logger.info(message)
    }
}
