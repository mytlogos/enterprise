package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class CreatorNameColumn<E extends CreationEntry> extends AbstractColumn<E, String> {
    public CreatorNameColumn(Module module) {
        super("creatorName", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().getCreator().nameProperty();
    }

    @Override
    public String getName() {
        return "Autor";
    }

    protected void initDefault() {
        setDefault(2, 80, true);
    }

}
