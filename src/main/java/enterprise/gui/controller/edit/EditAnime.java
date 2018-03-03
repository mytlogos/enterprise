package enterprise.gui.controller.edit;

import enterprise.data.impl.SourceableEntryImpl;
import enterprise.gui.controller.InputLimiter;
import enterprise.gui.controller.SourceableEdit;
import enterprise.gui.general.BasicMode;
import enterprise.misc.EntrySingleton;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * The controller of {@code editAnime.fxml} with
 * {@link BasicMode#EDIT} and {@link BasicModule#ANIME}.
 */
public class EditAnime extends SourceableEdit implements InputLimiter {

    @Override
    public void open() {
        Stage stage = loadStage();

        setData(stage);
        stage.setResizable(false);

        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("AnimeContent bearbeiten");
    }

    @Override
    public void initialize() {
        super.initialize();
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

    @Override
    public Module getModule() {
        return BasicModule.ANIME;
    }
}
