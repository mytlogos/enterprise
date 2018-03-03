package enterprise.gui.controller.add;

import enterprise.gui.controller.SourceableAdd;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the {@link enterprise.gui.controller.Controller} of the
 * {@code addNovel.fxml} file with {@link BasicMode#ADD} and {@link BasicModule#NOVEL}.
 */
public class AddNovel extends SourceableAdd implements Initializable {

    @Override
    public void open() {
        Stage stage = loadStage();
        stage.setResizable(false);
        setData(stage);
        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Novel hinzufügen");
        label.setText("Füge neuen Novel hinzu");
    }

    @Override
    public Module getModule() {
        return BasicModule.NOVEL;
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize();
        //ready GUI
        onCloseOperation();
        readyComboBoxes();
        readyInput();
        readySourceColumns();
        readyAddSourceBtn();
    }

    /**
     * Executes some Task on {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     */
    private void onCloseOperation() {
        Platform.runLater(() -> addBtn.getScene().getWindow().setOnCloseRequest(event -> System.out.println("do sth cool")));
    }
}
