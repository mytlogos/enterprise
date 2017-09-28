package Enterprise.gui.enterprise.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import scrape.Post;

import java.io.IOException;

/**
 * Created by Dominik on 30.07.2017.
 * Part of OgameBot.
 */
@Deprecated
public class PostCell extends ListCell<Post> {

    @FXML
    public AnchorPane postPane;

    @FXML
    private Text title;

    @FXML
    private Text date;

    @FXML
    private Label content;

    @FXML
    private Text footer;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Post item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("../fxml/PostCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (title == null ||  content == null) {
                System.out.println(title);
                System.out.println(content);
                System.out.println(footer);
            } else {
                title.setText(item.getTitle());
                content.setText(item.getContent().toString());
                date.setText(item.getTime());
                content.setWrapText(true);
                setGraphic(postPane);
            }
        }
    }
}
