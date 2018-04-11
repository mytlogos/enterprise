package scrape.sources.novel;

import com.google.gson.Gson;
import enterprise.data.Default;
import enterprise.data.intface.Creation;
import org.jsoup.Connection;
import scrape.sources.Content;
import scrape.sources.SourceAccessor;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.novel.toc.structure.intface.Portion;
import scrape.sources.novel.toc.structure.novel.Chapter;
import scrape.sources.posts.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class QidianAdapter implements WebAdapter {

    private Creation creation;
    private String csrfToken;

    public QidianAdapter() {
    }

    public TableOfContent getToc(String uri) {
        Matcher matcher = Pattern.compile("webnovel\\.com/book/(\\d+)/").matcher(uri);

        long bookId;
        if (matcher.find()) {
            String idString = matcher.group(1);
            bookId = Long.parseUnsignedLong(idString);
        } else {
            throw new IllegalArgumentException("is not a toc page of a book");
        }

        TableOfContent root = new TableOfContent(creation.getTitle(), uri);

        try {

            String requestToc = "https://www.webnovel.com/apiajax/chapter/GetChapterList?bookId=" + bookId;
            TocResponse tocResponse = getTocResponse(requestToc);

            for (ChapterItem item : tocResponse.data.chapterItems) {
                String name = item.chapterName;
                int index = item.chapterIndex;
                Chapter chapter = new Chapter(name, false, index, 0);
                root.add(chapter);
            }

        } catch (IOException e) {
            Default.LOGGER.log(Level.WARNING, "could not get document", e);
            return null;
        }
        return root;
    }

    private TocResponse getTocResponse(String link) throws IOException {
        final String body = SourceAccessor.getJson(link);
        return new Gson().fromJson(body, TocResponse.class);
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
        List<String> novels = new ArrayList<>();
        try {
            final Connection.Response response = SourceAccessor.getResponse("https://www.webnovel.com/");
            csrfToken = response.cookies().get("_csrfToken");


            for (int i = 1; ; i++) {
                final String link = "https://www.webnovel.com/apiajax/category/ajax?_csrfToken=" + csrfToken + "&pageIndex=" + i + "&category=0";
                final String json = SourceAccessor.getJson(link);
                final QidianNovelList novelList = new Gson().fromJson(json, QidianNovelList.class);

                if (novelList.code == 1 || novelList.data.items.length == 0) {
                    break;
                }

                for (NovelItem item : novelList.data.items) {
                    final String name = item.bookName;

                    if (name != null && !name.isEmpty()) {
                        novels.add(name);
                    }
                }
            }
        } catch (IOException e) {
            return novels;
        }
        return novels;
    }

    private Portion get(String link) throws IOException {
        ChapterResponse response = getChapterResponse(link);

        int index = response.data.chapterInfo.chapterIndex;
        String chapterName = response.data.chapterInfo.chapterName;
        return new Chapter(chapterName, false, index, 0);
    }

    private ChapterResponse getChapterResponse(String link) throws IOException {
        final String body = SourceAccessor.getJson(link);
        return new Gson().fromJson(body, ChapterResponse.class);
    }

    private static class QidianNovelList {
        private int code;
        private Data data;
        private String msg;
    }

    private static class Data {
        private NovelItem[] items;
        private int total;
        private int orderBy;
        private int categoryId;
    }

    private static class NovelItem {
        private long bookId;
        private String bookName;
        private String categoryName;
        private double totalScore;
        private String description;
        private int type;
        private TagInfo[] tagInfo;
    }

    private static class TagInfo {
        private int tagId;
        private String tagName;
    }

    private static class TocResponse {
        private int code;
        private TocData data;
        private String msg;
    }


    private static class ChapterResponse {
        private int code;
        private ChapterData data;
        private String msg;
    }

    private static class ChapterData {
        private ChapterBookInfo bookInfo;
        private ChapterInfo chapterInfo;
    }

    private static class ChapterBookInfo {
        private long bookId;
        private String bookName;
        private long authorId;
        private String authorName;
        private int totalChapterNum;
        private int actionStatus;
        private int reviewTotal;
        private String patreonLink;
    }

    private static class ChapterInfo {
        private long chapterId;
        private String chapterName;
        private int chapterIndex;
        private GroupItem[] groupItems;
        private TranslatorItem[] translatorItems;
        private EditorItem[] editorItems;
        private long preChapterId;
        private long nextChapterId;
        private int isVip;
        private String source;
        private String content;
        private long firstChapterId;
        private int firstChapterIndex;
        private int reviewTotal;
        private Note notes;
        private boolean isAuth;
        private boolean isRichFormat;
        private int SSPrice;
    }

    private static class Note {
        private String name;
        private String penName;
        private String avatar;
        private String role;
        private String note;
    }

    private static class TranslatorItem {
        private int guid;
        private int id;
        private String name;
    }

    private static class EditorItem {
        private int guid;
        private int id;
        private String name;
    }


    private static class GroupItem {
    }

    private static class TocData {
        private TocBookInfo bookInfo;
        private ChapterItem[] chapterItems;
    }

    private static class TocBookInfo {
        private long bookId;
        private String bookName;
        private int totalChapterNum;
        private long newChapterId;
        private int newChapterIndex;
        private String newChapterName;
        private String newChapterTime;
    }

    private static class ChapterItem {
        private long chapterId;
        private String chapterName;
        private int chapterIndex;
        private int isVip;
        private String source;
        private int uuid;
        private int feeType;
    }
}
