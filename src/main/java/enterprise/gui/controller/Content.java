package enterprise.gui.controller;

import enterprise.ControlComm;
import enterprise.data.Default;
import enterprise.data.EntryCarrier;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.SourceableEntry;
import enterprise.gui.general.BasicMode;
import enterprise.gui.general.ColumnManager;
import enterprise.gui.general.Columns.*;
import enterprise.gui.general.Mode;
import enterprise.misc.EntrySingleton;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
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

/**
 * The basic controller for all controller with {@link BasicMode#CONTENT}.
 * Provides common fields and functionality.
 */
public abstract class Content<E extends CreationEntry> implements Controller {
    private final Logger logger = Default.LOGGER;
    @FXML
    protected HBox addBox;
    @FXML
    protected TextField titleField;
    @FXML
    protected TextField creatorField;
    @FXML
    protected TableView<E> entryTable;
    @FXML
    protected MenuItem fullAdd;
    @FXML
    protected MenuItem edit;
    @FXML
    protected MenuItem delete;
    @FXML
    private SegmentedButton segmentedButtons;
    @FXML
    private VBox paneBox;
    @FXML
    private TableColumn<E, Number> indexColumn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Menu moveToMenu;

    private FilteredList<E> filteredList;

    private ColumnManager<E> columnManager = new ColumnManager<>(getColumnList());

    @Override
    public void initialize() {
        Controller.super.initialize();

        filteredList = (FilteredList<E>) getModule().getEntries().filtered(null);
        entryTable.setItems(filteredList);
    }

    @Override
    public final Mode getMode() {
        return BasicMode.CONTENT;
    }

