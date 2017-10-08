package Enterprise.gui.controller;

import Enterprise.data.impl.*;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.Columns.Column;
import Enterprise.gui.general.Columns.TranslatorColumn;
import Enterprise.modules.Module;

import java.util.List;

/**
 * This class represents an corresponding extension of {@code ContentController}
 * for all controller which handle {@link Enterprise.data.intface.SourceableEntry}s.
 */
public abstract class SourceableContentCont<R extends Enum<R> & Module> extends ContentController<SourceableEntry, R> {
    @Override
    protected List<Column<SourceableEntry, ?>> getColumnList() {
        List<Column<SourceableEntry, ?>> columns = super.getColumnList();

        TranslatorColumn translatorColumn = new TranslatorColumn(module);
        translatorColumn.setColumnModule(module);
        translatorColumn.loadMenuItem(this);
        columns.add(translatorColumn);

        return columns;
    }

    @Override
    protected SourceableEntry getSimpleEntry() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new CreatorImpl.CreatorBuilder(author).build();
        Creation creation = new CreationImpl.CreationImplBuilder(title).build();

        return new SourceableEntryImpl(new SimpleUser(), creation, creator, new SourceableImpl(), module);
    }
}
