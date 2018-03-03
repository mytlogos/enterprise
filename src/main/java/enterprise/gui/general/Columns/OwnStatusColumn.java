package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class OwnStatusColumn<E extends CreationEntry> extends AbstractColumn<E, String> {
    public OwnStatusColumn(Module module) {
        super("ownStatus", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getUser().ownStatusProperty();
    }

    @Override
    public String getName() {
        return "Eigener Status";
    }

    protected void initDefault() {
        setDefault(5, 80, true);
    }
}
