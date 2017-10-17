package Enterprise.test;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Dominik on 10.07.2017.
 * Part of OgameBot.
 */
public class hallo extends Application {
    static int idCounter = 1;
    private static ArrayList<String> uris = new ArrayList<>();

    static String getNull() {
        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ParseException, SQLException {
        String decoded = "http://www.amazon.co.jp/%E7%81%B0%E3%81%A8%E7%8E%8B%E5%9B%BD1-%E5%8C%97%E8%BE%BA%E3%81%AE%E9%97%87-%E9%A2%A8%E7%BE%BD%E6%B4%B8%E6%B5%B7/dp/4047296155";
        System.out.println(decoded);
        System.out.println(URLDecoder.decode(decoded, "UTF-8"));
    }

    private static void getTime(String date) {
        if (date.contains("+00:00")) {
            int index = date.indexOf("+00:00");
            date = date.substring(0, index);
            LocalDateTime dateTime = LocalDateTime.parse(date);
            System.out.println(dateTime.toLocalDate());
        } else System.out.println(date);
    }

    private static void addUris() {
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
    }

    public static void scrapeTopic(String url) throws IOException {
        String URI = "http://www.wikipedia.org/" + url;
        String html = getUrl(URI);
        Document doc = Jsoup.parse(html);
        Document document = Jsoup.connect(URI).get();
        System.out.println(doc);
        String contentText = document.select("#mw-content-text > p").first().text();
        System.out.println(contentText);
    }

    private static String getUrl(String url) {
        URL urlObj;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("The url was malformed!");
            return "";
        }
        URLConnection urlCon;
        BufferedReader in;
        String outputText = "";
        try {
            urlCon = urlObj.openConnection();
            in = new BufferedReader(new
                    InputStreamReader(urlCon.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                outputText += line;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("There was an error connecting to the URL");
            return "";
        }
        return outputText;
    }

    private static String counter() {
        return null;
    }

    private static String setString(String column, String value, int id) {
        String tableName = "HIHAHOPTABLE";
        String update = "Update ";
        String set = " Set ";
        String where = " Where ID = " + id;

        StringBuilder setBuilder = new StringBuilder();

        setBuilder.append(set);

        setBuilder.append(column).append(" = '").append(value).append("'");

        return update + tableName +
                setBuilder + where;
    }

    private static String setString(String column, int value, int id) {
        String tableName = "HIHAHOPTABLE";
        String update = "Update ";
        String set = " Set ";
        String where = " Where ID = " + id;

        StringBuilder setBuilder = new StringBuilder();

        setBuilder.append(set);

        setBuilder.append(column).append(" = ").append(value);

        return update + tableName +
                setBuilder + where;
    }

    private static String firstToCap(String string) {
        String firstChar;
        String remnantChars;
        String remnant = string;
        String newString = "";
        System.out.println(string.indexOf(" "));
        int start = 0;
        int index = remnant.indexOf(" ");
        while (index != -1) {
            firstChar = remnant.substring(start, start + 1).toUpperCase();

            remnantChars = remnant.substring(start + 1, index);
            remnant = remnant.substring(index).trim();

            index = remnant.indexOf(" ");
            newString = newString.concat(" ").concat(firstChar.concat(remnantChars));
        }

        return newString;
    }

   /* private static String createTableStatement(List<String> columnList, String tableName) {
        StringBuilder statement = new StringBuilder("CREATE TABLE " + tableName + " (");
        ListIterator<String> iterator = columnList.listIterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            if (column.length() > 0) {
                statement.append(column.getColumnName())
                        .append(" ")
                        .append(column.getString())
                        .append("(")
                        .append(column.getCharLength())
                        .append(")");
            } else {
                statement.append(column.getColumnName())
                        .append(" ")
                        .append(column.getString());
            }
            if (iterator.hasNext()) {
                statement.append(", ");
            }
        }

        statement.append(")");
        return statement.getString();
    }*/

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*    FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/enterprise/fxml/postView.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Post");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        */

    }
}


