package life.qbic.portal.offermanager.components.project

import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import org.apache.commons.lang3.StringUtils

import java.util.function.BiPredicate

/**
 * <p>Tests an Optional<ProjectIdentifier> on whether a user defined String is included in the
 * textual representation or not.</p>
 *
 * @since 1.0.0-rc
 */
class ProjectIdContainsString implements BiPredicate<Optional<ProjectIdentifier>, String> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param projectIdentifier the first input argument
     * @param userInput the textual user input
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     * @since 1.0.0-rc
     */
    @Override
    boolean test(Optional<ProjectIdentifier> projectIdentifier, String userInput) {
        if (projectIdentifier.isPresent()) {
            return StringUtils.containsIgnoreCase(projectIdentifier.get().toString(), userInput)
        } else {
            return false
        }
    }
}
