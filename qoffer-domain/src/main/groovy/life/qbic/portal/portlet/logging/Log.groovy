package life.qbic.portal.portlet.logging

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
class Log {

    private final Logger logger

    Log(Class clazz){
        logger = LogManager.getLogger(clazz)
    }

    /**
     * Logs a message on the 'error' level
     *
     * @param message that should be logged
     */
    void error(String message){
        logger.error(message)
    }

    /**
     * Logs a message on the 'warn' level
     *
     * @param message that should be logged
     */
    void warning(String message){
        logger.warn(message)
    }

    /**
     * Logs a message on the 'debug' level
     *
     * @param message that should be logged
     */
    void debug(String message){
        logger.debug(message)
    }

    /**
     * Logs a message on the 'info' level
     *
     * @param message that should be logged
     */
    void info(String message){
        logger.info(message)
    }
}
