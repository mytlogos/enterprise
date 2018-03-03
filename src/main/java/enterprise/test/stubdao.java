package enterprise.test;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class stubdao {
    private static String safe = "";

    public static void main(String[] args) throws IOException {
        File file = new File("test.html");
        String html = Jsoup.parse(stubdao.class.getClassLoader().getResourceAsStream("sousetsuka-testchapter8.html"), "UTF-8", "sousetsuka.com").outerHtml();
        FileUtils.writeStringToFile(file, html, "UTF-8");
//        convertToEpub();
    }

    private static void convertToEpub() throws IOException {
        Process process = Runtime.getRuntime().exec("ebook-convert.exe D:\\Programmieren\\Java\\Projects\\enterprise\\src\\main\\resources\\test.html D:\\Programmieren\\Java\\Projects\\enterprise\\ebooks\\testEbook.epub --no-default-epub-cover");
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;

        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
