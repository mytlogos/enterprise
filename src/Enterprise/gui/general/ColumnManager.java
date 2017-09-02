package Enterprise.gui.general;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Should create column with settings specified by the {@link Column}.
 * Maps the {@code Column} and {@code TableColumn}, shows and hides the
 * {@code TableColumn} from the user when calling {@link #showColumn(Column)},
 * {@link #hideColumn(Column)}.
 * <p>
 * // TODO: 02.09.2017 do sth with this or delete this shit
 */
public class ColumnManager<E> {
    private Map<Column, TableColumn<E, ?>> consumerMap = new HashMap<>();

    private TableView<E> tableView;

    public ColumnManager(TableView<E> tableView, List<Column> columns) {
        this.tableView = tableView;
        columns.forEach(column ->
                consumerMap.put(
                        column,
                        columnFactory(
                                column.getName(),
                                column.getPrefWidth(),
                                column.getCallBack())));
    }

    public void hideColumn(Column column) {
        tableView.getColumns().remove(consumerMap.get(column));
    }

    public void showColumn(Column column) {
        tableView.getColumns().add(consumerMap.get(column));
    }

    /**
     * Creates a {@link TableColumn} with {@code String} as it´s value type.
     * Sets the data provider through the {@link Callback}, the Name of the Column
     * and the preferred width.
     *
     * @param columnName name of the column
     * @param prefWidth  preferred width of the column
     * @param callback   callback to add to the {@code CelValueFactory} of the  {@code TableColumn}
     * @return column - a complete {@code TableColumn}
     */
    private TableColumn<E, Object> columnFactory(String columnName, double prefWidth,
                                                 Callback<TableColumn.CellDataFeatures<E, Object>, ObservableValue<Object>> callback) {

        TableColumn<E, Object> column = new TableColumn<>();
        column.setPrefWidth(prefWidth);
        column.setMinWidth(40);
        column.setEditable(true);
        column.setText(columnName);
        column.setCellValueFactory(callback);
        return column;
    }
}
