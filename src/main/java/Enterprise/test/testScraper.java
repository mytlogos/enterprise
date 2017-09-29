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
        System.out.println(testScraper.class.getResource("D:\\Programmieren\\Java\\Projects\\Enterprise\\src\\main\\java\\Enterprise\\gui\\fxml\\add.fxml"));
        System.out.println(DriverManager.getConnection("jdbc:sqlite:enterprise.db"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
