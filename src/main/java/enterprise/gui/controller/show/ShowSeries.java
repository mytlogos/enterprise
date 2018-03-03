package enterprise.gui.controller.show;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Show;
import enterprise.misc.EntrySingleton;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class ShowSeries extends Show<CreationEntry> implements Initializable {
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setTitle("Serie Details");
        stage.setResizable(false);

        stage.show();
    }

    @Override
    public Module getModule() {
        return BasicModule.SERIES;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryData = EntrySingleton.getInstance().getEntry();
        loadEntry();
    }

}
