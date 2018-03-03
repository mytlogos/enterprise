package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class CommentColumn<E extends CreationEntry> extends AbstractColumn<E, String> {

    public CommentColumn(Module module) {
        super("comment", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getUser().commentProperty();
    }

    @Override
    public String getName() {
        return "Kommentar";
    }

    protected void initDefault() {
        setDefault(8, 200, false);
    }
}
