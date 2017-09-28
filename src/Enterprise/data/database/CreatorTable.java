package Enterprise.data.database;

import Enterprise.data.Person;
import Enterprise.data.impl.CreatorImpl;
import Enterprise.data.intface.Creator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * DAO class of {@link Creator}.
 */
public class CreatorTable extends AbstractDataTable<Creator> {
    private static final DataColumn nameC = new DataColumn("AUTHOR", TEXT, NOT_NULL);
    private static final DataColumn sortNameC = new DataColumn("AUTHORSORT", TEXT, NOT_NULL);
    private static final DataColumn statusC = new DataColumn("AUTHORSTATUS", TEXT, NOT_NULL);

    private static CreatorTable INSTANCE;


    static {
        try {
            INSTANCE = new CreatorTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code CreatorTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private CreatorTable() throws SQLException {
        super("CREATORTABLE", "AUTHOR_ID");
        init();
    }

    /**
     * Returns a static Instance of this {@code CreatorTable}.
     *
     * @return instance - Instance of this {@code CreatorTable}
     * @see #CreatorTable()
     */
    public static CreatorTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(), nameC, sortNameC, statusC);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + getTableId() + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + nameC + " " + TEXT + " NOT NULL" +
                "," + sortNameC + " " + TEXT + " NOT NULL" +
                "," + statusC + " " + TEXT + " NOT NULL" +
                ")";*/
    }

    @Override
    protected void setInsertData(Creator entry, PreparedStatement stmt) throws SQLException {
        String authName = entry.getName();
        String authSortName = entry.getSortName();
        String authStat = entry.getStatus();

        setIntNull(stmt, getIdColumn());
        setString(stmt, nameC, authName);
        setString(stmt, sortNameC, authSortName);
        setString(stmt, statusC, authStat);
        /*
        stmt.setString(2,authName);
        stmt.setString(3,authSortName);
        stmt.setString(4,authStat);*/

    }

    @Override
    protected Creator getData(ResultSet rs) throws SQLException {

        int authorId = getInt(rs, getIdColumn());
        String name = getString(rs, nameC);
        String sortName = getString(rs, sortNameC);
        String stat = getString(rs, statusC);

        // TODO: 12.08.2017 implement personTable
        Person person = new Person();

        Creator entry = new CreatorImpl.CreatorBuilder(name).
                setId(authorId).
                setSortName(sortName).
                setStatus(stat).
                setPerson(person).
                build();

        entry.setEntryOld();
        return entry;
    }
}
