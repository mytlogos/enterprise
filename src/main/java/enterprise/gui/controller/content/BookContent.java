package enterprise.gui.controller.content;

import enterprise.data.impl.CreationBuilder;
import enterprise.data.impl.CreationEntryImpl;
import enterprise.data.impl.CreatorImpl;
import enterprise.data.impl.UserImpl;
import enterprise.data.intface.Creation;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.Creator;
import enterprise.gui.controller.Content;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.scene.control.TableView;

/**
 * Created on 24.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class BookContent extends Content<CreationEntry> {

    @Override
    public void initialize() {
        super.initialize();

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
    protected CreationEntry getSimpleEntry() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new CreatorImpl.CreatorBuilder(author).build();
        Creation creation = new CreationBuilder(title).build();

        return new CreationEntryImpl(new UserImpl(), creation, creator, getModule());
    }

    @Override
    public Module getModule() {
        return BasicModule.BOOK;
    }

    @Override
    protected void setGui() {
        super.setGui();
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }
}
