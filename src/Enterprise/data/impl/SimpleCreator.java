package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.Person;
import Enterprise.data.intface.*;
import Enterprise.misc.SetList;
import Enterprise.misc.SQL;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Implementation of Creator
 * @see Creator
 */
public class SimpleCreator extends EnterpriseEntry implements DataBase,Comparable<Creator>, Creator {
    private int creatorId;
    private static int idCounter = 1;

    @SQL
    private StringProperty name = new SimpleStringProperty();
    @SQL
    private StringProperty sortName = new SimpleStringProperty();
    @SQL
    private StringProperty status = new SimpleStringProperty();
    private Person personalInfo;

    private List<Creation> creatorWorks = new SetList<>();

    private BooleanProperty nameChanged = new SimpleBooleanProperty(false);
    private BooleanProperty sortNameChanged = new SimpleBooleanProperty(false);
    private BooleanProperty statusChanged = new SimpleBooleanProperty(false);

    /**
     * The constructor of {@code SimpleCreator}
     *
     * @param name name of this {@code SimpleCreator}
     * @param sortName sorting name of this {@code SimpleCreator}
     * @param status status of this {@code SimpleCreator}
     */
    public SimpleCreator(String name, String sortName, String status) {
        this(Default.VALUE, name, sortName, status, new Person(), new ArrayList<>());
    }

    /**
     * The constructor of {@code SimpleCreator}
     *
     * @param name name of this {@code SimpleCreator}
     * @param sortName sorting name of this {@code SimpleCreator}
     */
    public SimpleCreator(String name, String sortName) {
        this(Default.VALUE, name, sortName, Default.STRING, new Person(), new ArrayList<>());
    }

    /**
     * The constructor of {@code SimpleCreator}.
     *
     * @param name name of this {@code SimpleCreator}
     */
    public SimpleCreator(String name) {
        this(Default.VALUE, name, Default.STRING, Default.STRING, new Person(), new ArrayList<>());
    }

    /**
     * The no-argument constructor of {@code SimpleCreator}
     */
    public SimpleCreator() {
        this(Default.VALUE, Default.STRING, Default.STRING, Default.STRING, new Person(), new ArrayList<>());
    }


    /**
     * The constructor of {@code SimpleCreator}
     *
     * @param id database id
     * @param name name of this {@code SimpleCreator}
     * @param sortName sorting name of this {@code SimpleCreator}
     * @param status status of this {@code SimpleCreator}
     * @param person personalInfo of this {@code SimpleCreator}
     * @param simpleCreations creations of this {@code SimpleCreator}
     */
    public SimpleCreator(int id, String name, String sortName, String status, Person person, List<Creation> simpleCreations) {
        this.name.set(name);
        this.sortName.set(sortName);
        this.status.set(status);
        this.personalInfo = person;
        this.creatorWorks = simpleCreations;

        if (id == 0) {
            creatorId = idCounter;
            idCounter++;
        } else {
            creatorId = id;
            if (idCounter <= id) {
                idCounter = id;
                idCounter++;
            }
        }
        validateState();
        invalidListener();
        bindUpdated();
    }

    /**
     * validates the State of this {@code SimpleCreator}
     *
     * @throws IllegalArgumentException if any fields are null
     */
    private void validateState() {
        String message = "";
        if (creatorId < 0) {
            message = message + "creatorId is invalid: " + creatorId + ", ";
        }
        if (name.get() == null) {
            message = message + "name is null, ";
        }
        if (sortName.get() == null) {
            message = message + "sortName is null, ";
        }
        if (status.get() == null) {
            message = message + "status is null, ";
        }
        if (personalInfo == null) {
            message = message + "personalInfo is null, ";
        }
        if (creatorWorks == null) {
            message = message + "creatorWorks is null";
        }
        if (!message.isEmpty()) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            logger.log(Level.WARNING, "object creation failed", exception);
            throw exception;
        }
    }

    /**
     * adds invalidListeners to the dataField-Properties, sets stateChanged BooleanProperties to true,
     * if state has changed
     */
    private void invalidListener() {
        name.addListener(observable -> nameChanged.set(true));
        sortName.addListener(observable -> sortNameChanged.set(true));
        status.addListener(observable -> statusChanged.set(true));
    }

    @Override
    protected void bindUpdated() {
        updated.bind(nameChanged.or(sortNameChanged).or(statusChanged));
    }

    @Override
    public void setUpdated() {
        nameChanged.set(false);
        sortNameChanged.set(false);
        statusChanged.set(false);
    }

    @Override
    public boolean isSortNameChanged() {
        return sortNameChanged.get();
    }

    @Override
    public Person getPersonalInfo() {
        return personalInfo;
    }

    @Override
    public void setPersonalInfo(Person personalInfo) {
        this.personalInfo = personalInfo;
    }

    @Override
    public List<Creation> getCreatorWorks() {
        return Collections.unmodifiableList(creatorWorks);
    }

    @Override
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public BooleanProperty updatedProperty() {
        return updated;
    }

    @Override
    public boolean isNameChanged() {
        return nameChanged.get();
    }

    @Override
    public boolean isStatusChanged() {
        return statusChanged.get();
    }

    @Override
    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public void setSortName(String sortName) {
        this.sortName.set(sortName);
    }

    @Override
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getSortName() {
        return sortName.get();
    }

    @Override
    public StringProperty sortNameProperty() {
        return sortName;
    }

    @Override
    public String getStatus() {
        return status.get();
    }

    @Override
    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public int getId() {
        return creatorId;
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id < 1) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.creatorId = id;
    }

    @Override
    public void addWork(Creation creation) {
        if (creation != null) {
            creatorWorks.add(creation);
        }
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public int compareTo(Creator o) {
        int compared = -1;
        if (o != null) {
            String thisName = name.get();
            String thatName = o.nameProperty().get();

            compared = thisName.compareTo(thatName);

            /*if (compared == 0) {
                Person thisPerson = personalInfo;
                Person thatPerson = o.getPersonalInfo();

                compared = thisPerson.compareTo(thatPerson);
            }*/
        }
        return compared;
    }
    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof SimpleCreator) {
            String thisName = name.get();
            String thatName = ((SimpleCreator) o).nameProperty().get();

            equals = thisName.equalsIgnoreCase(thatName);

            /*if (equals) {
                Person thisPerson = personalInfo;
                Person thatPerson = ((SimpleCreator) o).getPersonalInfo();

                equals = thisPerson.equals(thatPerson);
            }*/
            // TODO: 15.08.2017 implement Person and the others
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return name.get().hashCode() /* + personalInfo.hashCode()*/;
    }

}
