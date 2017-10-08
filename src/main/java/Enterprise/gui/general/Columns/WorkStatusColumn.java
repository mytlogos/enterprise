package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class WorkStatusColumn<E extends CreationEntry> extends AbstractColumn<E, String> {

    public WorkStatusColumn(Module module) {
        super("workStatus", module);
    }

    protected void initDefault() {
        setDefault(5, 120, false);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().workStatusProperty();
    }

    @Override
    public String getName() {
        return "Status";
    }
}
