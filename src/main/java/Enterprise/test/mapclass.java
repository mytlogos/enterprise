package Enterprise.test;

import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class mapclass {
    private static int counter = 0;

    // TODO: 12.09.2017 lesyt the special case, need a browser for that
    public static void main(String[] args) throws InterruptedException {

    }


    private static void write(String location, Elements elements) {

        String fileName = "file" + counter + ".html";
        try {
            URL url = new URL(location);
            fileName = url.getHost() + ".html";
        } catch (MalformedURLException e) {
            counter++;
        }

        fileName = System.getProperty("user.dir") + "\\TestData\\TestPosts\\" + fileName;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < elements.size(); i++) {
                bufferedWriter.write("Element Nr." + (i + 1) + "\n");
                bufferedWriter.write(elements.get(i).outerHtml());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> allLinks() {
        List<String> links = new ArrayList<>();
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
        // TODO: 13.09.2017 lesyt die sonderwurst
//        links.add("https://lesyt.xyz/");
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
//        links.add("https://www.facebook.com/Baka.Tsuki.org");
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

        Collections.sort(links);
        return links;
    }
}
