package life.qbic.business.offers.policies.taxes;


/**
 * <b>Policy Violation Exception</b>
 *
 * <p>To be thrown if the wrong policy was applied</p>
 *
 * @since 1.3.0
 */
public class PolicyViolationException extends RuntimeException {

  PolicyViolationException() {
    super();
  }

  PolicyViolationException(String message) {
    super(message);
  }

}
