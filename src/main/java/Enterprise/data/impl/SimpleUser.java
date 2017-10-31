package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.intface.User;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import Enterprise.misc.SetList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Implementation of User
 *
 * @see User
 */
@DataAccess(daoClass = "UserTable")
public class SimpleUser extends AbstractDataEntry implements User {

    @SQLUpdate(columnField = "ownStatusC")
    private final StringProperty ownStatus = new SimpleStringProperty();

    @SQLUpdate(columnField = "commentC")
    private final StringProperty comment = new SimpleStringProperty();

    @SQLUpdate(columnField = "ratingC")
    private final IntegerProperty rating = new SimpleIntegerProperty();

    @SQLUpdate(columnField = "processedPortionC")
    private final IntegerProperty processedPortion = new SimpleIntegerProperty();

    @SQLUpdate(columnField = "listC")
    private final StringProperty listName = new SimpleStringProperty();

    @SQLUpdate(columnField = "keyWordsC")
    private final StringProperty keyWords = new SimpleStringProperty();

    /**
     * The no-argument constructor of {@code SimpleUser}
     */
    public SimpleUser() {
        this(Default.VALUE, Default.STRING, Default.STRING, Default.VALUE, Default.LIST, Default.VALUE, Default.KEYWORDS);
    }

    /**
     * The constructor of {@code SimpleUser}
     *
     * @param ownStatus        status of the {@link User}
     * @param comment          comment about the {@link Enterprise.data.intface.Creation}
     * @param rating           rating of the {@code Creation}
     * @param list             name of the listName assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords         search keyWords for the {@code Creation}
     */
    public SimpleUser(String ownStatus, String comment, int rating, String list, int processedPortion, String keyWords) {
        this(Default.VALUE, ownStatus, comment, rating, list, processedPortion, keyWords);
    }

    /**
     * The constructor of {@code SimpleUser}
     *
     * @param id               database Id of this {@code SimpleUser}
     * @param ownStatus        status of the {@link User}
     * @param comment          comment about the {@link Enterprise.data.intface.Creation}
     * @param rating           rating of the {@code Creation}
     * @param listName         name of the listName assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords         search keyWords for the {@code Creation}
     */
    public SimpleUser(int id, String ownStatus, String comment, int rating, String listName, int processedPortion, String keyWords) {
        super(id);
        this.ownStatus.set(ownStatus);
        this.comment.set(comment);
        this.rating.set(rating);
        this.listName.set(listName);
        this.processedPortion.set(processedPortion);
        this.keyWords.set(keyWords);

        validateState();
    }

    /**
     * validates the correctness of the State of this {@code SimpleUser}
     *
     * @throws IllegalArgumentException if any dataFields are invalid, meaning null or integers smaller than zero
     */
    private void validateState() {
        String message = "";
        if (getId() < 0) {
            message = message + "userId is invalid: " + getId() + ",";
        }
        if (ownStatus.get() == null) {
            message = message + "ownStatus is null, ";
        }
        if (comment.get() == null) {
            message = message + "comment is null, ";
        }
        //rating numbers are between 0 and 10, including both
        if (rating.get() < 0 || rating.get() > 10) {
            message = message + "rating is invalid: " + rating.get() + ", ";
        }
        if (listName.get() == null) {
            message = message + "listName is null, ";
        }
        if (processedPortion.get() < 0) {
            message = message + "processedPortion is invalid: " + processedPortion.get() + ", ";
        }
        if (keyWords.get() == null) {
            message = message + "keyWords is null";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public String getOwnStatus() {
        return ownStatus.get();
    }

    @Override
    public StringProperty ownStatusProperty() {
        return ownStatus;
    }

    @Override
    public String getComment() {
        return comment.get();
    }

    @Override
    public StringProperty commentProperty() {
        return comment;
    }

    @Override
    public int getRating() {
        return rating.get();
    }

    @Override
    public IntegerProperty ratingProperty() {
        return rating;
    }

    @Override
    public int getProcessedPortion() {
        return processedPortion.get();
    }

    @Override
    public IntegerProperty processedPortionProperty() {
        return processedPortion;
    }

    @Override
    public String getListName() {
        return listName.get();
    }

    @Override
    public void setListName(String listName) {
        this.listName.set(listName);
    }

    @Override
    public String getKeyWords() {
        return keyWords.get();
    }

    @Override
    public StringProperty keyWordsProperty() {
        return keyWords;
    }

    @Override
    public StringProperty listNameProperty() {
        return listName;
    }

    @Override
    public List<String> getKeyWordList() {
        String keyWords = getKeyWords();
        if (keyWords == null) {
            keyWords = "";
        }

        List<String> stringList = new SetList<>();
        for (String string : keyWords.split("[\\s,]")) {
            if (!string.isEmpty() && !stringList.contains(string)) {
                stringList.add(string);
            }
        }
        //removes all 'old' keyWords, adds only 'new' keyWords
        return stringList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleUser)) return false;

        SimpleUser that = (SimpleUser) o;

        return (ownStatus != null ? ownStatus.get().equals(that.ownStatus.get()) :
                that.ownStatus == null) && (comment.get() != null ? comment.get().equals(that.comment.get()) :

                that.comment == null) && (rating != null ? rating.get() == that.rating.get() :
                that.rating == null) && (processedPortion != null ? processedPortion.get() == that.processedPortion.get() :

                that.processedPortion == null) && (listName.get() != null ? listName.get().equals(that.listName.get()) :
                that.listName == null) && (keyWords.get() != null ? keyWords.get().equals(that.keyWords.get()) :

                that.keyWords == null);
    }

    @Override
    public int hashCode() {
        int result = ownStatus.get() != null ? ownStatus.get().hashCode() : 0;
        result = 31 * result + (comment.get() != null ? comment.hashCode() : 0);
        result = 31 * result + rating.get();
        result = 31 * result + processedPortion.get();
        result = 31 * result + (listName.get() != null ? listName.get().hashCode() : 0);
        result = 31 * result + (keyWords.get() != null ? keyWords.get().hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(User o) {
        int compared = ownStatus.get().compareTo(o.getOwnStatus());
        if (compared == 0) {
            compared = comment.get().compareTo(o.getComment());
        }
        if (compared == 0) {
            compared = rating.get() - o.getRating();
        }
        if (compared == 0) {
            compared = processedPortion.get() - o.getProcessedPortion();
        }
        if (compared == 0) {
            compared = listName.get().compareTo(o.getListName());
        }
        if (compared == 0) {
            compared = keyWords.get().compareTo(o.getKeyWords());
        }
        return compared;
    }
}
