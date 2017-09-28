package Enterprise.test;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import scrape.Post;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Dominik on 02.08.2017.
 * Part of OgameBot.
 */
public class DisplayPost implements Initializable{
    @FXML
    private TextArea content;

    private static Post elements;

    public void open(Post elements) throws IOException {
        DisplayPost.elements = elements;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DisplayPost.fxml"));
        Parent root = loader.load();

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Post");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(elements.getTitle()).append("\t\t\t").append(elements.getTime()).append("\n\n");
        elements.getContent().forEach(s -> stringBuilder.append(s).append("\n"));
        stringBuilder.append("\n").append(elements.getFooter()).append("\n\n");


        content.setText(stringBuilder.toString());
    }
}
