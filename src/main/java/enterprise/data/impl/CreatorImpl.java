package enterprise.data.impl;

import enterprise.data.Default;
import enterprise.data.Person;
import enterprise.data.intface.Creation;
import enterprise.data.intface.Creator;
import gorgon.external.GorgonEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Builder;
import tools.Cache;
import tools.SetList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of Creator
 *
 * @see Creator
 */
public class CreatorImpl extends AbstractDataEntry implements Creator {

    private static final Cache<String, CreatorImpl> creatorCache = new Cache<>();

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty sortName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final List<Creation> creatorWorks = new SetList<>();
    // TODO: 30.08.2017 implement
    private Person personalInfo;

    public CreatorImpl() {
        name.set("NoName");
        personalInfo = new Person();
    }

    /**
     * The constructor of {@code CreatorImpl}
     */
    private CreatorImpl(CreatorBuilder builder) {
        this.name.set(builder.name);
        this.sortName.set(builder.sortName);
        this.status.set(builder.status);
        this.personalInfo = builder.person;
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
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String name) {
        this.name.set(name);
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
    public void setSortName(String sortName) {
        this.sortName.set(sortName);
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
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public void addWork(Creation creation) {
        if (creation != null) {
            creatorWorks.add(creation);
        }
    }

    @Override
    public int hashCode() {
        return name.get().hashCode() /* + personalInfo.hashCode()*/;
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
    public String toString() {
        return name.get();
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Creator)) return -1;

        Creator o = (Creator) gorgonEntry;

        int compared = getName().compareTo(o.getName());

            /*if (compared == 0) {
                Person thisPerson = personalInfo;
                Person thatPerson = o.getPersonalInfo();

                compared = thisPerson.compareTo(thatPerson);
            }*/
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
            return creatorCache.checkCache(
                    new CreatorImpl(this),
                    CreatorImpl::getName,
                    (creator, creator2) -> {
                        if (creator2.getName().isEmpty()) {
                            return creator2;
                        } else {
                            return Objects.equals(creator, creator2) ? creator : creator2;
                        }
                    });
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

}
