package life.qbic.business.projects.create

/**
 * <h1>Exception that indicates violations during an project application process</h1>
 *
 * <p>This exception is supposed to be thrown, if an application request to create
 * a data resource in the data management system is not possible.</p>
 *
 * Example: A project with a given space cannot be created, as the space does not exist in
 * the data source. So the method should throw an SpaceNonExistingException and not a
 * DatabaseQueryException.
 * <br>
 * With this, the use case can be made aware of, that it is not a technical issue during the
 * execution of an SQL query for example.
 *
 * @since 1.0.0
 */
class SpaceNonExistingException extends RuntimeException{

    SpaceNonExistingException(){
        super()
    }

    SpaceNonExistingException(String message) {
        super(message)
    }

    SpaceNonExistingException(String message, Throwable throwable){
        super(message, throwable)
    }
}
