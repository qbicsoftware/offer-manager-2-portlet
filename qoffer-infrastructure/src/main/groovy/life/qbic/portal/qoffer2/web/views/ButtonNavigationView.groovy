package life.qbic.portal.qoffer2.web.views

import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: 0.1.0
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

    void indicateCurrentStep(){
        buttonList.get(currentStep).addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        buttonList.get(currentStep).setEnabled(true)

        if (currentStep < buttonList.size()) currentStep++
    }
}
