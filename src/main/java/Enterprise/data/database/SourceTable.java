package Enterprise.data.database;

import Enterprise.data.ReflectUpdate;
import scrape.sources.PostConfigs;
import scrape.sources.Source;
import scrape.sources.novels.FeedGetter;
import scrape.sources.novels.strategies.ArchiveGetter;
import scrape.sources.novels.strategies.PostsWrapper;
import scrape.sources.novels.strategies.intface.Filter;
import scrape.sources.novels.strategies.intface.impl.*;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * DAO class of {@link Source}.
 */
public class SourceTable extends AbstractDataTable<Source> {
    private static final DataColumn sourceUrl = new DataColumn("SOURCEURL", TEXT, NOT_NULL);
    private static final DataColumn sourceType = new DataColumn("SOURCETYPE", TEXT, NOT_NULL);
    private static final DataColumn archiveSearcher = new DataColumn("ARCHIVESEARCHER", TEXT);
    private static final DataColumn feed = new DataColumn("FEED", TEXT);
    private static final DataColumn postWrapper = new DataColumn("POSTWRAPPER", TEXT);
    private static final DataColumn postElement = new DataColumn("POSTELEMENT", TEXT);
    private static final DataColumn timeElement = new DataColumn("TIMEELEMENT", TEXT);
    private static final DataColumn titleElement = new DataColumn("TITLEELEMENT", TEXT);
    private static final DataColumn contentElement = new DataColumn("CONTENTELEMENT", TEXT);
    private static final DataColumn footerElement = new DataColumn("FOOTERELEMENT", TEXT);

    private static SourceTable INSTANCE;

    static {
        try {
            INSTANCE = new SourceTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code SourceTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private SourceTable() throws SQLException {
        super("SOURCETABLE", "SOURCE_ID");
        init();
    }

    /**
     * Returns a static Instance of this {@code SourceTable}.
     *
     * @return instance - Instance of this {@code SourceTable}
     * @see #SourceTable()
     */
    public static SourceTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(),
                sourceUrl,
                sourceType,
                archiveSearcher,
                feed,
                postWrapper,
                postElement,
                timeElement,
                titleElement,
                contentElement,
                footerElement);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + getTableId() + " INTEGER PRIMARY KEY NOT NULL, " +
                sourceUrl + " TEXT NOT NULL, " +
                sourceType + " TEXT NOT NULL, " +
                archiveSearcher + " TEXT, " +
                feed + " TEXT, " +
                postWrapper + " TEXT, " +
                postElement + " TEXT, " +
                timeElement + " TEXT, " +
                titleElement + " TEXT, " +
                contentElement + " TEXT, " +
                footerElement + " TEXT, PRIMARY KEY(`SOURCE_ID`) )";*/
    }

    @Override
    protected void setInsertData(Source entry, PreparedStatement stmt) throws SQLException {
        String url = entry.getUrl();
        String srceType = entry.getSourceType().name();
        PostConfigs configs = entry.getConfigs();

        setIntNull(stmt, getIdColumn());
        setString(stmt, sourceUrl, url);
        setString(stmt, sourceType, srceType);

        insertConfig(archiveSearcher, configs.getArchive(), stmt);
        insertConfig(feed, configs.getFeed(), stmt);
        insertConfig(postWrapper, configs.getWrapper(), stmt);
        insertConfig(postElement, configs.getPosts(), stmt);
        insertConfig(timeElement, configs.getTime(), stmt);
        insertConfig(titleElement, configs.getTitle(), stmt);
        insertConfig(contentElement, configs.getPostContent(), stmt);
        insertConfig(footerElement, configs.getFooter(), stmt);
    }

    private void insertConfig(DataColumn column, Filter filter, PreparedStatement stmt) throws SQLException {
        if (filter != null) {
            setString(stmt, column, filter.toString());
        } else {
            setStringNull(stmt, column);
        }
    }

    @Override
    protected Source getData(ResultSet rs) throws SQLException {
        Source entry = null;

        int id = getInt(rs, getIdColumn());
        String url = getString(rs, sourceUrl);
        String type = getString(rs, sourceType);

        Source.SourceType sourceType = Source.SourceType.valueOf(type);

        try {
            entry = Source.create(id, url, sourceType);
            setPostConfigs(rs, entry.getConfigs());
            entry.setEntryOld();
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "corrupt data: URL: "
                    + url + " SourceType: " +
                    sourceType + " Id: " + id, e);
            e.printStackTrace();
        }
        return entry;
    }

    private void setPostConfigs(ResultSet rs, PostConfigs configs) throws SQLException {
        String archiveSearcherString = getString(rs, archiveSearcher);
        String feedString = getString(rs, feed);
        String postWrapperString = getString(rs, postWrapper);

        String postElementString = getString(rs, postElement);
        String timeElementString = getString(rs, timeElement);
        String titleElementString = getString(rs, titleElement);

        String contentElementString = getString(rs, contentElement);
        String footerElementString = getString(rs, footerElement);

        configs.setArchive(getMatch(ArchiveGetter.getFilter(), archiveSearcherString));
        configs.setFeed(getMatch(FeedGetter.getFilter(), feedString));
        configs.setWrapper(getMatch(Arrays.asList(PostsWrapper.values()), postWrapperString));

        configs.setPosts(getMatch(new PostsFilter().getFilter(), postElementString));
        configs.setTitle(getMatch(new TitlesFilter().getFilter(), titleElementString));
        configs.setTime(getMatch(new TimeFilter().getFilter(), timeElementString));

        configs.setPostBody(getMatch(new ContentFilter().getFilter(), contentElementString));
        configs.setFooter(getMatch(new FooterFilter().getFilter(), footerElementString));
    }

    private <E> E getMatch(Collection<E> collection, String match) {
        if (match != null) {
            for (E e : collection) {
                if (e.toString().matches(match)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Deletes a {@code Collection} of {@code Source} with the given connection in the database.
     *
     * @param entries    {@code Collection} to be deleted
     * @param connection {@code Connection} to be used for this transaction
     * @return integers return an integer {@code Array} of affected Rows per Statement
     * @throws SQLException if {@code Collection} could not be deleted
     */
    boolean deleteSources(Collection<Source> entries, Connection connection) throws SQLException {
        validate(entries, connection);

        if (entries.isEmpty()) {
            return false;
        }

        int[] deleted;
        String delete = "Delete from " + getTableName() + " where " + getTableId() + "= ?";

        try (PreparedStatement statement = connection.prepareStatement(delete)) {

            for (Source entry : entries) {
                setDeleteData(statement, entry);
                statement.addBatch();

                System.out.println("Entry mit ID " + entry.getId() + " wurde gelöscht!");
            }
            deleted = statement.executeBatch();
        }

        if (deleted.length == entries.size()) {
            return true;
        } else {
            throw new SQLException("could not delete all");
        }
    }

    /**
     * Checks if entry is dead and sets the parameter of a {@code PreparedStatement}
     * of an DELETE operation, if it is true.
     *
     * @param statement {@code PreparedStatement} to operate on
     * @param entry     {@link Enterprise.data.intface.Entry} which shall be deleted
     * @throws SQLException if there is an problem with the {@code PreparedStatement}
     */
    private void setDeleteData(PreparedStatement statement, Source entry) throws SQLException {
        if (entry.isDead()) {
            statement.setInt(1, entry.getId());
        }
    }

    @Override
    final protected Set<String> getStatements(ReflectUpdate classSpy, Source entry) {
        return classSpy.updateStrings(entry.getConfigs(), entry, getTableName(), getTableId());
    }
}
