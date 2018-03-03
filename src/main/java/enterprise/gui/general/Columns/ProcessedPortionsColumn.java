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
public class ProcessedPortionsColumn<E extends CreationEntry> extends AbstractColumn<E, Number> {
    public ProcessedPortionsColumn(Module module) {
        super("processedPortions", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().processedPortionProperty();
    }

    @Override
    public String getName() {
        return getModule() == BasicModule.ANIME ? "Gesehen" :
                getModule() == BasicModule.BOOK ? "Gelesen" :
                        getModule() == BasicModule.MANGA ? "Gelesen" :
                                getModule() == BasicModule.NOVEL ? "Gelesen" :
                                        getModule() == BasicModule.SERIES ? "Gesehen" :
                                                "Fehler";
    }

    protected void initDefault() {
        setDefault(4, 80, true);
    }
}
