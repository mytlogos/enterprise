package enterprise.gui.controller;

import enterprise.data.Default;
import enterprise.data.intface.SourceableEntry;
import enterprise.gui.general.GlobalItemValues;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.Source;
import scrape.sources.SourceType;
import scrape.sources.posts.PostManager;

/**
 *
 */
public abstract class SourceableEdit extends Edit<SourceableEntry> {

    @FXML
    private ComboBox<String> translator;
    @FXML
    private TextField sourceURL;
    @FXML
    private Button addSource;
    @FXML
    private TableView<Source> sourceTable;
    @FXML
    private TableColumn<Source, String> sourceColumn;
    @FXML
    private TableColumn<Source, String> urlColumn;
    @FXML
    private ComboBox<SourceType> urlType;


    @Override
    public void initialize() {
        super.initialize();

//        sourceTable.itemsProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }

    /**
     * Gets the data from the {@code urlType} ComboBox and
     * the {@code sourceURL} TextField, constructing a {@link Source}
     * object and adding it to the {@code sourceTable} TableView.
     */
    @FXML
    protected void addSource() {
        SourceType type = urlType.getValue();
        String url = validateStringInput(sourceURL);
        try {
            Source source = Source.create(url, type);
            ObservableList<Source> sources = entryData.getSourceable().getSources();

            System.err.println("adding " + source);
            sources.add(source);
            System.err.println(sourceTable.getItems());
            System.err.println(sourceTable.getItems() == sources);
            System.err.println(sources);
            sourceURL.clear();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 01.08.2017 throw error icon
        }
    }

    @Override
    protected void readyComboBoxes() {
        super.readyComboBoxes();
        setCombo(translator, GlobalItemValues.getInstance().getTranslators());
        comboAdd(translator);
        getComboOnClose(translator, GlobalItemValues.getInstance().getTranslators());

        urlType.getItems().setAll(SourceType.values());
        urlType.getSelectionModel().select(SourceType.START);
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
            PostManager.getInstance().addSearchEntries(entryData);
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
     * Checks if {@code entryData} is null, if true
     * it sets the text of all Text Nodes.
     * If the field is not null, it will proceed to bindByOwn the {@link enterprise.data.intface.CreationEntry}
     * object to the graphic Nodes.
     */
    protected void loadEntry() {
        if (entryData == null) {
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
    protected void bindEntry() {
        super.bindEntry();
        bindToComboBox(translator, entryData.getSourceable().translatorProperty());
        ObservableList<Source> sources = entryData.getSourceable().getSources();
        System.out.println("binding " + sources + " of " + entryData.getId());
        sourceTable.setItems(sources);
    }

    @Override
    protected void unBindEntry() {
        super.unBindEntry();
        unbindFromComboBox(translator, entryData.getSourceable().translatorProperty());
    }
}
