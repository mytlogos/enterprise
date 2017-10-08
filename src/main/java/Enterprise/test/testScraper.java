package Enterprise.test;

import Enterprise.data.impl.CreationImpl;
import Enterprise.misc.TimeMeasure;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.ChapterSearchEntry;
import scrape.LinkDetective;
import scrape.sources.Source;
import scrape.sources.chapter.ChapterScraper;
import scrape.sources.chapter.strategies.ChapterConfigSetter;
import scrape.sources.posts.PostScraper;
import scrape.sources.posts.PostSearchEntry;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.FilterElement;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

/**
 *
 */
public class testScraper extends Application {
    private static Map<String, Integer> numberChaps = new HashMap<>();

    /*
        to set the logging of htmlUnit off:
        is ultra slow compared to jsoup, due to error messages?
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED);
        HtmlPage page = client.getPage(chapter);*/
    private static List<String> errors = new ArrayList<>();
    private static List<String> messages = new ArrayList<>();
    private static List<Integer> integers = new ArrayList<>();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, SQLException, ClassNotFoundException, BackingStoreException {
        launch();
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
    }

    private static List<String> getMistakes() {
        String mistakes = "http://www.sousetsuka.com/2017/09/shinsetsu-nobu-san-isekai-ki-chapter-23.html\n" +
                "http://thelordofpie.blogspot.com/2016/06/chapter-19-talhand-clifftop.html\n" +
                "http://razpyon.tumblr.com/post/165729142545/the-good-for-nothing-seventh-young-lady-200\n" +
                "http://www.rebirth.online/novel/the-good-for-nothing-seventh-young-lady/215\n" +
                "http://www.wuxiaheroes.com/nine-yang-sword-saint/chapter-18/\n" +
                "http://moonbunnycafe.com/inside-the-cave-of-obscenity/ico-v3-ch1-pt1/\n" +
                "http://eccentrictranslations.com/vwccm-chapter-25/\n" +
                "http://japtem.com/dd-volume-2-chapter-51/\n" +
                "http://skythewood.blogspot.de/2017/09/ASP125.html\n" +
                "http://raisingthedead.ninja/2017/10/02/return-of-the-former-hero-104/\n" +
                "http://volarenovels.com/bone-painting-coroner/bpc-chapter-63/\n" +
                "http://jigglypuffsdiary.com/i-came-back-but-the-world-is-still-a-fantasy/i-came-back-but-the-world-is-still-a-fantasy-repercussions-arc-act-1-the-first-day-of-transfer-03-11-hidden-circumstances-food-12/\n" +
                "http://saigotranslation.com/wradk-index/wradk-chapter-42/\n" +
                "http://tseirptranslations.com/2017/10/is-b11c227.html\n" +
                "http://infinitenoveltranslations.net/nidoume-no-jinsei-wo-isekai-de/chapter-61-70/chapter-67-it-seems-to-be-the-cooking-of-rice/\n" +
                "http://yukkuri-literature-service.blogspot.de/2017/09/KuronoiyashiteLN1-3-2.html\n" +
                "https://arsl31.wordpress.com/unmotivated-heros-tale/unmotivated-heros-tale-chapter-15/\n" +
                "http://unlimitednovelfailures.mangamatters.com/risou-no-himo-seikatsu/risou-no-himo-seikatsu-volume-01/\n" +
                "https://gilatranslationmonster.wordpress.com/star-sea-lord/star-sea-lord-volume-1-chapter-4/\n" +
                "http://www.wuxiatranslations.com/swallowed-star-volume-2-chapter-14/\n" +
                "https://honyakusite.wordpress.com/2017/09/25/originstory-029-trap-door/\n" +
                "https://monktranslations.wordpress.com/ryuugoroshi-ch27/\n" +
                "https://arkmachinetranslations.wordpress.com/act-5-the-red-man/\n" +
                "https://bayabuscotranslation.com/2017/09/25/world-teacher-95-self-edited-the-oracle-ceremony/2/\n" +
                "https://larvyde.wordpress.com/2017/06/16/genou-04-01e/\n" +
                "https://kakkokaritranslations.com/se-chapter-168/\n" +
                "https://kungfubears.wordpress.com/2017/09/21/56-prologue-liliana-part-2/\n" +
                "https://isohungrytls.com/raising-a-fox-spirit-in-my-home/fox-spirit-chapter-34\n" +
                "https://mayonaizeshrimp.wordpress.com/2017/09/26/isekai-gm-extra-episode-1-a-day-off-in-the-kingdom/\n" +
//                "http://www.wuxiaworld.com/rmji-index/rmji-chapter-431/\n" +
                "https://lightnovelstranslations.com/road-to-kingdom/chapter-93-healing-of-the-heart/\n" +
//                "http://www.wuxiaworld.com/renegade-index/renegade-chapter-619/\n" +
                "https://paichuntranslations.com/lgpmhr/kujibiki-tokushou-musou-haremu-ken-chapter-128/\n" +
                "https://hikkinomori.mistbinder.org/2017/10/01/filwtv-64th-update/\n" +
                "https://shikkakutranslations.org/kamigoroshi-no-eiyuu-to-nanatsu-no-seiyaku/kens-chapter-74/\n" +
                "https://starrydawntranslations.wordpress.com/karma64/\n" +
                "https://sunshowerfields.wordpress.com/2016/11/08/when-a-snail-falls-in-love-episode-3-recap/\n" +
                "https://sylver135.wordpress.com/teasers/647-2/aaiaalss-chapter-25-this-is-the-life-together-with-a-loli/\n" +
                "https://shintranslations.com/vol-8-chapter-3-part-3/\n" +
                "https://wuxianation.com/storm-in-the-wilderness/storm-in-the-wilderness-chapter-86.html\n" +
                "https://www.oppatranslations.com/master-hunter-k/chapter-157-great-plains-barrastan-8/\n" +
                "https://yoraikun.wordpress.com/2017/09/08/about-the-reckless-girl-who-kept-challenging-a-reborn-man-like-me/\n" +
                "https://zirusmusings.com/ico3-ch1-pt1/";

        return new ArrayList<>(Arrays.asList(mistakes.split("\\n")));
    }

