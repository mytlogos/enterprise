package Enterprise.gui.controller.add;

import Enterprise.gui.controller.SourceableAddController;
import Enterprise.modules.BasicModules;
import javafx.stage.Stage;

/**
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddMangaController extends SourceableAddController<BasicModules> {

    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    protected void setModule() {
        module = BasicModules.MANGA;
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Manga hinzufügen");
        label.setText("Füge neuen Manga hinzu");
    }
}
