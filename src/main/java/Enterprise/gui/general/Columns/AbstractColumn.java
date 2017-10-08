package Enterprise.gui.general.Columns;

import Configs.SettingsManager;
import Enterprise.gui.controller.ContentController;
import Enterprise.gui.general.ItemFactory;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public abstract class AbstractColumn<E, T> implements Column<E, T> {
    private final String node;
    protected TableColumn<E, T> column;
    protected CheckMenuItem item;
    protected Module module;
    private String indexKey = "index";
    private String widthKey = "prefWidth";
    private String defaultSelectKey = "defaultSelect";
    private Map<String, String> keyMap = new HashMap<>();
    private Map<String, String> defaultMap = new HashMap<>();
    private boolean shown;


    protected AbstractColumn(String node, Module module) {
        this.module = module;
        this.node = node;
        initDefault();
        SettingsManager.getInstance().register(this);
    }

    @Override
    public void loadSetting(String key, String value) {
        this.keyMap.put(key, value);
    }

    @Override
    public String getNodeName() {
        return node;
    }

    @Override
    public boolean isModule() {
        return true;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public void setColumnModule(Module module) {
        this.module = module;
    }

    @Override
    public CheckMenuItem getMenuItem() {
        return item;
    }

    @Override
    public TableColumn<E, T> getTableColumn() {
        if (column == null) {
            initColumn();
        }
        return column;
    }

    @Override
    public int getPrefIndex() {
        return getInt(indexKey);
    }

    @Override
    public double getPrefWidth() {
        return getDouble(widthKey);
    }

    @Override
    public boolean getDefaultSelect() {
        return getBoolean(defaultSelectKey);
    }

    private void setDefaultSelect(boolean b) {
        putDefault(defaultSelectKey, String.valueOf(b));
    }

    @Override
    public void loadMenuItem(ContentController controller) {
        item = new ItemFactory().getCheckMenuItem(controller, this);
        item.selectedProperty().addListener((observable, oldValue, newValue) -> setSelect(newValue));
    }

    @Override
    public void addColumnsListener(TableView<E> tableView) {
        tableView.getColumns().addListener((ListChangeListener<? super TableColumn<E, ?>>) observable -> {
            if (observable.next()) {
                int index = tableView.getColumns().indexOf(column);
                if (index > 0) {
                    setIndex(index);
                }
            }
        });
    }

    @Override
    public String getSetting(String key) {
        return keyMap.get(key);
    }

    @Override
    public Set<String> getKeys() {
        return defaultMap.keySet();
    }

    @Override
    public String getDefault(String key) {
        return defaultMap.get(key);
    }

    public boolean isShown() {
        return shown;
    }

    @Override
    public void setShown(boolean b) {
        shown = b;
    }

    @Override
    public int compareTo(Column<E, T> o) {
        if (o == this) {
            return 0;
        }
        if (o == null) {
            return -1;
        }

        return this.getName().compareToIgnoreCase(o.getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractColumn<?, ?> that = (AbstractColumn<?, ?>) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (item != null ? !item.equals(that.item) : that.item != null) return false;
        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        return node != null ? node.equals(that.node) : that.node == null;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (module != null ? module.hashCode() : 0);
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }

    protected double getDouble(String key) {
        String value = keyMap.get(key);
        if (isDigit(value)) {
            return Double.parseDouble(value);
        } else {
            return Double.parseDouble(getDefault(key));
        }
    }

    private boolean isDigit(String value) {
        return value != null && value.chars().allMatch(Character::isDigit);
    }

    protected int getInt(String key) {
        String value = keyMap.get(key);
        if (isDigit(value)) {
            int index = Integer.parseInt(value);
            return index < 0 ? getDefaultInt(key) : index;
        } else {
            return getDefaultInt(key);
        }
    }

    private int getDefaultInt(String key) {
        String def = getDefault(key);
        return Integer.parseInt(def);
    }

    protected boolean getBoolean(String key) {
        String value = keyMap.get(key);
        if (value != null) {
            Matcher matcher = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE).matcher(value);
            if (matcher.matches()) {
                return Boolean.parseBoolean(value);
            } else {
                return Boolean.parseBoolean(getDefault(key));
            }
        } else {
            return Boolean.parseBoolean(getDefault(key));
        }
    }

    protected void initColumn() {
        column = columnFactory(getName(), getPrefWidth(), getCallBack());
        // FIXME: 01.10.2017  width change does not activate
        column.widthProperty().addListener((observable, oldValue, newValue) -> {
            setWidth(newValue.doubleValue());
        });
    }

    protected TableColumn<E, T> columnFactory(String columnName, double prefWidth,
                                              Callback<TableColumn.CellDataFeatures<E, T>, ObservableValue<T>> callback) {

        TableColumn<E, T> column = new TableColumn<>();
        column.setPrefWidth(prefWidth);
        column.setMinWidth(40);
        column.setEditable(true);
        column.setText(columnName);
        column.setCellValueFactory(callback);
        return column;
    }

    protected void putDefault(String key, String value) {
        defaultMap.put(key, value);
    }

    private void setDefaultWidth(double width) {
        putDefault(widthKey, String.valueOf(width));
    }

    private void setDefaultIndex(int index) {
        putDefault(indexKey, String.valueOf(index));
    }

    protected abstract void initDefault();

    protected void setDefault(int index, double width, boolean select) {
        setDefaultIndex(index);
        setDefaultWidth(width);
        setDefaultSelect(select);
    }

    private void setSelect(boolean b) {
        keyMap.put(defaultSelectKey, String.valueOf(b));
    }

    private void setWidth(double width) {
        keyMap.put(widthKey, String.valueOf(width));
    }

    private void setIndex(int index) {
        keyMap.put(indexKey, String.valueOf(index));
    }
}
