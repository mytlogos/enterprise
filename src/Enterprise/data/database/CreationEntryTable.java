package Enterprise.data.database;

import Enterprise.data.impl.CreationEntryImpl;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.data.intface.*;
import Enterprise.misc.SetList;
import Enterprise.modules.BasicModules;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.INTEGER;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * The Main Part of the Data Access Layer, responsible for holding the foreign keys of the EntryParts {@link Creation},
 * {@link Creator}, {@link User} and {@link Sourceable}.
 * Except {@code Sourceable}, no column is nullable.
 * <p>
 * It is important, because the {@link CreationEntry} will be extracted from their Id´s
 * and based on the Id of the {@code Sourceable} Column, either a {@code CreationEntry},
 * if null, or a {@link SourceableEntry}, if not null,
 * will be constructed.
 * </p>
 */
public class CreationEntryTable extends AbstractRelationTable<CreationEntry> {

    private final String selectAll = "SELECT * FROM " + getTableName();

    private static final DataColumn userIdC = new DataColumn("USER_ID", INTEGER, NOT_NULL);
    private static final DataColumn creationIdC = new DataColumn("CREATION_ID", INTEGER, NOT_NULL);
    private static final DataColumn creatorIdC = new DataColumn("CREATOR_ID", INTEGER, NOT_NULL);
    private static final DataColumn sourceableIdC = new DataColumn("SOURCEABLE_ID", INTEGER, NOT_NULL);
    private static final DataColumn moduleC = new DataColumn("MODULE", TEXT, NOT_NULL);

    private static CreationEntryTable INSTANCE;

