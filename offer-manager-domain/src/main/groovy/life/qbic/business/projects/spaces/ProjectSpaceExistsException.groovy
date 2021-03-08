package life.qbic.business.projects.spaces

/**
 * <h1>Exception that indicates violations during the creation af a new project space</h1>
 *
 * <p>This exception is supposed to be thrown, if an application request to create
 * a data resource in the data management system is not possible.</p>
 *
 * Example: A project space with a given identifier already exists in the data source. So the
 * method should throw an ProjectSpaceExistsException and not a DatabaseQueryException.
 * <br>
 * With this, the use case can be made aware of, that it is not a technical issue during the
 * execution of an SQL query for example.
 *
 * @since 1.0.0
 */
class ProjectSpaceExistsException extends RuntimeException{

    ProjectSpaceExistsException(){
        super()
    }

    ProjectSpaceExistsException(String message) {
        super(message)
    }

    ProjectSpaceExistsException(String message, Throwable throwable){
        super(message, throwable)
    }
}
