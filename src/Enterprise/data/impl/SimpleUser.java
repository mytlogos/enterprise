package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.DataTable;
import Enterprise.data.intface.Table;
import Enterprise.data.intface.User;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.*;

import java.util.logging.Level;

/**
 * Implementation of User
 * @see User
 */
@DataAccess(daoClass = "UserTable")
public class SimpleUser extends EnterpriseEntry implements User{
    private int userId;
    private static int idCounter = 1;

    @SQLUpdate(stateGet = "isOwnStatusChanged", valueGet = "getOwnStats", columnField = "ownStatusC")
    private StringProperty ownStatus = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isCommentChanged", valueGet = "getComment", columnField = "commentC")
    private StringProperty comment = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isRatingChanged", valueGet = "getRating", columnField = "ratingC")
    private IntegerProperty rating = new SimpleIntegerProperty();

    @SQLUpdate(stateGet = "isProcessedPortionChanged", valueGet = "getProcessedPortion", columnField = "processedPortionC")
    private IntegerProperty processedPortion = new SimpleIntegerProperty();

    @SQLUpdate(stateGet = "isListChanged", valueGet = "getList", columnField = "listC")
    private StringProperty list = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isKeyWordsChanged", valueGet = "getKeyWords", columnField = "keyWordsC")
    private StringProperty keyWords = new SimpleStringProperty();

    private BooleanProperty ownStatusChanged = new SimpleBooleanProperty(false);
    private BooleanProperty commentChanged = new SimpleBooleanProperty(false);
    private BooleanProperty ratingChanged = new SimpleBooleanProperty(false);
    private BooleanProperty processedPortionChanged = new SimpleBooleanProperty(false);
    private BooleanProperty listChanged = new SimpleBooleanProperty(false);
    private BooleanProperty keyWordsChanged = new SimpleBooleanProperty(false);

    /**
     * The no-argument constructor of {@code SimpleUser}
     */
    public SimpleUser() {
        this(Default.VALUE, Default.STRING, Default.STRING, Default.VALUE, Default.STRING, Default.VALUE, Default.KEYWORDS);
    }

    /**
     * The constructor of {@code SimpleUser}
     *
     * @param ownStatus status of the {@link User}
     * @param comment comment about the {@link Enterprise.data.intface.Creation}
     * @param rating rating of the {@code Creation}
     * @param list name of the list assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords search keyWords for the {@code Creation}
     */
    public SimpleUser(String ownStatus, String comment, int rating, String list, int processedPortion, String keyWords) {
        this(Default.VALUE, ownStatus, comment, rating, list, processedPortion, keyWords);
    }

    /**
     * The constructor of {@code SimpleUser}
     *
     * @param id database Id of this {@code SimpleUser}
     * @param ownStatus status of the {@link User}
     * @param comment comment about the {@link Enterprise.data.intface.Creation}
     * @param rating rating of the {@code Creation}
     * @param list name of the list assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords search keyWords for the {@code Creation}
     */
    public SimpleUser(int id, String ownStatus, String comment, int rating, String list, int processedPortion, String keyWords) {
        this.ownStatus.set(ownStatus);
        this.comment.set(comment);
        this.rating.set(rating);
        this.list.set(list);
        this.processedPortion.set(processedPortion);
        this.keyWords.set(keyWords);

        // TODO: 20.08.2017 replace this through database id incrementation

        if (id == 0) {
            userId = idCounter;
            idCounter++;
        } else {
            userId = id;
            if (idCounter <= id) {
                idCounter = id;
                idCounter++;
            }
        }
        validateState();
        invalidListeners();
        bindUpdated();
    }

    /**
     * validates the correctness of the State of this {@code SimpleUser}
     *
     * @throws IllegalArgumentException if any dataFields are invalid, meaning null or integers smaller than zero
     */
    private void validateState() {
        String message = "";
        if (userId < 0) {
            message = message + "userId is invalid: " + userId + ",";
        }
        if (ownStatus.get() == null) {
            message = message + "ownStatus is null, ";
        }
        if (comment.get() == null) {
            message = message + "comment is null, ";
        }
        //rating numbers are between 0 and 10, including both
        if (rating.get() < 0 || rating.get() > 10) {
            message = message + "rating is invalid: " + rating.get() +", ";
        }
        if (list.get() == null) {
            message = message + "list is null, ";
        }
        if (processedPortion.get() < 0) {
            message = message + "processedPortion is invalid: " + processedPortion.get() + ", ";
        }
        if (keyWords.get() == null) {
            message = message + "keyWords is null";
        }
        if (!message.isEmpty()) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            logger.log(Level.WARNING, "object creation failed", exception);
            throw exception;
        }
    }

    @Override
    protected void bindUpdated() {
        updated.bind(ownStatusChanged.or(commentChanged).or(ratingChanged).or(processedPortionChanged).or(listChanged).or(keyWordsChanged));
    }

    /**
     * adds invalidListeners to dataField-Properties, set stateChanged-Flags to true, if state has changed
     */
    private void invalidListeners() {
        ownStatus.addListener(observable -> ownStatusChanged.set(true));
        comment.addListener(observable -> commentChanged.set(true));
        rating.addListener(observable -> ratingChanged.set(true));
        processedPortion.addListener(observable -> processedPortionChanged.set(true));
        list.addListener(observable -> listChanged.set(true));
        keyWords.addListener(observable -> keyWordsChanged.set(true));
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
    public void setUpdated() {
        listChanged.set(false);
        processedPortionChanged.set(false);
        ratingChanged.set(false);
        commentChanged.set(false);
        ownStatusChanged.set(false);
        keyWordsChanged.set(false);
    }

    @Override
    public int getId() {
        return userId;
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id < 1) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.userId = id;
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
    public String getList() {
        return list.get();
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
    public StringProperty listProperty() {
        return list;
    }

    @Override
    public boolean isOwnStatusChanged() {
        return ownStatusChanged.get();
    }

    @Override
    public boolean isCommentChanged() {
        return commentChanged.get();
    }

    @Override
    public boolean isRatingChanged() {
        return ratingChanged.get();
    }

    @Override
    public boolean isProcessedPortionChanged() {
        return processedPortionChanged.get();
    }

    @Override
    public boolean isListChanged() {
        return listChanged.get();
    }

    @Override
    public boolean isKeyWordsChanged() {
        return keyWordsChanged.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleUser)) return false;

        SimpleUser that = (SimpleUser) o;

        return (ownStatus != null ? ownStatus.get().equals(that.ownStatus.get()) :
                that.ownStatus == null) && (comment != null ? comment.get().equals(that.comment.get()) :

                that.comment == null) && (rating != null ? rating.get() == that.rating.get() :
                that.rating == null) && (processedPortion != null ? processedPortion.get() == that.processedPortion.get() :

                that.processedPortion == null) && (list != null ? list.get().equals(that.list.get()) :
                that.list == null) && (keyWords != null ? keyWords.get().equals(that.keyWords.get()) :

                that.keyWords == null);
    }

    @Override
    public int hashCode() {
        int result = ownStatus != null ? ownStatus.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (processedPortion != null ? processedPortion.hashCode() : 0);
        result = 31 * result + (list != null ? list.hashCode() : 0);
        result = 31 * result + (keyWords != null ? keyWords.hashCode() : 0);
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
            compared = list.get().compareTo(o.getList());
        }
        if (compared == 0) {
            compared = keyWords.get().compareTo(o.getKeyWords());
        }
        return compared;
    }
}
