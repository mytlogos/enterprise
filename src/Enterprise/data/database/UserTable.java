package Enterprise.data.database;

import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * DAO class of {@link User}.
 *
 * @see AbstractTable
 */
public class UserTable extends AbstractDataTable<User> {

    private UserTable() throws SQLException {
        super("USERTABLE", "USER_ID");
    }

    private static final String ownStatusC = "OWNSTATUS";
    private static final String commentC = "COMMENT";
    private static final String listC = "LIST";
    private static final String processedPortionC = "PROCESSED";
    private static final String ratingC = "RATING";
    private static final String keyWordsC = "KEYWORDS";

    private static UserTable instance;

    static {
        try {
            instance = new UserTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    final String getInsert() {
        return "insert into " + getTableName() +
                " values(?,?,?,?,?,?,?)";
    }

    @Override
    protected String createString() {
        return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + tableId + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + ownStatusC + " " + TEXT + " NOT NULL" +
                "," + commentC + " " + TEXT + " NOT NULL" +
                "," + listC + " " + TEXT + " NOT NULL" +
                "," + processedPortionC + " " + INTEGER + " NOT NULL" +
                "," + ratingC + " " + INTEGER + " NOT NULL" +
                "," + keyWordsC + " " + TEXT + " NOT NULL" +
                ")";
    }

    @Override
    void setInsertData(User entry, PreparedStatement stmt) throws SQLException {
        String ownStatus = entry.getOwnStatus();
        String comment = entry.getComment();
        int rating = entry.getRating();
        int processedPortion = entry.getProcessedPortion();
        String list = entry.getList();
        String keyWords = entry.getKeyWords();

        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,ownStatus);
        stmt.setString(3,comment);
        stmt.setString(4,list);
        stmt.setInt(5,processedPortion);
        stmt.setInt(6,rating);
        stmt.setString(7,keyWords);
    }

    @Override
    User getData(ResultSet rs) throws SQLException {
        User entry;
        int id = rs.getInt(tableId);
        String ownStatus = rs.getString(ownStatusC);
        String comment = rs.getString(commentC);
        int rating = rs.getInt(ratingC);
        int processed = rs.getInt(processedPortionC);
        String list = rs.getString(listC);
        String keyWords = rs.getString(keyWordsC);


        entry = new SimpleUser(id, ownStatus, comment, rating, list, processed, keyWords);
        entry.setEntryOld();
        return entry;
    }

    @Override
    void queryIdData(User entry, PreparedStatement stmt) throws SQLException {
        String ownStatus = entry.getOwnStatus();
        String comment = entry.getComment();
        int rating = entry.getRating();
        int processedPortion = entry.getProcessedPortion();
        String list = entry.getList();
        String keyWords = entry.getKeyWords();

        stmt.setString(1,ownStatus);
        stmt.setString(2,comment);
        stmt.setString(3,list);
        stmt.setInt(4,processedPortion);
        stmt.setInt(5,rating);
        stmt.setString(6,keyWords);
    }

    @Override
    String getRowQuery() {
        return "Select " + tableId + " from " + getTableName() + " where "
                + ownStatusC + " = ? AND "
                + commentC + " = ? AND "
                + ratingC + " = ? AND "
                + processedPortionC + " = ? AND "
                + listC + " = ? AND "
                + keyWordsC + " = ?";
    }
}
