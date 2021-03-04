package life.qbic.business.exceptions

/**
 * Exception that indicates violations during an application process.
 *
 * This exception is supposed to be thrown, if an application request to create
 * a data resource in the data management system is not possible.
 *
 * Example: A project with a given title already exists in the data source. So the method should
 * throw an ApplicationDeniedException and not a DatabaseQueryException.
 *
 * With this, the use case can be made aware of, that it is not a technical issue during the
 * execution of an SQL query for example.
 *
 * @since 1.0.0
 */
class ApplicationDeniedException extends RuntimeException{

    ApplicationDeniedException(){
        super()
    }

    ApplicationDeniedException(String message) {
        super(message)
    }

    ApplicationDeniedException(String message, Throwable throwable){
        super(message, throwable)
    }
}
