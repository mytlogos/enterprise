package Enterprise.gui.controller;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.Columns;
import Enterprise.modules.Module;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * This class represents an corresponding extension of {@code ContentController}
 * for all controller which handle {@link Enterprise.data.intface.SourceableEntry}s.
 */
public abstract class SourceableContentCont<R extends Enum<R> & Module> extends ContentController<SourceableEntry, R> {
    private TableColumn<SourceableEntry, String> translatorColumn;

    /**
     * Adds the translator column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public void showTranslatorColumn() {
        translatorColumn = stringColumnFactory(Columns.getTranslator(module), 80,
                data -> data.getValue().getSourceable().translatorProperty());
        //'shows' the column in the TableView
        entryTable.getColumns().add(translatorColumn);
    }

    /**
     * Removes the Translator Column from the {@code entryTable TableView}.
     */
    public void hideTranslatorColumn() {
        entryTable.getColumns().remove(translatorColumn);
    }
}
