package scrape.sources.chapter;

import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import scrape.sources.toc.structure.CreationRoot;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class GravityNovel {
    private String Name;
    private String CoverUrl;
    private String Slug;
    private int Id;


    public static CreationRoot lookUpToc(String link) {
        if (link == null || link.isEmpty()) {
            return null;
        }
        String string = "http://gravitytales.com/api/novels/";
        Gson gson = new Gson();
        try {
            GravityNovel[] gravityNovels = getJson(string, gson, GravityNovel[].class);

            URI uri = URI.create(link);
            GravityNovel novel = null;

            for (GravityNovel gravityNovel : gravityNovels) {
                if (uri.getPath().contains(gravityNovel.Slug)) {
                    novel = gravityNovel;
                    break;
                }
            }

            if (novel != null) {
                String chapterGroups = "http://gravitytales.com/api/novels/chaptergroups/" + novel.Id;
                GravityToc[] tocs = getJson(chapterGroups, gson, GravityToc[].class);

                List<GravityChapter> chapterList = getGravityChapters(gson, tocs);
                return createToc(novel, chapterList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T getJson(String link, Gson gson, Class<T> classOfT) throws IOException {
        Connection.Response response = Jsoup.connect(link).ignoreContentType(true).execute();
        String novelsJsonArray = response.body();

        return gson.fromJson(novelsJsonArray, classOfT);
    }

    private static List<GravityChapter> getGravityChapters(Gson gson, GravityToc[] tocs) throws IOException {
        List<GravityChapter> chapterList = new ArrayList<>();
        for (GravityToc toc : tocs) {
            String chapterGroup = "http://gravitytales.com/api/novels/chaptergroup/" + toc.ChapterGroupId;
            chapterList.addAll(Arrays.asList(getJson(chapterGroup, gson, GravityChapter[].class)));
        }
        return chapterList;
    }

    private static CreationRoot createToc(GravityNovel novel, List<GravityChapter> chapterList) {
        List<Element> elements = new ArrayList<>();

        for (GravityChapter gravityChapter : chapterList) {
            String chapterLink = "http://gravitytales.com/novel/" + novel.Slug + "/" + gravityChapter.Slug;

            Element element = new Element("a");
            element.attr("href", chapterLink);
            element.text(gravityChapter.Name);

            elements.add(element);
        }
        // TODO: 02.11.2017 convert the elements to an toc
        return null;
    }

    /**
     *
     */
    private static class GravityToc {
        private int ChapterGroupId;
        private int NovelId;
        private int Order;
        private String Title;
    }

    private static class GravityChapter {
        private int Number;
        private String Name;
        private String Slug;
    }
}
