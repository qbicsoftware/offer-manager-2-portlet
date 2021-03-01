package life.qbic.portal.offermanager.security.local

import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService

/**
 * Example role service for local testing.
 *
 * Will always return the role of an project manager.
 *
 * @since 1.0.0
 */
class LocalManagerRoleService implements RoleService {

    /**
     * {@inheritDoc}
     */
    @Override
    Optional<Role> getRoleForUser(String userId) {
        return Optional.of(Role.PROJECT_MANAGER)
    }
}
