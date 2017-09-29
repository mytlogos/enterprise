package Enterprise.gui.controller.content;

import Enterprise.ControlComm;
import Enterprise.gui.controller.SourceableContentCont;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The {@link BasicModes#CONTENT} Controller of the {@code manga.fxml} file.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class MangaController extends SourceableContentCont<BasicModules> implements Initializable {

    @Override
    protected void setGui() {
        super.setGui();
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }

    @Override
    public void open() {
        throw new IllegalAccessError();
    }

    @Override
    protected void setModule() {
        module = BasicModules.MANGA;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setController(this, module, mode);

        //readies the graphical interface components
        setIndexColumn();
        //adjusts the size of the columns to fill the size of the table
        entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setGui();
        setRowListener();
        lockEditBtn();
        lockAddBtn();
    }
}
