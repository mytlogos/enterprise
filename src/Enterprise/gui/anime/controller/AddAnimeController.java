package Enterprise.gui.anime.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.gui.controller.SourceableAddController;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.PostSingleton;
import Enterprise.modules.BasicModules;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of {@code addAnime.fxml} with {@link BasicModules#ANIME}
 * and {@link BasicModes#ADD}.
 */
public class AddAnimeController extends SourceableAddController<BasicModules> implements Initializable {

    /**
     * Opens a new Window with the specified {@link Enterprise.gui.general.Mode}
     * and {@link Enterprise.modules.Module}.
     */
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @FXML @Override
    protected void add() {
        SourceableEntryImpl entry = getSourceableEntry(module);

        //add if this entry is unique (not equal to any existing entry in the list)
        if (module.addEntry(entry)) {

            // TODO: 24.08.2017 maybe make the list of anime, the direct underlying list of the TableView in AnimeController
            AnimeController controller = (AnimeController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);
            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);

            //Make content available for Scraping
            PostSingleton.getInstance().addSearchEntries(entry.getSourceable());
        } else {
            System.out.println("Adding the entry failed.");

            //show alert for trying to add an already existing entry
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!");
            alert.show();
        }
        //close window
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

    @Override
    protected void setModule() {
        module = BasicModules.ANIME;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Anime hinzufügen");
        label.setText("Füge neuen anime hinzu");
    }

    /**
     * Executes some Task on {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     */
    private void onCloseOperation() {
        Platform.runLater(() -> addBtn.getScene().getWindow().setOnCloseRequest(event -> {
            System.out.println("do sth cool");
        }));
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set controller instance in ControlComm to 'this' instance
        ControlComm.getInstance().setController(this, module, mode);
        onCloseOperation();

        //ready the graphical interface components
        readyComboBoxes();
        readyInput();
        readySourceColumns();
        readyAddSourceBtn();
    }
}
