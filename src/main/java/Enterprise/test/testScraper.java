package Enterprise.test;

import Enterprise.data.impl.CreationImpl;
import Enterprise.misc.TimeMeasure;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.ChapterSearchEntry;
import scrape.LinkDetective;
import scrape.Scraper;
import scrape.sources.Source;
import scrape.sources.chapter.ChapterScraper;
import scrape.sources.chapter.GravityNovel;
import scrape.sources.chapter.strategies.ChapterConfigSetter;
import scrape.sources.posts.PostScraper;
import scrape.sources.posts.PostSearchEntry;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.FilterElement;
import scrape.sources.toc.Processor;
import scrape.sources.toc.strategies.impl.HeaderFilter;
import scrape.sources.toc.strategies.intface.HeaderElement;
import scrape.sources.toc.strategies.intface.TocProcessor;

import java.awt.*;
import java.io.File;
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

    private Map<String, List<String>> map = new HashMap<>();
    private List<String> links = new ArrayList<>();
    private List<String> texts = new ArrayList<>();

    public static List<String> getTocLinks() {
        String mistakes = "http://imgur.com/DQL0KHd,E2Fmz85,ZXy33mC,Dc8bZa1,vspQ9Fh,0YG978i\n" +
                "https://pirateyoshi.wordpress.com/gcr-toc/gcr-illustrations/\n" +
                "https://pirateyoshi.wordpress.com/gcr-prologue/\n" +
                "https://zirusmusings.com/gcr-ch1/\n" +
                "https://zirusmusings.com/gcr-ch1-pt1/\n" +
                "https://zirusmusings.com/gcr-ch1-pt2/\n" +
                "https://zirusmusings.com/gcr-ch2/\n" +
                "https://zirusmusings.com/gcr-ch2-pt1/\n" +
                "https://zirusmusings.com/gcr-ch2-pt2/\n" +
                "https://zirusmusings.com/gcr-ch2-pt3/\n" +
                "https://zirusmusings.com/gcr-ch2-pt4/\n" +
                "https://zirusmusings.com/gcr-ch2-pt5/\n" +
                "https://zirusmusings.com/gcr-ch3/\n" +
                "https://zirusmusings.com/gcr-ch3-pt1/\n" +
                "https://zirusmusings.com/gcr-ch3-pt2/\n" +
                "https://zirusmusings.com/gcr-ch3-pt3/\n" +
                "https://zirusmusings.com/gcr-ch3-pt4/\n" +
                "https://zirusmusings.com/gcr-ch3-pt5/\n" +
                "https://zirusmusings.com/gcr-ch3-pt6/\n" +
                "https://zirusmusings.com/gcr-ch3-pt7/\n" +
                "https://zirusmusings.com/gcr-ch3-pt8/\n" +
                "https://zirusmusings.com/gcr-ch3-pt9/\n" +
                "https://zirusmusings.com/gcr-ch3-pt10/\n" +
                "https://zirusmusings.com/gcr-ch3-pt11/\n" +
                "https://zirusmusings.com/gcr-ch3-pt12/\n" +
                "https://zirusmusings.com/gcr-epilogue/\n" +
                "https://zirusmusings.com/gcr-ss/\n" +
                "https://zirusmusings.com/gcr-ss-pt1/\n" +
                "https://zirusmusings.com/gcr-ss-pt2/\n" +
                "https://zirusmusings.com/gcr2-illustrations/\n" +
                "https://pirateyoshi.wordpress.com/gcr2-prologue/\n" +
                "https://zirusmusings.com/gcr2-ch1/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt3/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt4/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt5/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt6/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt7/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt8/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt9/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt10/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt11/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt12/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt13/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt14/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt15/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt16/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt3/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt4/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt5/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt6/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt7/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt8/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt3/\n" +
                "https://zirusmusings.com/gcr2-5-illustrations/\n" +
                "https://zirusmusings.com/gcr3-illustrations/\n" +
                "https://zirusmusings.com/gcr4-illustrations/\n" +
                "http://www.novelupdates.com/series/the-guilds-cheat-receptionist/\n" +
                "http://booklive.jp/product/index/title_id/282541/vol_no/001\n" +
                "http://www.amazon.co.jp/%E3%82%AE%E3%83%AB%E3%83%89%E3%81%AE%E3%83%81%E3%83%BC%E3%83%88%E3%81%AA%E5%8F%97%E4%BB%98%E5%AC%A2-1-%E3%83%A2%E3%83%B3%E3%82%B9%E3%82%BF%E3%83%BC%E6%96%87%E5%BA%AB-%E5%A4%8F%E3%81%AB%E3%82%B3%E3%82%BF%E3%83%84/dp/4575750085/ref=sr_1_2?ie=UTF8&qid=1465133683&sr=8-2&keywords=%E3%82%AE%E3%83%AB%E3%83%89%E3%81%AE%E3%83%81%E3%83%BC%E3%83%88%E3%81%AA%E5%8F%97%E4%BB%98%E5%AC%A2";

        return new ArrayList<>(Arrays.asList(mistakes.split("\\n")));
    }

    public static List<String> getTocs() {
        List<String> list = new ArrayList<>();
        list.add("http://eccentrictranslations.com/atf-catalogue/");
        list.add("http://gravitytales.com/novel/a-mercenarys-war");
        list.add("http://infinitenoveltranslations.net/hachinan-tte-sore-wa-nai-deshou/");
        list.add("http://japtem.com/projects/dd-toc/");
        list.add("http://jigglypuffsdiary.com/ashes-and-kingdoms/");
        list.add("http://moonbunnycafe.com/a-lonesome-fragrance-waiting-to-be-appreciated/");
        list.add("http://myoniyonitranslations.com/demon-king-hero/");
        list.add("http://raisingthedead.ninja/current-projects/demon-lords-pet/");
        list.add("http://razpyon.tumblr.com/tgfnsyl");
        list.add("http://saigotranslation.com/isekaitenseionna-index-page/");
        list.add("http://scrya.org/my-disciple-died-yet-again/");
        list.add("http://skythewood.blogspot.de/p/altina-sword-princess.html");
        list.add("http://thelordofpie.blogspot.com/p/mushoku-tensei.html");
        list.add("http://tseirptranslations.com/invincible-saint-salaryman-toc");
        list.add("http://unlimitednovelfailures.mangamatters.com/risou-no-himo-seikatsu/");
        list.add("http://volarenovels.com/adorable-creature-attack/");
        list.add("http://www.novelsaga.com/martial-god-space-index/");
        list.add("http://www.oyasumireads.com/isekaijin-no-tebikisho-2/");
        list.add("http://www.rebirth.online/novel/400-years-old-virgin-demon-king");
        list.add("http://www.sousetsuka.com/p/blog-page_11.html");
        list.add("http://www.wuxiaheroes.com/nine-yang-sword-saint/");
        list.add("http://www.wuxiatranslations.com/law-of-the-devil/");
        list.add("http://www.wuxiaworld.com/7-killers/");
        list.add("http://yukkuri-literature-service.blogspot.de/p/translation-service.html");
        list.add("https://arkmachinetranslations.wordpress.com/ark-table-of-contents/");
        list.add("https://arsl31.wordpress.com/konjiki-no-word-master/");
        list.add("https://avertranslation.blogspot.de/p/blog-page.html");
        list.add("https://bayabuscotranslation.com/table-of-content/");
        list.add("https://binhjamin.wordpress.com/sayonara-ryuusei-konnichiwa-jinsei/");
        list.add("https://defiring.wordpress.com/my-death-flags-show-no-sign-of-ending/");
        list.add("https://durasama.wordpress.com/arifureta-chapters/");
        list.add("https://ensjtrans.com/projects/im-sorry-for-being-born-in-this-world/");
        list.add("https://gilatranslationmonster.wordpress.com/star-sea-lord/");
        list.add("https://hikkinomori.mistbinder.org/falling-in-love-with-the-villainess/");
        list.add("https://honyakusite.wordpress.com/originstory-the-vrmmo-the-advent-of-axebear/");
        list.add("https://isekailunatic.wordpress.com/tsuki-ga-michibiku-isekai-douchuu/");
        list.add("https://isohungrytls.com/bewitching-prince-spoils-his-wife-genius-doctor-unscrupulous-consort");
        list.add("https://kakkokaritranslations.com/se/");
        list.add("https://kobatochan.com/korean-novels/everyone-else-is-a-returnee/");
        list.add("https://konobuta.wordpress.com/translations/uchimusume/");
        list.add("https://kungfubears.wordpress.com/isekai-mahou-wa-okureteru-wn/");
        list.add("https://larvyde.wordpress.com/genou/");
        list.add("https://lightnovelbastion.com/project.php?p=148");
        list.add("https://lightnovelstranslations.com/himekishi-ga-classmate-isekai-cheat-de-dorei-ka-harem/");
        list.add("https://lylisasmodeus.wordpress.com/the-unicorn-legion/");
        list.add("https://manga0205.wordpress.com/sendai-yuusha-wa-inkyou-shitai/");
        list.add("https://mayonaizeshrimp.wordpress.com/no-fatigue/");
        list.add("https://monktranslations.wordpress.com/ryuugoroshi-no-sugosuhibi/");
        list.add("https://nightbreezetranslations.wordpress.com/stellar-transformations/");
        list.add("https://omegaharem.wordpress.com/destruction-flag-otome/");
        list.add("https://oniichanyamete.wordpress.com/index/kenkyo-kenjitsu/");
        list.add("https://paichuntranslations.com/lgpmhr/");
        list.add("https://shalvationtranslations.wordpress.com/we-should-have-slept-while-only-holding-hands-and-yet-table-of-contents/");
        list.add("https://shikkakutranslations.org/kamigoroshi-no-eiyuu-to-nanatsu-no-seiyaku/");
        list.add("https://shintranslations.com/the-new-gate-toc/");
        list.add("https://sodtranslations.wordpress.com/ore-to-kawazu-san-no-isekai-houriki/");
        list.add("https://starrydawntranslations.wordpress.com/katahane/");
        list.add("https://sunshowerfields.wordpress.com/index/");
        list.add("https://tensaitranslations.wordpress.com/light-novel-projects/tsuyokute-new-saga-light-novel/");
        list.add("https://wcctranslation.wordpress.com/table-of-contents/");
        list.add("https://weitranslations.wordpress.com/table-of-contents/");
        list.add("https://wuxianation.com/the-lame-daoist-priest");
        list.add("https://www.oppatranslations.com/main-character-hides-his-strength/");
        list.add("https://yoraikun.wordpress.com/wwl-chapters/");
        list.add("https://youshokutranslations.wordpress.com/theotherworlddininghall/");
        list.add("https://zirusmusings.com/gcr-toc/");
        return list;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IntegerProperty integer = new SimpleIntegerProperty();
//        doGetAllTocs(integer, getTocs().get(33));

//        tryIt(integer, "https://youshokutranslations.wordpress.com/theotherworlddininghall/v1c15/");
        doSequential(getTocs(), this::doGetAllTocs);
        doConcurrent(getTocs(), this::printTocs);
//        getChapters().forEach(this::tryNextLink);
//        openAll(getChapters());
//        browseGuiLess(getChapters());
//        browse(getChapters().get(0));
//        doGetAllChapters("http://gravitytales.com/Novel/era-of-disaster/eod-chapter-4");
//        getChapters().forEach(this::doGetAllChapters);
    }

    private void printTocs(IntegerProperty property, String s) {
        List<Element> selected = new ArrayList<>();
        try {
            Document document = Scraper.getCleanDocument(s);
            if (document.location().contains("gravitytales")) {
                selected = GravityNovel.lookUpToc(document.location());
            } else {
                ContentWrapper wrapper = ContentWrapper.tryAll(document);

                if (wrapper == null) {
                    System.out.println(document.location() + " not supported");
                } else {
                    Element content = wrapper.apply(document);
                    selected = content.select("a");
                }
            }

            List<String> list = new ArrayList<>();
            list.add("LINK");
            selected.forEach(element -> list.add((element.attr("href"))));
            list.add("TEXT");
            selected.forEach(element -> list.add((element.text())));

            map.put(s, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
    }

    private void doGetAllHeaders(IntegerProperty property, String link) {
        try {
            HeaderElement element = HeaderFilter.tryAll(Scraper.getCleanDocument(link));
            if (element == null) {
                errors.add(link);
            }
            property.set(property.get() + 1);
            System.out.println(property.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void doGetAllTocs(IntegerProperty property, String link) {
        TocProcessor processor = new Processor();
        processor.process(link);

        property.set(property.get() + 1);
//            System.out.println(property.get());
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

    private void writeMapToFile() {
        File file = new File("tocFile.txt");
        for (String s : map.keySet()) {
            try {
                map.get(s).add(0, s);
                map.get(s).add(0, "PAGE");
                System.out.println("writing...");
                FileUtils.writeLines(file, map.get(s), true);
                System.out.println("writing finished...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        writeMapToFile();

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
