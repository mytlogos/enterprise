package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableAddController;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.PostManager;
import Enterprise.modules.BasicModules;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the {@link Enterprise.gui.controller.Controller} of the
 * {@code addNovel.fxml} file with {@link BasicModes#ADD} and {@link BasicModules#NOVEL}.
 */
public class AddNovelController extends SourceableAddController<BasicModules> implements Initializable {

    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @FXML @Override
    protected void add() {
        SourceableEntry entry = getSourceableEntry(module);

        //add if this entry is not contained in the entry list of module
        if (module.addEntry(entry)) {

            //add it to the TableView of the Content Display
            NovelController controller = (NovelController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);

            //Make content available for Scraping
            PostManager.getInstance().addSearchEntries(entry.getSourceable());
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
    protected void setModule() {
        module = BasicModules.NOVEL;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Novel hinzufügen");
        label.setText("Füge neuen Novel hinzu");
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
        //set ControlComm instance of this class to 'this'
        ControlComm.getInstance().setController(this, module, mode);

        //ready GUI
        onCloseOperation();
        readyComboBoxes();
        readyInput();
        readySourceColumns();
        readyAddSourceBtn();
    }
}
