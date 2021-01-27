package life.qbic.portal.portlet.logging

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import spock.lang.Specification

/**
 * Tests the current logging implementation
 * and fails if changes have made to the logging API.
 *
 * @since 1.0.0
 */
class LoggerSpec extends Specification {

    def "All four log levels should be callable without exception"() {
        given: "A log instance"
        Logging logger = Logger.getLogger(this.class)

        when:
        logger.info("Just a test info message")
        logger.warn("Just a test warning message")
        logger.error("Just a test error message")
        logger.debug("Just a test debug message")

        then:
        noExceptionThrown()
    }
}
