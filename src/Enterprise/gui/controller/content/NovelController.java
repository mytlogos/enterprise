package Enterprise.gui.controller.content;

import Enterprise.ControlComm;
import Enterprise.gui.controller.SourceableContentCont;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dominik on 24.06.2017.
 * Part of OgameBot.
 */
public class NovelController extends SourceableContentCont<BasicModules> implements Initializable {

    @Override
    protected void setGui() {
        super.setGui();
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
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

    @Override
    protected void setModule() {
        module = BasicModules.NOVEL;
    }

    @Override
    public void open() {
        throw new IllegalAccessError();
    }

    public void paneFocus() {
// TODO: 25.08.2017 do sth
    }
}
