package Enterprise.gui.general;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public interface Column<V> {
    <E> Callback<TableColumn.CellDataFeatures<V, E>, ObservableValue<E>> getCallBack();

    double getPrefWidth();

    String getName();

    boolean getDefaultSelect();
}
