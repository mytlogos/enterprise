package Enterprise.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
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
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       addPC.disableProperty().bind(name.textProperty().isEmpty().not());
    }
}
