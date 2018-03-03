package enterprise.gui.controller.edit;

import enterprise.data.impl.SourceableEntryImpl;
import enterprise.gui.controller.SourceableEdit;
import enterprise.misc.EntrySingleton;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class EditNovel extends SourceableEdit implements Initializable {
    @Override
    public void open() {
        Stage stage = loadStage();

        setData(stage);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Novel bearbeiten");
    }

    @Override
    public Module getModule() {
        return BasicModule.NOVEL;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //loads entry to edit
        creationEntry = (SourceableEntryImpl) EntrySingleton.getInstance().getEntry();

        //loads entry to the nodes
        loadEntry();

        //ready the gui
        readySourceColumns();
        readyInput();
        readyComboBoxes();
        onCloseOperation();
        disableAddSource();
    }
}
