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
