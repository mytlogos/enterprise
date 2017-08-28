package Enterprise.gui.book.controller;

import Enterprise.gui.general.Columns;
import Enterprise.modules.Book;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.controller.ModuleController;
import Enterprise.modules.Module;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

/**
 * Created by Dominik on 24.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class BookController extends ModuleController<CreationEntry,Book>{
    @FXML
    protected void deleteRow(KeyEvent event) {

    }

    @FXML
    protected void openFullAdd() {

    }

    @FXML
    protected void deleteSelected() {

    }


    protected void add() {

    }

    @FXML
    protected void openEdit() {

    }

    @Override
    protected void setRowListener() {

    }

    @Override
    protected void setGui() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void open() {

    }

    @Override
    public void paneFocus() {

    }

    @Override
    public void setModuleEntry() {

    }

    public void showTitleColumn() {
        titleColumn = stringColumnFactory(Columns.getTitle(Module.BOOK), 210, data -> data.getValue().getCreation().titleProperty());
        entryTable.getColumns().add(titleColumn);
    }

    public void showCreatorColumn() {
        creatorNameColumn = stringColumnFactory(Columns.getCreatorName(Module.BOOK),80, data -> data.getValue().getCreator().nameProperty());
        entryTable.getColumns().add(creatorNameColumn);
    }

    public void showPresentColumn() {
        presentColumn = numberColumnFactory(Columns.getNumPortion(Module.BOOK), 120, data -> data.getValue().getCreation().numPortionProperty());
        entryTable.getColumns().add(presentColumn);
    }

    public void showProcessedColumn() {
        processedColumn = numberColumnFactory(Columns.getProcessed(Module.BOOK),125, data-> data.getValue().getUser().processedPortionProperty());
        entryTable.getColumns().add(processedColumn);
    }

    public void showOwnStatusColumn() {
        ownStatusColumn = stringColumnFactory(Columns.getOwnStat(Module.BOOK), 80, data -> data.getValue().getUser().ownStatusProperty());
        entryTable.getColumns().add(ownStatusColumn);
    }

    public void showSeriesColumn() {
        seriesColumn = stringColumnFactory(Columns.getSeries(Module.BOOK), 80, data -> data.getValue().getCreation().seriesProperty());
        entryTable.getColumns().add(seriesColumn);
    }

    public void showLastPortionColumn() {
        lastEpColumn = stringColumnFactory(Columns.getLastPortion(Module.BOOK), 80, data -> data.getValue().getCreation().dateLastPortionProperty());
        entryTable.getColumns().add(lastEpColumn);
    }

    public void showRatingColumn() {
        ratingColumn = numberColumnFactory(Columns.getRating(Module.BOOK), 80, data -> data.getValue().getUser().ratingProperty());
        entryTable.getColumns().add(ratingColumn);
    }

    public void showCreatorSortColumn() {
        creatorSortNameColumn = stringColumnFactory(Columns.getCreatorSort(Module.BOOK), 80, data -> data.getValue().getCreator().sortNameProperty());
        entryTable.getColumns().add(creatorSortNameColumn);
    }

    public void showWorkStatColumn() {
        workStatColumn = stringColumnFactory(Columns.getWorkStat(Module.BOOK), 80, data -> data.getValue().getCreation().workStatusProperty());
        entryTable.getColumns().add(workStatColumn);
    }

    public void showCommentColumn() {
        commentColumn = stringColumnFactory(Columns.getComment(Module.BOOK), 80, data -> data.getValue().getUser().commentProperty());
        entryTable.getColumns().add(commentColumn);
    }
}
