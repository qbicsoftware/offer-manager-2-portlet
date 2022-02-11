package life.qbic.business.persons;

/**
 * <p>Thrown when the person already exists.</p>
 *
 * @since 1.3.0
 */
public class PersonExistsException extends RuntimeException {
  public PersonExistsException(String message) {
    super(message);
  }
}
