package Enterprise.gui.controller.edit;

import Enterprise.ControlComm;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.gui.controller.InputLimiter;
import Enterprise.gui.controller.SourceableEditController;
import Enterprise.gui.general.BasicModes;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller of {@code editAnime.fxml} with
 * {@link BasicModes#EDIT} and {@link BasicModules#ANIME}.
 */
public class EditAnimeController extends SourceableEditController<BasicModules> implements Initializable, InputLimiter {

    @Override
    public void open() {
        Stage stage = loadStage();

        setData(stage);
        stage.setResizable(false);

        stage.show();
    }

    @Override
    protected void setModule() {
        module = BasicModules.ANIME;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Anime bearbeiten");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //loads entry to edit
        creationEntry = (SourceableEntryImpl) EntrySingleton.getInstance().getEntry();

        //sets instance in ControlComm to this
        ControlComm.getInstance().setController(this, module, mode);

        //loads entry to the nodes
        loadEntry();

        //ready the gui
        readySourceColumns();
        readyInput();
        readyComboBoxes();
        onCloseOperation();
        disableAddSource();
    }
}
