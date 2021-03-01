package life.qbic.portal.offermanager.security.liferay

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.model.User
import com.liferay.portal.service.UserLocalServiceUtil
import groovy.util.logging.Log4j2
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
@Log4j2
class LiferayRoleService implements RoleService{

    @Override
    Optional<Role> getRoleForUser(String userId) {
        Optional<Role> role = Optional.empty()
        User user
        try {
            user = tryToGetLiferayUser(userId)
        } catch (PortalException e) {
            log.error(String.format("Could not find user with id %s.", userId))
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
            return role
        }
        return determineLiferayRole(user)
    }

    private static User tryToGetLiferayUser(String userId) {
        return UserLocalServiceUtil.getUser(Long.parseLong(userId))
    }

    private static Optional<Role> determineLiferayRole(User user) {
        List<com.liferay.portal.model.Role> liferayRoles = user.getRoles()
        for (com.liferay.portal.model.Role role : liferayRoles) {
            if (role.title.equals("Project Manager")) {
                return Optional.of(Role.PROJECT_MANAGER)
            } else if (role.title.equals("Offer Administrator")) {
                return Optional.of(Role.OFFER_ADMIN)
            }
        }
        return Optional.empty()
    }
}
