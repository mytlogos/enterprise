package Enterprise.gui.manga.controller;

import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.controller.EditController;
import Enterprise.modules.BasicModules;
import javafx.stage.Stage;

/**
 * Created by Dominik on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class EditMangaController extends EditController<SourceableEntry, BasicModules> {

    @Override
    public void open() {

    }

    @Override
    protected void setModule() {
        module = BasicModules.MANGA;
    }

    @Override
    protected void setData(Stage stage) {

    }
}
