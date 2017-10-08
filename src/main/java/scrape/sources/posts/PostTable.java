package scrape.sources.posts;

import Enterprise.data.database.AbstractDataTable;
import Enterprise.data.database.CreationTable;
import Enterprise.data.database.DataColumn;
import Enterprise.data.database.SourceTable;
import Enterprise.data.intface.Creation;
import scrape.concurrent.PostCall;
import scrape.sources.Source;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.*;

/**
 *
 */
public class PostTable extends AbstractDataTable<Post> {
    private static final DataColumn TITLE = new DataColumn("TITLE", TEXT, NOT_NULL);
    private static final DataColumn PUBLISHED = new DataColumn("PUBLISHED", TEXT, NOT_NULL);
    private static final DataColumn SOURCE = new DataColumn("SOURCE_ID", INTEGER, NOT_NULL);
    private static final DataColumn CREATION = new DataColumn("CREATION_ID", INTEGER);
    private static final DataColumn POST_LINK = new DataColumn("POST_LINK", TEXT, NOT_NULL);
    private static final DataColumn STICKY = new DataColumn("STICKY", BOOLEAN, NOT_NULL);

    private static PostTable INSTANCE;

    static {
        try {
            INSTANCE = new PostTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of PostTable.
     *
     * @throws SQLException if the table could not be constructed
     */
    private PostTable() throws SQLException {
        super("POSTTABLE", "POST_ID");
        init();
    }

    public static PostTable getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    protected void setInsertData(Post entry, PreparedStatement stmt) throws SQLException {
        setIntNull(stmt, getIdColumn());

        setInt(stmt, SOURCE, entry.getSource().getId());
        setString(stmt, TITLE, entry.getTitle());
        setString(stmt, POST_LINK, entry.getFollowLink());
        setString(stmt, PUBLISHED, entry.getTimeStamp().toString());
        setCreation(stmt, CREATION, entry.getCreation());
        setBoolean(stmt, STICKY, entry.isSticky());
    }

    private void setCreation(PreparedStatement stmt, DataColumn column, Creation creation) throws SQLException {
        if (creation != null) {
            setInt(stmt, column, creation.getId());
        } else {
            setIntNull(stmt, column);
        }
    }

    @Override
    protected Post getData(ResultSet rs) throws SQLException {
        int sourceId = getInt(rs, SOURCE);
        int postId = getInt(rs, getIdColumn());
        int creationId = getInt(rs, CREATION);
        String title = getString(rs, TITLE);
        String time = getString(rs, PUBLISHED);
        String postLink = getString(rs, POST_LINK);
        boolean sticky = getBoolean(rs, STICKY);

        LocalDateTime dateTime = LocalDateTime.parse(time);
        Source source;
        if (sourceId != 0) {
            source = SourceTable.getInstance().getEntry(sourceId);
        } else {
            source = null;
        }

        Creation creation;
        if (creationId == 0) {
            creation = null;
        } else {
            creation = CreationTable.getInstance().getEntry(creationId);
        }

        Post post = new Post(source, title, dateTime, postLink, creation, sticky);
        post.setId(postId, this);
        post.setEntryOld();
        post.setUpdated();

        if (creation == null) {
            PostCall.Action.DELETE_ENTRIES.queueEntry(post);
            PostCall.Action.DELETE_ENTRIES.startTimer();
            return null;
        } else {
            return post;
        }
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(), TITLE, PUBLISHED, POST_LINK, STICKY, SOURCE, CREATION);
    }
}
