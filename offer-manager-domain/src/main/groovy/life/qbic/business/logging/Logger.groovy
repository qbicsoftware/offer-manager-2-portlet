package life.qbic.business.logging

import org.apache.logging.log4j.LogManager

/**
 * This class enables logging for the domain module
 *
 * To stick to clean architecture concepts the domain module should not contain external libraries. This class enables logging
 * though the facade patterns and allows logging while still sticking to the CA concept
 *
 * @since: 1.0.0
 *
 */
class Logger {

    /**
     * Use this static method to receive an instance of type {@link Logging}
     * if you want to make use of logging functionality from within your classes.
     *
     * @param c The class that is requesting a logging instance.
     * @return An implementation of type {@link Logging}
     */
    static final Logging getLogger(Class<?> c) {
        new Log4JLoggerFacade(c)
    }

    static class Log4JLoggerFacade implements Logging {

        private final org.apache.logging.log4j.Logger logger

        Log4JLoggerFacade(Class<?> c) {
            this.logger = LogManager.getLogger(c)
        }

        @Override
        void info(String message) {
            this.logger.info(message)
        }

        @Override
        void warn(String message) {
            this.logger.warn(message)
        }

        @Override
        void error(String message) {
            this.logger.error(message)
        }

        @Override
        void debug(String message) {
            this.logger.debug(message)
        }
    }

}
