package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableShowController;
import Enterprise.gui.general.BasicModes;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller for {@code showNovel.fxml} with
 * {@link BasicModules#NOVEL} and {@link BasicModes#SHOW}.
 */
public class ShowNovelController extends SourceableShowController<BasicModules> implements Initializable {
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("Novel Details");
        stage.setResizable(false);

        stage.show();
    }

    public void paneFocus() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.NOVEL;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = (SourceableEntry) EntrySingleton.getInstance().getEntry();
        ControlComm.getInstance().setController(this, module, mode);

        loadEntry();
    }
}
