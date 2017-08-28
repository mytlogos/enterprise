package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.gui.general.Mode;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.modules.Module;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.controller.ShowController;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.Novel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import scrape.sources.Source;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller for {@code showNovel.fxml} with
 * {@link Module#NOVEL} and {@link Mode#SHOW}.
 */
public class ShowNovelController extends ShowController<SourceableEntryImpl,Novel> implements Initializable {
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
        ControlComm.getInstance().getController(Module.NOVEL, Mode.EDIT).open();

    }

    @FXML @Override
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(Module.NOVEL, Mode.SHOW)));
        Stage stage = setWindow(loader);

        stage.setTitle("Novel Details");
        stage.setResizable(false);

        stage.show();
    }

    @Override
    public void paneFocus() {
// TODO: 25.08.2017 do this
    }

    @Override
    public void setModuleEntry() {
        moduleEntry = Novel.getInstance();
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
        sourceTable.setItems(entryData.getSourceable().getSourceList());
    }
}