    public static List<String> getChapters() {
        List<String> add = new ArrayList<>();
        add.add("http://infinitenoveltranslations.net/nidoume-no-jinsei-wo-isekai-de/chapter-61-70/chapter-67-it-seems-to-be-the-cooking-of-rice/");
        add.add("http://gravitytales.com/novel/godly-model-creator/gmc-chapter-136");
        add.add("http://eccentrictranslations.com/vwccm-chapter-25/");
        add.add("http://japtem.com/dd-volume-2-chapter-51/");
        add.add("http://jigglypuffsdiary.com/i-came-back-but-the-world-is-still-a-fantasy/i-came-back-but-the-world-is-still-a-fantasy-repercussions-arc-act-1-the-first-day-of-transfer-03-11-hidden-circumstances-food-12/");
        add.add("http://myoniyonitranslations.com/gods-song/gs-volume-8-chapter-266/");
        add.add("http://moonbunnycafe.com/inside-the-cave-of-obscenity/ico-v3-ch1-pt1/");
        add.add("http://raisingthedead.ninja/2017/10/02/return-of-the-former-hero-104/");
        add.add("http://razpyon.tumblr.com/post/165729142545/the-good-for-nothing-seventh-young-lady-200");
        add.add("http://saigotranslation.com/wradk-index/wradk-chapter-42/");
        add.add("http://scrya.org/my-disciple-died-yet-again/disciple-chapter-269/");
        add.add("http://skythewood.blogspot.de/2017/09/ASP125.html");
        add.add("http://thelordofpie.blogspot.com/2016/06/chapter-19-talhand-clifftop.html");
        add.add("http://tseirptranslations.com/2017/10/is-b11c227.html");
        add.add("http://unlimitednovelfailures.mangamatters.com/risou-no-himo-seikatsu/risou-no-himo-seikatsu-volume-01/");
        add.add("http://volarenovels.com/bone-painting-coroner/bpc-chapter-63/");
        add.add("http://www.novelsaga.com/dragon-martial-emperor-chapter-197/");
        add.add("http://www.oyasumireads.com/2017/09/29/ototsukai-wa-shi-odoru-chapter-31/");
        add.add("http://www.rebirth.online/novel/the-good-for-nothing-seventh-young-lady/215");
        add.add("http://www.sousetsuka.com/2017/09/shinsetsu-nobu-san-isekai-ki-chapter-23.html");
        add.add("http://www.wuxiaheroes.com/nine-yang-sword-saint/chapter-18/");
        add.add("http://www.wuxiatranslations.com/swallowed-star-volume-2-chapter-14/");
        add.add("http://www.wuxiaworld.com/rmji-index/rmji-chapter-431/");
        add.add("http://yukkuri-literature-service.blogspot.de/2017/09/KuronoiyashiteLN1-3-2.html");
        add.add("https://arkmachinetranslations.wordpress.com/act-5-the-red-man/");
        add.add("https://arsl31.wordpress.com/unmotivated-heros-tale/unmotivated-heros-tale-chapter-15/");
        add.add("https://avertranslation.blogspot.de/p/bifmsmtk-volume-1-chapter-1.html");
        add.add("https://bayabuscotranslation.com/2017/09/25/world-teacher-95-self-edited-the-oracle-ceremony/2/");
        add.add("https://binhjamin.wordpress.com/sayonara-ryuusei-konnichiwa-jinsei/volume-2/chapter-16/");
        add.add("https://defiring.wordpress.com/2017/09/24/my-death-flags-show-no-sign-of-ending-chapter-89/");
        add.add("https://durasama.wordpress.com/2016/01/10/arifureta-chapter-96/#more-487");
        add.add("https://ensjtrans.com/2017/09/05/king-shura-chapter-123/");
        add.add("https://gilatranslationmonster.wordpress.com/star-sea-lord/star-sea-lord-volume-1-chapter-4/");
        add.add("https://monktranslations.wordpress.com/ryuugoroshi-ch27/");
        add.add("https://honyakusite.wordpress.com/2017/09/25/originstory-029-trap-door/");
        add.add("https://hikkinomori.mistbinder.org/2017/10/01/filwtv-64th-update/");
        add.add("https://isohungrytls.com/raising-a-fox-spirit-in-my-home/fox-spirit-chapter-34");
        add.add("https://isekailunatic.wordpress.com/2017/10/01/chapter-283-the-beast-knight-and-the-devil/");
        add.add("https://kakkokaritranslations.com/se-chapter-168/");
        add.add("https://kobatochan.com/god-of-cooking-chapter-164/");
        add.add("https://konobuta.wordpress.com/2016/11/18/uchimusume-chapter-59/");
        add.add("https://kungfubears.wordpress.com/2017/09/21/56-prologue-liliana-part-2/");
        add.add("https://larvyde.wordpress.com/2017/06/16/genou-04-01e/");
        add.add("https://lightnovelbastion.com/release.php?p=926");
        add.add("https://lightnovelstranslations.com/road-to-kingdom/chapter-93-healing-of-the-heart/");
        add.add("https://lylisasmodeus.wordpress.com/2016/01/21/the-unicorn-legion-mage-academy-chapter-12/");
        add.add("https://manga0205.wordpress.com/2017/03/28/sendai-yuusha-wa-inkyou-shitai-chapter-151/");
        add.add("https://mayonaizeshrimp.wordpress.com/2017/09/26/isekai-gm-extra-episode-1-a-day-off-in-the-kingdom/");
        add.add("https://nightbreezetranslations.wordpress.com/2015/05/13/book-11-chapter-55/");
        add.add("https://omegaharem.wordpress.com/2017/09/18/eliza-chapter-204-the-long-long-prequels-conclusion/");
        add.add("https://oniichanyamete.wordpress.com/2016/07/13/chapter-1-2/");
        add.add("https://paichuntranslations.com/lgpmhr/kujibiki-tokushou-musou-haremu-ken-chapter-128/");
        add.add("https://shalvationtranslations.wordpress.com/2017/09/12/we-should-have-slept-while-only-holding-hands-and-yet-volume-1-authors-afterword/");
        add.add("https://shikkakutranslations.org/kamigoroshi-no-eiyuu-to-nanatsu-no-seiyaku/kens-chapter-74/");
        add.add("https://shintranslations.com/vol-8-chapter-3-part-3/");
        add.add("https://sodtranslations.wordpress.com/2017/09/15/ore-to-kawazu-san-no-isekai-hourouki-chapter-55/");
        add.add("https://starrydawntranslations.wordpress.com/karma64/");
        add.add("https://sunshowerfields.wordpress.com/2016/11/08/when-a-snail-falls-in-love-episode-3-recap/");
        add.add("https://sylver135.wordpress.com/teasers/647-2/aaiaalss-chapter-25-this-is-the-life-together-with-a-loli/");
        add.add("https://tensaitranslations.wordpress.com/2016/03/16/spirit-migration-vol-3-ch16-the-request-to-subjugate-a-gang-of-bandits/");
        add.add("https://wcctranslation.wordpress.com/2017/02/13/chapter-97-decision-and-preaching-lunaris/");
        add.add("https://weitranslations.wordpress.com/ccg-chapter-111/");
//        add.add("https://www.webnovel.com/book/7979375206002305/22025750413988334/Experimental-Log-of-the-Crazy-Lich/I-Really-Am-a-Good-Person");
        add.add("https://wuxianation.com/storm-in-the-wilderness/storm-in-the-wilderness-chapter-86.html");
        add.add("https://www.oppatranslations.com/master-hunter-k/chapter-157-great-plains-barrastan-8/");
        add.add("https://yoraikun.wordpress.com/2017/09/08/about-the-reckless-girl-who-kept-challenging-a-reborn-man-like-me/");
        add.add("https://youshokutranslations.wordpress.com/theotherworlddininghall/v1c15/");
        add.add("https://zirusmusings.com/ico3-ch1-pt1/");

        add.sort(Comparator.naturalOrder());
        return add;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IntegerProperty integer = new SimpleIntegerProperty();
//        tryIt(integer, "https://youshokutranslations.wordpress.com/theotherworlddininghall/v1c15/");
//        doSequential(getMistakes(), (integerProperty, s) -> doGetAllChapters(s));
//        doConcurrent(getMistakes(),(integerProperty, s) -> doGetAllChapters(s));
//        getChapters().forEach(this::tryNextLink);
//        openAll(getChapters());
//        browseGuiLess(getChapters());
//        browse(getChapters().get(0));
//        doGetAllChapters("http://gravitytales.com/Novel/era-of-disaster/eod-chapter-4");
//        getChapters().forEach(this::doGetAllChapters);
    }

