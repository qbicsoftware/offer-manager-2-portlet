package life.qbic.portal.offermanager.components.projectcreation

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import groovy.beans.Bindable
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class CreateProjectModel {

    @Bindable Boolean createProjectEnabled

    enum SPACE_SELECTION {
        NEW_SPACE, EXISTING_SPACE
    }
    DataProvider spaceSelectionDataProvider

    @Bindable String resultingSpaceName

    @Bindable String desiredSpaceName

    DataProvider availableSpaces

    @Bindable String desiredProjectCode

    @Bindable String resultingProjectCode

    List<ProjectCode> existingProjects

    @Bindable String projectCodeValidationMessage

    @Bindable Boolean projectCodeIsValid

    CreateProjectModel() {
        spaceSelectionDataProvider = new ListDataProvider<>([SPACE_SELECTION.NEW_SPACE,
                                                             SPACE_SELECTION.EXISTING_SPACE])
        resultingSpaceName = ""
        desiredSpaceName = ""
        desiredProjectCode = ""
        resultingProjectCode = ""
        projectCodeValidationMessage = ""
        projectCodeIsValid = false
        availableSpaces = new ListDataProvider([new ProjectSpace("Example Space One"),
                           new ProjectSpace("Example Space Two")])
        existingProjects = [
                new ProjectCode("QABCD"),
                new ProjectCode("QTEST")
        ]
        createProjectEnabled = false
        setupListeners()
    }

    private void setupListeners() {
        this.addPropertyChangeListener("desiredSpaceName", {
            ProjectSpace space = new ProjectSpace(desiredSpaceName)
            // you have to use the setter. Otherwise the bindable String with its listeners is
            // overwritten and no events are fired
            this.setResultingSpaceName(space.name)
        })

        this.addPropertyChangeListener("desiredProjectCode", {
            evaluateProjectCode()
            evaluateProjectCreation()
        })
    }

    private void evaluateProjectCode() {
        try {
            ProjectCode projectCode = new ProjectCode(desiredProjectCode.toUpperCase())
            this.setProjectCodeIsValid(existingProjects.every {it.code != projectCode.code})
            this.setProjectCodeValidationMessage(projectCodeIsValid ? "Project code is valid." : "Project with code ${desiredProjectCode.toUpperCase()} already exists.")
            this.setResultingProjectCode(projectCode.code)
        } catch (IllegalArgumentException ignored) {
            this.setProjectCodeIsValid(false)
            this.setProjectCodeValidationMessage("${desiredProjectCode} is not a valid QBiC project code.")
        }
    }

    private void evaluateProjectCreation() {
        createProjectEnabled = projectCodeIsValid && resultingSpaceName
    }

}
