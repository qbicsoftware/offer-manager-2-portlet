package life.qbic.portal.offermanager.security.local

import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService

/**
 * Example role service for local testing.
 *
 * Will always return the role of an offer admin.
 *
 * @since 1.0.0
 */
class LocalAdminRoleService implements RoleService {

    /**
     * {@inheritDoc}
     */
    @Override
    Optional<Role> getRoleForUser(String userId) {
        return Optional.of(Role.OFFER_ADMIN)
    }
}
