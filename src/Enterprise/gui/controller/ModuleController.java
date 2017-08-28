package Enterprise.gui.controller;

import Enterprise.gui.general.Mode;
import Enterprise.modules.EnterpriseSegments;
import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The basic controller for all controller with {@link Mode#CONTENT}.
 * Provides common fields and functionality.
 */
public abstract class ModuleController<E extends CreationEntry, R extends EnterpriseSegments> implements Controller {

    protected Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    {
        try {
            //creates a FileHandler for this Package and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + this.getClass().getPackage().getName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    protected R moduleEntry;

    @FXML
    protected TextField titleField;
    @FXML
    protected TextField creatorField;
    @FXML
    protected TableView<E> entryTable;

    @FXML
    protected TableColumn<E, Number> indexColumn;

    protected TableColumn<E, String> titleColumn;

    protected TableColumn<E, String> creatorNameColumn;

    protected TableColumn<E, Number> presentColumn;

    protected TableColumn<E, Number> processedColumn;

    protected TableColumn<E, String> ownStatusColumn;

    protected TableColumn<E, String> seriesColumn;

    protected TableColumn<E, String> lastEpColumn;

    protected TableColumn<E, Number> ratingColumn;

    protected TableColumn<E, String> creatorSortNameColumn;

    protected TableColumn<E, String> workStatColumn;

    protected TableColumn<E, String> commentColumn;

    @FXML
    protected Button addBtn;
    @FXML
    protected Button editBtn;

    /**
     * Adds a {@link CreationEntry} to the {@code entryTable TableView}.
     *
     * @param entry {@code CreationEntry} to add
     */
    public void addEntry(E entry) {
        if (entry != null || entryTable != null) {
            entryTable.getItems().add(entry);
        } else {
            logger.log(Level.SEVERE, "error while adding a new entry; entryTable: "
                                            + entryTable + "; entry: " + entry);
        }

    }

    /**
     * Disables the {@code editBtn} if no item is selected in the {@code entryTable TableView}.
     */
    protected void lockEditBtn() {
        editBtn.disableProperty().bind(entryTable.getSelectionModel().selectedItemProperty().isNull());
    }

    /**
     * Sets the IndexColumn to increment the value by one, to match
     * the position of the row in the tableView.
     */
    protected void setIndexColumn() {
        indexColumn.setCellValueFactory((TableColumn.CellDataFeatures<E, Number> p)
                -> new ReadOnlyObjectWrapper<>((entryTable.getItems().indexOf(p.getValue()) + 1)));
    }

    /**
     * Clears all columns except the {@code indexColumn} in the {@code entryTable}.
     */
    public void clearColumns() {
        entryTable.getColumns().remove(1, entryTable.getColumns().size());
    }

    /**
     * Registers listeners to the rows of the {@code entryTable TableView}.
     * The registered Listener opens a new Window in {@link Mode} {@code SHOW} and the corresponding {@link Module}.
     * <p>
     * Makes the data of the selected row available to the new Window.
     * </p>
     */
    protected abstract void setRowListener();

    /**
     * Sets the value of textProperty of several {@link javafx.scene.text.Text}
     * and {@link javafx.scene.control.Label}.
     */
    protected abstract void setGui();

    /**
     * Loads all {@link CreationEntry}s from the specific subclass of
     * {@link EnterpriseSegments}.
     */
    protected abstract void loadData();

    /**
     * Creates a {@link TableColumn} with {@code Number} as it´s value type.
     * Sets the data provider through the {@link Callback}, the Name of the Column
     * and the preferred width.
     *
     * @param columnName name of the column
     * @param prefWidth preferred width of the column
     * @param callback callback to add to the {@code CelValueFactory} of the  {@code TableColumn}
     * @return column - a complete {@code TableColumn}
     */
    protected TableColumn<E, Number> numberColumnFactory(
            String columnName, double prefWidth,
            Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> callback) {

        TableColumn<E, Number> column = new TableColumn<>();
        column.setPrefWidth(prefWidth);
        column.setMinWidth(40);
        column.setEditable(true);
        column.setText(columnName);
        column.setCellValueFactory(callback);
        return column;
    }

    /**
     * Creates a {@link TableColumn} with {@code String} as it´s value type.
     * Sets the data provider through the {@link Callback}, the Name of the Column
     * and the preferred width.
     *
     * @param columnName name of the column
     * @param prefWidth preferred width of the column
     * @param callback callback to add to the {@code CelValueFactory} of the  {@code TableColumn}
     * @return column - a complete {@code TableColumn}
     */
    protected TableColumn<E, String> stringColumnFactory(String columnName, double prefWidth, Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> callback) {
        TableColumn<E, String> column = new TableColumn<>();
        column.setPrefWidth(prefWidth);
        column.setMinWidth(40);
        column.setEditable(true);
        column.setText(columnName);
        column.setCellValueFactory(callback);
        return column;
    }

    /**
     * Adds the title column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showTitleColumn();

    /**
     * Adds the creator column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showCreatorColumn();

    /**
     * Adds the present column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public abstract void showPresentColumn();

    /**
     * Adds the processed column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public abstract void showProcessedColumn();

    /**
     * Adds the ownStatus column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showOwnStatusColumn();

    /**
     * Adds the series column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showSeriesColumn();

    /**
     * Adds the lastEp column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showLastPortionColumn();

    /**
     * Adds the rating column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public abstract void showRatingColumn();

    /**
     * Adds the creatorSort column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showCreatorSortColumn();

    /**
     * Adds the workStat column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showWorkStatColumn();

    /**
     * Adds the comment column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showCommentColumn();

    /**
     * Removes the title Column from the {@code entryTable TableView}.
     */
    public void hideTitleColumn() {
        entryTable.getColumns().remove(titleColumn);
    }

    /**
     * Removes the series Column from the {@code entryTable TableView}.
     */
    public void hideSeriesColumn() {
        entryTable.getColumns().remove(seriesColumn);
    }

    /**
     * Removes the lastEp Column from the {@code entryTable TableView}.
     */
    public void hideLastPortionColumn() {
        entryTable.getColumns().remove(lastEpColumn);
    }

    /**
     * Removes the present Column from the {@code entryTable TableView}.
     */
    public void hidePresentColumn() {
        entryTable.getColumns().remove(presentColumn);
    }

    /**
     * Removes the processed Column from the {@code entryTable TableView}.
     */
    public void hideProcessedColumn() {
        entryTable.getColumns().remove(processedColumn);
    }

    /**
     * Removes the rating Column from the {@code entryTable TableView}.
     */
    public void hideRatingColumn() {
        entryTable.getColumns().remove(ratingColumn);
    }

    /**
     * Removes the creator Column from the {@code entryTable TableView}.
     */
    public void hideCreatorColumn() {
        entryTable.getColumns().remove(creatorNameColumn);
    }

    /**
     * Removes the creatorSort Column from the {@code entryTable TableView}.
     */
    public void hideCreatorSortColumn() {
        entryTable.getColumns().remove(creatorSortNameColumn);
    }

    /**
     * Removes the workStat Column from the {@code entryTable TableView}.
     */
    public void hideWorkStatColumn() {
        entryTable.getColumns().remove(workStatColumn);
    }

    /**
     * Removes the ownStatus Column from the {@code entryTable TableView}.
     */
    public void hideOwnStatusColumn() {
        entryTable.getColumns().remove(ownStatusColumn);
    }

    /**
     * Removes the comment Column from the {@code entryTable TableView}.
     */
    public void hideCommentColumn() {
        entryTable.getColumns().remove(commentColumn);
    }
}
