package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.DataTable;
import Enterprise.data.intface.Table;
import Enterprise.data.intface.User;
import Enterprise.misc.DataAccess;
import Enterprise.misc.KeyWordList;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.*;

import java.util.List;

/**
 * Implementation of User
 * @see User
 */
@DataAccess(daoClass = "UserTable")
public class SimpleUser extends EnterpriseEntry implements User{
    private int userId;
    private static int idCounter = 1;

    @SQLUpdate(stateGet = "isOwnStatusChanged", valueGet = "getOwnStatus", columnField = "ownStatusC")
    private StringProperty ownStatus = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isCommentChanged", valueGet = "getComment", columnField = "commentC")
    private StringProperty comment = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isRatingChanged", valueGet = "getRating", columnField = "ratingC")
    private IntegerProperty rating = new SimpleIntegerProperty();

    @SQLUpdate(stateGet = "isProcessedPortionChanged", valueGet = "getProcessedPortion", columnField = "processedPortionC")
    private IntegerProperty processedPortion = new SimpleIntegerProperty();

    @SQLUpdate(stateGet = "isListNameChanged", valueGet = "getListName", columnField = "listC")
    private StringProperty listName = new SimpleStringProperty();

    @SQLUpdate(stateGet = "isKeyWordsChanged", valueGet = "getKeyWords", columnField = "keyWordsC")
    private StringProperty keyWords = new SimpleStringProperty();

    private List<String> keyWordList = new KeyWordList();


    private BooleanProperty ownStatusChanged = new SimpleBooleanProperty(false);
    private BooleanProperty commentChanged = new SimpleBooleanProperty(false);
    private BooleanProperty ratingChanged = new SimpleBooleanProperty(false);
    private BooleanProperty processedPortionChanged = new SimpleBooleanProperty(false);
    private BooleanProperty listNameChanged = new SimpleBooleanProperty(false);
    private BooleanProperty keyWordsChanged = new SimpleBooleanProperty(false);

    /**
     * The no-argument constructor of {@code SimpleUser}
     */
    public SimpleUser() {
        this(Default.VALUE, Default.STRING, Default.STRING, Default.VALUE, Default.LIST, Default.VALUE, Default.KEYWORDS);
    }

    /**
     * The constructor of {@code SimpleUser}
     *
     * @param ownStatus status of the {@link User}
     * @param comment comment about the {@link Enterprise.data.intface.Creation}
     * @param rating rating of the {@code Creation}
     * @param list name of the listName assigned to the {@code Creation}
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
     * @param listName name of the listName assigned to the {@code Creation}
     * @param processedPortion processed Portion of the {@code Creation}
     * @param keyWords search keyWords for the {@code Creation}
     */
    public SimpleUser(int id, String ownStatus, String comment, int rating, String listName, int processedPortion, String keyWords) {
        this.ownStatus.set(ownStatus);
        this.comment.set(comment);
        this.rating.set(rating);
        this.listName.set(listName);
        this.processedPortion.set(processedPortion);
        this.keyWords.set(keyWords);
        userId = id;

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
    protected void bindUpdated() {
        updated.bind(ownStatusChanged.or(commentChanged).or(ratingChanged).or(processedPortionChanged).or(listNameChanged).or(keyWordsChanged));
    }

    /**
     * adds invalidListeners to dataField-Properties, set stateChanged-Flags to true, if state has changed
     */
    private void invalidListeners() {
        ownStatus.addListener(observable -> ownStatusChanged.set(true));
        comment.addListener(observable -> commentChanged.set(true));
        rating.addListener(observable -> ratingChanged.set(true));
        processedPortion.addListener(observable -> processedPortionChanged.set(true));
        listName.addListener(observable -> listNameChanged.set(true));
        keyWords.addListener(observable -> {
            keyWordsChanged.set(true);

            String keyWords = getKeyWords();
            if (keyWords == null) {
                keyWords = "";
            }

            List<String> stringList = new KeyWordList();
            for (String string : keyWords.split("[\\s,]")) {
                if (!string.isEmpty() && !stringList.contains(string)) {
                    stringList.add(string);
                }
            }
            //removes all 'old' keyWords, adds only 'new' keyWords
            keyWordList.retainAll(stringList);
            stringList.removeAll(keyWordList);
            keyWordList.addAll(stringList);
        });
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
        listNameChanged.set(false);
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
        if (id <= 0) {
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
    public String getListName() {
        return listName.get();
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
    public void setListName(String listName) {
        this.listName.set(listName);
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
    public StringProperty listNameProperty() {
        return listName;
    }

    @Override
    public boolean isKeyWordsChanged() {
        return keyWordsChanged.get();
    }

    @Override
    public boolean isListNameChanged() {
        return listNameChanged.get();
    }

    @Override
    public List<String> getKeyWordList() {
        return keyWordList;
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

                that.processedPortion == null) && (listName != null ? listName.get().equals(that.listName.get()) :
                that.listName == null) && (keyWords != null ? keyWords.get().equals(that.keyWords.get()) :

                that.keyWords == null);
    }

    @Override
    public int hashCode() {
        int result = ownStatus != null ? ownStatus.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (processedPortion != null ? processedPortion.hashCode() : 0);
        result = 31 * result + (listName != null ? listName.hashCode() : 0);
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
            compared = listName.get().compareTo(o.getListName());
        }
        if (compared == 0) {
            compared = keyWords.get().compareTo(o.getKeyWords());
        }
        return compared;
    }
}
