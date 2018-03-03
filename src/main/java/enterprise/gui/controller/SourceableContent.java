package enterprise.gui.controller;

import enterprise.data.impl.*;
import enterprise.data.intface.Creation;
import enterprise.data.intface.Creator;
import enterprise.data.intface.SourceableEntry;
import enterprise.gui.general.Columns.Column;
import enterprise.gui.general.Columns.TranslatorColumn;

import java.util.List;

/**
 * This class represents an corresponding extension of {@code Content}
 * for all controller which handle {@link enterprise.data.intface.SourceableEntry}s.
 */
public abstract class SourceableContent extends Content<SourceableEntry> {
    @Override
    protected List<Column<SourceableEntry, ?>> getColumnList() {
        List<Column<SourceableEntry, ?>> columns = super.getColumnList();

        TranslatorColumn translatorColumn = new TranslatorColumn(getModule());
        translatorColumn.setColumnModule(getModule());
        translatorColumn.loadMenuItem(this);
        columns.add(translatorColumn);

        return columns;
    }

    @Override
    protected SourceableEntry getSimpleEntry() {
        String author = creatorField.getText();
        String title = titleField.getText();

        Creator creator = new CreatorImpl.CreatorBuilder(author).build();
        Creation creation = new CreationBuilder(title).build();

        return new SourceableEntryImpl(new UserImpl(), creation, creator, SourceableImpl.get(), getModule());
    }
}
