package Enterprise.gui.controller.show;

import Enterprise.ControlComm;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.controller.ShowController;
import Enterprise.misc.EntrySingleton;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dominik on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class ShowBookController extends ShowController<CreationEntry, BasicModules> implements Initializable {

    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("Buch Details");
        stage.setResizable(false);

        stage.show();
    }

    public void paneFocus() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.BOOK;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = EntrySingleton.getInstance().getEntry();
        ControlComm.getInstance().setController(this, module, mode);

        loadEntry();
    }
}
