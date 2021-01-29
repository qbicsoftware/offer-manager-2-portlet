package life.qbic.business

/**
 * Output of a use case with success and failure message processing
 *
 * A UseCaseFailure provides functionality to receive success and failure notification messages.
 *
 * @since: 1.0.0
 */
interface UseCaseFailure {

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    void failNotification(String notification)

}