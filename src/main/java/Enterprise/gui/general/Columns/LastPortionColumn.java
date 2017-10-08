package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class LastPortionColumn<E extends CreationEntry> extends AbstractColumn<E, String> {
    public LastPortionColumn(Module module) {
        super("lastPortion", module);
    }

    protected void initDefault() {
        setDefault(6, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().dateLastPortionProperty();
    }

    @Override
    public String getName() {
        return module == BasicModules.ANIME ? "Datum letzter Folge" :
                module == BasicModules.BOOK ? "Datum letztes Kapitel" :
                        module == BasicModules.MANGA ? "Datum letztes Kapitel" :
                                module == BasicModules.NOVEL ? "Datum letztes Kapitel" :
                                        module == BasicModules.SERIES ? "Datum letzter Folge" :
                                                "Fehler";
    }
}
