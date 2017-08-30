package Enterprise.gui.manga.controller;

import Enterprise.ControlComm;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableShowController;
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
public class ShowMangaController extends SourceableShowController<BasicModules> implements Initializable {
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("Manga Details");
        stage.setResizable(false);

        stage.show();
    }

    public void paneFocus() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.MANGA;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = (SourceableEntry) EntrySingleton.getInstance().getEntry();
        ControlComm.getInstance().setController(this, module, mode);

        loadEntry();
    }

}
