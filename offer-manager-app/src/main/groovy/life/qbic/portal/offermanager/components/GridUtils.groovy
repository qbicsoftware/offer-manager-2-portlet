package life.qbic.portal.offermanager.components

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.DateField
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.time.ZoneId
import java.util.function.BiPredicate

/**
 * A helper class with static utility functions for Vaadin Grids.
 *
 * @since 1.0.0
 */
class GridUtils {

    private static Logging log = Logger.getLogger(this.class)
    public final static int DESCRIPTION_MAX_WIDTH = 400


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
        styleFilterTextField(filterTextField, column.getCaption())
        headerRow.getCell(column).setComponent(filterTextField)
    }

    /**
     * Provides a filter field into a header row of a grid for a given column.
     *
     * <p>This filter tests for the provided predicate based on textual user input.</p>
     *
     *
     * @param <T> the grid bean type
     * @param <V> the column value type
     * @param dataProvider the grid's {@link ListDataProvider}
     * @param column the column to add the filter to
     * @param predicate the predicate that has to be true for a tested value and a user input String
     * @param headerRow the {@link com.vaadin.ui.components.grid.HeaderRow} of the {@link Grid}, where the filter input field is added
     * @since 1.0.0-rc.1
     */
    static <T,V> void setupColumnFilter(ListDataProvider<T> dataProvider,
                                        Grid.Column<T, V> column,
                                        BiPredicate<V, String> predicate,
                                        HeaderRow headerRow) {
        TextField filterTextField = new TextField()
        filterTextField.addValueChangeListener(event -> {
            dataProvider.addFilter({ element ->
                try {
                    String searchString = filterTextField.getValue()
                    // only apply predicate if user input is present
                    if (searchString) {
                        V value = column.getValueProvider().apply(element)
                        return predicate.test(value, searchString)
                    } else {
                        return true
                    }
                } catch (ClassCastException castException) {
                    log.error("Value provider provided wrong value type. Excluding entry from filtering. $castException.message")
                    log.debug("Value provider provided wrong value type. Excluding entry from filtering. $castException.message", castException)
                    return true
                }
            })
        })
        styleFilterTextField(filterTextField, column.getCaption())
        headerRow.getCell(column).setComponent(filterTextField)
    }



    /**
     * Provides a filter field into a header row of a grid for a given column of type Date.
     *
     * The current implementation filters a date column based on a picked date. If no date is provided,
     * the filter does not apply.
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
            dataProvider.addFilter(element -> {
                LocalDate filterValue = dateFilterField.getValue()
                Date columnValue = column.getValueProvider().apply(element)
                if (filterValue) {
                    return isSameDate(filterValue, columnValue)
                } else {
                    return true // when no filter argument is provided
                }
            })
        })
        dateFilterField.addStyleName(ValoTheme.DATEFIELD_TINY)

        headerRow.getCell(column).setComponent(dateFilterField)
        dateFilterField.setSizeFull()
    }

    private static boolean isSameDate(LocalDate localDate, Date date){
        try {
            Date dateFromLocal = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            return dateFromLocal == date
        } catch(Exception unexpected) {
            log.error("Unexpected exception for $localDate and $date")
            log.debug("Unexpected exception for $localDate and $date", unexpected)
            return false
        }
    }

    private static void styleFilterTextField(TextField filterTextField, String columnCaption) {
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER)
        filterTextField.addStyleName(ValoTheme.TEXTFIELD_TINY)
        filterTextField.setPlaceholder("Filter by $columnCaption")
        filterTextField.setSizeFull()

    }

}
