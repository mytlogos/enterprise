package Enterprise.gui.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.Columns;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The basic controller for all controller with {@link BasicModes#CONTENT}.
 * Provides common fields and functionality.
 */
public abstract class ModuleController<E extends CreationEntry, R extends Enum<R> & Module> extends AbstractController<R, BasicModes> {

    @FXML
    protected HBox addBox;

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

    @FXML
    protected TextField titleField;
    @FXML
    protected TextField creatorField;
    @FXML
    protected TableView<E> entryTable;

    @FXML
    protected TableColumn<E, Number> indexColumn;
    @FXML
    protected MenuItem fullAdd;
    @FXML
    protected MenuItem edit;
    @FXML
    protected MenuItem delete;
    private TableColumn<E, String> titleColumn;
    private TableColumn<E, String> creatorNameColumn;
    private TableColumn<E, Number> presentColumn;
    private TableColumn<E, Number> processedColumn;
    private TableColumn<E, String> ownStatusColumn;
    private TableColumn<E, String> seriesColumn;
    private TableColumn<E, String> lastEpColumn;
    private TableColumn<E, Number> ratingColumn;
    private TableColumn<E, String> creatorSortNameColumn;

    @FXML
    protected Button addBtn;
    @FXML
    protected Button editBtn;
    private TableColumn<E, String> workStatColumn;
    private TableColumn<E, String> commentColumn;
    private TableColumn<E, String> keyWordsColumn;

    @Override
    final protected void setMode() {
        mode = BasicModes.CONTENT;
    }

    /**
     * Loads all {@link CreationEntry}s from the specific {@link Module}.
     */
    protected void loadData() {
        Platform.runLater(() -> module.getEntries().forEach(entry -> entryTable.getItems().add((E) entry)));
    }

    /**
     * Adds the keyWords column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showKeyWordsColumn() {
        keyWordsColumn = stringColumnFactory(Columns.getKeyWords(module), 80,
                data -> data.getValue().getUser().keyWordsProperty());
        //'shows' the column in the TableView
        entryTable.getColumns().add(keyWordsColumn);
    }

    /**
     * Removes the keyWords Column from the {@code entryTable TableView}.
     */
    public void hideKeyWordsColumn() {
        entryTable.getColumns().remove(keyWordsColumn);
    }

    /**
     * Opens a new Window with {@link BasicModes} {@code ADD} and {@link Module}
     * specified by the subclass.
     */
    @FXML
    protected void openFullAdd() {
        ControlComm.getInstance().getController(module, BasicModes.ADD).open();
    }

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
     * Adds the simplified Version of a {@link Enterprise.data.intface.CreationEntry}
     * to the {@code entryTable} and the corresponding Module entry list.
     * Simplified meaning, that only the title and creator are provided through user input,
     * while the rest is set to default.
     */
    @FXML
    protected void add() {
        E entry = getSimpleEntry();

        if (module.addEntry(entry)) {
            //make it available to add it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);
            addEntry(entry);

            //clears the TextFields for the next user input
            creatorField.clear();
            titleField.clear();
        } else {
            System.out.println("adding the entry failed");

            //alerts that this entry does already exist
            new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!").show();
        }
    }

    /**
     * Deletes the selected Row from the list of the TableView and the corresponding Module Entry List.
     */
    @FXML
    protected void deleteSelected() {
        E entry = entryTable.getSelectionModel().getSelectedItem();

        if (entryTable.getItems().remove(entry) && module.deleteEntry(entry)) {
            System.out.println(entry + " deleted");

        } else {
            logger.log(Level.WARNING, "row could not be deleted"
                    + "TableView: " + entryTable.getItems()
                    + "Entry" + entry);
            System.out.println("Löschen fehlgeschlagen!");
        }
    }

    /**
     * Disables the 'small add' Button, if
     * {@code titleField} and {@code creatorField} are empty.
     */
    protected void lockAddBtn() {
        addBtn.disableProperty().bind(titleField.textProperty().isEmpty().and(creatorField.textProperty().isEmpty()));
    }

    /**
     * Opens a new Window in {@link BasicModes#EDIT} and the module specified by subclass.
     * Makes the data of the selected row available to the new Window.
     */
    @FXML
    protected void openEdit() {
        EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
        ControlComm.getInstance().getController(module, BasicModes.EDIT).open();
    }

    /**
     * Deletes a row of the {@code entryTable}, if the DELETE key was pressed,
     * while a row was selected.
     * This method makes the deleted row available for deleting.
     *
     * @param event {@code KeyEvent} which will be filtered
     */
    @FXML
    protected void deleteRow(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {

            E entry = entryTable.getSelectionModel().getSelectedItem();
            // TODO: 30.08.2017 make list of BasicModules the underlying list
            if (entryTable.getItems().remove(entry) && module.deleteEntry(entry)) {
                System.out.println(entry + " deleted");
            } else {
                System.out.println("Löschen fehlgeschlagen!");
            }
        }
    }

    protected abstract E getSimpleEntry();

    /**
     * Registers listeners to the rows of the {@code entryTable TableView}.
     * The registered Listener opens a new Window in {@link BasicModes} {@code SHOW} and the corresponding {@link BasicModules}.
     * <p>
     * Makes the data of the selected row available to the new Window.
     * </p>
     */
    protected void setRowListener() {
        entryTable.setRowFactory(tv -> {
            TableRow<E> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (!row.isEmpty())) {

                    EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
                    ControlComm.getInstance().getController(module, BasicModes.SHOW).open();

                }

            });
            return row;
        });
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
     * Sets the value of textProperty of several {@link javafx.scene.text.Text}
     * and {@link javafx.scene.control.Label}.
     */
    protected abstract void setGui();

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
    private TableColumn<E, Number> numberColumnFactory(
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
    TableColumn<E, String> stringColumnFactory(String columnName, double prefWidth, Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> callback) {
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
    public void showTitleColumn() {
        titleColumn = stringColumnFactory(Columns.getTitle(module), 210, data -> data.getValue().getCreation().titleProperty());
        entryTable.getColumns().add(titleColumn);
    }

    /**
     * Adds the creator column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showCreatorColumn() {
        creatorNameColumn = stringColumnFactory(Columns.getCreatorName(module), 80, data -> data.getValue().getCreator().nameProperty());
        entryTable.getColumns().add(creatorNameColumn);
    }

    /**
     * Adds the present column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public void showPresentColumn() {
        presentColumn = numberColumnFactory(Columns.getNumPortion(module), 120, data -> data.getValue().getCreation().numPortionProperty());
        entryTable.getColumns().add(presentColumn);
    }

    /**
     * Adds the processed column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public void showProcessedColumn() {
        processedColumn = numberColumnFactory(Columns.getProcessed(module), 125, data -> data.getValue().getUser().processedPortionProperty());
        entryTable.getColumns().add(processedColumn);
    }

    /**
     * Adds the ownStatus column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showOwnStatusColumn() {
        ownStatusColumn = stringColumnFactory(Columns.getOwnStat(module), 80, data -> data.getValue().getUser().ownStatusProperty());
        entryTable.getColumns().add(ownStatusColumn);
    }

    /**
     * Adds the series column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showSeriesColumn() {
        seriesColumn = stringColumnFactory(Columns.getSeries(module), 80, data -> data.getValue().getCreation().seriesProperty());
        entryTable.getColumns().add(seriesColumn);
    }

    /**
     * Adds the lastEp column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showLastPortionColumn() {
        lastEpColumn = stringColumnFactory(Columns.getLastPortion(module), 80, data -> data.getValue().getCreation().dateLastPortionProperty());
        entryTable.getColumns().add(lastEpColumn);
    }

    /**
     * Adds the rating column to the {@code entryTable TableView}
     * after going through the {@link #numberColumnFactory(String, double, Callback)}.
     */
    public void showRatingColumn() {
        ratingColumn = numberColumnFactory(Columns.getRating(module), 80, data -> data.getValue().getUser().ratingProperty());
        entryTable.getColumns().add(ratingColumn);
    }


    /**
     * Adds the creatorSort column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showCreatorSortColumn() {
        creatorSortNameColumn = stringColumnFactory(Columns.getCreatorSort(module), 80, data -> data.getValue().getCreator().sortNameProperty());
        entryTable.getColumns().add(creatorSortNameColumn);
    }

    /**
     * Adds the workStat column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showWorkStatColumn() {
        workStatColumn = stringColumnFactory(Columns.getWorkStat(module), 80, data -> data.getValue().getCreation().workStatusProperty());
        entryTable.getColumns().add(workStatColumn);
    }

    /**
     * Adds the comment column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showCommentColumn() {
        commentColumn = stringColumnFactory(Columns.getComment(module), 80, data -> data.getValue().getUser().commentProperty());
        entryTable.getColumns().add(commentColumn);
    }

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
