package enterprise.gui.general.Columns;

import enterprise.data.intface.SourceableEntry;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 */
public class TranslatorColumn extends AbstractColumn<SourceableEntry, String> {
    public TranslatorColumn(Module module) {
        super("translator", module);
    }

    @Override
    public Callback<TableColumn.CellDataFeatures<SourceableEntry, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getSourceable().translatorProperty();
    }

    @Override
    public String getName() {
        return getModule() == BasicModule.ANIME ? "SubGruppe" :
                getModule() == BasicModule.MANGA ? "Übersetzer" :
                        getModule() == BasicModule.NOVEL ? "Übersetzer" :
                                "Fehler";
    }

    protected void initDefault() {
        setDefault(12, 80, false);
    }
}
