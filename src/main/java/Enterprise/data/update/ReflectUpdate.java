package Enterprise.data.update;

import Enterprise.data.database.DataColumn;
import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.Entry;
import Enterprise.misc.DataAccess;
import Enterprise.misc.Log;
import Enterprise.misc.SQLUpdate;
import com.sun.istack.internal.NotNull;
import javafx.beans.property.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A Standard Implementation of UpdateReflector.
 */
public class ReflectUpdate implements UpdateReflector {
    private String tableName;
    private String idColumn;

    @Override
    public Set<String> getUpdateStrings(Entry dataProvider, Object idProvider, String idColumn, String tableName) {
        validate(dataProvider, idProvider, tableName, idColumn);

        List<Field> fieldList = getFieldsByAnnotation(dataProvider, SQLUpdate.class);
        Set<String> statements = new HashSet<>();

        if (fieldList.isEmpty()) {
            throw new IllegalArgumentException(dataProvider + " does not seem to have the required SQLUpdate annotation");
        }

        try {
            List<DataObjectMethods> methodsList = getDataObjectMethods(dataProvider, idProvider, fieldList);

            methodsList.forEach(methods -> {
                String updateString;
                try {
                    updateString = setString(methods);
                    if (!updateString.isEmpty()) {
                        statements.add(updateString);
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            });
        } catch (ReflectiveOperationException e) {
            Log.classLogger(this).log(Level.SEVERE, "could not create updateStatements", e);
        }

        return statements;
    }

    /**
     * Gets the Update SQL Statements by reflecting several classes.
     * The {@link DataEntry} object is required to have fields annotated with
     * {@link SQLUpdate}.
     *
     * @param dataProvider an implementation of the {@code DataEntry} interface
     * @param idColumn name of the Column with the primary key
     * @param tableName name of the Table
     * @return updateString - {@code Set} of Strings of complete SQL statements
     * @throws IllegalArgumentException if no fields annotated with {@code SQLUpdate} were found in
     *                                  the parameter o
     */
    public Set<String> updateStrings(@NotNull Object dataProvider, @NotNull Object idProvider, @NotNull String idColumn, @NotNull String tableName) {
        validate(dataProvider, idProvider, tableName, idColumn);

        List<Field> fieldList = getFieldsByAnnotation(dataProvider, SQLUpdate.class);
        Set<String> statements = new HashSet<>();

        if (fieldList.isEmpty()) {
            throw new IllegalArgumentException(dataProvider + " does not seem to have the required SQLUpdate annotation");
        }

        try {
            List<DataObjectMethods> methodsList = getDataObjectMethods(dataProvider, idProvider, fieldList);

            methodsList.forEach(methods -> {
                String updateString;
                try {
                    updateString = setString(methods);
                    if (!updateString.isEmpty()) {
                        statements.add(updateString);
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            });
        } catch (ReflectiveOperationException e) {
            Log.classLogger(this).log(Level.SEVERE, "could not create updateStatements", e);
        }

        return statements;
    }

    private void validate(@NotNull Object o, Object idProvider, @NotNull String tableName, @NotNull String idColumn) {
        if (tableName != null && !tableName.isEmpty()) {
            this.tableName = tableName;
        } else {
            throw new IllegalArgumentException("tableName needs to be non-null and non-empty");
        }
        if (idColumn != null && !idColumn.isEmpty()) {
            this.idColumn = idColumn;
        } else {
            throw new IllegalArgumentException("idColumn needs to be non-null and non-empty");
        }
        Objects.requireNonNull(o, "dataProvider needs to be non-null");
        Objects.requireNonNull(idProvider, "idProvider needs to be non-null");
    }

    private List<DataObjectMethods> getDataObjectMethods(@NotNull Object o, Object idProvider, List<Field> fieldList) throws ReflectiveOperationException {
        List<DataObjectMethods> methodsList = new ArrayList<>();
        for (Field field : fieldList) {
            SQLUpdate sql = field.getAnnotation(SQLUpdate.class);
// TODO: 22.10.2017 invalid
//            Method booleanMethod = getMethod(o, sql.stateGet());
//            Method contentGetter = getMethod(o, sql.valueGet());
//            Method idGetter = getMethod(idProvider, "getId");
//            String columnName = getColumnName(o, sql);
//
//            methodsList.add(new DataObjectMethods(booleanMethod, contentGetter, idGetter, idProvider, o, columnName));
        }
        return methodsList;
    }

    /**
     * Gets the SQL statements with the given parameters.
     *
     * @param column name of the Column which will be updated
     * @param value  value which will be set
     * @param id     id of the row which will be updated
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
     * @param o         implementation of the {@code DataEntry} interface
     * @param sqlUpdate annotation of the field to update the database with
     * @return columnName - the name of the column which will be updated
     */
    private String getColumnName(Object o, SQLUpdate sqlUpdate) throws ReflectiveOperationException {
        String columnName = "";
        Class<?> tableClass;
        DataAccess access = o.getClass().getAnnotation(DataAccess.class);

        if (access == null) {
            throw new IllegalArgumentException("the class of the database " +
                    "object does not seem to be annotated with the DataAccess Annotation");
        }

        //load dao class
        String clazz = "Enterprise.data.database." + access.daoClass();
        try {
            tableClass = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            Log.classLogger(this.getClass()).log(Level.SEVERE, "could not find class " + clazz, e);
            throw new ReflectiveOperationException(e);
        }

        if (tableClass != null) {
            try {
                columnName = getColumnField(sqlUpdate, tableClass);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Log.classLogger(this.getClass()).log(Level.SEVERE, "could not get columnName ", e);
                throw new ReflectiveOperationException(e);
            }
        }
        return columnName;
    }

    /***
     *
     * @param sqlUpdate
     * @param tableClass
     * @return
     * @throws ReflectiveOperationException
     */
    private String getColumnField(SQLUpdate sqlUpdate, Class<?> tableClass) throws ReflectiveOperationException {
        String columnName;//load specified field
        Field tableField = tableClass.getDeclaredField(sqlUpdate.columnField());
        tableField.setAccessible(true);

        //load getInstance method, to get an object of the dao class
        Method getInstance = tableClass.getDeclaredMethod("getInstance", (Class<?>[]) null);
        //set accessible to true, to invoke the method without throwing an exception
        getInstance.setAccessible(true);

        if (String.class.isAssignableFrom(tableField.getType())) {
            //get the value
            columnName = (String) tableField.get(getInstance.invoke(null, (Object[]) null));

        } else if (DataColumn.class.isAssignableFrom(tableField.getType())) {
            //gets the columnName from the DataColumn field
            DataColumn column = (DataColumn) tableField.get(getInstance.invoke(null, (Object[]) null));
            columnName = column.getName();
        } else {
            throw new ReflectiveOperationException("unknown column field type");
        }
        return columnName;
    }

    /**
     * Invokes the Methods which were reflected by the {@code getMethod} methods
     * and uses the returned values to construct the SQL statement.
     *
     * @param methods the internal Data transfer object
     * @return setString - the complete SQL update statement
     */
    private String setString(DataObjectMethods methods) throws ReflectiveOperationException {
        String setString;

        String columnName = methods.getColumnName();

        String changedString = getStringFromMethod(methods.getBooleanMethod(), methods.getObject());
        boolean changed = Boolean.parseBoolean(changedString);

        if (changed) {
            String value = getStringFromMethod(methods.getContentGetter(), methods.getObject());

            int id = getId(methods.getIdGetter(), methods.getIdProvider());
            setString = setString(columnName, value, id);
        } else {
            setString = "";
        }
        return setString;
    }

    private int getId(Method idGetter, Object idProvider) throws ReflectiveOperationException {
        String idString = getStringFromMethod(idGetter, idProvider);
        return Integer.parseInt(idString);
    }

    /**
     * Invokes the method and returns their values as a String.
     *
     * @param method method to be invoked
     * @param o      object which the method will be invoked from
     * @return string - return value as an {@code String}
     */
    private String getStringFromMethod(Method method, Object o) throws ReflectiveOperationException {
        String string;
        try {
            if (Object.class.isAssignableFrom(method.getReturnType())) {
                //wraps the String for the SQL statement in quotation marks ''
                string = "'" + String.valueOf(method.invoke(o, (Object[]) null)) + "'";
            } else {
                string = String.valueOf(method.invoke(o, (Object[]) null));
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            Log.classLogger(this.getClass()).log(Level.SEVERE, "could not invoke " + method + " on " + o.getClass().getSimpleName(), e);
            throw new ReflectiveOperationException(e);
        }
        return string;
    }

    /**
     * Gets the Method from the name of the field, which has
     * a {@code String} appended at the end or beginning
     * of the given Object.
     *
     * @param o        object which should have the method
     * @param field    {@code Field} which name is part of the method
     * @param fragment String to be appended
     * @param type     enum marker for appending at the beginning or end
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, Field field, String fragment, FixType type) throws ReflectiveOperationException {
        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String methodName;

        if (type == FixType.SUFFIX) {
            methodName = fieldName + fragment;
        } else {
            methodName = fragment + fieldName;
        }
        Method method;
        try {
            method = o.getClass().getDeclaredMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            Log.classLogger(this.getClass()).log(Level.SEVERE, "method " + methodName + " does not exist in " + o.getClass().getSimpleName(), e);
            throw new ReflectiveOperationException(e);
        }
        return method;
    }

    /**
     * Gets the Method from the String of the parameter from
     * the class of the given Object.
     *
     * @param o          object which should have the method
     * @param methodName name of the sought method
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, String methodName) throws ReflectiveOperationException {
        Method method;
        try {
            method = o.getClass().getMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            Log.classLogger(this.getClass()).log(Level.SEVERE, "method " + methodName + " does not exist in " + o.getClass().getSimpleName(), e);
            throw new ReflectiveOperationException(e);
        }
        return method;
    }

    /**
     * Gets the Method from the name of the field, which has
     * a {@code String}s appended at the end and the beginning
     * of the given Object.
     *
     * @param o      object which should have the method
     * @param prefix {@code Field} which name is part of the method
     * @param suffix String to be appended
     * @param field  enum marker for appending at the beginning or end
     * @return method - Method which is part of the class of Object o
     */
    private Method getMethod(Object o, Field field, String prefix, String suffix) throws ReflectiveOperationException {
        Method method;

        String fieldName = field.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String methodName = prefix + fieldName + suffix;

        try {
            method = o.getClass().getDeclaredMethod(methodName, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            Log.classLogger(this.getClass()).log(Level.SEVERE, "method " + methodName + " does not exist in " + o.getClass().getSimpleName(), e);
            throw new ReflectiveOperationException(e);
        }
        return method;
    }

    /**
     * Gets the fields of the given Object and returns the {@link Field}s
     * annotated with the specified annotation.
     *
     * @param o             object to search through
     * @param annotateClass {@code Annotation} to look for
     * @return fields - {@code List} of {@code Field}s annotated with the specified {@code Annotation}
     */
    public List<Field> getFieldsByAnnotation(Object o, Class<? extends Annotation> annotateClass) {
        List<Field> fields = getFields(o);
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

    public List<Field> getFieldsByType(Object o, Class<?> clazz) {
        List<Field> fields = getFields(o);
        return fields.stream().filter(field -> clazz.isAssignableFrom(field.getType())).collect(Collectors.toList());
    }

    private List<Field> getFields(Object o) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = o.getClass();

        while (clazz != null && clazz != Object.class) {
            fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Marker enum, to differentiate between appending
     * at the beginning or the end on a String.
     */
    private enum FixType {
        SUFFIX,
        PREFIX
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
        private Object idProvider;
        private String columnName;

        /**
         * The constructor of {@code DataObjectMethods}.
         *
         * @param booleanMethod the stateChanged getter
         * @param contentGetter the value getter
         * @param idGetter      the getter of the id of the DMO
         * @param idProvider    the object declaring the idGetter
         * @param object        the DMO object itself
         * @param columnName    name of the column which will be updated
         */
        DataObjectMethods(Method booleanMethod, Method contentGetter, Method idGetter, Object idProvider, Object object, String columnName) {
            this.booleanMethod = booleanMethod;
            this.contentGetter = contentGetter;
            this.idGetter = idGetter;
            this.object = object;
            this.idProvider = idProvider;
            this.columnName = columnName;
            validateState();
        }

        /**
         * Validates the state of this {@code DataObjectMethods} object.
         */
        private void validateState() {
            Objects.requireNonNull(booleanMethod);
            Objects.requireNonNull(contentGetter);
            Objects.requireNonNull(idGetter);
            Objects.requireNonNull(object);
            Objects.requireNonNull(idProvider);

            if (columnName == null || columnName.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }

        /**
         * Gets the stateChanged-Getter
         *
         * @return method, not null
         */
        Method getBooleanMethod() {
            return booleanMethod;
        }

        /**
         * Gets the Getter-method of the {@code Field}.
         *
         * @return method, not null
         */
        Method getContentGetter() {
            return contentGetter;
        }

        /**
         * Gets the Id-Getter.
         *
         * @return method - the 'getId' method, not null
         */
        Method getIdGetter() {
            return idGetter;
        }

        /**
         * Gets the implementation instance of the {@link DataEntry} interface.
         *
         * @return database, not null
         */
        Object getObject() {
            return object;
        }

        /**
         * Gets the Object instance which declared the id-Getter Method.
         *
         * @return object, not null
         */
        Object getIdProvider() {
            return idProvider;
        }

        /**
         * Gets the name of the Column.
         *
         * @return string - name of the Column in the Database, not null or empty
         */
        String getColumnName() {
            return columnName;
        }
    }
}
