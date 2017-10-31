package Enterprise.data.update;

import Enterprise.data.database.DataColumn;
import Enterprise.data.intface.Entry;
import Enterprise.misc.DataAccess;
import Enterprise.misc.Log;
import Enterprise.misc.SQLUpdate;
import com.sun.istack.internal.NotNull;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 */
public class EntryUpdater implements UpdateReflector {
    private String tableName;
    private String idColumn;

    public Set<String> getUpdateStrings(@NotNull Entry dataProvider, @NotNull Object idProvider, @NotNull String idColumn, @NotNull String tableName) {
        validate(dataProvider, idProvider, tableName, idColumn);

        EntryWrapper wrapper = EntryWrapper.getWrapper(dataProvider);
        List<Field> updatedDataFields = wrapper.getUpdatedDataFields();
        Set<String> statements = new HashSet<>();

        updatedDataFields.forEach(field -> getUpdateString(dataProvider, idProvider, statements, field));
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

    private void getUpdateString(@NotNull Entry dataProvider, @NotNull Object idProvider, Set<String> statements, Field field) {
        try {
            if (ObservableValue.class.isAssignableFrom(field.getType())) {
                ObservableValue<?> o = (ObservableValue<?>) field.get(dataProvider);

                field.setAccessible(true);

                String value;
                if (StringProperty.class.isAssignableFrom(o.getClass()) || ObjectProperty.class.isAssignableFrom(o.getClass())) {
                    value = String.valueOf("'" + o.getValue() + "'");
                } else {
                    value = String.valueOf(o.getValue());
                }

                SQLUpdate annotation = field.getAnnotation(SQLUpdate.class);
                Method idGetter = getMethod(idProvider, "getId");

                int id = getId(idGetter, idProvider);
                String columnName = getColumnName(dataProvider, annotation);

                String result = setString(columnName, value, id);
                if (!result.isEmpty()) {
                    statements.add(result);
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
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
}
