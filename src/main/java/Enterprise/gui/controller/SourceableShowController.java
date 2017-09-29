package Enterprise.gui.controller;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.modules.Module;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import scrape.sources.Source;

/**
 *
 */
public abstract class SourceableShowController<R extends Enum<R> & Module> extends ShowController<SourceableEntry, R> {

    @FXML
    protected Text translator;
    @FXML
    protected TableView<Source> sourceTable;

    @FXML
    protected TableColumn<Source, String> sourceColumn;

    @FXML
    protected TableColumn<Source, String> urlColumn;

    /**
     * Sets the fields providing data for the Columns.
     */
    protected void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    @Override
    protected void bindEntry() {
        super.bindEntry();
        bindToText(translator, entryData.getSourceable().translatorProperty());
        sourceTable.setItems(FXCollections.observableArrayList(entryData.getSourceable().getSourceList()));
    }
}
