package enterprise.gui.controller.edit;

import enterprise.data.impl.SourceableEntryImpl;
import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Edit;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * Created on 25.06.2017.
 * <p>
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class EditSeries extends Edit<SourceableEntryImpl> {

    @Override
    public void open(CreationEntry entry) {

    }

    @Override
    public Module getModule() {
        return BasicModule.SERIES;
    }

    @Override
    protected void setData(Stage stage) {

    }
}
