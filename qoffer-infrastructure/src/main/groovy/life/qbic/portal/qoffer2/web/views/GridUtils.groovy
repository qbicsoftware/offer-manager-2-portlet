package life.qbic.portal.qoffer2.web.views

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.server.SerializablePredicate
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import org.apache.commons.lang3.StringUtils

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
    static <T, V> void setupColumnFilter(ListDataProvider<T> dataProvider,
                                         Grid.Column<T, V> column,
                                         HeaderRow headerRow,
                                         TextField filterTextField,
                                         SerializablePredicate<V> predicate) {
        filterTextField.addValueChangeListener(event -> {
            println dataProvider.getClass().getTypeName()
            dataProvider.addFilter(column.getValueProvider(), predicate)
        })
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER)
        filterTextField.addStyleName(ValoTheme.TEXTFIELD_TINY)
        filterTextField.setPlaceholder("Filter Me")

        headerRow.getCell(column).setComponent(filterTextField)
        filterTextField.setSizeFull()
    }
}
