package life.qbic.portal.qoffer2.web.controllers

import com.vaadin.data.ValidationResult
import com.vaadin.data.ValueContext
import com.vaadin.data.validator.EmailValidator
import com.vaadin.data.validator.StringLengthValidator
import com.vaadin.server.UserError

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.portal.portlet.customers.create.CreateCustomerInput
import life.qbic.portal.qoffer2.web.views.CreateCustomerView


/**
 * Controller class responsible for the data flow into qoffer-2
 *
 * This class creates instances of qoffer-2 classes and injects them as described in the architectural draft.
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 */
class CreateCustomerController {

    CreateCustomerView view

    CreateCustomerInput useCaseInput

    CreateCustomerController(CreateCustomerView view, CreateCustomerInput useCaseInput) {
        this.view = view
        this.useCaseInput = useCaseInput
        setupViewElements()
    }

    private void setupViewElements(){
        //Add Listeners to all Fields in the Formlayout
        view.firstNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator("Please input a valid first " +
                "Name", 1, null).apply(event.getValue(), new ValueContext(view.firstNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                view.firstNameField.setComponentError(error)
                view.firstName = null
            } else {
                view.firstNameField.setComponentError(null)
                view.firstName = event.getValue().toString()
            }
        })

        view.lastNameField.addValueChangeListener({ event ->
            ValidationResult result = new StringLengthValidator("Please input a valid last Name",
                1, null).apply(event.getValue(), new ValueContext(view.lastNameField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                view.lastNameField.setComponentError(error)
                view.lastName = null
            } else {
                view.lastNameField.setComponentError(null)
                view.lastName = event.getValue().toString()
                view.customerInfo.put("Last Name", view.lastName)
            }
        })

        view.emailField.addValueChangeListener({ event ->
            ValidationResult result = new EmailValidator("Please input a valid email address")
                .apply(event.getValue(), new ValueContext(view.emailField))
            if (result.isError()) {
                UserError error = new UserError(result.getErrorMessage())
                view.email = null
                view.emailField.setComponentError(error)
            } else {
                view.emailField.setComponentError(null)
                view.email = event.getValue().toString()
                view.customerInfo.put("email", view.email)
            }
        })

        view.affiliationComboBox.addSelectionListener({ event ->
            view.affiliation = event.getValue()
            println view.affiliation
            view.customerInfo.put("Affiliation", view.affiliation)
        })

        view.submitButton.addClickListener({ event ->
            createNewCustomer()
        })
    }

    private void createNewCustomer() {
        Customer customer = new Customer("first name","last name","title","mail", [view.affiliation] as List<Affiliation>)
        this.useCaseInput.createCustomer(customer)
    }
}
