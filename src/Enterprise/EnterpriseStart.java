package Enterprise;

import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class
EnterpriseStart extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(Module.ENTERPRISE,Mode.CONTENT)));
        Parent root = loader.load();

        primaryStage.setTitle("Enterprise");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
