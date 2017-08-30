package Enterprise.gui.series.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.controller.AddController;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Created by Dominik on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddSeriesController extends AddController<BasicModules> {
    @Override
    protected void add() {
        CreationEntry entry = getCreationEntry(module);

        //add if this entry is not contained in the entry list of module
        if (module.addEntry(entry)) {

            //add it to the TableView of the Content Display
            SeriesController controller = (SeriesController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);
        } else {
            //show alert for trying to add an already existing entry
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!");
            alert.show();
        }
        //close this window either way
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Serie hinzufügen");
    }

    @Override
    protected void setModule() {
        module = BasicModules.SERIES;
    }
}
