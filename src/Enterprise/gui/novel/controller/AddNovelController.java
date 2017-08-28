package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.impl.*;
import Enterprise.data.intface.*;
import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;
import scrape.sources.SourceList;
import scrape.sources.Source;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.controller.AddController;
import Enterprise.gui.general.PostSingleton;
import Enterprise.modules.Novel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the {@link Enterprise.gui.controller.Controller} of the
 * {@code addNovel.fxml} file with {@link Mode#ADD} and {@link Module#NOVEL}.
 */
public class AddNovelController extends AddController<Novel> implements Initializable{
    @FXML // fx:id="translator"
    private ComboBox<String> translator; // Value injected by FXMLLoader

    @FXML // fx:id="sourceURL"
    private TextField sourceURL; // Value injected by FXMLLoader

    @FXML // fx:id="addSource"
    private Button addSource; // Value injected by FXMLLoader

    @FXML // fx:id="sourceTable"
    private TableView<Source> sourceTable; // Value injected by FXMLLoader

    @FXML // fx:id="sourceColumn"
    private TableColumn<Source, String> sourceColumn; // Value injected by FXMLLoader

    @FXML // fx:id="urlColumn"
    private TableColumn<Source, String> urlColumn; // Value injected by FXMLLoader

    @FXML
    private Text label;

    @FXML
    private ComboBox<Source.SourceType> urlType;

    @Override
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(Module.NOVEL, Mode.ADD)));
        Stage stage = setWindow(loader);

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Novel.getInstance();
    }

    @FXML @Override
    protected void add() {
        SourceableEntry entry = getSourceableEntry(Module.NOVEL);

        //add if this entry is not contained in the entry list of moduleEntry
        if (moduleEntry.addEntry(entry)) {

            //add it to the TableView of the Content Display
            NovelController controller = (NovelController) ControlComm.getInstance().getController(Module.NOVEL,Mode.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);

            //Make content available for Scraping
            PostSingleton.getInstance().addSearchEntries(entry.getSourceable());
        } else {
            System.out.println("Adding the entry failed.");

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
        Creator creator = null;
        Creation creation = null;
        User user = null;
        Sourceable sourceable = null;
        try {
            creator = getCreator();
            creation = getCreation();
            user = getUser();
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

        return new SimpleSourceable(source,tlGroup);
    }

    @FXML
    private void addSource() {
        Source.SourceType type = urlType.getValue();
        String url = validateStringInput(sourceURL);
        try {
            Source source = new Source(url,type);

            sourceTable.getItems().add(source);
            sourceURL.clear();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: 01.08.2017 throw error icon
        }
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Novel hinzufügen");
        label.setText("Füge neuen Novel hinzu");
    }

    /**
     * Executes some Task on {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     */
    private void onCloseOperation() {
        Platform.runLater(() -> addBtn.getScene().getWindow().setOnCloseRequest(event -> {
            System.out.println("do sth cool");
        }));
    }

    @Override
    protected void readyComboBoxes() {
        super.readyComboBoxes();
        setCombo(translator,Novel.getInstance().getTlGroup());
        comboAdd(translator);
        getComboOnClose(translator,Novel.getInstance().getTlGroup());
        urlType.getItems().setAll(Source.SourceType.values());
    }

    @Override
    protected void readyInput() {
        super.readyInput();
        limitToLength(translator.getEditor(),100);
        limitToLength(sourceURL,100);
    }

    /**
     * Sets the fields providing data for the Columns.
     */
    private void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //set ControlComm instance of this class to 'this'
        ControlComm.getInstance().setController(this);

        //ready GUI
        setModuleEntry();
        onCloseOperation();
        readyComboBoxes();
        readyInput();
        readySourceColumns();
        readyAddSourceBtn();
    }

    /**
     * Disables the {@code addSource} Button,
     * if no item of {@code urlType} is selected or
     * the {@code sourceURL} TextField is empty.
     */
    private void readyAddSourceBtn() {
        addSource.disableProperty().bind(urlType.valueProperty().isNull().or(sourceURL.textProperty().isEmpty()));
    }
}
