package life.qbic.portal.portlet.logging

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
     * @param message
     */
    void info(String message)

    /**
     * Logs a warn event, that does not indicate a runtime exception, but still might be
     * important to report.
     * @param message
     */
    void warn(String message)

    /**
     * Logs a error or exception during the application execution.
     * @param message
     */
    void error(String message)

    /**
     * Logs runtime information that are useful for debugging.
     * @param message
     */
    void debug(String message)
}
