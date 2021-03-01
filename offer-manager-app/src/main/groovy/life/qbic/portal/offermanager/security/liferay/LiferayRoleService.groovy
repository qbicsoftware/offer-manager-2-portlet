package life.qbic.portal.offermanager.security.liferay

import life.qbic.portal.offermanager.security.Role
import life.qbic.portal.offermanager.security.RoleService

/**
 * Role service implementation for Liferay 6.2 GA-6
 *
 * This role service can be used to determine the role of a user
 * from a Liferay 6.2 portal environment.
 *
 * The current implementation matches the following Liferay roles
 * to the roles of this application context: project manager and offer admin.
 *
 * The association is (Liferay role to app role):
 *
 *      ProjectManager <-> PROJECT_MANAGER
 *      OfferAdmin <-> OFFER_ADMIN
 *
 * @since 1.0.0
 */
class LiferayRoleService implements RoleService{

    @Override
    Optional<Role> getRoleForUser(String userId) {
        return null
    }
}
