package Enterprise.test;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class testScraper extends Application {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, SQLException, ClassNotFoundException {
        String s = testScraper.class.getClassLoader().getResource("fxml/add.fxml").toString();
        System.out.println(s);
        System.out.println(DriverManager.getConnection("jdbc:sqlite:enterprise.db"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
