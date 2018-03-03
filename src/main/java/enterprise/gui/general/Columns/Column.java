package enterprise.gui.general.Columns;

import Configs.Setting;
import enterprise.gui.controller.Content;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 */
public interface Column<V, R> extends Setting, Comparable<Column<V, R>> {
    Callback<TableColumn.CellDataFeatures<V, R>, ObservableValue<R>> getCallBack();

    double getPrefWidth();

    String getName();

    boolean getDefaultSelect();

    void setColumnModule(Module module);

    int getPrefIndex();

    CheckMenuItem getMenuItem();

    TableColumn<V, R> getTableColumn();

    void loadMenuItem(Content controller);

    void addColumnsListener(TableView<V> tableView);

    void setShown(boolean b);
}
