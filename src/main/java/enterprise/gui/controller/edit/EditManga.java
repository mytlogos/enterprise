package enterprise.gui.controller.edit;

import enterprise.data.intface.SourceableEntry;
import enterprise.gui.controller.Edit;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * Created on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class EditManga extends Edit<SourceableEntry> {

    @Override
    public void open() {

    }

    @Override
    public Module getModule() {
        return BasicModule.MANGA;
    }

    @Override
    protected void setData(Stage stage) {

    }
}
