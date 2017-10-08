package Enterprise.gui.general.Columns;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.Module;
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

    protected void initDefault() {
        setDefault(11, 80, true);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
        return param -> param.getValue().getUser().ratingProperty();
    }

    @Override
    public String getName() {
        return "Bewertung";
    }
}