    static {
        try {
            INSTANCE = new CreationEntryTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code CreationEntryTable}.
     *
     * @throws SQLException if there was a problem with the database, while trying to create the table
     */
    private CreationEntryTable() throws SQLException {
        super("CREATIONENTRYTABLE");
        init();
    }

    /**
     * Creates a new Instance of this{@code CreationEntryTable}.
     *
     * @return instance - an instance of this class
     * @throws RuntimeException if class could not be instantiated
     */
    public static CreationEntryTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    final String getDelete() {
        return "Delete from " + getTableName() + " where " + userIdC.getName() + " = ?";
    }

    /**
     * Creates the table schema for the table.
     *
     * @return createString - the SQL statement to create the table
     */
    protected String createString() {
        return createRelationTableHelper(creationIdC, userIdC, creatorIdC, sourceableIdC, moduleC);
       /* return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + creationIdC + " INTEGER NOT NULL" +
                "," + userIdC + " INTEGER NOT NULL" +
                "," + creatorIdC + " INTEGER NOT NULL" +
                "," + sourceableIdC + " INTEGER" +
                "," + moduleC + " TEXT NOT NULL" +
                ")";*/
    }

    @Override
    public boolean insert(CreationEntry entry) {
        return Connections.getConnection(connection -> insert(entry, connection));
    }

    /**
     * Inserts the id´s of the {@code CreationEntry} in the table
     * and delegates the task of inserting new parts of the {@code CreationEntry}
     * to other DAO classes.
     *
     * @param entry entry which will be added into the database
     * @param connection connection to the database
     * @return true, if inserting was successful
     */
    private boolean insert(CreationEntry entry, Connection connection) throws SQLException {
        boolean inserted = false;
        if (entry.isNewEntry()) {

            try (PreparedStatement stmt = connection.prepareStatement(insert)) {
                //inserts new parts and sets their id´s, if not set
                insertNewParts(entry, connection);

                //check for invalid id
                checkId(entry);

                //inserts the parameters
                insertIds(entry, stmt);
                // TODO: 15.07.2017 do sth about execute/executeUpdate
                if (stmt.executeUpdate() == 1) {
                    inserted = true;
                }
            }
        }
        return inserted;
    }

    private void checkId(CreationEntry entry) throws SQLException {
        if (entry.getUser().getId() == 0 ||
                entry.getCreation().getId() == 0 ||
                entry.getCreator().getId() == 0) {
            throw new SQLException("illegal id, error in inserting process");
        }

        if (entry instanceof SourceableEntry) {
            SourceableEntry entryS = (SourceableEntry) entry;
            if (entryS.getSourceable().getId() == 0 ||
                    (entryS.getSourceable().getSourceList().stream().anyMatch(source -> source.getId() == 0))) {
                throw new SQLException("illegal id, error in inserting process");
            }
        }
    }

    @Override
    public boolean insert(Collection<? extends CreationEntry> entries) {
        if (entries.isEmpty()) {
            return false;
        }
        return Connections.getConnection(connection -> {
            int inserts = 0;
            try (PreparedStatement stmt = connection.prepareStatement(insert)) {
                for (CreationEntry entry : entries) {

                    //inserts new Parts of the Entry and gets their generated id
                    insertNewParts(entry, connection);

                    //check for invalid Id´s
                    checkId(entry);

                    //populates the PreparedStatement with id parameters
                    insertIds(entry, stmt);

                    if (stmt.executeUpdate() != 1) {
                        inserts++;
                    }
                }
            }
            boolean inserted = false;

            if (inserts == entries.size()) {
                inserted = true;
            }

            return inserted;
        });
    }

    /**
     * Updates the database with the input parameter.
     *
     * @param entry {@code CreationEntry}, with the data to update the database
     */
    public void updateEntry(CreationEntry entry) {
        User user = entry.getUser();
        Creation creation = entry.getCreation();
        Creator creator = entry.getCreator();
        Sourceable sourceable;

        CreatorTable.getInstance().updateEntry(creator);
        CreationTable.getInstance().updateEntry(creation);
        UserTable.getInstance().updateEntry(user);

        if (entry instanceof SourceableEntryImpl) {
            sourceable = (((SourceableEntryImpl) entry).getSourceable());
            SourceableTable.getInstance().updateEntry(sourceable);
        }


    }

    /**
     * Updates the Entries in the Database.
     *
     * @param entries {@code Collection} of {@code CreationEntry}, who will update the database.
     * @return true, if any row was affected
     */
    public boolean updateEntries(List<? extends CreationEntry> entries) {
        boolean creatorsUpdated;
        boolean creationsUpdated;
        boolean usersUpdated;
        boolean sourceablesUpdated = false;

        creatorsUpdated = checkForCreatorUpdates(entries);
        creationsUpdated = checkForCreationUpdates(entries);
        usersUpdated = checkForUserUpdates(entries);
        try {
            sourceablesUpdated = checkForSourceableUpdates(entries);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return creationsUpdated || creatorsUpdated || usersUpdated || sourceablesUpdated;
    }

    /**
     * Gets all Entries available in the table.
     * Skips and logs entries which could not be constructed.
     *
     * @return entries - {@code List} of {@code CreationEntry}s extracted from the database
     */
    @Override
    public List<CreationEntry> getEntries() {
        return Connections.getConnection(connection -> {

            List<CreationEntry> entries = new SetList<>();
            try(Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(selectAll);

                if (!rs.isClosed()) {
                    while (rs.next()) {
                        try {
                            entries.add(constructEntry(rs, stmt.getConnection()));
                        } catch (SQLException | IllegalArgumentException e) {
                            logger.log(Level.SEVERE, "possible corrupt data, entry could not be constructed", e);
                        }
                    }
                } else {
                    logger.log(Level.INFO, "No Entries found in " + getTableName());
                }
            }
            entries.forEach(Entry::fromDataBase);
            return entries;
        });
    }

    @Override
    public boolean delete(CreationEntry entry) {
        return Connections.getConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(getDelete())) {
                removeDead(entry, connection);

                setDeleteData(entry, statement);

                System.out.println("Einige Einträge von " + entry.getClass().getSimpleName() + " ID " + entry.getUser().getId() + " wurden gelöscht!");

                //deleting one entry should affect only one row in this table
                return  statement.executeUpdate() == 1;
            }
        });
    }

    /**
     * Deletes a row of foreign keys in this table and checks for dead Entries,
     * which will be deleted by their responsible DAO´s class.
     * Whole deletion will be done as one Transaction and will rollback, if one failed.
     *
     * @param entries Collection of {@code CreationEntry}, which will be deleted
     * @return true, if the row of this table was deleted
     */
    public boolean delete(Collection<? extends CreationEntry> entries) {
        return Connections.getConnection(connection -> {
            boolean deleted = false;
            if (entries.isEmpty()) {
                return false;
            }
            System.out.println("Delete Statement: " + getDelete());
            try (PreparedStatement statement = connection.prepareStatement(getDelete())) {
                for (CreationEntry entry : entries) {
                    removeDead(entry, connection);
                    setDeleteData(entry, statement);
                    statement.addBatch();

                    System.out.println("Einige Einträge von " + entry.getClass().getSimpleName() + " ID " + entry.getUser().getId() + " wurden gelöscht!");
                }

                if (statement.executeBatch().length == entries.size()) {
                    deleted = true;
                }
            }
            return deleted;
        });
    }

    /**
     * Constructs a {@code CreationEntry} out of the Data of the {@code ResultSet}.
     * The caller needs to move the internal cursor the {@code ResultSet}, with {@code ResultSet.next},
     * prior to calling this Method.
     *
     * @param rs {@code ResultSet} with the queried data
     * @param connection {@code Connection}, uses available connection, to do sth // TODO: 22.08.2017 do sth about this maybe
     * @return entry - returns a CreationEntry if no SQLException was thrown
     * @throws SQLException if the ResultSet is invalid or DAO´s from other tables throw one
     */
    private CreationEntry constructEntry(ResultSet rs, Connection connection) throws SQLException {
        int userId = getInt(rs, userIdC);
        int creationId = getInt(rs, creationIdC);
        int creatorId = getInt(rs, creatorIdC);
        int sourceableId = getInt(rs, sourceableIdC);
        String modl = getString(rs, moduleC);

        BasicModules module = BasicModules.valueOf(modl);

        //delegates getUser to UserTable DAO
        User user = UserTable.getInstance().getEntry(userId, connection);
        //delegates getCreation to CreationTable DAO
        Creation  creation = CreationTable.getInstance().getEntry(creationId, connection);
        //delegates getCreator to CreatorTable
        Creator creator = CreatorTable.getInstance().getEntry(creatorId, connection);

        CreationEntry entry;

        if (sourceableId > 0) {
            Sourceable sourceable = SourceableTable.getInstance().getEntry(sourceableId, connection);
            entry = new SourceableEntryImpl(user, creation, creator, sourceable, module);
        } else {
            entry = new CreationEntryImpl(user, creation, creator, module);
        }

        entry.getModule().addEntry(entry);

        return entry;
    }

    /**
     * Checks for dead EntryParts ({@link Creation}, {@link Creator}, {@link User}, {@link Sourceable})
     * and delegates the Task of deleting them to their corresponding DAO´s class.
     *
     * @param entry {@code CreationEntry} which will be checked
     * @param connection {@code Connection} which will be used for the Transaction
     * @throws SQLException if there is a error in the DAO´s objects
     */
    private void removeDead(CreationEntry entry, Connection connection) throws SQLException {
        User user = entry.getUser();
        Creation creation = entry.getCreation();
        Creator creator = entry.getCreator();

        int toDelete = 0;
        int deleted = 0;

        if (entry instanceof SourceableEntry) {
            Sourceable sourceable = ((SourceableEntry) entry).getSourceable();

            if (!sourceable.isNewEntry() && (((SourceableEntry) entry).readySourceableRemoval() || sourceable.isDead())) {
                toDelete++;
                //SubRelationTable of Sourceables and their sources
                if (EntrySourceTable.getInstance().delete(sourceable, connection)) {
                    deleted++;
                }

            }
        }
        if (!user.isNewEntry() && (entry.readyUserRemoval() || user.isDead())) {
            toDelete++;
            if (UserTable.getInstance().delete(user, connection)) {
                deleted++;
            }
        }
        if (!creator.isNewEntry() && (entry.readyCreatorRemoval() || creator.isDead())) {
            toDelete++;
            if (CreatorTable.getInstance().delete(creator, connection)) {
                deleted++;
            }
        }
        if (!creation.isNewEntry() && (entry.readyCreationRemoval() || creation.isDead())) {
            toDelete++;
            if (CreationTable.getInstance().delete(creation, connection)) {
                deleted++;
            }
        }
        if (toDelete != deleted) {
            throw new SQLException("inconsistent data");
        }
    }

    private void setDeleteData(CreationEntry entry, PreparedStatement statement) throws SQLException {
        statement.setInt(1, entry.getUser().getId());/*
        statement.setInt(2, entry.getCreation().getId());
        statement.setInt(3, entry.getCreator().getId());
        and " + creationIdC + " = ? and " + creatorIdC + " = ?*/
    }

    /**
     * Updates the database with the data of {@code User} from the parameter input.
     *
     * @param entries {@code Collection} of {@code CreationEntry}s, to be updated
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     */
    private boolean checkForUserUpdates(Collection<? extends CreationEntry> entries) {
        Collection<User> users = new ArrayList<>();

        for (CreationEntry entry : entries) {
            User user = entry.getUser();
            if (user.isUpdated()) {
                users.add(user);
            }
        }
        return !users.isEmpty() && UserTable.getInstance().updateEntries(users);

    }

    /**
     * Updates the database with the data of {@code Creator} from the parameter input.
     *
     * @param entries {@code Collection} of {@code CreationEntry}s, to be updated
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     */
    private boolean checkForCreatorUpdates(Collection<? extends CreationEntry> entries) {
        Collection<Creator> creators = new ArrayList<>();

        for (CreationEntry entry : entries) {
            Creator creator = entry.getCreator();
            if (creator.isUpdated()) {
                creators.add(creator);
            }
        }
        return !creators.isEmpty() && CreatorTable.getInstance().updateEntries(creators);
    }

    /**
     * Updates the database with the data of {@code Creation} from the parameter input.
     *
     * @param entries {@code Collection} of {@code CreationEntry}s, to be updated
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     */
    private boolean checkForCreationUpdates(Collection<? extends CreationEntry> entries) {
        Collection<Creation> creations = new ArrayList<>();

        for (CreationEntry entry : entries) {
            Creation creation = entry.getCreation();
            if (creation.isUpdated()) {
                creations.add(creation);
            }
        }
        return !creations.isEmpty() && CreationTable.getInstance().updateEntries(creations);
    }

    /**
     * Updates the database with the data of {@code Sourceable} from the parameter input.
     *
     * @param entries {@code Collection} of {@code CreationEntry}s, to be updated
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     */
    private boolean checkForSourceableUpdates(Collection<? extends CreationEntry> entries) throws SQLException {
        Collection<Sourceable> sourceables = new ArrayList<>();

        for (CreationEntry entry : entries) {
            if (entry instanceof SourceableEntry) {
                Sourceable sourceable = ((SourceableEntry) entry).getSourceable();

                if (sourceable.isUpdated()) {
                    sourceables.add(sourceable);
                }
            }
        }
        return !sourceables.isEmpty() && EntrySourceTable.getInstance().updateEntries(sourceables);
    }

    /**
     * Sets the Parameter for the {@code PreparedStatement} of the INSERT operation.
     * <p>
     * Differentiates between normal instances of {@link CreationEntry} and instances of {@link SourceableEntry}.
     * </p>
     *
     * @param entry entry, which id´s will be inserted
     * @param statement {@code PreparedStatement} which will be operated on
     * @throws SQLException if there parameters cant be set
     */
    private void insertIds(CreationEntry entry, PreparedStatement statement) throws SQLException {
        // TODO: 22.08.2017 check if statement has insert SQL statement, so no invalid statement is passed in here
        int creationId = entry.getCreation().getId();
        int creatorId = entry.getCreator().getId();
        int userId = entry.getUser().getId();
        String module = entry.getModule().toString().toUpperCase();

        checkId(creationId);
        checkId(creatorId);
        checkId(userId);

        setInt(statement, creationIdC, creationId);
        setInt(statement, userIdC, userId);
        setInt(statement, creatorIdC, creatorId);
        setString(statement, moduleC, module);

        //sets sourceableId to id of this entry or to null if it is an instance of SourceableEntry
        if (entry instanceof SourceableEntry) {
            int sourceableId = ((SourceableEntry) entry).getSourceable().getId();
            checkId(sourceableId);
            setInt(statement, sourceableIdC, sourceableId);
        }else {
            // FIXME: 27.08.2017 probable bug: will maybe handle it like an id and increment in the database
            setIntNull(statement, sourceableIdC);
        }
    }

    private void checkId(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
    }

    /**
     * Checks for new EntryParts and inserts them,
     * if they were not added to the database before.
     *
     * @param entry {@code CreationEntry} to be checked
     * @param connection {@code Connection} to be used for manual commit
     * @throws SQLException if there are problems while inserting in the delegating DAO´s
     *
     * @see SourceableTable
     * @see CreationTable
     * @see CreatorTable
     * @see UserTable
     */
    private void insertNewParts(CreationEntry entry, Connection connection) throws SQLException {
        UserTable.getInstance().insert(entry.getUser(), connection);
        CreationTable.getInstance().insert(entry.getCreation(), connection);
        CreatorTable.getInstance().insert(entry.getCreator(), connection);

        if (entry instanceof SourceableEntry) {
            SourceableEntry sourceableEntry = (SourceableEntry) entry;
            EntrySourceTable.getInstance().insert(sourceableEntry.getSourceable(), connection);
        }

    }

}
