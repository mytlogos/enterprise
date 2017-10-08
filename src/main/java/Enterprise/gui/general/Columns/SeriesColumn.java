package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class SeriesColumn<E extends CreationEntry> extends AbstractColumn<E, String> {
    public SeriesColumn(Module module) {
        super("series", module);
    }

    protected void initDefault() {
        setDefault(7, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().getCreator().sortNameProperty();
    }

    @Override
    public String getName() {
        return "Reihe";
    }
}
