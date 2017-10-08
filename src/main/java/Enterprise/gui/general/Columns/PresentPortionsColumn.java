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
public class PresentPortionsColumn<E extends CreationEntry> extends AbstractColumn<E, Number> {
    public PresentPortionsColumn(Module module) {
        super("presentPortions", module);
    }

    protected void initDefault() {
        setDefault(8, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().processedPortionProperty();
    }

    @Override
    public String getName() {
        return module == BasicModules.ANIME ? "Anzahl Episoden" :
                module == BasicModules.BOOK ? "Anzahl Kapitel" :
                        module == BasicModules.MANGA ? "Anzahl Kapitel" :
                                module == BasicModules.NOVEL ? "Anzahl Kapitel" :
                                        module == BasicModules.SERIES ? "Anzahl Episoden" :
                                                "Fehler";
    }
}
