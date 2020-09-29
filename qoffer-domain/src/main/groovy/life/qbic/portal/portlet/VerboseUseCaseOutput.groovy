package life.qbic.portal.portlet

/**
 * Output of a use case with success and failure message processing
 *
 * A VerboseUseCaseOutput provides functionality to receive success and failure notification messages.
 *
 * @since: 1.0.0
 */
interface VerboseUseCaseOutput {

    /**
     * Sends success notifications that have been
     * recorded during the use case.
     * @param notification containing a success message
     */
    void successNotification(String notification)

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     */
    void failNotification(String notification)

}