package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.themes.ValoTheme

/*
 * This class generates and tracks the state of the individual View components of the navigation bar
 *
 * @since: 1.0.0
 *
 */
class ButtonNavigationView extends HorizontalLayout{

    List<Button> buttonList = []
    private int currentStep = 0

    /**
     * This method allows to add an item to the navigation bar and returns the resulting layout.
     * This allows to add multiple buttons to the view by calling this method multiple times in the same method call.
     *
     * @param itemName The displayed name of the navigation item
     * @param layout The layout that is displayed after clicking the navigation item button
     * @return the layout with the added button
     */
    ButtonNavigationView addNavigationItem(String itemName){
        Button button = new Button(itemName)
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS)
        button.setEnabled(false)

        buttonList.add(button)

        this.addComponent(button)
        return this
    }

    /**
     * Colors the next step to show the current status of the process
     */
    void showNextStep(){
        buttonList.get(currentStep).setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)

        if (currentStep < buttonList.size()) currentStep++
    }

    /**
     * Removes the previous colored step to show the current status of the process
     */
    void showPreviousStep() {
        if (currentStep >= 0) currentStep--
        buttonList.get(currentStep).setStyleName(ValoTheme.BUTTON_BORDERLESS)
    }
}
