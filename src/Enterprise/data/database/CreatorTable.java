package Enterprise.data.database;

import Enterprise.data.Person;
import Enterprise.data.impl.SimpleCreator;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.Creator;

import java.sql.*;
import java.util.*;

/**
 * DAO class of {@link Creator}.
 */
public class CreatorTable extends AbstractDataTable<Creator> {
    private static final String nameC;

    private static final String sortNameC;

    private static final String statusC;

    private static CreatorTable instance;

    static {
        nameC = "AUTHOR";

        sortNameC = "AUTHORSORT";

        statusC = "AUTHORSTATUS";
        try {
            instance = new CreatorTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code CreatorTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    public CreatorTable() throws SQLException {
        super("CREATORTABLE", "AUTHOR_ID");
    }

    /**
     * Returns a static Instance of this {@code CreatorTable}.
     *
     * @return instance - Instance of this {@code CreatorTable}
     * @throws SQLException if class could not be instantiated
     * @see #CreatorTable()
     */
    public static CreatorTable getInstance() throws SQLException {
        return instance;
    }

    @Override
    protected String createString() {
        return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + tableId + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + nameC + " " + TEXT + " NOT NULL" +
                "," + sortNameC + " " + TEXT + " NOT NULL" +
                "," + statusC + " " + TEXT + " NOT NULL" +
                ")";
    }

    @Override
    void setInsertData(Creator entry, PreparedStatement stmt) throws SQLException {
        String authName = entry.getName();
        String authSortName = entry.getSortName();
        String authStat = entry.getStatus();

        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,authName);
        stmt.setString(3,authSortName);
        stmt.setString(4,authStat);
    }

    @Override
    Creator getData(ResultSet rs) throws SQLException {
        Creator entry;
        int authorId = rs.getInt(tableId);
        String name = rs.getString(nameC);
        String sortName = rs.getString(sortNameC);
        String stat = rs.getString(statusC);
        // TODO: 12.08.2017 implement personTable
        Person person = new Person();
        // TODO: 12.08.2017 get Works
        List<Creation> works = new ArrayList<>();

        entry = new SimpleCreator(authorId,name,sortName,stat,person,works);
        entry.setEntryOld();
        return entry;
    }

    @Override
    void queryIdData(Creator entry, PreparedStatement stmt) throws SQLException {
        String authName = entry.getName();
        String authSortName = entry.getSortName();
        String authStat = entry.getStatus();

        stmt.setString(1,authName);
        stmt.setString(2,authSortName);
        stmt.setString(3,authStat);
    }

    @Override
    String getRowQuery() {
        return "Select " + tableId + " from " + getTableName() + " where "
                + nameC + " = ? AND"
                + sortNameC + " = ? AND"
                + statusC + " = ?";
    }

    @Override
    String getInsert() {
        return "insert into " + getTableName() + " values(?,?,?,?)";
    }
}
