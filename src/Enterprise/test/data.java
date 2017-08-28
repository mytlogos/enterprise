package Enterprise.test;

import Enterprise.data.intface.ConHandler;
import Enterprise.data.intface.User;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Created by Dominik on 29.06.2017.
 * Part of OgameBot.
 */
public class data extends Application {

    private static Function<Connection, Object> connectionRFunction;

    private static ImageView coverImage = new ImageView();

    static class afrik {
        static AtomicInteger integer = new AtomicInteger(1);
        static List<Integer> reusableInts = new ArrayList<>();

        static int creatId() {
            if (reusableInts.isEmpty()) {
                return integer.getAndIncrement();
            } else {
                int id = reusableInts.get(0);
                reusableInts.remove(0);
                return id;
            }
        }

        static void deleteId(int id) {
            reusableInts.add(id);
        }
    }

    private static  <E> E getConnection(ConHandler<Connection, E> connectionRFunction) {
        E result = null;
        try (Connection connection = connection()) {
            try {
                connection.setAutoCommit(false);
                result = connectionRFunction.handle(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String coverPath = "";
    private static String nonRelativeCoverPath = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        HashMap<String, WeakReference<Object>> hashMap = new HashMap<>();
        launch();

    }

    protected static void addLocalImage() {
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
            coverImage.setImage(img);
            nonRelativeCoverPath = file.toURI().toString();
            //relativize path against home directory
            coverPath = new File("").toURI().relativize(file.toURI()).toString();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        addLocalImage();
        System.out.println(coverPath);
        System.out.println(nonRelativeCoverPath);
        URI uri1 = new URI(coverPath);
        URI uri2 = new URI(nonRelativeCoverPath);
        System.out.println(uri1);
        System.out.println(uri2);
        String path = "img/touhouxumaru.jpg";
        File file = new File(path);
        System.out.println(file);
        System.out.println(file.toURI());
        System.out.println(file.toString());
        System.out.println(path);
    }

    private static Boolean getaBoolean(String select, User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, user.getOwnStatus());
            statement.setString(2, user.getComment());
            statement.setString(3, user.getList());
            statement.setInt(4, user.getProcessedPortion());
            statement.setInt(5, user.getRating());
            statement.setString(6, user.getKeyWords());

            ResultSet set = statement.executeQuery();
            System.out.println("Closed: " + statement.isClosed());
            while (set.next()) {
                System.out.println(set.getInt(1));
            }
        }
        return false;
    }

    private static Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
