package scrape;

import Enterprise.data.database.AbstractDataTable;
import scrape.sources.Post;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class PostTable extends AbstractDataTable<Post> {

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
    }

    @Override
    protected void setInsertData(Post entry, PreparedStatement stmt) throws SQLException {

    }

    @Override
    protected Post getData(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected String createString() {
        return null;
    }

    @Override
    protected String getInsert() {
        return null;
    }
}
