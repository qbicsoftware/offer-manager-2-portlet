package life.qbic.portal.portlet

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: <versiontag>
 */
interface UseCaseOutput {

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