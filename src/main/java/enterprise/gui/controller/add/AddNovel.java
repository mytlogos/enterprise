package enterprise.gui.controller.add;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.SourceableAdd;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * This class is the {@link enterprise.gui.controller.Controller} of the
 * {@code addNovel.fxml} file with {@link BasicMode#ADD} and {@link BasicModule#NOVEL}.
 */
public class AddNovel extends SourceableAdd {

    @Override
    public void open(CreationEntry entry) {
        Stage stage = loadStage(entry);
        stage.setResizable(false);
        setData(stage);
        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Novel hinzufügen");
        label.setText("Füge neuen Novel hinzu");
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    @Override
    public void initialize() {
        super.initialize();
        //ready GUI
        onCloseOperation();
        readyComboBoxes();
        readyInput();
        readySourceColumns();
        readyAddSourceBtn();
    }

    @Override
    public Module getModule() {
        return BasicModule.NOVEL;
    }

    /**
     * Executes some Task on {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST}.
     */
    private void onCloseOperation() {
        Platform.runLater(() -> addBtn.getScene().getWindow().setOnCloseRequest(event -> System.out.println("do sth cool")));
    }
}