    private void doGetAllChapters(String s) {
        try {
            ChapterSearchEntry entry = new ChapterSearchEntry(new CreationImpl.CreationImplBuilder("moin").build(), Source.create(s, Source.SourceType.START));
            ChapterScraper scraper = ChapterScraper.scraper(entry);
            int size = scraper.getAllChapters().size();
            if (size == 1) {
                errors.add(s);
            } else {
                String message = "for " + s + " number of chapters " + size;
                messages.add(message);
                numberChaps.put(s, size);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void browseGuiLess(List<String> chapters) throws IOException {
        for (String chapter : chapters) {
            browse(chapter);
        }
    }

    private void browse(String chapter) throws IOException {
//        new FirefoxDriver();
    }

    private void openAll(List<String> links) {
        links.forEach(s -> {
            try {
                Thread.sleep(100);
                Desktop.getDesktop().browse(URI.create(s));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void tryNextLink(String s) {
        LinkDetective detective = new LinkDetective();
        System.out.println("Old Link: " + s);
//        System.out.println("New Link: " + detective.tryNextLink(s));
        detective.tryNextLink(s);
        System.out.println();
    }

    private void doSequential(List<String> mistakes, BiConsumer<IntegerProperty, String> biConsumer) {
        IntegerProperty integer = new SimpleIntegerProperty(0);
        TimeMeasure measure = TimeMeasure.start();
        mistakes.forEach(s -> {
            try {
                biConsumer.accept(integer, s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        measure.finish();
        System.out.println(measure.getMessage(s -> "Time needed: " + s));
        errors.forEach(System.out::println);
        messages.forEach(System.out::println);
    }

    private void doConcurrent(List<String> strings, BiConsumer<IntegerProperty, String> function) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(20);
        IntegerProperty integer = new SimpleIntegerProperty(0);
        TimeMeasure measure = TimeMeasure.start();

        for (String s : strings) {
            service.submit(() -> {
                try {
                    function.accept(integer, s);
                } catch (Exception ignored) {
                }
            });
        }
        service.shutdown();
        while (!service.isTerminated()) {
            Thread.sleep(2000);
        }
        System.out.println("Lowest Character Count: " + integers.stream().min(Comparator.naturalOrder()).orElse(0));
        System.out.println("Highest Character Count: " + integers.stream().max(Comparator.naturalOrder()).orElse(0));

        errors.forEach(System.out::println);
//        messages.forEach(System.out::println);
        List<Map.Entry<String, Integer>> list = sortByValue(numberChaps);
        list.forEach(stringIntegerEntry -> System.out.println("for " + stringIntegerEntry.getKey() + " number of chapters " + stringIntegerEntry.getValue()));
        list.forEach(stringIntegerEntry -> System.out.println(stringIntegerEntry.getKey()));

        measure.finish();
        System.out.println(measure.getMessage(s -> "Time needed: " + s));
        System.exit(0);
    }

    private void tryIt(IntegerProperty integer, String s) throws URISyntaxException, IOException {
        Source source = Source.create(s, Source.SourceType.START);
        List<String> strings = new ArrayList<>();
        PostSearchEntry entry = new PostSearchEntry(null, source, strings);

        Document document = Jsoup.connect(s).get();
        document = PostScraper.cleanDoc(document);
        ContentWrapper wrapper = ContentWrapper.tryAll(document);

        Element contentWrapper;
        if (wrapper == null) {
            contentWrapper = document.body();
        } else {
            contentWrapper = wrapper.apply(document);
        }

        FilterElement element = new ChapterConfigSetter().tryAllPagination(contentWrapper);
//        FilterElement element = ChapterPagination.Pagination.LINK_CONTAINS_TEXT;

        if (element == null) {
            String error = s + " not supported";
            Set<String> classnames = contentWrapper.classNames();
            if (!classnames.isEmpty()) {
                error = error.concat("\n").concat(contentWrapper.tag() + "" + classnames);
            } else {
                error = error.concat("\n").concat(contentWrapper.tag() + "[#" + contentWrapper.id() + "]");
            }
            errors.add(error);
        } else {
            System.out.println(element + " for " + s);
            int length = element.apply(contentWrapper).text().length();
            integers.add(length);
            System.out.println(length);
        }

        integer.set(integer.get() + 1);
        System.out.println(integer.get());
    }
}
