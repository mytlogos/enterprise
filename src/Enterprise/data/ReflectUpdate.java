package Enterprise.data;

import Enterprise.data.intface.DataBase;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import com.sun.istack.internal.NotNull;
import javafx.beans.property.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Reflection class.
 * At the moment it is more of an Utility Class and has no real theme.
 * It is very probable that this class will change much in the future,
 * it will be expanded, divided or something else.
 * <p>
 * The main theme is the reflection of several Classes related to the Data Model Classes.
 * The class {@link Enterprise.data.database.AbstractDataTable} requires dynamic update
 * statements.
 * These are provided through this class {@link #updateStrings(DataBase, String, String)}.
 * </p>
 * <p>
 * The {@code ReflectUpdate} searches in an object derived from {@link DataBase}
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
public class ReflectUpdate {
    private String tableName;
    private String idColumn;

    /**
     * Gets the Update SQL Statements by reflecting several classes.
     * The {@link DataBase} object is required to have fields annotated with
     * {@link SQLUpdate}.
     *
     * @param o an implementation of the {@code DataBase} interface
     * @param tableName name of the table which will be updated
     * @param idColumn name of the Column with the primary key
     * @return updateString - {@code Set} of Strings of complete SQL statements
     * @throws IllegalArgumentException if no fields annotated with {@code SQLUpdate} were found in
     *                                  the parameter o
     */
    public Set<String> updateStrings(@NotNull DataBase o, @NotNull String tableName, @NotNull String idColumn) {
        if (tableName != null && !tableName.isEmpty()) {
            this.tableName = tableName;
        } else {
            throw new NullPointerException("tableName needs to be non-null and non-empty");
        }
        if (idColumn != null && !idColumn.isEmpty()) {
            this.idColumn = idColumn;
        } else {
            throw new NullPointerException("idColumn needs to be non-null and non-empty");
        }

        Objects.requireNonNull(o, "database object needs to be non-null");

        List<Field> fieldList = getFieldsByAnnotation(o, SQLUpdate.class);
        Set<String> statements = new HashSet<>();

        if (fieldList.isEmpty()) {
            throw new IllegalArgumentException("database object " + o +
                    " does not seem to have the required SQLUpdate annotation");
        }

        List<DataObjectMethods> methodsList = getDataObjectMethods(o, fieldList);

        methodsList.forEach(methods -> {
            String updateString = setString(methods);
            if (!updateString.isEmpty()) {
                statements.add(updateString);
            }
        });

        return statements;
    }

    private List<DataObjectMethods> getDataObjectMethods(@NotNull DataBase o, List<Field> fieldList) {
        List<DataObjectMethods> methodsList = new ArrayList<>();
        for (Field field : fieldList) {
            SQLUpdate sql = field.getAnnotation(SQLUpdate.class);

            Method booleanMethod = getMethod(o, sql.stateGet());
            Method contentGetter = getMethod(o, sql.valueGet());
            Method idGetter = getMethod(o, "getId");
            String columnName = getColumnName(o, sql);

            methodsList.add(new DataObjectMethods(booleanMethod, contentGetter, idGetter, o, columnName));
        }
        return methodsList;
    }

    /**
     * Gets the SQL statements with the given parameters.
     *
     * @param column name of the Column which will be updated
     * @param value value which will be set
     * @param id id of the row which will be updated
     * @return string - the complete SQL statement
     */
    private String setString(String column, String value, int id) {
        String update = "Update ";
        String set = " Set ";
        String where = " Where " + idColumn + " = " + id;

        StringBuilder setBuilder = new StringBuilder();

        setBuilder.append(set);

        setBuilder.append(column).append(" = ").append(value);

        return update + tableName +
                setBuilder + where;
    }

    /**
     * Gets the name of the column of the value field
     * by reflecting the class specified by the {@link DataAccess}
     * annotation of the class of parameter {@code o}.
     *
     * @param o implementation of the {@code DataBase} interface
     * @param sqlUpdate annotation of the field to update the database with
     * @return columnName - the name of the column which will be updated
     */
    private String getColumnName(DataBase o, SQLUpdate sqlUpdate) {
        String columnName = "";
        Class<?> tableClass = null;
        DataAccess access = o.getClass().getAnnotation(DataAccess.class);

        if (access == null) {
            throw new IllegalArgumentException("the class of the database " +
                    "object does not seem to be annotated with the DataAccess Annotation");
        }

        //load dao class
        try {
            tableClass = Class.forName("Enterprise.data.database." + access.daoClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (tableClass != null) {
            try {
                //load specified field
                Field tableField = tableClass.getDeclaredField(sqlUpdate.columnField());
                tableField.setAccessible(true);

                //load getInstance method, to get an object of the dao class
                Method getInstance = tableClass.getDeclaredMethod("getInstance", (Class<?>[]) null);
                //set accessible to true, to invoke the method without throwing an exception
                getInstance.setAccessible(true);
                //get the value
                columnName = (String) tableField.get(getInstance.invoke(null, (Object[]) null));

            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                // TODO: 30.08.2017 log this
                e.printStackTrace();
            }
        }
        return columnName;
    }

    /**
     * Internal data transfer class of an entity with the corresponding getter.
     * Is immutable.
     */
    private class DataObjectMethods {
        private Method booleanMethod;
        private Method contentGetter;
        private Method idGetter;
        private Object object;
        private String columnName;

        /**
         * The constructor of {@code DataObjectMethods}.
         *
         * @param booleanMethod the stateChanged getter
         * @param contentGetter the value getter
         * @param idGetter the getter of the id of the DMO
         * @param object the DMO object itself
         * @param columnName name of the column which will be updated
         */
        DataObjectMethods(Method booleanMethod, Method contentGetter, Method idGetter, Object object, String columnName) {
            this.booleanMethod = booleanMethod;
            this.contentGetter = contentGetter;
            this.idGetter = idGetter;
            this.object = object;
            this.columnName = columnName;
            validateState();
        }

        /**
         * Validates the state of this {@code DataObjectMethods} object.
         */
        private void validateState() {
            if (booleanMethod == null) {
                throw new NullPointerException();
            }
            if (contentGetter == null) {
                throw new NullPointerException();

            }

            if (idGetter == null) {
                throw new NullPointerException();

            }

            if (object == null) {
                throw new NullPointerException();

            }

            if (columnName == null || columnName.isEmpty()) {
                throw new IllegalStateException();
            }
        }

        /**
         * Gets the stateChanged-Getter
         *
         * @return method
         */
        Method getBooleanMethod() {
            return booleanMethod;
        }

        /**
         * Gets the Getter-method of the {@code Field}.
         *
         * @return method
         */
        Method getContentGetter() {
            return contentGetter;
        }

        /**
         * Gets the Id-Getter.
         *
         * @return method - the 'getId' method
         */
        Method getIdGetter() {
            return idGetter;
        }

        /**
         * Gets the implementation instance of the {@link DataBase} interface.
         * @return database
         */
        Object getObject() {
            return object;
        }

        /**
         * Gets the name of the Column.
         * @return string - name of the Column in the Database
         */
        String getColumnName() {
            return columnName;
        }
    }

    /**
     * Invokes the Methods which were reflected by the {@code getMethod} methods
     * and uses the returned values to construct the SQL statement.
     *
     * @param methods the internal Data transfer object
     * @return setString - the complete SQL update statement
     */
    private String setString(DataObjectMethods methods) {
        String setString;

        String columnName = methods.getColumnName();

        String changedString = getStringFromMethod(methods.getBooleanMethod(), methods.getObject());
        boolean changed = Boolean.parseBoolean(changedString);

        if (changed) {
            String value = getStringFromMethod(methods.getContentGetter(),methods.getObject());

            String idString = getStringFromMethod(methods.getIdGetter(), methods.getObject());
            int id = Integer.parseInt(idString);
            setString = setString(columnName, value, id);
        } else {
            setString = "";
        }
        return setString;
    }

    /**
     * Invokes the method and returns their values as a String.
     *
     * @param method method to be invoked
     * @param o object which the method will be invoked from
     * @return string - return value as an {@code String}
     */
    private String getStringFromMethod(Method method, Object o) {
        String string = "";
        try {
            if (method.getReturnType().equals(String.class)) {
                //wraps the String for the SQL statement in quotation marks ''
                string = "'" + String.valueOf(method.invoke(o, (Object[]) null)) + "'";
            } else {
                string = String.valueOf(method.invoke(o, (Object[]) null));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * Gets the Method from the name of the field, which has
     * a {@code String} appended at the end or beginning
     * of the given Object.
     *
     * @param o object which should have the method
     * @param field {@code Field} which name is part of the method
     * @param fragment String to be appended
     * @param type enum marker for appending at the beginning or end
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, Field field, String fragment, FixType type) {
        Method method = null;
        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String methodName;

        if (type == FixType.SUFFIX) {
            methodName = fieldName + fragment;
        } else {
            methodName = fragment + fieldName ;
        }
        try {
            method = o.getClass().getDeclaredMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * Gets the Method from the String of the parameter from
     * the class of the given Object.
     *
     * @param o object which should have the method
     * @param methodName name of the sought method
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, String methodName) {
        Method method = null;
        try {
            method = o.getClass().getDeclaredMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * Gets the Method from the name of the field, which has
     * a {@code String}s appended at the end and the beginning
     * of the given Object.
     *
     * @param o object which should have the method
     * @param prefix {@code Field} which name is part of the method
     * @param suffix String to be appended
     * @param field enum marker for appending at the beginning or end
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, Field field, String prefix, String suffix) {
        Method method = null;

        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String methodName = prefix + fieldName + suffix;

        try {
            method = o.getClass().getDeclaredMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * Gets the fields of the given Object and returns the {@link Field}s
     * annotated with the specified annotation.
     *
     * @param o object to search through
     * @param annotateClass {@code Annotation} to look for
     * @return fields - {@code List} of {@code Field}s annotated with the specified {@code Annotation}
     */
    private List<Field> getFieldsByAnnotation(Object o, Class<? extends Annotation> annotateClass) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<Field> annotateFields = new ArrayList<>();

        for (Field field : fields) {
            //check if field type is subtype of Property
            if (Property.class.isAssignableFrom(field.getType())) {
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotateClass.isAssignableFrom(annotation.annotationType())) {
                        annotateFields.add(field);
                    }
                }
            }
        }
        return annotateFields;
    }

    /**
     * Marker enum, to differentiate between appending
     * at the beginning or the end on a String.
     */
    private enum  FixType {
        SUFFIX,
        PREFIX
    }
}
