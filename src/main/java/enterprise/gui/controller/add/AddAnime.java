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
 * This class is the controller of {@code addAnime.fxml} with {@link BasicModule#ANIME}
 * and {@link BasicMode#ADD}.
 */
public class AddAnime extends SourceableAdd implements Initializable {

    /**
     * Opens a new Window with the specified {@link enterprise.gui.general.Mode}
     * and {@link enterprise.modules.Module}.
     */
    public void open() {
        final Stage stage = loadStage();

        stage.setResizable(false);
        stage.show();
    }

    @Override
    public Module getModule() {
        return BasicModule.ANIME;
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize();
        onCloseOperation();

        //ready the graphical interface components
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

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("AnimeContent hinzufügen");
        label.setText("Füge neuen anime hinzu");
    }
}
