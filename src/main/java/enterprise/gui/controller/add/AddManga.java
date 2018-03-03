package enterprise.gui.controller.add;

import enterprise.gui.controller.SourceableAdd;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddManga extends SourceableAdd {

    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    public Module getModule() {
        return BasicModule.MANGA;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Manga hinzufügen");
        label.setText("Füge neuen MangaContent hinzu");
    }
}
