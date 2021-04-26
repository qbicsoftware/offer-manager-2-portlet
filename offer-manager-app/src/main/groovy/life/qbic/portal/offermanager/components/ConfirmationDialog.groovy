package life.qbic.portal.offermanager.components

import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import com.vaadin.ui.themes.ValoTheme

/**
 * <h1>Dialog to ask for user confirmation</h1>
 *
 * <p>The confirmation dialog requests the users confirmation about specific processes that will follow.
 * E.g before deleting the user should be asked if he really wants to do that</p>
 *
 * @since 1.0.0
 *
*/
class ConfirmationDialog extends Window{

    Button confirm
    Button decline
    private HorizontalLayout buttonLayout
    private Label descriptionLabel
    private Label titleLabel
    private String descriptionText
    private VerticalLayout content


    ConfirmationDialog(String description){
        center()
        // Disable the close button
        setClosable(false)

        descriptionText = description

        init()
        addListeners()

        content.addComponents(titleLabel, descriptionLabel, buttonLayout)
        content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT)

        setContent(content)
    }

    private void init(){
        confirm = new Button("Confirm")
        decline = new Button("Decline")

        descriptionLabel = new Label(descriptionText)
        titleLabel = new Label ("Are you sure?")
        titleLabel.addStyleName(ValoTheme.LABEL_HUGE)

        this.setResizable(false)

        content = new VerticalLayout()
        content.setMargin(true)

        buttonLayout = new HorizontalLayout()
        buttonLayout.addComponents(decline, confirm)
    }

    private void addListeners(){
        confirm.addClickListener({
            close()
        })

        decline.addClickListener({
            close()
        })
    }

}