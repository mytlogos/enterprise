package Enterprise.test;

import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.general.Column;
import Enterprise.gui.general.ColumnManager;
import Enterprise.gui.general.ContentColumns;
import Enterprise.modules.BasicModules;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class testScraper extends Application {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String s = "asdasji21312ioj0asdjklycjök1)(=,asdi90a+sa";
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new VBox();
        Scene scene = new Scene(pane, 1500, 500);
        primaryStage.setScene(scene);
        TableView<CreationEntry> entryTable = new TableView<>();
        pane.getChildren().add(entryTable);

        primaryStage.show();


        List<Column<CreationEntry>> columns = new ContentColumns<>().asList();
        for (Column<CreationEntry> column : columns) {
            column.setColumnModule(BasicModules.ANIME);
        }
        ColumnManager<CreationEntry> columnManager = new ColumnManager<>(entryTable, columns);


        Button show = new Button("show");
        Button hide = new Button("hide");
        show.setOnAction(event -> {
            if (entryTable.getColumns().isEmpty()) {
                for (Column<CreationEntry> column : columns) {
                    columnManager.showColumn(column);
                    System.out.println("Name: " + column.getName());
                }
            }
        });
        hide.setOnAction(event -> {
            for (Column<CreationEntry> column : columns) {
                columnManager.hideColumn(column);
            }
        });

        pane.getChildren().addAll(hide, show);
    }
}
