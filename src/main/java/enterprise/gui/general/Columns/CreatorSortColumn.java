package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class CreatorSortColumn<E extends CreationEntry> extends AbstractColumn<E, String> {

    public CreatorSortColumn(Module module) {
        super("creatorSort", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().getCreator().sortNameProperty();
    }

    @Override
    public String getName() {
        return "Autorsortierung";
    }

    protected void initDefault() {
        setDefault(9, 120, false);
    }
}
