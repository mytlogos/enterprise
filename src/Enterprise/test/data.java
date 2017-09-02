package Enterprise.test;

import Enterprise.data.intface.ConHandler;
import Enterprise.data.intface.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by Dominik on 29.06.2017.
 * Part of OgameBot.
 */
public class data {


    public static StringBuilder fetchXmlContent(String url) throws IOException {
        StringBuilder xmlContent = new StringBuilder();
        org.jsoup.Connection.Response document = Jsoup.connect(url).execute();

        Document document1 = document.parse();

        Whitelist whitelist = Whitelist.relaxed();
        whitelist.addAttributes("span", "class");
        whitelist.addAttributes("div", "class");
        whitelist.addAttributes("span", "time");
        whitelist.addAttributes("div", "time");
        Cleaner cleaner = new Cleaner(whitelist);

        System.out.println(cleaner.isValid(document1));
        System.out.println(cleaner.isValidBodyHtml(document1.body().html()));
        document1 = cleaner.clean(document1);

        Elements postcontent = document1.getElementsByClass("post-body entry-content");

        Document document2 = Jsoup.parse(postcontent.html());

        xmlContent.append(document2.html());
        return xmlContent;
    }

    public static void saveXmlFile(StringBuilder xmlContent, String saveLocation) throws IOException {
        FileWriter fileWriter = new FileWriter(saveLocation);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(xmlContent.toString());
        bufferedWriter.close();
        System.out.println("Downloading completed successfully..!");
    }

    public static void downloadXml() throws IOException {
        String url = "http://www.sousetsuka.com/2017/08/death-march-kara-hajimaru-isekai_28.html";
        String saveLocation = System.getProperty("user.dir") + "\\sousetsuka-testchapter8.html";
        saveXmlFile(fetchXmlContent(url), saveLocation);
    }

    public static void main(String[] args) throws IOException {
        downloadXml();
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

    private static Boolean getaBoolean(String select, User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, user.getOwnStatus());
            statement.setString(2, user.getComment());
            statement.setString(3, user.getListName());
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
