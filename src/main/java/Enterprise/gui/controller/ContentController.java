package Enterprise.gui.controller;

import Enterprise.ControlComm;
import Enterprise.data.Default;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.ColumnManager;
import Enterprise.gui.general.Columns.*;
import Enterprise.misc.EntrySingleton;
import Enterprise.misc.Log;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.SegmentedButton;
import scrape.sources.posts.PostManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The basic controller for all controller with {@link BasicModes#CONTENT}.
 * Provides common fields and functionality.
 */
public abstract class ContentController<E extends CreationEntry, R extends Enum<R> & Module> extends AbstractController<R, BasicModes> {
    protected Logger logger = Log.packageLogger(this);
    @FXML
    protected SegmentedButton segmentedButtons;
    @FXML
    protected VBox paneBox;
    @FXML
    protected HBox addBox;
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
    @FXML
    protected Button addBtn;
    @FXML
    protected Button editBtn;
    @FXML
    private Menu moveToMenu;
    private ColumnManager<E> columnManager = new ColumnManager<>(getColumnList());

    @Override
    final protected void setMode() {
        mode = BasicModes.CONTENT;
    }

    private void initColumnManager() {
        columnManager.setTableView(entryTable);
    }

    private void initSegmentButtons() {
        if (segmentedButtons != null) {
            segmentedButtons.setToggleGroup(new ToggleGroup());
            ToggleButton standardButton = toggleButtonFactory(Default.LIST);
            segmentedButtons.getButtons().add(standardButton);

            //selects the showAll toggle and shows all entries, if no button (newValue == null) would have been selected
            segmentedButtons.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    segmentedButtons.getToggleGroup().selectToggle(standardButton);
                    Platform.runLater(this::showAllToggle);
                    // TODO: 01.09.2017 do sth better?
                }
            });
            module.getListNames().forEach(s -> {
                if (!s.equals(Default.LIST)) {
                    segmentedButtons.getButtons().add(toggleButtonFactory(s));
                }
            });
            standardButton.setSelected(true);
            showAllToggle();
            paneBox.getChildren().add(0, segmentedButtons);
        }
    }

    private ToggleButton toggleButtonFactory(String s) {
        ToggleButton button = new ToggleButton(s);
        if (s.equals(Default.LIST)) {
            button.setOnMouseClicked(event -> showAllToggle());
        } else {
            button.setOnMouseClicked(event -> showTargetList(s));
        }
        return button;
    }

    private void showTargetList(String s) {
        List<CreationEntry> entries = module.getEntries().
                stream().
                filter(entry -> entry.getUser().getListName().equals(s)).
                collect(Collectors.toList());

        entryTable.setItems((ObservableList<E>) new ObservableListWrapper<>(entries));
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

    private void showAllToggle() {
        List<? extends CreationEntry> entries = module.getEntries();
        entryTable.setItems((ObservableList<E>) entries);
    }

    /**
     * Deletes a row of the {@code entryTable}, if the DELETE key was pressed,
     * while a row was selected.
     * This method makes the deleted row available for deleting.
     *
     * @param event {@code KeyEvent} which will be filtered
     */
    @FXML
    protected void keyHandler(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            deleteSelected();
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

    protected abstract E getSimpleEntry();

    /**
     * Deletes the selected Row from the list of the TableView and the corresponding Module Entry List.
     */
    @FXML
    protected void deleteSelected() {
        E entry = entryTable.getSelectionModel().getSelectedItem();
        if (entryTable.getItems() == module.getEntries()) {
            deleteEntry(() -> module.deleteEntry(entry), entry);
        } else {
            deleteEntry(() -> entryTable.getItems().remove(entry), entry);
            entry.getUser().setListName(Default.LIST);
        }
    }

    private void deleteEntry(BooleanSupplier supplier, E entry) {
        if (supplier.getAsBoolean()) {
            //prevents getting the entry to be added to the database
            entry.setEntryOld();
            System.out.println(entry + " deleted");
            if (entry instanceof SourceableEntry) {
                PostManager.getInstance().removeSearchEntries((SourceableEntry) entry);
            }
        } else {
            logger.log(Level.WARNING, "row could not be deleted"
                    + "TableView: " + entryTable.getItems()
                    + "Entry" + entry);
            System.out.println("Löschen fehlgeschlagen!");
        }
    }

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
                openEditOnClick(row, event);
                openShowOnDoubleClick(row, event);
            });

            row.setOnContextMenuRequested(event -> {
                row.setContextMenu(tableViewContextMenu());
                if (!row.isEmpty()) {
                    moveToMenu = new Menu("Verschieben nach...");
                    row.getContextMenu().getItems().add(moveToMenu);
                    row.getContextMenu().setOnShowing(event1 -> loadLists());

                    // FIXME: 02.09.2017 sometimes shows these items even on empty rows, or does not load moveToMenu items
                    MenuItem edit = menuItemFactory("Bearbeiten...", event1 -> openEdit());
                    MenuItem delete = menuItemFactory("Löschen", event1 -> deleteSelected());

                    row.getContextMenu().getItems().add(edit);
                    row.getContextMenu().getItems().add(delete);
                }
            });
            return row;
        });
    }

    private void openEditOnClick(TableRow<E> row, MouseEvent event) {
        if (event.isControlDown() && event.getButton().equals(MouseButton.PRIMARY) && !row.isEmpty()) {
            EntrySingleton.getInstance().setEntry(row.getItem());
            ControlComm.getInstance().getController(module, BasicModes.EDIT).open();
            event.consume();
        }
    }

    private void openShowOnDoubleClick(TableRow<E> row, MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !row.isEmpty()) {

            EntrySingleton.getInstance().setEntry(row.getItem());
            ControlComm.getInstance().getController(module, BasicModes.SHOW).open();
            event.consume();
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
     * Sets the value of textProperty of several {@link javafx.scene.text.Text}
     * and {@link javafx.scene.control.Label}.
     */
    protected void setGui() {
        initColumnManager();
        initSegmentButtons();
    }

    private ContextMenu tableViewContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem item = menuItemFactory("Kompletten Eintrag hinzufügen", event -> openFullAdd());
        menu.getItems().add(item);
        return menu;
    }

    public void addList(String list) {
        // TODO: 31.08.2017 do sth if the length of segmentedButtons goes over the edge
        if (paneBox == null) {
            logger.log(Level.SEVERE, "tried add a list while " + this.getClass().getSimpleName() + " was not injected");
        } else {
            module.getListNames().add(list);
            segmentedButtons.getButtons().add(toggleButtonFactory(list));
        }
    }

    @FXML
    private void loadLists() {
        for (String s : module.getListNames()) {
            MenuItem item = menuItemFactory(s, event -> moveToList(s));

            if (moveToMenu.getItems().stream().noneMatch(menuItem -> menuItem.getText().equals(item.getText()))) {
                moveToMenu.getItems().add(item);
            }
        }
    }

    private MenuItem menuItemFactory(String text, EventHandler<ActionEvent> handler) {
        MenuItem item = new MenuItem();
        item.setText(text);
        item.setOnAction(handler);
        return item;
    }

    private void moveToList(String listName) {
        entryTable.getSelectionModel().getSelectedItem().getUser().setListName(listName);
    }

    public ColumnManager<E> getColumnManager() {
        if (columnManager == null) {
            columnManager = new ColumnManager<>(getColumnList());
        }
        return columnManager;
    }

    public Module getModule() {
        return module;
    }

    protected List<Column<E, ?>> getColumnList() {
        List<Column<E, ?>> list = new ArrayList<>();

        list.add(new CommentColumn<>(module));
        list.add(new CreatorNameColumn<>(module));
        list.add(new CreatorSortColumn<>(module));
        list.add(new KeyWordsColumn<>(module));
        list.add(new LastPortionColumn<>(module));
        list.add(new OwnStatusColumn<>(module));
        list.add(new PresentPortionsColumn<>(module));
        list.add(new ProcessedPortionsColumn<>(module));
        list.add(new RatingColumn<>(module));
        list.add(new SeriesColumn<>(module));
        list.add(new TitleColumn<>(module));
        list.add(new WorkStatusColumn<>(module));

        list.forEach(this::setColumn);
        return list;
    }

    protected void setColumn(Column<E, ?> column) {
        column.setColumnModule(module);
        column.loadMenuItem(this);
    }
}
