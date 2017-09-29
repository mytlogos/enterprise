package Enterprise.gui.general;

import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public interface Column<V> {
    <T> Callback<TableColumn.CellDataFeatures<V, T>, ObservableValue<T>> getCallBack();

    double getPrefWidth();

    String getName();

    boolean getDefaultSelect();

    void setColumnModule(Module module);

}
