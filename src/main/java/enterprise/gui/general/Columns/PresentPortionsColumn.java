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
public class PresentPortionsColumn<E extends CreationEntry> extends AbstractColumn<E, Number> {
    public PresentPortionsColumn(Module module) {
        super("presentPortions", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().processedPortionProperty();
    }

    @Override
    public String getName() {
        return getModule() == BasicModule.ANIME ? "Anzahl Episoden" :
                getModule() == BasicModule.BOOK ? "Anzahl Kapitel" :
                        getModule() == BasicModule.MANGA ? "Anzahl Kapitel" :
                                getModule() == BasicModule.NOVEL ? "Anzahl Kapitel" :
                                        getModule() == BasicModule.SERIES ? "Anzahl Episoden" :
                                                "Fehler";
    }

    protected void initDefault() {
        setDefault(8, 80, true);
    }
}
