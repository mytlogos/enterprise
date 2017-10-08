package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
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

    protected void initDefault() {
        setDefault(2, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getCreation().getCreator().nameProperty();
    }

    @Override
    public String getName() {
        return "Autor";
    }

}
