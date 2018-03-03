package enterprise.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class TableViewer extends Application implements Initializable {


    private AnchorPane sideBar = null;
    private Stage stage = null;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField name;
    @FXML
    private Button addUrl;
    @FXML
    private Button addPC;
    @FXML
    private WebView browser;
    @FXML
    private Button show;
    @FXML
    private BorderPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TableView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addPC.disableProperty().bind(name.textProperty().isEmpty().not());
    }

    @FXML
    void addInternet() {
        try {
            URI uri = new URI(name.getText());
            WebEngine engine = browser.getEngine();
            engine.load(uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addLocal() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("Html files (*.html)", "*.html");
        fileChooser.getExtensionFilters().addAll(extFilterJPG);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            WebEngine engine = browser.getEngine();
            engine.load(file.toURI().toString());
        }
    }

    @FXML
    void showSideBar(ActionEvent event) throws IOException {
        Window window = root.getScene().getWindow();
        showOutSiteStage(window);
    }

    private void showOutSiteStage(Window window) throws IOException {
        if (stage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("extendWindow.fxml"));
            AnchorPane loadedPane = loader.load();

            stage = new Stage();
            stage.initOwner(window);
            stage.setTitle("Posts");
            stage.setScene(new Scene(loadedPane));

            int closeWindowGap = -0;

            window.xProperty().addListener((observable, oldValue, newValue) -> stage.setX(newValue.doubleValue() + window.getWidth() + closeWindowGap));
            window.yProperty().addListener((observable, oldValue, newValue) -> stage.setY(newValue.doubleValue()));

            window.widthProperty().addListener((observable, oldValue, newValue) -> stage.setX(window.getX() + newValue.doubleValue() + closeWindowGap));

            stage.minHeightProperty().bind(window.heightProperty());
            stage.maxHeightProperty().bind(window.heightProperty());

            stage.setX(window.getX() + window.getWidth() + closeWindowGap);
            stage.setHeight(window.getHeight());
            stage.setY(window.getY());
            stage.setMaxWidth(300);
            stage.setMinWidth(300);

            stage.show();
        } else {
            stage.close();
            stage = null;
        }
    }

    private void showWithinStage(Window window) throws IOException {
        if (sideBar == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("extendWindow.fxml"));
            sideBar = loader.load();

            double loadedHeight = sideBar.getPrefHeight();
            double loadedWidth = sideBar.getPrefWidth();

            double windowWidth = window.getWidth();
            double windowHeight = window.getHeight();

            window.setWidth(windowWidth + loadedWidth);

            if (windowHeight < loadedHeight) {
                window.setHeight(loadedHeight);
            }

            System.out.println(window.getHeight());
            System.out.println(window.getWidth());

            root.setRight(sideBar);
        } else {
            window.setWidth(window.getWidth() - sideBar.getWidth());
            root.setRight(null);
            sideBar = null;
        }
    }
}
