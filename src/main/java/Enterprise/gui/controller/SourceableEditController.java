package Enterprise.gui.controller;

import Enterprise.data.Default;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.GlobalItemValues;
import Enterprise.modules.Module;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.Source;
import scrape.sources.posts.PostManager;

import java.net.URISyntaxException;

/**
 *
 */
public abstract class SourceableEditController<R extends Enum<R> & Module> extends EditController<SourceableEntry, R> {

    @FXML
    protected ComboBox<String> translator;
    @FXML
    protected TextField sourceURL;
    @FXML
    protected Button addSource;
    @FXML
    protected TableView<Source> sourceTable;
    @FXML
    protected TableColumn<Source, String> sourceColumn;
    @FXML
    protected TableColumn<Source, String> urlColumn;
    @FXML
    protected ComboBox<Source.SourceType> urlType;

    /**
     * Gets the data from the {@code urlType} ComboBox and
     * the {@code sourceURL} TextField, constructing a {@link Source}
     * object and adding it to the {@code sourceTable} TableView.
     */
    @FXML
    protected void addSource() {
        Source.SourceType type = urlType.getValue();
        String url = validateStringInput(sourceURL);
        try {
            Source source = Source.create(url, type);

            sourceTable.getItems().add(source);
            sourceURL.clear();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: 01.08.2017 throw error icon
        }
    }

    @Override
    protected void bindEntry() {
        super.bindEntry();
        bindToComboBox(translator, creationEntry.getSourceable().translatorProperty());

        sourceTable.setItems(creationEntry.getSourceable().getSourceList());
    }

    @Override
    protected void readyComboBoxes() {
        super.readyComboBoxes();
        setCombo(translator, GlobalItemValues.getInstance().getTranslators());
        comboAdd(translator);
        getComboOnClose(translator, GlobalItemValues.getInstance().getTranslators());
        urlType.getItems().setAll(Source.SourceType.values());
    }

    @Override
    protected void readyInput() {
        super.readyInput();
        limitToLength(translator.getEditor(), 100);
        limitToLength(sourceURL, 100);
    }

    /**
     * Sets an EventHandler to the
     * {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     * Unbinds the Nodes from the entry and makes the Sourceable data available for the
     * {@link ScheduledPostScraper}.
     */
    protected void onCloseOperation() {
        Platform.runLater(() -> root.getScene().getWindow().setOnCloseRequest(event -> {
            unBindEntry();
            PostManager.getInstance().addSearchEntries(creationEntry);
        }));
    }

    /**
     * Disable the {@code addSource} Button if no item of {@code urlType} is selected
     * or {@code sourceURL} is empty.
     */
    protected void disableAddSource() {
        addSource.disableProperty().bind(urlType.valueProperty().isNull().or(sourceURL.textProperty().isEmpty()));
    }

    /**
     * Sets the fields providing data for the Columns.
     */
    protected void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    /**
     * Checks if {@code creationEntry} is null, if true
     * it sets the text of all Text Nodes.
     * If the field is not null, it will proceed to bindByOwn the {@link Enterprise.data.intface.CreationEntry}
     * object to the graphic Nodes.
     */
    protected void loadEntry() {
        if (creationEntry == null) {
            for (Node node : endEditBtn.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Text) {
                    ((Text) node).setText(Default.STRING);
                }
            }
        } else {
            bindEntry();
        }
    }

    @Override
    protected void unBindEntry() {
        super.unBindEntry();
        unbindFromComboBox(translator, creationEntry.getSourceable().translatorProperty());
    }
}
