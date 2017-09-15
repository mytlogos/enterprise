package scrape.sources;

import Enterprise.data.Cache;
import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a Source from the internet.
 * It contains the url and type of source.
 * This class is not immutable, but should not be
 * susceptible for changes, meaning that the state
 * should not change once constructed.
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
public class Source extends EnterpriseEntry implements DataBase, Comparable<Source> {
    private StringProperty sourceName = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private SourceType sourceType;
    private PostConfigs configs = new PostConfigs();
    private Map<CreationEntry, Post> newestPosts = new HashMap<>();

    private final ObservableSet<Sourceable> creationEntries = FXCollections.observableSet();
    private final Set<Sourceable> deletedEntries = new HashSet<>();

    private static int counter = 0;
    private int id;


    private static Cache<String, Source> sourceCache = new Cache<>();

    /**
     * The constructor of {@code Source}.
     *
     * @param url  the url of the source
     * @param type the type of the source
     * @throws URISyntaxException if url is invalid
     */
    private Source(String url, SourceType type) throws URISyntaxException {
        this(url, type, Default.VALUE);
    }

    /**
     * The constructor of {@code Source}.
     *
     * @param url  the url of the source
     * @param type the type of the source
     * @param id   the database id
     * @throws URISyntaxException if url is invalid
     */
    private Source(String url, SourceType type, int id) throws URISyntaxException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url is invalid: " + url);
        } else {
            URI uri = new URI(url);
            sourceName.set(uri.getHost());
            this.url.set(uri.toString());
        }
        this.id = id;
        sourceType = type;

        setListener();
    }

    public static Source create(int id, String url, SourceType type) throws URISyntaxException {
        return sourceCache.checkCache(new Source(url, type, id), source -> source.url.get());
    }

    public static Source create(String url, SourceType type) throws URISyntaxException {
        return create(Default.VALUE, url, type);
    }

    public void putPost(CreationEntry entry, Post post) {
        if (newestPosts.containsKey(entry)) {
            if (newestPosts.get(entry).getTimeStamp().compareTo(post.getTimeStamp()) < 0) {
                newestPosts.put(entry, post);
            }
        }
    }

    public Post getNewestPost(CreationEntry entry) {
        return newestPosts.get(entry);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id <= 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    @Override
    @Deprecated
    protected void bindUpdated() {
        throw new IllegalAccessError();
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
     * Gets the name (host) of the Source.
     *
     * @return name of the source
     */
    public String getSourceName() {
        return sourceName.get();
    }

    /**
     * Gets the SourceType of this {@code Source}.
     *
     * @return sourceType
     */
    public SourceType getSourceType() {
        return sourceType;
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
     * Gets the value of the {@code url}.
     *
     * @return url of this {@code Source}
     */
    public String getUrl() {
        return url.get();
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
    public void setUpdated() {
        setEntryOld();
    }

    @Override
    @Deprecated
    public boolean isUpdated() {
        throw new IllegalAccessError();
    }

    @Override
    @Deprecated
    public BooleanProperty updatedProperty() {
        throw new IllegalAccessError();
    }

    Set<Sourceable> getSourceables() {
        return creationEntries;
    }

    @Override
    public int compareTo(Source o) {
        int compare = this.sourceName.get().compareTo(o.sourceName.get());
        if (compare == 0) {
            compare = this.url.get().compareTo(o.url.get());
        }
        if (compare == 0) {
            compare = this.sourceType.compareTo(o.sourceType);
        }
        return compare;
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
    public int hashCode() {
        int sourceNameHash = 31 * sourceName.get().hashCode();
        int sourceUrlHash = 31 * url.get().hashCode();
        int sourceTypeHash = 31 * sourceType.hashCode();
        return sourceNameHash + sourceUrlHash + sourceTypeHash;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + sourceName.get() + "@" + sourceType.name() + "@" + creationEntries.size();
    }

    public PostConfigs getConfigs() {
        return configs;
    }

    public void setConfigs(PostConfigs configs) {
        this.configs = configs;
    }

    /**
     * The type enumerator of this {@code Source}.
     * At the moment it has no real function, but
     * will be important later for the Scraper.
     */
    public enum SourceType {
        /**
         * The site contains the table of content,
         * used for indexing the portions (chapters,...)
         * or fetching the whole content.
         */
        TOC("TOC"),
        /**
         * The site has no long path, mainly used
         * for scraping posts.
         */
        START("Homepage");

        private final String type;

        /**
         * The constructor of {@code SourceType}.
         *
         * @param type the string representation of the enum
         */
        SourceType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

    }

}
