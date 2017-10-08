package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class KeyWordsColumn<E extends CreationEntry> extends AbstractColumn<E, String> {


    public KeyWordsColumn(Module module) {
        super("keyWords", module);
    }

    protected void initDefault() {
        setDefault(10, 120, false);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getUser().keyWordsProperty();
    }

    @Override
    public String getName() {
        return "Stichwörter";
    }

}
