package Enterprise.data.update;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.ContainerEntry;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.Entry;
import Enterprise.misc.SQLUpdate;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * // TODO: 22.10.2017 do sth about postconfigs and source
 */
public class EntryWrapper {
    private static List<EntryWrapper> wrapperList = new ArrayList<>();
    private static List<Entry> entriesKeys = new ArrayList<>();
    private Entry entry;
    private BooleanProperty updated = new SimpleBooleanProperty(this, "updated", false);
    private Map<Field, BooleanProperty> fieldMap = new HashMap<>();
    private Map<ContainerEntry, List<DataEntry>> entryMap = new TreeMap<>();


    private EntryWrapper(Entry entry) {
        this.entry = entry;
    }

    private EntryWrapper() {

    }

    public static EntryWrapper wrap(Entry entry, BooleanProperty... dependencies) {
        if (entry == null) {
            throw new IllegalArgumentException();
        }
        if (!contains(entry)) {
            EntryWrapper entryWrapper = new EntryWrapper(entry);
            entryWrapper.ready(dependencies);
            return entryWrapper;
        } else {
            return new EntryWrapper();
        }
    }

    public static EntryWrapper getWrapper(Entry entry) {
        for (EntryWrapper entryWrapper : wrapperList) {
            if (entryWrapper.entry.equals(entry)) {
                return entryWrapper;
            }
        }
        return new EntryWrapper();
    }

    private static boolean contains(Entry key) {
        for (Entry entriesKey : entriesKeys) {
            if (entriesKey == key) {
                return true;
            }
        }
        return false;
    }

    private static boolean check(DataEntry dataEntry) {
        EntryWrapper wrapper = getWrapper(dataEntry);
        return wrapper.updated.get();
    }

    public void addListener(ChangeListener<? super Boolean> changeListener) {
        updated.addListener(changeListener);
    }

    public void setUpdated() {
        if (!updated.isBound()) {
            updated.set(false);
        }
    }

    public void addListener(InvalidationListener listener) {
        updated.addListener(listener);
    }

    public List<Field> getUpdatedDataFields() {
        return fieldMap.
                keySet().
                stream().
                filter(field -> fieldMap.get(field).get()).
                collect(Collectors.toList());
    }

    public List<DataEntry> getUpdatedEntries() {
        if (entry instanceof ContainerEntry) {
            return entryMap.
                    get(entry).
                    stream().
                    filter(EntryWrapper::check).
                    collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public boolean isUpdated() {
        return updated.get();
    }

    private void bind(Collection<BooleanProperty> properties) {
        BooleanProperty[] dependencies = properties.toArray(new BooleanProperty[0]);

        Callable<Boolean> callable = () -> Arrays.stream(dependencies).anyMatch(BooleanProperty::get);

        BooleanBinding booleanBinding = Bindings.createBooleanBinding(callable, dependencies);

        updated.bind(booleanBinding);
    }

    private void readyFields(List<Field> byAnnotation) {
        byAnnotation.forEach(field -> fieldMap.put(field, new SimpleBooleanProperty()));
        bindMap();
    }

    private void bindMap() {
        for (Field field : fieldMap.keySet()) {
            field.setAccessible(true);
            try {
                Object o = field.get(entry);
                if (o instanceof Property) {
                    ((Property) o).addListener(observable -> fieldMap.get(field).set(true));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Field> getDataFields() {
        return new ReflectUpdate().getFieldsByAnnotation(entry, SQLUpdate.class);
    }

    private void ready(BooleanProperty... dependencies) {
        Collection<BooleanProperty> booleanProperties = new ArrayList<>();
        booleanProperties.addAll(Arrays.asList(dependencies));

        if (entry instanceof DataEntry && entry instanceof ContainerEntry) {
            readyDataEntry();

            List<BooleanProperty> properties = readyContainerEntry();
            booleanProperties.addAll(properties);
            booleanProperties.addAll(fieldMap.values());

            bind(booleanProperties);
        } else {
            if (entry instanceof DataEntry) {
                readyDataEntry();
                booleanProperties.addAll(fieldMap.values());
                bind(booleanProperties);
            } else if (entry instanceof ContainerEntry) {
                List<BooleanProperty> properties = readyContainerEntry();
                booleanProperties.addAll(properties);
                bind(booleanProperties);
            }
        }
        if (entry instanceof CreationEntry) {
            updated.addListener((observable, oldValue, newValue) -> {
                if (newValue && !entry.isNewEntry()) {
                    OpEntryCarrier.getInstance().addUpdateEntry((CreationEntry) entry);
                }

            });
        }
        wrapperList.add(this);
    }

    private List<BooleanProperty> readyContainerEntry() {
        List<Field> byType = new ReflectUpdate().getFieldsByType(entry, DataEntry.class);
        return processDataEntryFields(byType);
    }

    private void readyDataEntry() {
        List<Field> byAnnotation = getDataFields();
        readyFields(byAnnotation);
    }

    private List<BooleanProperty> processDataEntryFields(List<Field> byType) {
        List<BooleanProperty> booleanProperties = new ArrayList<>();
        List<DataEntry> dataEntries = new ArrayList<>();

        for (Field field : byType) {
            try {
                field.setAccessible(true);
                DataEntry o = (DataEntry) field.get(entry);
                dataEntries.add(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        dataEntries.forEach(dataEntry -> {
            wrap(dataEntry);
            EntryWrapper wrapper = getWrapper(dataEntry);
            booleanProperties.add(wrapper.updated);
        });

        entryMap.put((ContainerEntry) entry, dataEntries);
        return booleanProperties;
    }
}
