package life.qbic.portal.offermanager.components;

/**
 * Implements a user input. Input validation and retrieval is possible.
 * @param <T>
 */
public interface UserInput<T> {

  /**
   * @return whether the user input is considered valid.
   */
  boolean isValid();

  /**
   * Retrieves the user input provided it is valid. Throws a RuntimeException for invalid input.
   * @return the valid input
   * @throws RuntimeException in case of invalid user input
   */
  T get() throws RuntimeException;
}
