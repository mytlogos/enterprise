package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
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

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().dateLastPortionProperty();
    }

    @Override
    public String getName() {
        Module module = getModule();
        return module == BasicModule.ANIME ? "Datum letzter Folge" :
                module == BasicModule.BOOK ? "Datum letztes Kapitel" :
                        module == BasicModule.MANGA ? "Datum letztes Kapitel" :
                                module == BasicModule.NOVEL ? "Datum letztes Kapitel" :
                                        module == BasicModule.SERIES ? "Datum letzter Folge" :
                                                "Fehler";
    }

    protected void initDefault() {
        setDefault(6, 80, true);
    }
}
