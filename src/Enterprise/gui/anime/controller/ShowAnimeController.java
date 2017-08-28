package Enterprise.gui.anime.controller;

import Enterprise.ControlComm;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.modules.Anime;
import javafx.collections.FXCollections;
import scrape.sources.Source;
import Enterprise.misc.EntrySingleton;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;
import Enterprise.gui.controller.ShowController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller for {@code showAnime.fxml} with
 * {@link Module#ANIME} and {@link Mode#SHOW}.
 */
public class ShowAnimeController extends ShowController<SourceableEntryImpl, Anime> implements Initializable {

    @FXML
    private Text translator;
    @FXML
    private TableView<Source> sourceTable;

    @FXML
    private TableColumn<Source, String> sourceColumn;

    @FXML
    private TableColumn<Source, String> urlColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = (SourceableEntryImpl) EntrySingleton.getInstance().getEntry();
        ControlComm.getInstance().setController(this);

        readySourceColumns();
        loadEntry();
    }

    @FXML @Override
    protected void openEdit() {
        EntrySingleton.getInstance().setEntry(entryData);
        ControlComm.getInstance().getController(Module.ANIME, Mode.EDIT).open();
    }

    @FXML @Override
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(Module.ANIME, Mode.SHOW)));

        Stage stage = setWindow(loader);

        stage.setTitle("anime Details");
        stage.setResizable(false);

        stage.show();
    }

    @Override
    public void paneFocus() {
        // TODO: 25.08.2017 do this
    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Anime.getInstance();
    }

    /**
     * Sets the fields providing data for the Columns.
     */
    private void readySourceColumns() {
        sourceColumn.setCellValueFactory(param -> param.getValue().sourceNameProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
    }

    @Override
    protected void bindEntry() {
        super.bindEntry();
        bindToText(translator,entryData.getSourceable().translatorProperty());
        bindToText(keyWords,entryData.getUser().keyWordsProperty());
        sourceTable.setItems(FXCollections.observableArrayList(entryData.getSourceable().getSourceList()));
    }
}
