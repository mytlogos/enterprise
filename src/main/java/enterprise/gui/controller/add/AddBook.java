package enterprise.gui.controller.add;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Add;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddBook extends Add {
    @Override
    public void open(CreationEntry entry) {
        Stage stage = loadStage(entry);

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Buch hinzufügen");
    }

    @Override
    public Module getModule() {
        return BasicModule.BOOK;
    }
}
