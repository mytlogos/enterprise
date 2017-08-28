package Enterprise.gui.controller;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.EnterpriseSegments;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * This class represents an corresponding extension of {@code ModuleController}
 * for all controller which handle {@link Enterprise.data.intface.SourceableEntry}s.
 */
public abstract class SourceableModuleCont<E extends CreationEntry, R extends EnterpriseSegments> extends ModuleController<E,R> {
    protected TableColumn<E, String> translatorColumn;
    protected TableColumn<E, String> keyWordsColumn;

    /**
     * Adds the keyWords column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showKeyWordsColumn();

    /**
     * Adds the translator column to the {@code entryTable TableView}
     * after going through the {@link #stringColumnFactory(String, double, Callback)}.
     */
    public abstract void showTranslatorColumn();

    /**
     * Removes the Translator Column from the {@code entryTable TableView}.
     */
    public void hideTranslatorColumn() {
        entryTable.getColumns().remove(translatorColumn);
    }

    /**
     * Removes the keyWords Column from the {@code entryTable TableView}.
     */
    public void hideKeyWordsColumn() {
        entryTable.getColumns().remove(keyWordsColumn);
    }
}
