package life.qbic.business.offers

/**
 * Thrown in case an offer unexpectedly exists
 */
class OfferExistsException extends RuntimeException {

    OfferExistsException(String message) {
        super(message)
    }

    OfferExistsException(String message, Throwable cause) {
        super(message, cause)
    }

}
