package Enterprise.data.database;

import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.INTEGER;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * DAO class of {@link User}.
 *
 * @see AbstractTable
 */
public class UserTable extends AbstractDataTable<User> {

    private static final DataColumn ownStatusC = new DataColumn("OWNSTATUS", TEXT, NOT_NULL);
    private static final DataColumn commentC = new DataColumn("COMMENT", TEXT, NOT_NULL);
    private static final DataColumn listC = new DataColumn("LIST", TEXT, NOT_NULL);
    private static final DataColumn processedPortionC = new DataColumn("PROCESSED", INTEGER, NOT_NULL);
    private static final DataColumn ratingC = new DataColumn("RATING", INTEGER, NOT_NULL);
    private static final DataColumn keyWordsC = new DataColumn("KEYWORDS", TEXT, NOT_NULL);
    private static UserTable instance;

    static {
        try {
            instance = new UserTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UserTable() throws SQLException {
        super("USERTABLE", "USER_ID");
        init();
    }

    /**
     * Returns a static Instance of this {@code UserTable}.
     *
     * @return instance - a static instance of this class
     */
    public static UserTable getInstance(){
        if (instance == null) {
            throw new IllegalStateException();
        } else {
            return instance;
        }
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(), ownStatusC, commentC, listC, processedPortionC, ratingC, keyWordsC);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + getTableId() + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + ownStatusC + " " + TEXT + " NOT NULL" +
                "," + commentC + " " + TEXT + " NOT NULL" +
                "," + listC + " " + TEXT + " NOT NULL" +
                "," + processedPortionC + " " + INTEGER + " NOT NULL" +
                "," + ratingC + " " + INTEGER + " NOT NULL" +
                "," + keyWordsC + " " + TEXT + " NOT NULL" +
                ")";*/
    }

    @Override
    protected void setInsertData(User entry, PreparedStatement stmt) throws SQLException {
        String ownStatus = entry.getOwnStatus();
        String comment = entry.getComment();
        int rating = entry.getRating();
        int processedPortion = entry.getProcessedPortion();
        String list = entry.getListName();
        String keyWords = entry.getKeyWords();

        setIntNull(stmt, getIdColumn());
        setString(stmt, ownStatusC, ownStatus);
        setString(stmt, commentC, comment);
        setString(stmt, listC, list);
        setInt(stmt, processedPortionC, processedPortion);
        setInt(stmt, ratingC, rating);
        setString(stmt, keyWordsC, keyWords);
    }

    @Override
    protected User getData(ResultSet rs) throws SQLException {
        User entry;
        int id = getInt(rs, getIdColumn());
        String ownStatus = getString(rs, ownStatusC);
        String comment = getString(rs, commentC);
        int rating = getInt(rs, ratingC);
        int processed = getInt(rs, processedPortionC);
        String list = getString(rs, listC);
        String keyWords = getString(rs, keyWordsC);


        entry = new SimpleUser(id, ownStatus, comment, rating, list, processed, keyWords);
        entry.setEntryOld();
        return entry;
    }
}
