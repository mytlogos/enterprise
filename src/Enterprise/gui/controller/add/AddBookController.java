package Enterprise.gui.controller.add;

import Enterprise.gui.controller.AddController;
import Enterprise.modules.BasicModules;
import javafx.stage.Stage;

/**
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddBookController extends AddController<BasicModules> {
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Buch hinzufügen");
    }

    @Override
    protected void setModule() {
        module = BasicModules.BOOK;
    }
}
