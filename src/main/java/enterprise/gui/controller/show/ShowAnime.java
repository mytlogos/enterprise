package enterprise.gui.controller.show;

import enterprise.data.intface.SourceableEntry;
import enterprise.gui.controller.SourceableShow;
import enterprise.gui.general.BasicMode;
import enterprise.misc.EntrySingleton;
import enterprise.modules.BasicModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the Controller for {@code showAnime.fxml} with
 * {@link BasicModule#ANIME} and {@link BasicMode#SHOW}.
 */
public class ShowAnime extends SourceableShow implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = (SourceableEntry) EntrySingleton.getInstance().getEntry();
        readySourceColumns();
        loadEntry();
    }

    @FXML
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("anime Details");
        stage.setResizable(false);

        stage.show();
    }

    @Override
    public BasicModule getModule() {
        return BasicModule.ANIME;
    }
}
