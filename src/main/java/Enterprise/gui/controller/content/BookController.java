package Enterprise.gui.controller.content;

import Enterprise.ControlComm;
import Enterprise.data.impl.CreationEntryImpl;
import Enterprise.data.impl.CreationImpl;
import Enterprise.data.impl.CreatorImpl;
import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.Creator;
import Enterprise.gui.controller.ContentController;
import Enterprise.modules.BasicModules;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dominik on 24.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class BookController extends ContentController<CreationEntry, BasicModules> implements Initializable {

    @Override
    protected CreationEntry getSimpleEntry() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new CreatorImpl.CreatorBuilder(author).build();
        Creation creation = new CreationImpl.CreationImplBuilder(title).build();

        return new CreationEntryImpl(new SimpleUser(), creation, creator, module);
    }

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
        module = BasicModules.BOOK;
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
