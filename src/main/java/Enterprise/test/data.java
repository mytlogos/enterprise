package Enterprise.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

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
        String string = "binhjamin.wordpress.com//sayonara-ryuusei-konnichiwa-jinsei/volume-2/chapter-15";
        URI uri = URI.create("https://binhjamin.wordpress.com/sayonara-ryuusei-konnichiwa-jinsei/volume-2/chapter-16/");
        System.out.println(uri.getUserInfo());
        System.out.println(uri.getAuthority());
        System.out.println(uri.getFragment());
        System.out.println(uri.getSchemeSpecificPart());
        System.out.println(uri.getScheme());
//        downloadXml();
    }
}
