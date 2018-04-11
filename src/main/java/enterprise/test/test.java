package enterprise.test;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Element;
import scrape.sources.novel.toc.NovelTocProcessor;
import scrape.sources.novel.toc.structure.TableOfContent;
import tools.TimeMeasure;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created on 05.08.2017.
 */
public class test {
    public static void main(String[] args) {
        NovelTocProcessor process = new NovelTocProcessor();
//        getProcess(process, testScraper.getTocs().getAll(0));
        doSequential(testScraper.getTocs(), s -> getProcess(process, s));
    }

    private static void doSequential(List<String> list, Consumer<String> consumer) {
        TimeMeasure measure = TimeMeasure.start();
        list.forEach(s -> {
            try {
                consumer.accept(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        measure.finish();
        System.out.println(measure.getMessage(s -> "PostTime needed: " + s));
    }

    private static void getProcess(NovelTocProcessor process, String s) {
        System.out.println("for " + s);
        TableOfContent processed = process.process(s);
        System.out.println(processed);
    }

    private static void writeToc(Element processed) {
        String pathname = "D:\\Programmieren\\Java\\Projects\\enterprise\\TestData";
        URI uri = URI.create(processed.baseUri());
        String fileName = pathname + "\\" + uri.getHost() + ".html";

        try {
            FileUtils.write(new File(fileName), processed.outerHtml(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getTocsWithoutVol() {
        String s = "http://eccentrictranslations.com/atf-catalogue/\n" +
                "http://gravitytales.com/novel/a-mercenarys-war\n" +
                "http://infinitenoveltranslations.net/hachinan-tte-sore-wa-nai-deshou/\n" +
                "http://jigglypuffsdiary.com/ashes-and-kingdoms/\n" +
                "http://myoniyonitranslations.com/demon-king-hero/\n" +
                "http://raisingthedead.ninja/current-projects/demon-lords-pet/\n" +
                "http://razpyon.tumblr.com/tgfnsyl\n" +
                "http://saigotranslation.com/isekaitenseionna-index-page/\n" +
                "http://unlimitednovelfailures.mangamatters.com/risou-no-himo-seikatsu/\n" +
                "http://volarenovels.com/adorable-creature-attack/\n" +
                "http://www.novelsaga.com/martial-god-space-index/\n" +
                "http://www.rebirth.online/novel/400-years-old-virgin-demon-king\n" +
                "http://www.sousetsuka.com/p/blog-page_11.html\n" +
                "http://www.wuxiaheroes.com/nine-yang-sword-saint/\n" +
                "http://www.wuxiatranslations.com/law-of-the-devil/\n" +
                "http://www.wuxiaworld.com/7-killers/\n" +
                "https://hikkinomori.mistbinder.org/falling-in-love-with-the-villainess/\n" +
                "https://isohungrytls.com/bewitching-prince-spoils-his-wife-genius-doctor-unscrupulous-consort\n" +
                "https://kakkokaritranslations.com/se/\n" +
                "https://kobatochan.com/korean-novels/everyone-else-is-a-returnee/\n" +
                "https://kungfubears.wordpress.com/isekai-mahou-wa-okureteru-wn/\n" +
                "https://lightnovelbastion.com/project.php?p=148\n" +
                "https://mayonaizeshrimp.wordpress.com/no-fatigue/\n" +
                "https://monktranslations.wordpress.com/ryuugoroshi-no-sugosuhibi/\n" +
                "https://nightbreezetranslations.wordpress.com/stellar-transformations/\n" +
                "https://oniichanyamete.wordpress.com/index/kenkyo-kenjitsu/\n" +
                "https://sodtranslations.wordpress.com/ore-to-kawazu-san-no-isekai-houriki/\n" +
                "https://starrydawntranslations.wordpress.com/katahane/\n" +
                "https://sunshowerfields.wordpress.com/index/\n" +
                "https://wcctranslation.wordpress.com/table-of-contents/\n" +
                "https://yoraikun.wordpress.com/wwl-chapters/\n";
        return new ArrayList<>(Arrays.asList(s.split("\n")));
    }

    private static List<String> getTocsWithVol() {
        String s = "http://japtem.com/projects/dd-toc/\n" +
                "http://moonbunnycafe.com/a-lonesome-fragrance-waiting-to-be-appreciated/\n" +
                "http://scrya.org/my-disciple-died-yet-again/\n" +
                "http://skythewood.blogspot.de/p/altina-sword-princess.html\n" +
                "http://thelordofpie.blogspot.com/p/mushoku-tensei.html\n" +
                "http://www.oyasumireads.com/isekaijin-no-tebikisho-2/\n" +
                "https://arkmachinetranslations.wordpress.com/ark-table-of-contents/\n" +
                "https://arsl31.wordpress.com/konjiki-no-word-master/\n" +
                "https://avertranslation.blogspot.de/p/blog-page.html\n" +
                "https://bayabuscotranslation.com/table-of-content/\n" +
                "https://binhjamin.wordpress.com/sayonara-ryuusei-konnichiwa-jinsei/\n" +
                "https://defiring.wordpress.com/my-death-flags-show-no-sign-of-ending/\n" +
                "https://durasama.wordpress.com/arifureta-chapters/\n" +
                "https://ensjtrans.com/projects/im-sorry-for-being-born-in-this-world/\n" +
                "https://gilatranslationmonster.wordpress.com/star-sea-lord/\n" +
                "https://honyakusite.wordpress.com/originstory-the-vrmmo-the-advent-of-axebear/\n" +
                "https://isekailunatic.wordpress.com/tsuki-ga-michibiku-isekai-douchuu/\n" +
                "https://konobuta.wordpress.com/translations/uchimusume/\n" +
                "https://larvyde.wordpress.com/genou/\n" +
                "https://lightnovelstranslations.com/himekishi-ga-classmate-isekai-cheat-de-dorei-ka-harem/\n" +
                "https://lylisasmodeus.wordpress.com/the-unicorn-legion/\n" +
                "https://manga0205.wordpress.com/sendai-yuusha-wa-inkyou-shitai/\n" +
                "https://omegaharem.wordpress.com/destruction-flag-otome/\n" +
                "https://shalvationtranslations.wordpress.com/we-should-have-slept-while-only-holding-hands-and-yet-table-of-contents/\n" +
                "https://shikkakutranslations.org/kamigoroshi-no-eiyuu-to-nanatsu-no-seiyaku/\n" +
                "https://shintranslations.com/the-new-gate-toc/\n" +
                "https://tensaitranslations.wordpress.com/light-novel-projects/tsuyokute-new-saga-light-novel/\n" +
                "https://weitranslations.wordpress.com/table-of-contents/\n" +
                "https://wuxianation.com/the-lame-daoist-priest\n" +
                "https://www.oppatranslations.com/main-character-hides-his-strength/\n" +
                "https://youshokutranslations.wordpress.com/theotherworlddininghall/\n" +
                "https://zirusmusings.com/gcr-toc/";
        return new ArrayList<>(Arrays.asList(s.split("\n")));
    }

}
