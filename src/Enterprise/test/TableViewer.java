package Enterprise.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class TableViewer extends Application implements Initializable {


    @FXML
    private AnchorPane pane;

    @FXML
    private TextField name;

    @FXML
    private Button addUrl;

    @FXML
    private Button addPC;

    @FXML
    private ImageView image;


    @FXML
    void addInternet(ActionEvent event) {
        try {
            URI uri = new URI(name.getText());
            Connection.Response resultImageResponse = Jsoup.connect(uri.toString()).ignoreContentType(true).execute();

            String path = uri.getPath();

            int slash = path.lastIndexOf("/");

            String file = path.substring(slash,path.length());

            File imageFile = new File("img\\" + file);

            FileOutputStream out = (new FileOutputStream(imageFile));
            out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
            out.close();

            Image img = new Image(imageFile.toURI().toString());

            image.setImage(img);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void addLocal(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        fileChooser.setInitialDirectory(new File("img"));

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image img = new Image(file.toURI().toString());
            image.setImage(img);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TableView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    AnchorPane sideBar = null;
    Stage stage = null;
    @FXML
    private Button show;
    @FXML
    private BorderPane root;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       addPC.disableProperty().bind(name.textProperty().isEmpty().not());
    }
}
