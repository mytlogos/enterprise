package Enterprise.gui.manga.controller;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableModuleCont;
import Enterprise.gui.general.Columns;
import Enterprise.modules.Manga;
import Enterprise.modules.Module;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

/**
 * The {@link Enterprise.gui.general.Mode#CONTENT} Controller of the {@code manga.fxml} file.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class MangaController extends SourceableModuleCont<SourceableEntry,Manga> {

    @FXML
    protected void deleteRow(KeyEvent event) {

    }

    @FXML
    protected void openFullAdd() {

    }

    @FXML
    protected void deleteSelected() {

    }

    @FXML
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

    @FXML
    public void open() {

    }

    @Override
    public void paneFocus() {

    }

    @Override
    public void setModuleEntry() {

    }

    @Override
    public void showTitleColumn() {
        titleColumn = stringColumnFactory(Columns.getTitle(Module.MANGA), 210,
                data -> data.getValue().getCreation().titleProperty());

        entryTable.getColumns().add(titleColumn);
    }

    @Override
    public void showCreatorColumn() {
        creatorNameColumn = stringColumnFactory(Columns.getCreatorName(Module.MANGA),80,
                data -> data.getValue().getCreator().nameProperty());

        entryTable.getColumns().add(creatorNameColumn);
    }

    @Override
    public void showPresentColumn() {
        presentColumn = numberColumnFactory(Columns.getNumPortion(Module.MANGA), 120,
                data -> data.getValue().getCreation().numPortionProperty());

        entryTable.getColumns().add(presentColumn);
    }

    @Override
    public void showProcessedColumn() {
        processedColumn = numberColumnFactory(Columns.getProcessed(Module.MANGA),125,
                data-> data.getValue().getUser().processedPortionProperty());

        entryTable.getColumns().add(processedColumn);
    }

    @Override
    public void showOwnStatusColumn() {
        ownStatusColumn = stringColumnFactory(Columns.getOwnStat(Module.MANGA), 80,
                data -> data.getValue().getUser().ownStatusProperty());

        entryTable.getColumns().add(ownStatusColumn);
    }

    @Override
    public void showSeriesColumn() {
        seriesColumn = stringColumnFactory(Columns.getSeries(Module.MANGA), 80,
                data -> data.getValue().getCreation().seriesProperty());

        entryTable.getColumns().add(seriesColumn);
    }

    @Override
    public void showLastPortionColumn() {
        lastEpColumn = stringColumnFactory(Columns.getLastPortion(Module.MANGA), 80,
                data -> data.getValue().getCreation().dateLastPortionProperty());

        entryTable.getColumns().add(lastEpColumn);
    }

    @Override
    public void showRatingColumn() {
        ratingColumn = numberColumnFactory(Columns.getRating(Module.MANGA), 80,
                data -> data.getValue().getUser().ratingProperty());

        entryTable.getColumns().add(ratingColumn);
    }

    @Override
    public void showCreatorSortColumn() {
        creatorSortNameColumn = stringColumnFactory(Columns.getCreatorSort(Module.MANGA), 80,
                data -> data.getValue().getCreator().sortNameProperty());

        entryTable.getColumns().add(creatorSortNameColumn);
    }

    @Override
    public void showWorkStatColumn() {
        workStatColumn = stringColumnFactory(Columns.getWorkStat(Module.MANGA), 80,
                data -> data.getValue().getCreation().workStatusProperty());

        entryTable.getColumns().add(workStatColumn);
    }

    @Override
    public void showCommentColumn() {
        commentColumn = stringColumnFactory(Columns.getComment(Module.MANGA), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(commentColumn);
    }

    @Override
    public void showKeyWordsColumn() {
        keyWordsColumn = stringColumnFactory(Columns.getKeyWords(Module.MANGA), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(keyWordsColumn);
    }

    @Override
    public void showTranslatorColumn() {
        translatorColumn = stringColumnFactory(Columns.getTranslator(Module.MANGA), 80,
                data -> data.getValue().getUser().commentProperty());

        entryTable.getColumns().add(translatorColumn);
    }
}
