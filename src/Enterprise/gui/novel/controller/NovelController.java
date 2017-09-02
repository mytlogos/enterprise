package Enterprise.gui.novel.controller;

import Enterprise.ControlComm;
import Enterprise.data.impl.*;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableContentCont;
import Enterprise.gui.general.BasicModes;
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
    protected SourceableEntry getSimpleEntry() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new CreatorImpl.CreatorBuilder(author).build();
        Creation creation = new CreationImpl.CreationImplBuilder(title).build();

        return new SourceableEntryImpl(new SimpleUser(), creation, creator, new SimpleSourceable(), module);
    }

    @Override
    protected void setGui() {
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setController(this, BasicModules.NOVEL, BasicModes.CONTENT);

        //readies the graphical interface components
        setIndexColumn();
        //adjusts the size of the columns to fill the size of the table
        entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setGui();
        setRowListener();
        lockEditBtn();
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
