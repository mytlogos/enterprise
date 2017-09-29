package Enterprise.gui.general;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.List;

/**
 *
 */
public class SourceableColumns extends ContentColumns<SourceableEntry> {
    private Column<SourceableEntry> Translator = new Column<SourceableEntry>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<SourceableEntry, String>, ObservableValue<String>> getCallBack() {
            return data -> data.getValue().getSourceable().translatorProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return module == BasicModules.ANIME ? "SubGruppe" :
                    module == BasicModules.MANGA ? "Übersetzer" :
                            module == BasicModules.NOVEL ? "Übersetzer" :
                                    "Fehler";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };

    @Override
    public List<Column<SourceableEntry>> asList() {
        List<Column<SourceableEntry>> list = super.asList();
        list.add(Translator);
        return list;
    }
}
