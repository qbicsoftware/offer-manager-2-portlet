package life.qbic.portal.offermanager.components

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.DateField
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import org.apache.commons.lang3.StringUtils

import java.time.ZoneId
import java.time.chrono.ChronoLocalDate

/**
 * A helper class with static utility functions for Vaadin Grids.
 *
 * @since 1.0.0
 */
class GridUtils {

    /**
     * Provides a filter field into a header row of a grid for a given column.
     *
     * The current implementation filters for content that contains the filter criteria in the
     * column values and ignores the case.
     *
     * @param dataProvider The grid's {@link ListDataProvider}
     * @param column The column to add the filter to
     * @param headerRow The{@link com.vaadin.ui.components.grid.HeaderRow} of the {@link Grid}, where the filter input field is added
     */
    static <T> void setupColumnFilter(ListDataProvider<T> dataProvider,
                                      Grid.Column<T, String> column,
                                      HeaderRow headerRow) {
        TextField filterTextField = new TextField()
        filterTextField.addValueChangeListener(event -> {
            dataProvider.addFilter(element ->
                    StringUtils.containsIgnoreCase(column.getValueProvider().apply(element), filterTextField.getValue())
            )
        })
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER)
        filterTextField.addStyleName(ValoTheme.TEXTFIELD_TINY)
        String columnId = StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(column.id),
                ' '
        )
        filterTextField.setPlaceholder("Filter by " + columnId)
        headerRow.getCell(column).setComponent(filterTextField)
        filterTextField.setSizeFull()
    }

    /**
     * Provides a filter field into a header row of a grid for a given column of type Date.
     *
     * The current implementation filters a date column based on a picked date
     *
     * @param dataProvider The grid's {@link ListDataProvider}
     * @param column The date column to add the filter to
     * @param headerRow The{@link com.vaadin.ui.components.grid.HeaderRow} of the {@link Grid}, where the filter input field is added
     */
    static <T> void setupDateColumnFilter(ListDataProvider<T> dataProvider,
                                      Grid.Column<T, Date> column,
                                      HeaderRow headerRow) {
        DateField dateFilterField = new DateField()
        dateFilterField.addValueChangeListener(event -> {
            dataProvider.addFilter(element ->
                    Date.from(dateFilterField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).equals(column.getValueProvider().apply(element))
            )
        })
        dateFilterField.addStyleName(ValoTheme.DATEFIELD_TINY)

        headerRow.getCell(column).setComponent(dateFilterField)
        dateFilterField.setSizeFull()
    }
}
