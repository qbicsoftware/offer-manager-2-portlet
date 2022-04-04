package life.qbic.business.offers.policies.taxes;

public class PolicyViolationException extends RuntimeException {

  PolicyViolationException() {
    super();
  }

  PolicyViolationException(String message) {
    super(message);
  }

}
