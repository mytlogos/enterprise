package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.data.*;
import Enterprise.data.impl.*;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableModuleCont;
import Enterprise.gui.general.*;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.Module;
import Enterprise.modules.Novel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dominik on 24.06.2017.
 * Part of OgameBot.
 */
public class NovelController extends SourceableModuleCont<SourceableEntry,Novel> implements Initializable {
    @FXML
    private HBox addBox;

    @FXML
    private MenuItem fullAdd;

    @FXML
    private MenuItem edit;

    @FXML
    private MenuItem delete;

    @FXML
    protected void deleteSelected() {
        SourceableEntry entry = entryTable.getSelectionModel().getSelectedItem();
        if (entryTable.getItems().remove(entry) && moduleEntry.deleteEntry(entry)) {
            System.out.println(entry + " deleted");
        }else {
            System.out.println("Löschen fehlgeschlagen!");
        }
    }

    @FXML
    protected void openFullAdd() {
            ControlComm.getInstance().getController(Module.NOVEL, Mode.ADD).open();
    }

    @FXML
    protected void add() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator simpleCreator = new SimpleCreator(author);
        Creation simpleCreation = new SimpleCreation(title);

        SourceableEntryImpl entry = new SourceableEntryImpl(new SimpleUser(), simpleCreation, simpleCreator, new SimpleSourceable(),Module.NOVEL);

        if (moduleEntry.addEntry(entry)) {
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

    @FXML
    protected void deleteRow(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            SourceableEntry entry = entryTable.getSelectionModel().getSelectedItem();

            if (entryTable.getItems().remove(entry) && moduleEntry.deleteEntry(entry)) {
                System.out.println(entry + " deleted");
            }else {
                System.out.println("Löschen fehlgeschlagen!");
            }
        }
    }

    @FXML
    protected void openEdit() {
        EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
        ControlComm.getInstance().getController(Module.NOVEL, Mode.EDIT).open();
    }

    @Override
    protected void setRowListener() {
        entryTable.setRowFactory(tv -> {
            TableRow<SourceableEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (!row.isEmpty())) {

                    EntrySingleton.getInstance().setEntry(entryTable.getSelectionModel().getSelectedItem());
                    ControlComm.getInstance().getController(Module.NOVEL, Mode.SHOW).open();
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
        Platform.runLater(()-> Novel.getInstance().getEntries().forEach(entry -> entryTable.getItems().add(entry)));
    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Novel.getInstance();
    }

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
    }

    @Override
    public void open() {
        throw new IllegalAccessError();
    }

    @Override
    public void paneFocus() {
// TODO: 25.08.2017 do sth
    }

    @Override
    public void showTitleColumn() {
        titleColumn = stringColumnFactory(Columns.getTitle(Module.NOVEL), 210,
                data -> data.getValue().getCreation().titleProperty());

        entryTable.getColumns().add(titleColumn);
    }

    @Override
    public void showCreatorColumn() {
        creatorNameColumn = stringColumnFactory(Columns.getCreatorName(Module.NOVEL),80,
                data -> data.getValue().getCreator().nameProperty());

        entryTable.getColumns().add(creatorNameColumn);
    }

    @Override
    public void showPresentColumn() {
        presentColumn = numberColumnFactory(Columns.getNumPortion(Module.NOVEL), 120,
                data -> data.getValue().getCreation().numPortionProperty());

        entryTable.getColumns().add(presentColumn);
    }

    @Override
    public void showProcessedColumn() {
        processedColumn = numberColumnFactory(Columns.getProcessed(Module.NOVEL),125,
                data-> data.getValue().getUser().processedPortionProperty());

        entryTable.getColumns().add(processedColumn);
    }

    @Override
    public void showOwnStatusColumn() {
        ownStatusColumn = stringColumnFactory(Columns.getOwnStat(Module.NOVEL), 80,
                data -> data.getValue().getUser().ownStatusProperty());

        entryTable.getColumns().add(ownStatusColumn);
    }

    @Override
    public void showSeriesColumn() {
        seriesColumn = stringColumnFactory(Columns.getSeries(Module.NOVEL), 80,
                data -> data.getValue().getCreation().seriesProperty());

        entryTable.getColumns().add(seriesColumn);
    }

    @Override
    public void showLastPortionColumn() {
        lastEpColumn = stringColumnFactory(Columns.getLastPortion(Module.NOVEL), 80,
                data -> data.getValue().getCreation().dateLastPortionProperty());

        entryTable.getColumns().add(lastEpColumn);
    }

    @Override
    public void showRatingColumn() {
        ratingColumn = numberColumnFactory(Columns.getRating(Module.NOVEL), 80,
                data -> data.getValue().getUser().ratingProperty());

        entryTable.getColumns().add(ratingColumn);
    }

    @Override
    public void showCreatorSortColumn() {
        creatorSortNameColumn = stringColumnFactory(Columns.getCreatorSort(Module.NOVEL), 80,
                data -> data.getValue().getCreator().sortNameProperty());

        entryTable.getColumns().add(creatorSortNameColumn);
    }

    @Override
    public void showWorkStatColumn() {
        workStatColumn = stringColumnFactory(Columns.getWorkStat(Module.NOVEL), 80,
                data -> data.getValue().getCreation().workStatusProperty());

        entryTable.getColumns().add(workStatColumn);
    }

    @Override
    public void showCommentColumn() {
        commentColumn = stringColumnFactory(Columns.getComment(Module.NOVEL), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(commentColumn);
    }

    @Override
    public void showKeyWordsColumn() {
        keyWordsColumn = stringColumnFactory(Columns.getKeyWords(Module.NOVEL), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(keyWordsColumn);
    }

    @Override
    public void showTranslatorColumn() {
        translatorColumn = stringColumnFactory(Columns.getTranslator(Module.NOVEL), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(translatorColumn);
    }
}
