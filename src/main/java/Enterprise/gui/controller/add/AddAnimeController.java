package Enterprise.gui.controller.add;

import Enterprise.ControlComm;
import Enterprise.gui.controller.SourceableAddController;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
        final Stage stage = loadStage();

        stage.setResizable(false);
        stage.show();
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
