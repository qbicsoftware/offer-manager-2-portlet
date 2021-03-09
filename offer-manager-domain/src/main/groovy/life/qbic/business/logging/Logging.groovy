package life.qbic.business.logging

/**
 * Describes logging functionality
 *
 * Classes implementing this interface indicate that provide a way
 * to log different event types during the application runtime.
 *
 * @since 1.0.0
 */
interface Logging {

    /**
     * Logs a common information event.
     * @param message the message string to log
     * @since 1.0.0
     */
    void info(String message)

    /**
     * Logs a message at the INFO level including the stack trace of the Throwable cause passed as parameter.
     * @param message the message object to log.
     * @cause the exception to log, including its stack trace
     */
    void info(String message, Throwable cause)

    /**
     * Logs a warn event, that does not indicate a runtime exception, but still might be
     * important to report.
     * @param message
     * @since 1.0.0
     */
    void warn(String message)

    /**
     * Logs a warn event, that does not indicate a runtime exception, but still might be
     * important to report.
     * @param message the message object to log.
     * @cause the exception to log, including its stack trace
     * @since 1.0.0
     */
    void warn(String message, Throwable cause)

    /**
     * Logs a error or exception during the application execution.
     * @param message
     * @since 1.0.0
     */
    void error(String message)

    /**
     * Logs a error or exception during the application execution.
     * @param message
     * @param message the message object to log.
     * @cause the exception to log, including its stack trace
     * @since 1.0.0
     */
    void error(String message, Throwable cause)

    /**
     * Logs runtime information that are useful for debugging.
     * @param message
     * @since 1.0.0
     */
    void debug(String message)

    /**
     * Logs runtime information that are useful for debugging.
     * @param message the message object to log.
     * @cause the exception to log, including its stack trace
     * @since 1.0.0
     */
    void debug(String message, Throwable cause)
}
