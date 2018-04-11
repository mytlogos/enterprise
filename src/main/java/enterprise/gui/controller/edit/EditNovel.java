package enterprise.gui.controller.edit;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.SourceableEdit;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 *
 */
public class EditNovel extends SourceableEdit {
    @Override
    public void open(CreationEntry entry) {
        Stage stage = loadStage(entry);

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
    public void initialize() {
        super.initialize();
        //loads entry to the nodes
        loadEntry();

        //ready the gui
        readySourceColumns();
        readyInput();
        readyComboBoxes();
        onCloseOperation();
        disableAddSource();
    }

    @Override
    public Module getModule() {
        return BasicModule.NOVEL;
    }
}
