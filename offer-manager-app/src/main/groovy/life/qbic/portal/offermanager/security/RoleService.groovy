package life.qbic.portal.offermanager.security

/**
 * A role service determines the role for a given user
 *
 * Implementations of this interface evaluate the userId
 * against the associated role in the deployed system.
 *
 * In addition, the implementation must take care of
 * the system's role mapping to the role provided in this
 * application.
 *
 * @since 1.0.0
 */
interface RoleService {

    /**
     * Determines the role of a user with a given user identifier.
     * @param userId The user identifier determined by the system environment after successful
     * authentication.
     * @return The role of the user
     */
    Optional<Role> getRoleForUser(String userId)

}
