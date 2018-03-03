package enterprise.gui.controller;

import enterprise.data.intface.SourceableEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import scrape.sources.Source;

/**
 *
 */
public abstract class SourceableShow extends Show<SourceableEntry> {

    @FXML
    private Text translator;
    @FXML
    private TableView<Source> sourceTable;

    @FXML
    private TableColumn<Source, String> sourceColumn;

    @FXML
    private TableColumn<Source, String> urlColumn;

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