    public void addList(String list) {
        // TODO: 31.08.2017 do sth if the length of segmentedButtons goes over the edge
        if (paneBox == null) {
            logger.log(Level.SEVERE, "tried add a list while " + this.getClass().getSimpleName() + " was not injected");
        } else {
            getModule().getListNames().add(list);
            segmentedButtons.getButtons().add(toggleButtonFactory(list));
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

    public ColumnManager<E> getColumnManager() {
        if (columnManager == null) {
            columnManager = new ColumnManager<>(getColumnList());
        }
        return columnManager;
    }

    List<Column<E, ?>> getColumnList() {
        List<Column<E, ?>> list = new ArrayList<>();

        list.add(new CommentColumn<>(getModule()));
        list.add(new CreatorNameColumn<>(getModule()));
        list.add(new CreatorSortColumn<>(getModule()));
        list.add(new KeyWordsColumn<>(getModule()));
        list.add(new LastPortionColumn<>(getModule()));
        list.add(new OwnStatusColumn<>(getModule()));
        list.add(new PresentPortionsColumn<>(getModule()));
        list.add(new ProcessedPortionsColumn<>(getModule()));
        list.add(new RatingColumn<>(getModule()));
        list.add(new SeriesColumn<>(getModule()));
        list.add(new TitleColumn<>(getModule()));
        list.add(new WorkStatusColumn<>(getModule()));

        list.forEach(this::setColumn);
        return list;
    }

    /**
     * Adds a {@link CreationEntry} to the {@code entryTable TableView}.
     *
     * @param entry {@code CreationEntry} to add
     */
    public void addEntry(E entry) {
        if (entry != null || entryTable != null) {
            getModule().addEntry(entry);
        } else {
            logger.log(Level.WARNING, "could not add new entry");
        }

    }

    /**
     * Adds the simplified Version of a {@link enterprise.data.intface.CreationEntry}
     * to the {@code entryTable} and the corresponding Module entry list.
     * Simplified meaning, that only the title and creator are provided through user input,
     * while the rest is set to default.
     */
    @FXML
    protected void add() {
        E entry = getSimpleEntry();

        if (getModule().addEntry(entry)) {
            //make it available to add it to the database
            EntryCarrier.getInstance().addNewEntry(entry);

            //clears the TextFields for the next user input
            creatorField.clear();
            titleField.clear();
        } else {
            System.out.println("adding the entry failed");

            //alerts that this entry does already exist
            new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!").show();
        }
    }

    protected abstract E getSimpleEntry();

    /**
     * Disables the 'small add' Button, if
     * {@code titleField} and {@code creatorField} are empty.
     */
    protected void lockAddBtn() {
        addBtn.disableProperty().bind(titleField.textProperty().isEmpty().and(creatorField.textProperty().isEmpty()));
    }

    /**
     * Registers listeners to the rows of the {@code entryTable TableView}.
     * The registered Listener opens a new Window in {@link BasicMode} {@code SHOW} and the corresponding {@link BasicModule}.
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
            ((OpenAble) ControlComm.get().getController(getModule(), BasicMode.EDIT)).open();
            event.consume();
        }
    }

    private void openShowOnDoubleClick(TableRow<E> row, MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !row.isEmpty()) {

            EntrySingleton.getInstance().setEntry(row.getItem());
            ((OpenAble) ControlComm.get().getController(getModule(), BasicMode.SHOW)).open();
            event.consume();
        }
    }

    private ContextMenu tableViewContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem item = menuItemFactory("Kompletten Eintrag hinzufügen", event -> openFullAdd());
        menu.getItems().add(item);
        return menu;
    }

    @FXML
    private void loadLists() {
        for (String s : getModule().getListNames()) {
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

    /**
     * Opens a new Window in {@link BasicMode#EDIT} and the module specified by subclass.
     * Makes the data of the selected row available to the new Window.
     */
    @FXML
    private void openEdit() {
        EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
        ((OpenAble) ControlComm.get().getController(getModule(), BasicMode.EDIT)).open();
    }

    /**
     * Deletes the selected Row from the list of the TableView and the corresponding Module Entry List.
     */
    @FXML
    private void deleteSelected() {
        E entry = entryTable.getSelectionModel().getSelectedItem();
        if (entryTable.getItems() == getModule().getEntries()) {
            deleteEntry(() -> getModule().deleteEntry(entry), entry);
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

    private void initColumnManager() {
        columnManager.setTableView(entryTable);
    }

    private void moveToList(String listName) {
        entryTable.getSelectionModel().getSelectedItem().getUser().setListName(listName);
    }

    private void initSegmentButtons() {
        if (segmentedButtons != null) {
            ToggleGroup group = new ToggleGroup();

            segmentedButtons.setToggleGroup(group);
            ToggleButton standardButton = toggleButtonFactory(Default.LIST);
            segmentedButtons.getButtons().add(standardButton);

            //selects the showAll toggle and shows all entries, if no button (newValue == null) would have been selected
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> deSelect(standardButton, newValue));

            getModule().getListNames().forEach(this::addLists);
            standardButton.setSelected(true);
            paneBox.getChildren().add(0, segmentedButtons);
        }
    }

    private void deSelect(ToggleButton standardButton, Toggle newValue) {
        if (newValue == null) {
            segmentedButtons.getToggleGroup().selectToggle(standardButton);
            Platform.runLater(this::showAllToggle);
            // TODO: 01.09.2017 do sth better?
        }
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
     * Opens a new Window with {@link BasicMode} {@code ADD} and {@link Module}
     * specified by the subclass.
     */
    @FXML
    private void openFullAdd() {
        ((OpenAble) ControlComm.get().getController(getModule(), BasicMode.ADD)).open();
    }

    private void showAllToggle() {
        if (filteredList != null) {
            System.out.println("setting predicate to " + null);
            filteredList.setPredicate(null);
        }
    }

    private void showTargetList(String s) {
        System.out.println("setting predicate to " + s);
        filteredList.setPredicate(e -> e.getUser().getListName().equals(s));
    }

    private void addLists(String s) {
        if (!s.equals(Default.LIST)) {
            segmentedButtons.getButtons().add(toggleButtonFactory(s));
        }
    }

    private void setColumn(Column<E, ?> column) {
        column.setColumnModule(getModule());
        column.loadMenuItem(this);
    }
}
