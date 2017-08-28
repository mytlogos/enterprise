package scrape.sources;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class for testing purposes.
 */
public class GravityTales {
    private URI hostString = new URI("http://gravitytales.com");

    public GravityTales() throws URISyntaxException {
    }

    private Document getDocument(String uri) throws IOException, URISyntaxException {
        URI startURI = new URI(uri);
        return Jsoup.connect(startURI.toString()).get();
    }

    private Elements getPostContent(Document document) {
        return document.getElementsByClass("hentry");
    }

    private Elements getChapterLink(Elements elements, String match) {
        Elements linkElements = new Elements();
        for (Element linkElement : elements) {
            linkElements.addAll(linkElement.getElementsByAttributeValueContaining("href", match));
        }
        return linkElements;
    }

    private String getLinkText(Elements elements, String linkText) {
        Element next = null;
        for (Element element : elements) {
            if (element.text().contains(linkText)) {
                next = element;
            }
        }
        String link = "";
        if (next != null) {
            link = next.attr("href");
        }
        return link;
    }

    private String getLink(Elements elements) {
        String nextLink = "";

        for (Element element : elements) {
            nextLink = element.attr("href");
        }
        return nextLink;
    }

    public static void writeDoc(String html,String fileName) throws IOException {
        Document doc = Jsoup.parse(html);
        File site = new File("D:\\Programmieren\\Java\\Projects\\Enterprise\\src\\scrape\\TestData\\" + fileName + ".html");
        System.out.println(site);
        FileOutputStream sitewriter = new FileOutputStream(site);

        ObjectOutputStream bufferedsiteWriter = new ObjectOutputStream(sitewriter);
        bufferedsiteWriter.writeBytes(doc.toString());
        bufferedsiteWriter.flush();
        bufferedsiteWriter.close();
    }

    private Element getChapterContent(String chapterLink) throws IOException {
        Document document1 = Jsoup.connect(chapterLink).get();

        return document1.getElementById("chapterContent");
    }


    public static void main(String[] args) throws Exception {
        GravityTales tales = new GravityTales();
        Document document = tales.getDocument("http://gravitytales.com/post/way-of-choices/ztj-chapter-560");
        Elements elements = tales.getPostContent(document);

        Elements link = tales.getChapterLink(elements,"ztj");

        String nextLink = tales.getLink(link);

        Document document1 = tales.getDocument(nextLink);
        Element elements1 = document1.getElementById("chapterContent");
        Elements elements2 = document1.getElementsByClass("btn-group btn-group-justified chapter-navigation");

        Elements elements3 = new Elements();
        for (Element element : elements2) {
            elements3.addAll(element.getElementsByAttribute("href"));
        }

        Elements elements4 = tales.getChapterLink(elements3,"ztj");


        String next = tales.getLinkText(elements4, "Next");
        String previous = tales.getLinkText(elements4, "Previous");

        URI nextURL = new URI(tales.hostString.getScheme() + "://" + tales.hostString.getHost() + next);
        URI previousURL = new URI(tales.hostString.getScheme() + "://" + tales.hostString.getHost() + previous);

        Elements chapterContent = elements1.getElementsByTag("p");

        tales.writeDoc(chapterContent.toString(), "writtenChapter.html");
        System.out.println(document.select("a[href]"));
    }
}
