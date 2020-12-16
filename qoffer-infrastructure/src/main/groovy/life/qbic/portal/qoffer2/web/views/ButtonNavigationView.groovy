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
class ButtonNavigationView extends VerticalLayout{

    private final HorizontalLayout navigationLayout = new HorizontalLayout()
    List<Button> buttonList = []

    /**
     * This method allows to add an item to the navigation bar and returns the resulting layout.
     * This allows to add multiple buttons to the view by calling this method multiple times in the same method call.
     *
     * @param itemName The displayed name of the navigation item
     * @param layout The layout that is displayed after clicking the navigation item button
     * @return the layout with the added button
     */
    ButtonNavigationView addNavigationItem(String itemName, VerticalLayout layout){
        Button button = new Button(itemName)
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS)

        buttonList.add(button)

        button.addClickListener({
            this.removeAllComponents()
            this.addComponents(navigationLayout,layout)
        })

        navigationLayout.addComponent(button)
        return this
    }

    def defaultSelectFirstButton(){
        Button first = navigationLayout.getComponent(0) as Button
        first.click()
    }
}
