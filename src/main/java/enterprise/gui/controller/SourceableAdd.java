package enterprise.gui.controller;

import enterprise.ControlComm;
import enterprise.data.EntryCarrier;
import enterprise.data.impl.SourceableEntryImpl;
import enterprise.data.impl.SourceableImpl;
import enterprise.data.intface.*;
import enterprise.gui.controller.content.NovelContent;
import enterprise.gui.general.BasicMode;
import enterprise.gui.general.GlobalItemValues;
import enterprise.modules.Module;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scrape.sources.Source;
import scrape.sources.SourceList;
import scrape.sources.SourceType;
import scrape.sources.posts.PostManager;

import java.net.URISyntaxException;

/**
 *
 */
public abstract class SourceableAdd extends Add {

    @FXML
    private ComboBox<SourceType> urlType;
    @FXML
    private
    ComboBox<String> translator;
    @FXML
    private
    TextField sourceURL;
    @FXML
    private
    Button addSource;
    @FXML
    private
    TableView<Source> sourceTable;
    @FXML
    private
    TableColumn<Source, String> sourceColumn;
    @FXML
    private
    TableColumn<Source, String> urlColumn;

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
            sourceTable.getItems().add(source);

            sourceURL.clear();
            urlType.getSelectionModel().clearSelection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: 01.08.2017 throw error icon
            System.out.println("invalid URL");
        }
    }

    @Override
    @FXML
    protected void add() {
        SourceableEntry entry = getSourceableEntry(getModule());

        //add if this entry is not contained in the entry list of module
        if (getModule().addEntry(entry)) {

            //add it to the TableView of the Content Display
            NovelContent controller = (NovelContent) ControlComm.get().getController(getModule(), BasicMode.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            EntryCarrier.getInstance().addNewEntry(entry);

            //Make content available for Scraping
            PostManager.getInstance().addSearchEntries(entry);
        } else {
            //show alert for trying to add an already existing entry
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!");
            alert.show();
        }
        //close this window either way
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Create a {@link SourceableEntry} from the available
     * user input data.
     *
     * @param module module of this {@code SourceableEntry}
     * @return entry - a complete SourceableEntry
     */
    private SourceableEntryImpl getSourceableEntry(Module module) {
        CreationEntry entry = getCreationEntry(module);

        Creator creator = entry.getCreator();
        Creation creation = entry.getCreation();
        User user = entry.getUser();

        Sourceable sourceable = null;
        try {
            sourceable = getSourceable();
        } catch (IllegalArgumentException e) {
            // TODO: 24.08.2017 show error message
            e.printStackTrace();
        }

        return new SourceableEntryImpl(user, creation, creator, sourceable, module);
    }

    /**
     * Constructs a {@code Sourceable} from the data input.
     *
     * @return sourceable
     */
    private Sourceable getSourceable() {
        ObservableList<Source> observableSource = sourceTable.getItems();
        SourceList source = new SourceList();
        source.addAll(observableSource);
        String tlGroup = validateStringInput(translator);

        return SourceableImpl.get(source, tlGroup);
    }

    /**
     * Sets the fields providing data for the Columns.
     */
    protected void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    /**
     * Disables the {@code addSource} Button,
     * if no item of {@code urlType} is selected or
     * the {@code sourceURL} TextField is empty.
     */
    protected void readyAddSourceBtn() {
        addSource.disableProperty().bind(urlType.valueProperty().isNull().or(sourceURL.textProperty().isEmpty()));
    }

    @Override
    protected void readyComboBoxes() {
        super.readyComboBoxes();
        setCombo(translator, GlobalItemValues.getInstance().getTranslators());
        comboAdd(translator);
        getComboOnClose(translator, GlobalItemValues.getInstance().getTranslators());
        urlType.getItems().setAll(SourceType.values());
    }

    @Override
    protected void readyInput() {
        super.readyInput();
        limitToLength(translator.getEditor(), 100);
        limitToLength(sourceURL, 100);
    }
}
