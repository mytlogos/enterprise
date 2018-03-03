package enterprise.gui.general.Columns;

import enterprise.data.intface.CreationEntry;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class RatingColumn<E extends CreationEntry> extends AbstractColumn<E, Number> {
    public RatingColumn(Module module) {
        super("rating", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().ratingProperty();
    }

    @Override
    public String getName() {
        return "Bewertung";
    }

    protected void initDefault() {
        setDefault(11, 80, true);
    }
}
