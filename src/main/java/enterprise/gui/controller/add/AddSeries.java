package enterprise.gui.controller.add;

import enterprise.gui.controller.Add;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.stage.Stage;

/**
 * Created on 25.06.2017.
 * Part of OgameBot.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class AddSeries extends Add {
    @Override
    public void open() {
        Stage stage = loadStage();

        stage.setResizable(false);
        setData(stage);

        stage.show();
    }

    @Override
    protected void setData(Stage stage) {
        stage.setTitle("Serie hinzufügen");
    }

    @Override
    public Module getModule() {
        return BasicModule.SERIES;
    }
}
