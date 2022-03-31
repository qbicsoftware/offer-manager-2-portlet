package life.qbic.business.persons
/**
 * <b>Exception thrown when no person was found</b>
 *
 * <p>When comparing person entries e.g. in the database, this exception should be thrown</p>
 *
 * @since 1.2.0
 */
class PersonNotFoundException extends RuntimeException{

    PersonNotFoundException() {
    }

    PersonNotFoundException(String message) {
        super(message)
    }

    PersonNotFoundException(String message, Throwable cause) {
        super(message, cause)
    }
}
