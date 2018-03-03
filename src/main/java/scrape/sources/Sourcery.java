package scrape.sources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostScraper;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class for testing purposes.
 */
public class Sourcery implements Initializable {

    PostScraper host;
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

    public Sourcery() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doItBtn.disableProperty().bind(urlField.textProperty().isEmpty().or(matchField.textProperty().isEmpty()));
    }

    @FXML
    void displayPost(ActionEvent event) {
        Post post = postsBox.getValue();
        title.setText(post.getTitle());
        content.setText(post.getContent());
        footer.setText(post.getFooter());
    }

    @FXML
    void getPost() {

    }
}
