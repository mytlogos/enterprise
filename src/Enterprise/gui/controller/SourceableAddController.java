package Enterprise.gui.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.data.impl.SourceableImpl;
import Enterprise.data.intface.*;
import Enterprise.gui.controller.content.NovelController;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.GlobalItemValues;
import Enterprise.modules.Module;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scrape.PostManager;
import scrape.sources.Source;
import scrape.sources.SourceList;

import java.net.URISyntaxException;

/**
 *
 */
public abstract class SourceableAddController<E extends Enum<E> & Module> extends AddController<E> {

    @FXML
    protected ComboBox<Source.SourceType> urlType;
    @FXML // fx:id="translator"
    protected ComboBox<String> translator; // Value injected by FXMLLoader
    @FXML // fx:id="sourceURL"
    protected TextField sourceURL; // Value injected by FXMLLoader
    @FXML // fx:id="addSource"
    protected Button addSource; // Value injected by FXMLLoader
    @FXML // fx:id="sourceTable"
    protected TableView<Source> sourceTable; // Value injected by FXMLLoader
    @FXML // fx:id="sourceColumn"
    protected TableColumn<Source, String> sourceColumn; // Value injected by FXMLLoader
    @FXML // fx:id="urlColumn"
    protected TableColumn<Source, String> urlColumn; // Value injected by FXMLLoader

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
        SourceableEntry entry = getSourceableEntry(module);

        //add if this entry is not contained in the entry list of module
        if (module.addEntry(entry)) {

            //add it to the TableView of the Content Display
            NovelController controller = (NovelController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);

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
     * Sets the fields providing data for the Columns.
     */
    protected void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    /**
     * Create a {@link SourceableEntry} from the available
     * user input data.
     *
     * @param module module of this {@code SourceableEntry}
     * @return entry - a complete SourceableEntry
     */
    protected SourceableEntryImpl getSourceableEntry(Module module) {
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
        urlType.getItems().setAll(Source.SourceType.values());
    }

    @Override
    protected void readyInput() {
        super.readyInput();
        limitToLength(translator.getEditor(), 100);
        limitToLength(sourceURL, 100);
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

        return new SourceableImpl(source, tlGroup);
    }
}
