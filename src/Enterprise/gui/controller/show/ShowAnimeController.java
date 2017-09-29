package Enterprise.gui.controller.show;

import Enterprise.ControlComm;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableShowController;
import Enterprise.gui.general.BasicModes;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.BasicModules;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller for {@code showAnime.fxml} with
 * {@link BasicModules#ANIME} and {@link BasicModes#SHOW}.
 */
public class ShowAnimeController extends SourceableShowController<BasicModules> implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = (SourceableEntry) EntrySingleton.getInstance().getEntry();
        ControlComm.getInstance().setController(this, module, mode);

        readySourceColumns();
        loadEntry();
    }

    @FXML @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("anime Details");
        stage.setResizable(false);

        stage.show();
    }

    public void paneFocus() {
        // TODO: 25.08.2017 do this
    }


    @Override
    protected void setModule() {
        module = BasicModules.ANIME;
    }
}
