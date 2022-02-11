package life.qbic.business.persons.affiliation;

/**
 * To be thrown when an affiliation already exists
 *
 * @since 1.3.0
 */
public class AffiliationExistsException extends RuntimeException {

  /**
   * Constructs a new affiliation exists exception with the specified detail message. The cause is
   * not initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   * @see RuntimeException#RuntimeException(String)
   */
  public AffiliationExistsException(String message) {
    super(message);
  }
}
