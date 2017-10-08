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
public class ProcessedPortionsColumn<E extends CreationEntry> extends AbstractColumn<E, Number> {
    public ProcessedPortionsColumn(Module module) {
        super("processedPortions", module);
    }

    protected void initDefault() {
        setDefault(4, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().processedPortionProperty();
    }

    @Override
    public String getName() {
        return module == BasicModules.ANIME ? "Gesehen" :
                module == BasicModules.BOOK ? "Gelesen" :
                        module == BasicModules.MANGA ? "Gelesen" :
                                module == BasicModules.NOVEL ? "Gelesen" :
                                        module == BasicModules.SERIES ? "Gesehen" :
                                                "Fehler";
    }
}
