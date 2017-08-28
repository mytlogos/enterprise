package Enterprise.data.database;

import Enterprise.data.impl.SimpleCreation;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.DataTable;

import java.sql.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * DAO class responsible for the table holding the data of {@code Creation}entries.
 */
public class CreationTable extends AbstractDataTable<Creation> implements DataTable<Creation> {
    private static final String titleC;
    private static final String seriesC;
    private static final String dateLastPortionC;
    private static final String numPortionC;
    private static final String coverPathC;
    private static final String workStatusC;

    private static CreationTable INSTANCE;

    static {
        titleC = "TITLE";
        seriesC = "SERIES";
        dateLastPortionC = "DATELASTPORTION";
        numPortionC = "NUMPORTION";
        coverPathC = "COVERPATH";
        workStatusC = "WORKSTATUS";

        try {
            INSTANCE = new CreationTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    String getInsert() {
        return "insert into " + getTableName() +
                " values(?,?,?,?,?,?,?)";
    }

    /**
     * The constructor of {@code CreationTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private CreationTable() throws SQLException {
        super("CREATIONTABLE", "CREATION_ID");
    }

    /**
     * Static getInstance Method. Gets the static Instance of this {@code CreationTable}.
     * @return instance - a static instance of this {@code CreationTable}
     */
    public static CreationTable getInstance(){
        return INSTANCE;
    }

    @Override
    String createString() {
        return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + tableId + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + titleC + " " + TEXT + " NOT NULL" +
                "," + seriesC + " " + TEXT + " NOT NULL" +
                "," + dateLastPortionC + " " + TEXT + " NOT NULL" +
                "," + numPortionC + " " + INTEGER + " NOT NULL" +
                "," + coverPathC + " " + TEXT + " NOT NULL" +
                "," + workStatusC + " " + TEXT + " NOT NULL" +
                ")";
    }

    @Override
    void setInsertData(Creation entry, PreparedStatement stmt) throws SQLException {
        // TODO: 23.08.2017 check the sql statement of stmt if it is an INSERT operation

        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,entry.getTitle());
        stmt.setString(3,entry.getSeries());
        stmt.setString(4,entry.getDateLastPortion());
        stmt.setInt(5,entry.getNumPortion());
        stmt.setString(6,entry.getCoverPath());
        stmt.setString(7,entry.getWorkStatus());
    }

    void queryIdData(Creation entry, PreparedStatement stmt) throws SQLException {
        stmt.setString(1,entry.getTitle());
        stmt.setString(2,entry.getSeries());
        stmt.setString(3,entry.getDateLastPortion());
        stmt.setInt(4,entry.getNumPortion());
        stmt.setString(5,entry.getCoverPath());
        stmt.setString(6,entry.getWorkStatus());
    }

    @Override
    String getRowQuery() {
        return "Select " + tableId + " from " + getTableName() + " where "
                + titleC + " = ? AND"
                + seriesC + " = ? AND"
                + dateLastPortionC + " = ? AND"
                + numPortionC + " = ? AND"
                + coverPathC + " = ? AND"
                + workStatusC + " = ?";
    }

    @Override
    Creation getData(ResultSet rs) throws SQLException {
        Creation entry;
        int authorId = rs.getInt(titleC);
        String title = rs.getString(titleC);
        String series = rs.getString(seriesC);
        String dateLastPortion = rs.getString(dateLastPortionC);
        int numPortion = rs.getInt(numPortionC);
        String coverPath = rs.getString(coverPathC);
        String workStatus = rs.getString(workStatusC);

        entry = new SimpleCreation(authorId,series,title,coverPath,numPortion,dateLastPortion,workStatus);
        entry.setEntryOld();
        return entry;
    }

    @Deprecated
    public int[] insertCreations(Collection<? extends  Creation> creations, Connection connection) {
        int[] inserted = new int[0];
        Iterator<? extends Creation> iterator = creations.iterator();
        try (PreparedStatement stmt = connection.prepareStatement(getInsert())) {

            while (iterator.hasNext()) {
                Creation entry = iterator.next();

                if (entry.isNewEntry()) {
                    setInsertData(entry, stmt);
                    stmt.addBatch();
                }
            }

            inserted = stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inserted;
    }
}
