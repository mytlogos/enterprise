package Enterprise.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 05.08.2017.
 * Part of OgameBot.
 */
public class test {
    private static int  counter = 0;
    private static URI uri;

    static {
        try {
            uri = new URI("http://www.wuxiaworld.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, IOException, URISyntaxException, InterruptedException {

        newLinks.add(uri.toString());

        getLinks();



        System.out.println("http://www.wuxiaworld.com/xmlrpc.php".contains("php"));
    }

    private static Set<String> allLinks = new HashSet<>();
    private static Set<String> newLinks = new HashSet<>();

    public static void getLinks() throws IOException, InterruptedException {
        Set<String> tempNewLinks = new HashSet<>();
        for (String link : newLinks) {

            try {
                Document document1 = Jsoup.connect(link).get();
                Elements header = document1.getElementsByTag("header");

                Elements allLinks1 = new Elements();

                for (Element element : header) {
                    allLinks1.addAll(element.getElementsByAttributeValueContaining("href", uri.getHost()));
                }

                for (Element allLink1 : allLinks1) {
                    String getLink = allLink1.attr("href");

                    if (getLink.contains("php") || getLink.contains("wp") || getLink.contains("#comment")) {

                        System.out.println("Hier ist ein ungewollter Link");

                    } else if (getLink.contains("http")){
                        if (!allLinks.contains(getLink)) {
                            allLinks.add(getLink);
                            tempNewLinks.add(getLink);
                        }
                    }
                    System.out.println(getLink);
                    Thread.sleep(10);
                    System.out.println(counter);
                    counter++;
                }
                System.out.println(counter);
                counter++;
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        newLinks.clear();
        newLinks.addAll(tempNewLinks);
        writeSet();
        if (!newLinks.isEmpty()) {
            //getLinks();
        }
    }

    private static int count = 0;

    private static void writeSet() {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter("WuxiaLinks"+count+".txt");
            bufferedWriter = new BufferedWriter(fileWriter);

            for (String allLink : allLinks) {
                bufferedWriter.write(allLink);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            count++;
        }
    }
}
