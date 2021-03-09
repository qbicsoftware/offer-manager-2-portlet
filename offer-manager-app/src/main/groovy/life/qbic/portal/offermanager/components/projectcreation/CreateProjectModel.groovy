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

    @Bindable String projectCodeValidationResult

    @Bindable Boolean codeIsValid

    CreateProjectModel() {
        spaceSelectionDataProvider = new ListDataProvider<>([SPACE_SELECTION.NEW_SPACE,
                                                             SPACE_SELECTION.EXISTING_SPACE])
        resultingSpaceName = ""
        desiredSpaceName = ""
        desiredProjectCode = ""
        resultingProjectCode = ""
        projectCodeValidationResult = ""
        codeIsValid = false
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
            resultingSpaceName = space.name
        })
        this.addPropertyChangeListener("desiredProjectCode", {
            try {
                ProjectCode code = new ProjectCode(desiredProjectCode.toUpperCase())
                resultingProjectCode = code.code
                if (code in existingProjects) {
                    codeIsValid = false
                    projectCodeValidationResult = "Project with code $resultingProjectCode " +
                            "already exists."
                } else {
                    codeIsValid = true
                    projectCodeValidationResult = "Project code is valid."
                }
            } catch (IllegalArgumentException e) {
                codeIsValid = false
                projectCodeValidationResult = "${desiredProjectCode} is not a valid QBiC project " +
                        "code."
            }
            evaluateProjectCreation()
        })
    }

    private void evaluateProjectCreation() {
        println codeIsValid
        println resultingSpaceName
        createProjectEnabled = codeIsValid && resultingSpaceName
        println createProjectEnabled
    }

}
