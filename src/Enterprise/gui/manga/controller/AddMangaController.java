package Enterprise.gui.manga.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableAddController;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.PostSingleton;
import Enterprise.modules.BasicModules;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddMangaController extends SourceableAddController<BasicModules> {

    @Override
    protected void add() {
        SourceableEntry entry = getSourceableEntry(module);

        //add if this entry is not contained in the entry list of module
        if (module.addEntry(entry)) {

            //add it to the TableView of the Content Display
            MangaController controller = (MangaController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);

            //Make content available for Scraping
            PostSingleton.getInstance().addSearchEntries(entry.getSourceable());
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
    protected void setModule() {
        module = BasicModules.MANGA;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Manga hinzufügen");
        label.setText("Füge neuen Manga hinzu");
    }
}
