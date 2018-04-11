package scrape.sources.novel;

import com.google.gson.Gson;
import enterprise.data.intface.Creation;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import scrape.sources.Content;
import scrape.sources.SourceAccessor;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.posts.Post;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class GravityAdapter implements WebAdapter {
    private String Name;
    private String CoverUrl;
    private String Slug;
    private int Id;
    private Creation creation;


    public static TableOfContent lookUpToc(String link) {
        if (link == null || link.isEmpty()) {
            return null;
        }
        String string = "http://gravitytales.com/api/novels/";
        Gson gson = new Gson();
        try {
            GravityAdapter[] gravityNovels = getJson(string, gson, GravityAdapter[].class);

            URI uri = URI.create(link);
            GravityAdapter novel = null;

            for (GravityAdapter gravityNovel : gravityNovels) {
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
        Connection.Response response = SourceAccessor.getResponse(link);
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

    private static TableOfContent createToc(GravityAdapter novel, List<GravityChapter> chapterList) {
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

    public TableOfContent getToc(String url) {
        return null;
    }

    @Override
    public TableOfContent getToc() {
        return null;
    }

    @Override
    public Collection<Post> getPosts() {
        return null;
    }

    @Override
    public Content getContent() {
        return null;
    }

    @Override
    public void setCreation(Creation creation) {
        this.creation = creation;
    }

    @Override
    public List<String> getMedia() {
        final ArrayList<String> list = new ArrayList<>();
        try {
            final String json = SourceAccessor.getJson("http://gravitytales.com/api/novels/");
            final GravityNovel[] gravityNovels = new Gson().fromJson(json, GravityNovel[].class);

            for (GravityNovel novel : gravityNovels) {
                final String name = novel.Name;

                if (name != null && !name.isEmpty()) {
                    list.add(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    /**
     * Class for using the Json-Data of the api for
     * the novel list.
     */
    private static class GravityNovel {
        private int Id;
        private String Name;
        private String CoverUrl;
        private String Slug;
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
