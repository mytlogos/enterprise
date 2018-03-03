package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class TitleColumn<E extends CreationEntry> extends AbstractColumn<E, String> {
    public TitleColumn(Module module) {
        super("title", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().titleProperty();
    }

    @Override
    public String getName() {
        return "Titel";
    }

    protected void initDefault() {
        setDefault(1, 120, true);
    }
}
