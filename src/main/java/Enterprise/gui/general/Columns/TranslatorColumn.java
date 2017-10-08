package Enterprise.gui.general.Columns;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
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

    protected void initDefault() {
        setDefault(12, 80, false);
    }


    @Override
    public Callback<TableColumn.CellDataFeatures<SourceableEntry, String>, ObservableValue<String>> getCallBack() {
        return param -> param.getValue().getSourceable().translatorProperty();
    }

    @Override
    public String getName() {
        return module == BasicModules.ANIME ? "SubGruppe" :
                module == BasicModules.MANGA ? "Übersetzer" :
                        module == BasicModules.NOVEL ? "Übersetzer" :
                                "Fehler";
    }
}
