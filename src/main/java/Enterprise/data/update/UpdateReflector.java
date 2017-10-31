package Enterprise.data.update;

import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.Entry;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import com.sun.istack.internal.NotNull;

import java.util.Set;

/**
 * Reflection class.
 * At the moment it is more of an Utility Class and has no real theme.
 * It is very probable that this class will change much in the future,
 * it will be expanded, divided or something else.
 * <p>
 * The main theme is the reflection of several Classes related to the Data Model Classes.
 * The class {@link Enterprise.data.database.AbstractDataTable} requires dynamic update
 * statements.
 * These are provided through this class {@link #getUpdateStrings(DataEntry, Object, String, String)}.
 * </p>
 * <p>
 * The {@code ReflectUpdate} searches in an object derived from {@link DataEntry}
 * for fields annotated with the {@link SQLUpdate}.
 * The class of the field to update needs to be annotated with {@link DataAccess} specifying
 * the corresponding DAO class.
 * </p>
 * <p>
 * It searches for the methods specified in {@code stateGet, valueGet} and the default
 * {@code getId} method in the class of the field.
 * Afterwards it searches for the field specified by {@code columnField} of the {@code SQLUpdate}
 * annotation in the class specified in {@code daoClass} of the {@code DataAccess} annotation,
 * which needs to be located in the {@code Enterprise.data.database} package.
 * It will consequently invoke the methods and depending von the value returned by the
 * "stateGet" method, construct the SQL statements with the values of the other methods and the
 * {@code columnField} or else it will skip this field.
 * </p>
 * Example:
 * <pre>
 * {@code
 * //value class
 *    {@literal @}DataAccess("DMOTable")
 *    class DMOImplementation {
 *         //field which will be saved into database
 *        {@literal @}SQLUpdate(stateGet = "isNameChanged", valueGet = "getName", columnField = "nameC")
 *        String name;
 *
 *         //stateChanged flag
 *        BooleanProperty nameChanged = new SimpleBooleanProperty();
 *
 *         //field value getter
 *        String getName(){
 *            return name;
 *        }
 *
 *         //stateChanged getter
 *        boolean isNameChanged(){
 *            return nameChanged.get;
 *        }
 *    }
 *    //DAO class of the value class
 *    class DMOTable{
 *          //name of the Column in the database
 *          String nameC = "NAME";
 *    }
 * }
 * </pre>
 */
public interface UpdateReflector {
    /**
     * Gets the Update SQL Statements by reflecting several classes.
     * The {@link DataEntry} object is required to have fields annotated with
     * {@link SQLUpdate}.
     *
     * @param dataProvider an implementation of the {@code DataEntry} interface
     * @param idColumn     name of the Column with the primary key
     * @param tableName    name of the Table
     * @return updateString - {@code Set} of Strings of complete SQL statements
     * @throws IllegalArgumentException if no fields annotated with {@code SQLUpdate} were found in
     *                                  the parameter o
     */
    Set<String> getUpdateStrings(@NotNull Entry dataProvider, @NotNull Object idProvider, @NotNull String idColumn, @NotNull String tableName);
}
