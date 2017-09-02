package Enterprise.gui.manga.controller;

import Enterprise.data.impl.*;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.SourceableContentCont;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;

/**
 * The {@link BasicModes#CONTENT} Controller of the {@code manga.fxml} file.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class MangaController extends SourceableContentCont<BasicModules> {

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
    public void open() {
        throw new IllegalAccessError();
    }

    public void paneFocus() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.MANGA;
    }
}
