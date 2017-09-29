package Enterprise.gui.general;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Should create column with settings specified by the {@link Column}.
 * Maps the {@code Column} and {@code TableColumn}, shows and hides the
 * {@code TableColumn} from the user when calling {@link #showColumn(Column)},
 * {@link #hideColumn(Column)}.
 * <p>
 */
public class ColumnManager<E> {
    private final Map<Column<E>, TableColumn<E, ?>> columnMap = new HashMap<>();
    private final TableView<E> tableView;

    public ColumnManager(TableView<E> tableView, List<Column<E>> columns) {
        this.tableView = tableView;
        columns.forEach((Column<E> column) -> columnMap.put(
                column,
                columnFactory(
                        column.getName(),
                        column.getPrefWidth(),
                        column.getCallBack())));
    }


    public void hideColumn(Column column) {
        tableView.getColumns().remove(columnMap.get(column));
    }

    public void showColumn(Column column) {
        TableColumn<E, ?> tableColumn = columnMap.get(column);
        if (!tableView.getColumns().contains(tableColumn)) {
            tableView.getColumns().add(tableColumn);
        }
    }

    public Set<Column<E>> getColumns() {
        return columnMap.keySet();
    }

    /**
     * Creates a {@link TableColumn} with {@code String} as it´s value type.
     * Sets the data provider through the {@link Callback}, the Name of the Column
     * and the preferred width.
     *
     * @param columnName name of the column
     * @param callback the data provider callback
     * @param prefWidth  preferred width of the column
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
