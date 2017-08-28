package Enterprise.gui.anime.controller;

import Enterprise.ControlComm;
import Enterprise.data.*;
import Enterprise.data.impl.*;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.Columns;
import Enterprise.gui.general.Mode;
import Enterprise.gui.controller.SourceableModuleCont;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.Anime;
import Enterprise.modules.Module;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * This class is the Controller of {@code anime.fxml} in mode {@link Mode#CONTENT}.
 */
public class AnimeController extends SourceableModuleCont<SourceableEntry, Anime> implements Initializable{
    @FXML
    private HBox addBox;

    @FXML
    private MenuItem fullAdd;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem delete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setController(this);

        //readies the graphical interface components
        setIndexColumn();
        //adjusts the size of the columns to fill the size of the table
        entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setModuleEntry();
        loadData();
        setGui();
        setRowListener();
        lockEditBtn();
        lockAddBtn();
    }

    /**
     * Disables the 'small add' Button, if
     * {@code titleField} and {@code creatorField} are empty.
     */
    private void lockAddBtn() {
        addBtn.disableProperty().bind(titleField.textProperty().isEmpty().and(creatorField.textProperty().isEmpty()));
    }

    /**
     * Deletes the selected Row from the list of the TableView and the corresponding Module Entry List.
     */
    @FXML
    protected void deleteSelected() {
        SourceableEntry entry = entryTable.getSelectionModel().getSelectedItem();
        if (entryTable.getItems().remove(entry) && Anime.getInstance().deleteEntry(entry)) {
            System.out.println(entry + " deleted");
        }else {
            logger.log(Level.WARNING, "row could not be deleted"
                    + "TableView: " + entryTable.getItems()
                    + "Entry" + entry);
            System.out.println("Löschen fehlgeschlagen!");
        }
    }

    /**
     * Opens a new Window with {@link Mode} {@code ADD} and {@link Module} {@code ANIME}.
     */
    @FXML
    protected void openFullAdd() {
        ControlComm.getInstance().getController(Module.ANIME, Mode.ADD).open();
    }

    /**
     * Adds the simplified Version of a {@link Enterprise.data.intface.CreationEntry}
     * to the {@code entryTable} and the corresponding Module entry list.
     * Simplified meaning, that only the title and creator are provided through user input,
     * while the rest is set to default.
     */
    @FXML
    protected void add() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new SimpleCreator(author);
        Creation creation = new SimpleCreation(title);

        SourceableEntryImpl entry = new SourceableEntryImpl(new SimpleUser(), creation, creator, new SimpleSourceable(),Module.ANIME);

        if (Anime.getInstance().addEntry(entry)) {
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
     * Deletes a row of the {@code entryTable}, if the DELETE key was pressed,
     * while a row was selected.
     * This method makes the deleted row available for deleting.
     *
     * @param event {@code KeyEvent} which will be filtered
     */
    @FXML
    protected void deleteRow(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {

            SourceableEntry entry = entryTable.getSelectionModel().getSelectedItem();
            if (entryTable.getItems().remove(entry) && Anime.getInstance().deleteEntry(entry)) {
                System.out.println(entry + " deleted");
            }else {
                System.out.println("Löschen fehlgeschlagen!");
            }
        }
    }

    /**
     * Opens a new Window in {@link Mode} {@code EDIT} and {@link Module} {@code ANIME}.
     * Makes the data of the selected row available to the new Window.
     */
    @FXML
    protected void openEdit() {
        EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
        ControlComm.getInstance().getController(Module.ANIME, Mode.EDIT).open();
    }

    @Override
    protected void setRowListener() {
        entryTable.setRowFactory(tv -> {
            TableRow<SourceableEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (!row.isEmpty())) {
                    EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
                    ControlComm.getInstance().getController(Module.ANIME, Mode.SHOW).open();
                }

            });
            return row;
        });
    }

    @Override
    protected void setGui() {
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }

    @Override
    protected void loadData() {
        Platform.runLater(() -> Anime.getInstance().getEntries().forEach(entry -> entryTable.getItems().add(entry)));
    }

    @Override
    public void showTranslatorColumn() {
        translatorColumn = stringColumnFactory(Columns.getTranslator(Module.ANIME), 80,
                data -> data.getValue().getSourceable().translatorProperty());
        //'shows' the column in the TableView
        entryTable.getColumns().add(translatorColumn);
    }

    @Override
    public void showKeyWordsColumn() {
        keyWordsColumn = stringColumnFactory(Columns.getKeyWords(Module.ANIME), 80,
                data -> data.getValue().getUser().keyWordsProperty());
        //'shows' the column in the TableView
        entryTable.getColumns().add(keyWordsColumn);
    }

    @Override
    public void showTitleColumn() {
        titleColumn = stringColumnFactory(Columns.getTitle(Module.ANIME), 210, data -> data.getValue().getCreation().titleProperty());
        entryTable.getColumns().add(titleColumn);
    }

    @Override
    public void showCreatorColumn() {
        creatorNameColumn = stringColumnFactory(Columns.getCreatorName(Module.ANIME),80, data -> data.getValue().getCreator().nameProperty());
        entryTable.getColumns().add(creatorNameColumn);
    }

    @Override
    public void showPresentColumn() {
        presentColumn = numberColumnFactory(Columns.getNumPortion(Module.ANIME), 120, data -> data.getValue().getCreation().numPortionProperty());
        entryTable.getColumns().add(presentColumn);
    }

    @Override
    public void showProcessedColumn() {
        processedColumn = numberColumnFactory(Columns.getProcessed(Module.ANIME),125, data-> data.getValue().getUser().processedPortionProperty());
        entryTable.getColumns().add(processedColumn);
    }

    @Override
    public void showOwnStatusColumn() {
        ownStatusColumn = stringColumnFactory(Columns.getOwnStat(Module.ANIME), 80, data -> data.getValue().getUser().ownStatusProperty());
        entryTable.getColumns().add(ownStatusColumn);
    }

    @Override
    public void showSeriesColumn() {
        seriesColumn = stringColumnFactory(Columns.getSeries(Module.ANIME), 80, data -> data.getValue().getCreation().seriesProperty());
        entryTable.getColumns().add(seriesColumn);
    }

    @Override
    public void showLastPortionColumn() {
        lastEpColumn = stringColumnFactory(Columns.getLastPortion(Module.ANIME), 80, data -> data.getValue().getCreation().dateLastPortionProperty());
        entryTable.getColumns().add(lastEpColumn);
    }

    @Override
    public void showRatingColumn() {
        ratingColumn = numberColumnFactory(Columns.getRating(Module.ANIME), 80, data -> data.getValue().getUser().ratingProperty());
        entryTable.getColumns().add(ratingColumn);
    }

    @Override
    public void showCreatorSortColumn() {
        creatorSortNameColumn = stringColumnFactory(Columns.getCreatorSort(Module.ANIME), 80, data -> data.getValue().getCreator().sortNameProperty());
        entryTable.getColumns().add(creatorSortNameColumn);
    }

    @Override
    public void showWorkStatColumn() {
        workStatColumn = stringColumnFactory(Columns.getWorkStat(Module.ANIME), 80, data -> data.getValue().getCreation().workStatusProperty());
        entryTable.getColumns().add(workStatColumn);
    }

    @Override
    public void showCommentColumn() {
        commentColumn = stringColumnFactory(Columns.getComment(Module.ANIME), 80, data -> data.getValue().getUser().commentProperty());
        entryTable.getColumns().add(commentColumn);
    }


    @Override @Deprecated
    public void open() {
        throw new IllegalAccessError();
    }

    @Override
    public void paneFocus() {

    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Anime.getInstance();
    }

    //methods for theoretical auto fit of the columns to their content
    private static Method columnToFitMethod;

    static {
        try {
            columnToFitMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
            columnToFitMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //copied Method for auto-Fitting the Widths of the Columns to their Content
    private void autoFitTable(TableView<?> tableView) {
        tableView.getItems().addListener((ListChangeListener<Object>) c -> {
            for (javafx.event.EventTarget column : tableView.getColumns()) {
                try {
                    columnToFitMethod.invoke(tableView.getSkin(), column, -1);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
