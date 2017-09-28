package Enterprise.gui.series.controller;

import Enterprise.data.impl.CreationEntryImpl;
import Enterprise.data.impl.CreationImpl;
import Enterprise.data.impl.CreatorImpl;
import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.Creator;
import Enterprise.gui.controller.ContentController;
import Enterprise.modules.BasicModules;

/**
 * Created by Dominik on 24.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class SeriesController extends ContentController<CreationEntry, BasicModules> {
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
        titleField.setPromptText("Titel");
        creatorField.setPromptText("Autor");
    }

    @Override
    public void open() {
        throw new IllegalAccessError();
    }

    public void paneFocus() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.SERIES;
    }
}
