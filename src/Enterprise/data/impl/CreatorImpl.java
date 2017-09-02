package Enterprise.data.impl;

import Enterprise.data.Cache;
import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.Person;
import Enterprise.data.intface.*;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import Enterprise.misc.SetList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Builder;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of Creator
 *
 * @see Creator
 */
@DataAccess(daoClass = "CreatorTable")
public class CreatorImpl extends EnterpriseEntry implements DataBase, Comparable<Creator>, Creator {
    private int creatorId;

    @SQLUpdate(stateGet = "isNameChanged", valueGet = "getName", columnField = "nameC")
    private StringProperty name = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isSortNameChanged", valueGet = "getSortName", columnField = "sortNameC")
    private StringProperty sortName = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isStatusChanged", valueGet = "getStatus", columnField = "statusC")
    private StringProperty status = new SimpleStringProperty();

    // TODO: 30.08.2017 implement
    private Person personalInfo;

    private List<Creation> creatorWorks = new SetList<>();

    private BooleanProperty nameChanged = new SimpleBooleanProperty(false);
    private BooleanProperty sortNameChanged = new SimpleBooleanProperty(false);
    private BooleanProperty statusChanged = new SimpleBooleanProperty(false);

    private static Cache<String, CreatorImpl> creatorCache = new Cache<>();

    /**
     * The constructor of {@code CreatorImpl}
     *
     */
    private CreatorImpl(CreatorBuilder builder) {
        this.name.set(builder.name);
        this.sortName.set(builder.sortName);
        this.status.set(builder.status);
        this.personalInfo = builder.person;
        creatorId = builder.id;

        invalidListener();
        bindUpdated();
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id <= 0) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.creatorId = id;
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
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof CreatorImpl) {
            String thisName = name.get();
            String thatName = ((CreatorImpl) o).nameProperty().get();

            equals = thisName.equalsIgnoreCase(thatName);

            /*if (equals) {
                Person thisPerson = personalInfo;
                Person thatPerson = ((CreatorImpl) o).getPersonalInfo();

                equals = thisPerson.equals(thatPerson);
            }*/
            // TODO: 15.08.2017 implement Person and the others
        }
        return equals;
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

    public static class CreatorBuilder implements Builder<CreatorImpl> {

        private final String name;
        private String sortName = Default.STRING;
        private String status = Default.STRING;
        private int id = Default.VALUE;
        private Person person = new Person();

        public CreatorBuilder(String name) {
            this.name = name;
        }

        public CreatorBuilder setSortName(String sortName) {
            this.sortName = sortName;
            return this;
        }

        public CreatorBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

        public CreatorBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public CreatorBuilder setPerson(Person person) {
            this.person = person;
            return this;
        }

        @Override
        public CreatorImpl build() {
            validateState();
            return creatorCache.checkCache(new CreatorImpl(this), CreatorImpl::getName);
        }

        /**
         * validates the State of this {@code CreatorImpl}
         *
         * @throws IllegalArgumentException if any fields are null
         */
        private void validateState() {
            String message = "";
            if (id < 0) {
                message = message + "creatorId is invalid: " + id + ", ";
            }
            if (name == null) {
                message = message + "name is null, ";
            }
            if (sortName == null) {
                message = message + "sortName is null, ";
            }
            if (status == null) {
                message = message + "status is null, ";
            }
            if (person == null) {
                message = message + "personalInfo is null, ";
            }
            if (!message.isEmpty()) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    @Override
    public int hashCode() {
        return name.get().hashCode() /* + personalInfo.hashCode()*/;
    }

}
