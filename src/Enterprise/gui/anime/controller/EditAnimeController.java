package Enterprise.gui.anime.controller;

import Enterprise.ControlComm;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.gui.general.GlobalItemValues;
import Enterprise.modules.Anime;
import Enterprise.data.Default;
import Enterprise.gui.general.PostSingleton;
import Enterprise.misc.EntrySingleton;
import scrape.sources.Source;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;
import Enterprise.gui.controller.EditController;
import Enterprise.gui.controller.InputLimiter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller of {@code editAnime.fxml} with
 * {@link Mode#EDIT} and {@link Module#ANIME}.
 */
public class EditAnimeController extends EditController<SourceableEntryImpl, Anime> implements Initializable, InputLimiter {

    @FXML
    private TextField title;

    @FXML
    private ComboBox<String> creator;

    @FXML
    private ComboBox<String> creatorSort;

    @FXML
    private ComboBox<String> collection;

    @FXML
    private ComboBox<String> ownStatus;

    @FXML
    private ComboBox<String> workStatus;

    @FXML
    private ComboBox<String> translator;

    @FXML
    private TextField rating;

    @FXML
    private TextArea commentArea;

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
    private TextField scrapeKeyWords;
    @FXML
    private ComboBox<Source.SourceType> urlType;

    /**
     * Gets the data from the {@code urlType} ComboBox and
     * the {@code sourceURL} TextField, constructing a {@link Source}
     * object and adding it to the {@code sourceTable} TableView.
     */
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

    /**
     * Checks if {@code creationEntry} is null, if true
     * it sets the text of all Text Nodes.
     * If the field is not null, it will proceed to bind the {@link Enterprise.data.intface.CreationEntry}
     * object to the graphic Nodes.
     */
    private void loadEntry() {
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
    protected void bindEntry() {
        super.bindEntry();
        bindToComboBox(translator, creationEntry.getSourceable().translatorProperty());
        scrapeKeyWords.textProperty().bindBidirectional(creationEntry.getUser().keyWordsProperty());

        sourceTable.setItems(creationEntry.getSourceable().getSourceList());
    }

    @Override
    protected void unBindEntry() {
        super.unBindEntry();
        unbindFromComboBox(translator, creationEntry.getSourceable().translatorProperty());
        scrapeKeyWords.textProperty().unbindBidirectional(creationEntry.getUser().keyWordsProperty());
        if (coverPath != null) {
            if (!creationEntry.getCreation().getCoverPath().equalsIgnoreCase(coverPath)) {
                creationEntry.getCreation().setCoverPath(coverPath);
            }
        }
    }

    @Override
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(Module.ANIME, Mode.EDIT)));
        Stage stage = setWindow(loader);

        setData(stage);
        stage.setResizable(false);

        stage.show();
    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Anime.getInstance();
    }

    /**
     * Sets an EventHandler to the
     * {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     * Unbinds the Nodes from the entry and makes the Sourceable data available for the
     * {@link scrape.concurrent.ScheduledScraper}.
     */
    private void onCloseOperation() {
        Platform.runLater(() -> root.getScene().getWindow().setOnCloseRequest(event -> {
            unBindEntry();
            PostSingleton.getInstance().addSearchEntries(creationEntry.getSourceable());
        }));
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("anime hinzufügen");
    }

    @Override
    protected void readyComboBoxes() {
        super.readyComboBoxes();
        setCombo(translator, GlobalItemValues.getInstance().getTranslators());
        comboAdd(translator);
        getComboOnClose(translator,GlobalItemValues.getInstance().getTranslators());
        urlType.getItems().setAll(Source.SourceType.values());
    }

    @Override
    protected void readyInput() {
        super.readyInput();
        limitToLength(translator.getEditor(),100);
        limitToLength(scrapeKeyWords,100);
        limitToLength(sourceURL,100);
    }

    /**
     * Sets the fields providing data for the Columns.
     */
    private void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //loads entry to edit
        creationEntry = (SourceableEntryImpl) EntrySingleton.getInstance().getEntry();

        //sets instance in ControlComm to this
        ControlComm.getInstance().setController(this);

        //loads entry to the nodes
        loadEntry();

        //ready the gui
        setModuleEntry();
        readySourceColumns();
        readyInput();
        readyComboBoxes();
        onCloseOperation();
        disableAddSource();
    }

    /**
     * Disable the {@code addSource} Button if no item of {@code urlType} is selected
     * or {@code sourceURL} is empty.
     */
    private void disableAddSource() {
        addSource.disableProperty().bind(urlType.valueProperty().isNull().or(sourceURL.textProperty().isEmpty()));
    }
}
