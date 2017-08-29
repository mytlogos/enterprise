package scrape.sources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import scrape.sources.novels.NovelPosts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    NovelPosts host;

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
    void getPost(ActionEvent event) throws URISyntaxException, IOException {
        /*
        String url = urlField.getText();
        String match = matchField.getText();
        host = new NovelPosts(url);
        Elements elements = host.getPosts(match);
        postsBox.getItems().addAll(host.toPosts(elements));
        */

        List<String> uris = new ArrayList<>();
        uris.add("https://honyakusite.wordpress.com/");
        uris.add("http://www.wuxiaworld.com/");
        uris.add("http://eccentrictranslations.com/");
        uris.add("https://hikkinomori.mistbinder.org/");
        uris.add("https://weitranslations.wordpress.com/");
        uris.add("http://www.sousetsuka.com/");
        uris.add("http://moonbunnycafe.com/");
        uris.add("https://kobatochan.com/");
        uris.add("https://larvyde.wordpress.com/");
        uris.add("http://jigglypuffsdiary.com/");
        uris.add("https://www.oppatranslations.com/");
        uris.add("https://defiring.wordpress.com/");
        uris.add("https://mayonaizeshrimp.wordpress.com/");
        uris.add("http://zenithnovels.com/");
        uris.add("http://volarenovels.com/");
        uris.add("http://gravitytales.com/");
        uris.add("http://www.oyasumireads.com/");
        uris.add("https://lightnovelbastion.com/");
        uris.add("https://shintranslations.com/");
        uris.add("https://isekailunatic.wordpress.com/");
        host = new NovelPosts();
        for (String s : uris) {

            postsBox.getItems().addAll(host.getPosts(s));
            postsBox.getSelectionModel().select(0);
            /*
            Element element;
            if (posts.size() != 0) {
                element = posts.get(0);
            } else {
                element = new Element("");
            }


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scrape/sources/displayPost.fxml"));
            Parent root = loader.load();
            DisplayPost displayPost = loader.getController();
            displayPost.setText(element.toString());
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Enterprise");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            */
        }

        Post post = postsBox.getItems().get(0);

        Notifications.create().title(post.getTitle()).text(post.getContentString()).hideAfter(Duration.seconds(10)).showInformation();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doItBtn.disableProperty().bind(urlField.textProperty().isEmpty().or(matchField.textProperty().isEmpty()));
    }
}
