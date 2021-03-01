package life.qbic.portal.offermanager.security

/**
 * Describes available roles in the offer manager application.
 *
 * @since 1.0.0
 */
enum Role {
    /**
     * A user with the role 'project manager' is able to access most offer
     * manager features, except the service product maintenance.
     */
    PROJECT_MANAGER,
    /**
     * A user with the role 'offer admin' is able to access all offer
     * manager features, including the service product maintenance.
     */
    OFFER_ADMIN
}
