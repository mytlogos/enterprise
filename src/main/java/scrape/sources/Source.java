package scrape.sources;

import enterprise.data.impl.AbstractDataEntry;
import enterprise.data.intface.DataEntry;
import enterprise.data.intface.Sourceable;
import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scrape.Config;
import scrape.scrapeDaos.SourceDao;
import scrape.sources.novel.chapter.ChapterConfigs;
import scrape.sources.posts.PostConfig;
import tools.Cache;
import tools.Condition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a Source from the internet.
 * It contains the url and type of source.
 * This class is not immutable, but should not be
 * susceptible for changes in their data.
 * <p>
 * The source holds references to the {@link Sourceable}
 * which hold them. If no references to {@code Sourceables}
 * are available this {@code Source} is dead, making it
 * susceptible for deleting it from the database if it
 * does exist there.
 * </p>
 * <p>
 * It can be alive again, if it gets references to {@code Sourceables}.
 * </p>
 */
@DataAccess(SourceDao.class)
public class Source extends AbstractDataEntry implements DataEntry {
    private static final Cache<String, Source> sourceCache = new Cache<>();
    private final StringProperty sourceName = new SimpleStringProperty();
    private final StringProperty url = new SimpleStringProperty();
    private final ChapterConfigs chapterConfigs = new ChapterConfigs();
    private SourceType sourceType;
    private Map<String, Config> configMap = new HashMap<>();

    Source() {

    }

    private Source(String url, SourceType type, PostConfig configs) throws URISyntaxException {
        this(url, type);
        Condition.check().nonNull(configs);
    }

    /**
     * The constructor of {@code Source}.
     *
     * @param url  the url of the source
     * @param type the type of the source
     * @throws URISyntaxException if url is invalid
     */
    private Source(String url, SourceType type) throws URISyntaxException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url is invalid: " + url);
        } else {
            URI uri = new URI(url);
            setName(uri);
            this.url.set(uri.toString());
        }
        sourceType = type;
    }

    public static void cache(Collection<Source> sources) {
        for (Source source : sources) {
            cache(source);
        }
    }

    public static Source cache(Source source) {
        return sourceCache.checkCache(source, Source::getUrl);
    }

    public static Source create(String url, SourceType type, PostConfig configs) throws URISyntaxException {
        Source value = new Source(url, type, configs);
        return sourceCache.checkCache(value, source -> source.url.get());
    }

    @Override
    public String toString() {
        return "Source{" +
                "url=" + url +
                ", configMap=" + configMap +
                '}';
    }

    public static Source create(String url, SourceType type) throws URISyntaxException {
        Source value = new Source(url, type);
        return sourceCache.checkCache(value, source -> source.url.get());
    }

    /**
     * Gets the sourceName {@code Property}.
     * Used for presenting purposes in the gui.
     *
     * @return stringProperty
     */
    public StringProperty sourceNameProperty() {
        return sourceName;
    }

    /**
     * Gets the url {@code Property}.
     * Used for presenting purposes in the gui.
     *
     * @return stringProperty
     */
    public StringProperty urlProperty() {
        return url;
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Source)) return -1;

        Source o = (Source) gorgonEntry;

        int compare = getSourceName().compareTo(o.getSourceName());
        if (compare == 0) {
            compare = getUrl().compareTo(o.getUrl());
        }
        if (compare == 0) {
            compare = getSourceType().compareTo(o.getSourceType());
        }
        return compare;
    }

    /**
     * Gets the name (host) of the Source.
     *
     * @return name of the source
     */
    public String getSourceName() {
        return sourceName.get();
    }

    public <E extends Config> E getConfig(E e) {
        //per key or per class?
        final Config config = configMap.computeIfAbsent(e.getKey(), k -> e);
        return (E) config;
    }

    /**
     * Gets the value of the {@code url}.
     *
     * @return url of this {@code Source}
     */
    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    /**
     * Gets the SourceType of this {@code Source}.
     *
     * @return sourceType
     */
    public SourceType getSourceType() {
        return sourceType;
    }

    @Override
    public int hashCode() {
        int sourceNameHash = 31 * sourceName.get().hashCode();
        int sourceUrlHash = 31 * url.get().hashCode();
        int sourceTypeHash = 31 * sourceType.hashCode();
        return sourceNameHash + sourceUrlHash + sourceTypeHash;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof Source) {
            equals = this.sourceName.get().equalsIgnoreCase(((Source) obj).sourceName.get());
            if (equals) {
                equals = this.url.get().equalsIgnoreCase(((Source) obj).url.get());
            }
            if (equals) {
                equals = this.sourceType.equals(((Source) obj).sourceType);
            }
        }
        return equals;
    }

    void setSourceName(String sourceName) {
        this.sourceName.set(sourceName);
    }

    private void setName(URI uri) {
        String host = uri.getHost();
        String[] strings = host.split("\\.");
        StringBuilder builder = new StringBuilder();

        for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
            String string = strings[i];
            if (!string.matches("www|com|de|en|org|blogspot|wordpress")) {
                builder.append(string);
                if (i < (stringsLength - 1)) {
                    builder.append(".");
                }
            }
        }

        if (builder.lastIndexOf(".") == builder.length() - 1) {
            builder.deleteCharAt(builder.lastIndexOf("."));
        }
        // TODO: 28.09.2017 do sth better to make a sensible name
        sourceName.set(builder.toString());
    }

}
