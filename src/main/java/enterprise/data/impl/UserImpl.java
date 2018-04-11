package enterprise.data.impl;

import enterprise.data.Default;
import enterprise.data.intface.User;
import gorgon.external.GorgonEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import tools.SetList;

import java.util.List;

/**
 * Implementation of User
 *
 * @see User
 */
public class UserImpl extends AbstractDataEntry implements User {

    private final StringProperty ownStatus = new SimpleStringProperty();

    private final StringProperty comment = new SimpleStringProperty();

    private final IntegerProperty rating = new SimpleIntegerProperty();

    private final IntegerProperty processedPortion = new SimpleIntegerProperty();

    private final StringProperty listName = new SimpleStringProperty();

    private final StringProperty keyWords = new SimpleStringProperty();

    /**
     * The no-argument constructor of {@code UserImpl}
     */
    public UserImpl() {
        this(Default.STRING, Default.STRING, Default.VALUE, Default.LIST, Default.VALUE, Default.KEYWORDS);
    }

    @Override
    public String toString() {
        return "UserImpl{" +
                "ownStatus=" + ownStatus.get() +
                ", comment=" + comment.get() +
                ", rating=" + rating.get() +
                ", processedPortion=" + processedPortion.get() +
                ", listName=" + listName.get() +
                ", keyWords=" + keyWords.get() +
                '}';
    }

    /**
     * The constructor of {@code UserImpl}
     *
     * @param ownStatus        status of the {@link User}
     * @param comment          comment about the {@link enterprise.data.intface.Creation}
     * @param rating           rating of the {@code Creation}
     * @param listName         name of the listName assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords         search keyWords for the {@code Creation}
     */
    public UserImpl(String ownStatus, String comment, int rating, String listName, int processedPortion, String keyWords) {
        this.ownStatus.set(ownStatus);
        this.comment.set(comment);
        this.rating.set(rating);
        this.listName.set(listName);
        this.processedPortion.set(processedPortion);
        this.keyWords.set(keyWords);

        validateState();
    }

    /**
     * validates the correctness of the State of this {@code UserImpl}
     *
     * @throws IllegalArgumentException if any dataFields are invalid, meaning null or integers smaller than zero
     */
    private void validateState() {
        String message = "";
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserImpl)) return false;

        UserImpl that = (UserImpl) o;

        return (ownStatus != null ? ownStatus.get().equals(that.ownStatus.get()) :
                that.ownStatus == null) && (comment.get() != null ? comment.get().equals(that.comment.get()) :

                that.comment == null) && (rating != null ? rating.get() == that.rating.get() :
                that.rating == null) && (processedPortion != null ? processedPortion.get() == that.processedPortion.get() :

                that.processedPortion == null) && (listName.get() != null ? listName.get().equals(that.listName.get()) :
                that.listName == null) && (keyWords.get() != null ? keyWords.get().equals(that.keyWords.get()) :

                that.keyWords == null);
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof User)) return -1;

        User o = (User) gorgonEntry;
        int compared = getOwnStatus().compareTo(o.getOwnStatus());
        if (compared == 0) {
            compared = getComment().compareTo(o.getComment());
        }
        if (compared == 0) {
            compared = getRating() - o.getRating();
        }
        if (compared == 0) {
            compared = getProcessedPortion() - o.getProcessedPortion();
        }
        if (compared == 0) {
            compared = getListName().compareTo(o.getListName());
        }
        if (compared == 0) {
            compared = getKeyWords().compareTo(o.getKeyWords());
        }
        return compared;
    }

    @Override
    public String getOwnStatus() {
        return ownStatus.get();
    }

    public void setOwnStatus(String ownStatus) {
        this.ownStatus.set(ownStatus);
    }

    @Override
    public StringProperty ownStatusProperty() {
        return ownStatus;
    }

    @Override
    public String getComment() {
        return comment.get();
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    @Override
    public StringProperty commentProperty() {
        return comment;
    }

    @Override
    public int getRating() {
        return rating.get();
    }

    public void setRating(int rating) {
        this.rating.set(rating);
    }

    @Override
    public IntegerProperty ratingProperty() {
        return rating;
    }

    @Override
    public int getProcessedPortion() {
        return processedPortion.get();
    }

    public void setProcessedPortion(int processedPortion) {
        this.processedPortion.set(processedPortion);
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

    public void setKeyWords(String keyWords) {
        this.keyWords.set(keyWords);
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
}
