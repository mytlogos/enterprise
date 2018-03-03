package scrape.sources;

import enterprise.data.impl.AbstractDataEntry;
import enterprise.data.intface.Creation;
import enterprise.data.intface.DataEntry;
import enterprise.data.intface.Sourceable;
import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import scrape.scrapeDaos.SourceDao;
import scrape.sources.chapter.ChapterConfigs;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostConfigs;
import tools.Cache;
import tools.Condition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    private final ObservableSet<Sourceable> creationEntries = FXCollections.observableSet();
    private final Set<Sourceable> deletedEntries = new HashSet<>();
    private final Map<Creation, Post> newestPosts = new TreeMap<>();
    private SourceType sourceType;
    private PostConfigs postConfigs = new PostConfigs();

    Source() {
        setListener();
    }

    /**
     * Sets this {@code Source} dead if {@code creationEntries}
     * is empty, else sets alive.
     */
    private void setListener() {
        creationEntries.addListener((SetChangeListener<? super Sourceable>) observable -> {
            if (creationEntries.isEmpty()) {
                setDead();
            } else {
                setAlive();
            }
        });
    }

    private Source(String url, SourceType type, PostConfigs configs) throws URISyntaxException {
        this(url, type);
        Condition.check().nonNull(configs);
        postConfigs = configs;
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
        setListener();
    }

    private void setName(URI uri) {
        String host = uri.getHost();
        String[] strings = host.split("\\.");
        StringBuilder builder = new StringBuilder();

        for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
            String string = strings[i];
            if (!string.matches("www|com|de|org|blogspot|wordpress")) {
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

    public static Source create(String url, SourceType type, PostConfigs configs) throws URISyntaxException {
        Source value = new Source(url, type, configs);
        return sourceCache.checkCache(value, source -> source.url.get());
    }

    public static Source create(String url, SourceType type) throws URISyntaxException {
        Source value = new Source(url, type);
        return sourceCache.checkCache(value, source -> source.url.get());
    }

    public void putPost(Creation entry, Post post) {
        if (newestPosts.containsKey(entry)) {
            if (newestPosts.get(entry).getTimeStamp().compareTo(post.getTimeStamp()) < 0) {
                newestPosts.put(entry, post);
            }
        }
    }

    public Post getNewestPost(Creation entry) {
        return newestPosts.get(entry);
    }

    /**
     * Returns the Set of {@link Sourceable} which held a reference
     * to this {@code Source}.
     *
     * @return set of {@code Sourceables}
     */
    public Set<Sourceable> getDeletedEntries() {
        return deletedEntries;
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

    public void setSourceName(String sourceName) {
        this.sourceName.set(sourceName);
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + sourceName.get() + "@" + sourceType.name() + "@" + creationEntries.size();
    }

    /**
     * Returns the {@code PostConfigs} of this Source.
     *
     * @return the postConfigs of this source
     */
    public PostConfigs getPostConfigs() {
        return postConfigs;
    }

    public ChapterConfigs getChapterConfigs() {
        return chapterConfigs;
    }

    Set<Sourceable> getSourceables() {
        return creationEntries;
    }

}
