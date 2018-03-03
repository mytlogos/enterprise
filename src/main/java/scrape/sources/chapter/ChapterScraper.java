package scrape.sources.chapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.ChapterSearchEntry;
import scrape.LinkDetective;
import scrape.Scraper;
import scrape.sources.chapter.strategies.ChapterConfigSetter;
import scrape.sources.chapter.strategies.ChapterFormat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * // FIXME: 08.10.2017 breaks of prematurely, some site are not wholly scraped
 */
public class ChapterScraper extends Scraper<ChapterConfigs, ChapterSearchEntry> {
    private final Set<String> visited = new HashSet<>();
    private final ChapterFormat format = new ChapterFormat();
    private int counter = 0;

    public static ChapterScraper scraper(ChapterSearchEntry searchEntry) throws IOException {
        Objects.requireNonNull(searchEntry);
        ChapterScraper scraper = new ChapterScraper();
        scraper.init(searchEntry);
        return scraper;
    }

    public List<Element> getChapters(List<String> strings) {
        try {
            document = getCleanDocument(strings.get(0));
            this.configs = new ChapterConfigs();
            if (!configs.isInit()) {
                initConfigs(document);
            }
        } catch (IOException e) {
            System.out.println("could not getAll chapter for " + strings.get(0));
            return new ArrayList<>();
        }
        List<Element> elements = new ArrayList<>();
        strings.forEach(s -> {
            Element element = getChapter(s);
            if (element != null) {
                elements.add(element);
            }
        });
        return elements;
    }

    private void initConfigs(Document document) {
        new ChapterConfigSetter(configs, document).setConfigs();
    }

    private Element getChapter(String s) {
        try {
            document = getCleanDocument(s);
            System.out.println("i am doing sth");
            System.out.println(++counter);
            return new ChapterFormat().format(document, configs);
        } catch (IOException e) {
            System.out.println("could not getAll chapter for " + s);
            return null;
        }
    }

    public Element getChapter() {
        return format.format(document, configs);
    }

    private void init(ChapterSearchEntry entry) throws IOException {
        this.source = entry.getSource();
        initScraper();
        this.search = entry;
    }

    private void initScraper() throws IOException {
        document = getCleanDocument(source.getUrl());
        this.configs = source.getChapterConfigs();
        if (!configs.isInit()) {
            initConfigs(document);
        }
    }

    public Deque<Element> getAllChapters() {
        Deque<Element> chapters = new LinkedList<>();
        Element first = format.format(document, configs);
        chapters.addFirst(first);

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> addPrevious(chapters, first));
        service.submit(() -> addNext(chapters, first));
        // FIXME: 07.10.2017 does not load the correct number of chapters, maybe it just loads all posts content?
        service.shutdown();
        while (!service.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        return chapters;
    }

    private void addPrevious(Deque<Element> chapters, Element first) {
        String previousLink = format.getPreviousLink(first);
        try {
            while (!previousLink.isEmpty() && LinkDetective.validChapterLink(previousLink)) {
                Thread.sleep(1000);
                Document previousDoc = getCleanDocument(previousLink);
                Element previousElement = format.format(previousDoc, configs);

                chapters.addFirst(previousElement);
                String link = format.getPreviousLink(previousElement);
                if (visited.add(link) && !link.equals(previousLink)) {
                    previousLink = link;
                } else {
                    break;
                }
                System.out.println(previousLink);
                System.out.println(++counter);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("could not fetch for " + previousLink);

        }
    }

    private void addNext(Deque<Element> chapters, Element first) {
        String nextLink = format.getNextLink(first);
        try {
            while (!nextLink.isEmpty() && LinkDetective.validChapterLink(nextLink)) {
                Thread.sleep(1000);
                Document previousDoc = getCleanDocument(nextLink);
                Element previousElement = format.format(previousDoc, configs);

                chapters.addLast(previousElement);
                String link = format.getNextLink(previousElement);
                if (visited.add(link) && !link.equals(nextLink)) {
                    nextLink = link;
                } else {
                    break;
                }
                System.out.println(nextLink);
                System.out.println(++counter);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("could not fetch for " + nextLink);
        }
    }

    public Deque<Element> getChapterFromTo(int from, int to) {
        return new LinkedList<>();
    }
}
