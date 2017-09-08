package Enterprise.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dominik on 08.07.2017.
 * Class for testing purposes.
 */
public class IntegerBean implements Serializable {

    private List<String> times = new ArrayList<>();
    private static List<String> links = new ArrayList<>();
    private static int counter = 0;
    private static String posts = "";

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        System.out.println(Jsoup.parse(new File("TestData/TestPosts/arkmachinetranslations.wordpress.com.html"), "UTF-8"));
        /*String s = addLinks().get(72);
        // TODO: 07.09.2017 empty archive in unlimitednovelfailures
        // TODO: 07.09.2017 empty archive in thelordofpie, does not search for non empty ones
        // TODO: 07.09.2017 get facebook scraper
        // TODO: 07.09.2017 limit input, do not select post from whole doc, narrow it down before
        // TODO: 07.09.2017 razpyon has other .shadow elements
        // TODO: 07.09.2017 fivestarspecialists archive has scrolldown without pages
        // TODO: 07.09.2017 gravity has other .row elements
        // TODO: 07.09.2017 omegaharem: searches in wrong archive, 2016.12 instead of 2017.09
        // TODO: 07.09.2017 sousetsuka: searches in wrong archive, 2016.12 instead of 2017.09
        try {
            Document document = getDocument(s);

            if (SiteArchive.hasArchive(document)) {
                System.out.println("has archive");
                Iterator<Document> iterator = SiteArchive.archiveSearcher(document).iterator();
                if (iterator.hasNext()) {
                    System.out.println("has next");
                    printPosts(iterator.next());
                } else {
                    System.out.println(s + " has no recent activity in last 12 months");
                    printPosts(document);
                }
            } else {
                printPosts(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void printPosts(Document document) {
        // TODO: 06.09.2017 get post, save conf.
        System.out.println(document.location());
        Elements elements;
        for (Types types : Types.priorityList()) {
            System.out.println(types);
            if (!(elements = document.select(types.selector)).isEmpty()) {
                // TODO: 07.09.2017 make whitelist/greenList?
                if (Types.ROW == types && document.select("[data-timestamp]").isEmpty()) {
                    System.out.println(types);
                    continue;
                }
                if (isValid(elements)) {
                    write(document, elements);
                    System.out.println("in " + types.selector + " " + document.location());
                    break;
                } else {
                    System.out.println(types.selector + " is invalid for " + document.location());
                }
            }
        }
    }

    private static boolean isValid(Elements elements) {
        // TODO: 07.09.2017 make a blacklist
        if (checkBlack(elements)) return false;
        if (checkGreen(elements)) return true;
        return checkRed(elements);
    }

    private static boolean checkRed(Elements elements) {
        boolean valid = true;
        for (Element element : elements) {
            for (Attribute attribute : element.attributes()) {
                if (attribute.getValue().contains("comment") || attribute.getValue().contains("support")) {
                    System.out.println(attribute);
                    valid = false;
                }
            }
            if (!element.getElementsByAttributeValueContaining("class", "widget").isEmpty()) {
                System.out.println("has widget");
                valid = false;
            }
        }
        return valid;
    }

    private static boolean checkBlack(Elements elements) {
        return false;
    }

    private static boolean checkGreen(Elements elements) {
        for (Element element : elements) {
            Elements classes = element.getElementsByAttributeValueMatching("class", ".*(content|head|footer|entry-meta).*");
            Elements ids = element.getElementsByAttributeValueMatching("id", ".*(content|head|footer|entry-meta).*");
//            Elements classes = element.select("[class~=content|head|footer|entry-meta]");
//            Elements ids = element.select("[id~=content|head|footer|entry-meta]");
            if (!classes.isEmpty() || !ids.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static void write(Document document, Elements elements) {

        elements = removeScript(elements);

        String fileName = "file" + counter + ".html";
        try {
            URL url = new URL(document.location());
            fileName = url.getHost() + ".html";
        } catch (MalformedURLException e) {
            counter++;
        }

        fileName = System.getProperty("user.dir") + "\\TestData\\TestPosts\\" + fileName;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < elements.size(); i++) {
                bufferedWriter.write("Post Nr." + (i + 1) + "\n");
                bufferedWriter.write(elements.get(i).outerHtml());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < elements.size(); i++) {
            System.out.println("Post Nr." + (i + 1));
            System.out.println(elements.get(i));
            System.out.println();
        }
    }

    private static Elements removeScript(Elements elements) {
        Document document = new Document("");

        for (Element element : elements) {
            // FIXME: 07.09.2017 does not remove script by shikkaku, rebirth
            element.getElementsByTag("script").remove();
            if (element.getElementsByTag("article").isEmpty()) {
                Element article = document.createElement("article");
                article.html(element.outerHtml());
                document.appendChild(article);
            } else {
                document.appendChild(element);
            }
        }
        Elements article = document.getElementsByTag("article");

        return article.isEmpty() ? elements : article;
    }

    public static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    private static List<String> addLinks() {
        links.add("https://webnovel.com/");
        links.add("https://bayabuscotranslation.com/");
        links.add("https://wcctranslation.wordpress.com/");
        links.add("https://konobuta.wordpress.com/");
        links.add("https://tensaitranslations.wordpress.com/");
        links.add("https://arkmachinetranslations.wordpress.com/");
        links.add("http://infinitenoveltranslations.net/");
        links.add("https://yoraikun.wordpress.com/");
        links.add("https://manga0205.wordpress.com/");
        links.add("http://www.novelsaga.com/");
        links.add("https://larvyde.wordpress.com/");
        links.add("https://subudai11.wordpress.com/");
        links.add("https://arsl31.wordpress.com/");
        links.add("https://shikkakutranslations.org/");
        links.add("http://raisingthedead.ninja/");
        links.add("http://tseirptranslations.com/");
        links.add("https://lightnovelstranslations.com/");
        links.add("https://shalvationtranslations.wordpress.com/");
        links.add("https://durasama.wordpress.com/");
        links.add("https://zirusmusings.com/");
        links.add("https://nightbreezetranslations.wordpress.com/");
        links.add("http://www.rebirth.online/");
        links.add("https://lylisasmodeus.wordpress.com/");
        links.add("https://sunshowerfields.wordpress.com/");
        links.add("https://lesyt.xyz/");
        links.add("https://youshokutranslations.wordpress.com/");
        links.add("http://www.wuxiatranslations.com/");
        links.add("http://myoniyonitranslations.com/");
        links.add("https://wuxianation.com/");
        links.add("https://kakkokaritranslations.com/");
        links.add("http://volarenovels.com/");
        links.add("http://unlimitednovelfailures.mangamatters.com/");
        links.add("https://binhjamin.wordpress.com/");
        links.add("https://hajiko.wordpress.com/");
        links.add("http://saigotranslation.com/");
        links.add("https://starrydawntranslations.wordpress.com/");
        links.add("https://paichuntranslations.com/");
        links.add("https://incarneous.wordpress.com/");
        links.add("https://sylver135.wordpress.com/");
        links.add("https://gilatranslationmonster.wordpress.com/");
        links.add("http://www.wuxiaheroes.com/");
        links.add("https://oniichanyamete.wordpress.com/");
        links.add("http://japtem.com/");
        links.add("https://isekailunatic.wordpress.com/");
        links.add("https://www.facebook.com/Baka.Tsuki.org");
        links.add("https://shintranslations.com");
        links.add("http://razpyon.tumblr.com/");
        links.add("http://www.oyasumireads.com/");
        links.add("https://ensjtrans.com/");
        links.add("https://fivestarspecialists.wordpress.com/");
        links.add("http://gravitytales.com/");
        links.add("https://sodtranslations.wordpress.com/");
        links.add("https://mayonaizeshrimp.wordpress.com/");
        links.add("https://lightnovelbastion.com/");
        links.add("http://scrya.org/");
        links.add("https://defiring.wordpress.com/");
        links.add("https://www.oppatranslations.com/");
        links.add("https://kungfubears.wordpress.com/");
        links.add("http://jigglypuffsdiary.com/");
        links.add("https://kobatochan.com/");
        links.add("http://moonbunnycafe.com/");
        links.add("https://weitranslations.wordpress.com/");
        links.add("https://omegaharem.wordpress.com/");
        links.add("https://hikkinomori.mistbinder.org/");
        links.add("http://eccentrictranslations.com/");
        links.add("http://www.wuxiaworld.com/");
        links.add("https://honyakusite.wordpress.com/");
        links.add("http://thelordofpie.blogspot.com/");
        links.add("https://avertranslation.blogspot.de/");
        links.add("http://skythewood.blogspot.de/");
        links.add("http://yukkuri-literature-service.blogspot.de/");
        links.add("http://www.sousetsuka.com/");
        return links;
    }

    private enum Types {
        LIST("ul.new-list > li > a", 1),
        TABLE("table[class*=update] > tbody > tr", 2),
        SHADOW(".shadow", 3),
        ROW(".row", 4),
        ARTICLE("article", 6),
        HENTRY(".hentry", 5);

        final String selector;
        final int priority;

        Types(String s, int priority) {
            selector = s;
            this.priority = priority;
        }

        private static List<Types> priorityList() {
            List<Types> list = new ArrayList<>(Arrays.asList(Types.values()));
            list.sort(Comparator.comparingInt(o -> o.priority));
            return list;
        }
    }

    private List<String> addTimes() {
        times.add("2017-09-01T23:42:00+00:00");
        times.add("2017-08-04T05:15:54+00:00");
        times.add("2017-08-27T15:48:33+00:00");
        times.add("2017-09-01T07:10:52+00:00");
        times.add("2017-09-01T09:36:49+00:00");
        times.add("2017-09-02T01:49:33+00:00");
        times.add("2017-05-01T18:13:13+00:00");
        times.add("2017-07-25T02:06:44+00:00");
        times.add("2017-09-02T15:44:28+00:00");
        times.add("2017-09-02T13:23:18+00:00");
        times.add("2017-08-28T14:10:01+00:00");
        times.add("2017-08-27T09:57:00+00:00");
        times.add("2017-09-02T11:43:16+00:00");
        times.add("2017-08-03T16:30:27+00:00");
        times.add("2017-09-03T02:34:40+00:00");
        times.add("2017-08-16T11:20:35+00:00");
        times.add("2017-08-11T14:40:39+00:00");
        times.add("2017-05-18T19:48:33+00:00");
        times.add("2017-09-02T23:00:00+08:00");
        times.add("2016-11-14T16:13:26+00:00");
        times.add("2017-08-01T09:42:33+00:00");
        times.add("2017-09-02T10:45:38+00:00");
        times.add("2017-09-02T20:12:24+00:00");
        times.add("2017-02-19T22:58:00+08:00");
        times.add("2017-09-02T08:30:07+00:00");
        times.add("2015-12-25T15:05:31+00:00");
        times.add("2016-01-21T08:31:46+00:00");
        times.add("2016-06-26T17:14:00+05:30");
        times.add("2017-09-02T20:35:30+00:00");
        times.add("2016-01-13T02:21:35+00:00");
        times.add("2017-08-31T20:29:48+00:00");
        times.add("2017-09-02T16:52:35+00:00");
        times.add("2017-06-07T16:04:03+00:00");
        times.add("2017-08-31T00:42:49+00:00");
        times.add("2017-03-13T04:26:52+00:00");
        times.add("2017-06-16T08:16:31+00:00");
        times.add("2017-03-28T18:38:23+00:00");
        times.add("2017-09-03T00:26:14+00:00");
        times.add("2017-07-11T19:00:31+00:00");
        times.add("2017-08-28T11:15:51+00:00");
        times.add("2017-02-13T11:36:32+00:00");
        times.add("2017-08-29T20:00:08+00:00");


        times.add("June 28, 2016");
        times.add("July 12, 2017");
        times.add("August 23, 2017");
        times.add("July 19, 2017");
        times.add("September 3, 2017");
        times.add("November 8, 2016");
        times.add("May 13, 2015");
        times.add("September 1, 2017");
        times.add("April 29, 2017");
        times.add("June 29, 2017");
        times.add("August 31, 2017");
        times.add("June 5, 2017");
        times.add("March 27, 2017");
        times.add("August 31, 2017");
        times.add("March 16, 2016");

        times.add("Saturday, September 2, 2017");

        times.add("2017-08-21");
        times.add("2017-03-20");
        times.add("2017-04-30");

        times.add("1504415349");
        times.add("1504413657");

        times.add("02.09.2017");

        times.add("26 minutes ago");
        times.add("3 hours ago");
        times.add("Posted 59 minutes ago");

        times.add("August 31, 2017 at 06:23 pm (UTC -5)");

        times.add("2017-09-02 14:31:40");
        times.add("2017-09-03 05:51");

        times.add("4:05 am (+02:00) on September 1, 2017");

        return times;
    }

}
