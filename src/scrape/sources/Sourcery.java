package scrape.sources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import scrape.Post;
import scrape.sources.novels.PostScraper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class for testing purposes.
 */
public class Sourcery implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField urlField;

    @FXML
    private TextField matchField;

    @FXML
    private Button doItBtn;

    @FXML
    private Label title;

    @FXML
    private Label content;

    @FXML
    private Label footer;

    @FXML
    private ComboBox<Post> postsBox;

    PostScraper host;

    public Sourcery() throws URISyntaxException {
    }

    @FXML
    void displayPost(ActionEvent event) {
        Post post = postsBox.getValue();
        title.setText(post.getTitle());
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : post.getContent()) {
            stringBuilder.append(s).append("\n");
        }
        content.setText(stringBuilder.toString());
        footer.setText(post.getFooter());
    }

    @FXML
    void getPost() throws URISyntaxException, IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doItBtn.disableProperty().bind(urlField.textProperty().isEmpty().or(matchField.textProperty().isEmpty()));
    }
}
