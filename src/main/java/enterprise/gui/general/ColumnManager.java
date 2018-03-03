package enterprise.gui.general;

import enterprise.gui.contextMenu.ContextMenuManager;
import enterprise.gui.general.Columns.Column;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.*;

/**
 * Should create column with settings specified by the {@link Column}.
 * Maps the {@code Column} and {@code TableColumn}, shows and hides the
 * {@code TableColumn} from the user when calling {@link #showColumn(Column)},
 * {@link #hideColumn(Column)}.
 * <p>
 */
public class ColumnManager<E> {
    private final Map<Column<E, ?>, TableColumn<E, ?>> columnMap = new TreeMap<>();
    private final ContextMenu contextMenu = ContextMenuManager.getInstance().getTempMenu();
    private TableView<E> tableView;
    private int counter = 0;

    public ColumnManager(List<Column<E, ?>> columns) {
        columns.sort(Comparator.comparingInt(Column::getPrefIndex));
        contextMenu.getItems().add(0, new SeparatorMenuItem());
        columns.forEach(this::init);
    }

    public void setTableView(TableView<E> tableView) {
        Objects.requireNonNull(tableView);
        this.tableView = tableView;
        this.columnMap.keySet().forEach(column -> {
            if (column.getDefaultSelect()) {
                showColumn(column);
            }
            column.addColumnsListener(tableView);
        });
        listen();
    }

    public void showColumn(Column<E, ?> column) {
        TableColumn<E, ?> tableColumn = getTableColumn(column);

        if (!tableView.getColumns().contains(tableColumn) && tableColumn != null) {
            int index = column.getPrefIndex();
            // TODO: 01.10.2017 sort the columns according to the prefIndices
            if (tableView.getColumns().size() >= index) {
                tableView.getColumns().add(column.getPrefIndex(), tableColumn);
            } else {
                tableView.getColumns().add(tableColumn);
            }
            column.setShown(true);
        }
    }

    private void listen() {
        if (tableView != null) {
            tableView.getColumns().addListener((ListChangeListener<? super TableColumn<E, ?>>) observable -> {
                while (observable.next()) {
                    if (observable.wasAdded()) {
                        System.out.println("column added ");
                        observable.getAddedSubList().forEach(column -> System.out.println(column.getText()));
                    }
                    if (observable.wasRemoved()) {
                        System.out.println("column removed");
                        observable.getRemoved().forEach(column -> System.out.println(column.getText()));
                    }
                    if (observable.wasPermutated()) {
                        System.out.println("column permutated ");
                    }
                    if (observable.wasReplaced()) {
                        System.out.println("column replaced ");
                    }
                    if (observable.wasUpdated()) {
                        System.out.println("column updated");

                    }
                }
            });
        }
    }

    private TableColumn<E, ?> getTableColumn(Column<E, ?> column) {
        return columnMap.get(column);
    }

    public void hideColumn(Column<E, ?> column) {
        TableColumn<E, ?> tableColumn = getTableColumn(column);
        tableView.getColumns().remove(tableColumn);
        column.setShown(false);
    }

    public List<Column<E, ?>> getColumns() {
        List<Column<E, ?>> columns = new ArrayList<>(columnMap.keySet());
        columns.sort(Comparator.comparingInt(Column::getPrefIndex));
        return columns;
    }

    private void init(Column<E, ?> column) {
        TableColumn<E, ?> value = column.getTableColumn();
        columnMap.put(column, value);
        value.setContextMenu(contextMenu);
        contextMenu.getItems().add(counter, column.getMenuItem());
        counter++;
    }

}
