package enterprise.test;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import scrape.sources.posts.Post;

/**
 * Created on 02.08.2017.
 */
public class DisplayPost {
    private Post post;
    @FXML
    private TextArea content;

    public void open(Post post) {
        this.post = post;

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Post");
        primaryStage.setScene(new Scene(getPane()));
        primaryStage.show();
    }

    private Pane getPane() {
        Pane pane = new AnchorPane();
        pane.setPrefSize(600, 400);
        TextArea area = new TextArea();
        pane.getChildren().add(area);

        area.setPrefSize(600, 400);

        AnchorPane.setBottomAnchor(area, 0d);
        AnchorPane.setTopAnchor(area, 0d);
        AnchorPane.setLeftAnchor(area, 0d);
        AnchorPane.setRightAnchor(area, 0d);

        String postDisplay = String.format(
                "%s\t\t\t%s\n\n%s\n%s\n\n",
                post.getTitle(),
                post.getTime(),
                post.getContent(),
                post.getFooter());

        content.setText(postDisplay);

        return pane;
    }
}
