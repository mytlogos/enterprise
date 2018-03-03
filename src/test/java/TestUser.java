import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
@DataAccess(UserDao.class)
public class TestUser implements GorgonEntry {
    private StringProperty ownStatus = new SimpleStringProperty();
    private StringProperty comment = new SimpleStringProperty();
    private IntegerProperty rating = new SimpleIntegerProperty();
    private IntegerProperty processedPortion = new SimpleIntegerProperty();
    private StringProperty listName = new SimpleStringProperty();
    private StringProperty keyWords = new SimpleStringProperty();


    TestUser(String ownStatus, String comment, Integer rating, Integer processedPortion, String listName, String keyWords) {
        this.ownStatus.set(ownStatus);
        this.comment.set(comment);
        this.rating.set(rating);
        this.processedPortion.set(processedPortion);
        this.listName.set(listName);
        this.keyWords.set(keyWords);
    }

    @Override
    public int hashCode() {
        int result = getOwnStatus().hashCode();
        result = 31 * result + getComment().hashCode();
        result = 31 * result + getRating();
        result = 31 * result + getProcessedPortion();
        result = 31 * result + getListName().hashCode();
        result = 31 * result + getKeyWords().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestUser testUser = (TestUser) o;

        if (!getOwnStatus().equals(testUser.getOwnStatus())) return false;
        if (!getComment().equals(testUser.getComment())) return false;
        if (!(getRating() == testUser.getRating())) return false;
        if (!(getProcessedPortion() == testUser.getProcessedPortion())) return false;
        if (!getListName().equals(testUser.getListName())) return false;
        return getKeyWords().equals(testUser.getKeyWords());
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "ownStatus=" + ownStatus +
                ", comment=" + comment +
                ", rating=" + rating +
                ", processedPortion=" + processedPortion +
                ", listName=" + listName +
                ", keyWords=" + keyWords +
                '}';
    }

    public String getOwnStatus() {
        return ownStatus.get();
    }

    public String getComment() {
        return comment.get();
    }

    public int getRating() {
        return rating.get();
    }

    public int getProcessedPortion() {
        return processedPortion.get();
    }

    public String getListName() {
        return listName.get();
    }

    public String getKeyWords() {
        return keyWords.get();
    }

    @Override
    public int compareTo(GorgonEntry entry) {
        if (entry == null) return -1;
        if (entry == this) return 0;
        if (!(entry instanceof TestUser)) return -1;

        TestUser o = (TestUser) entry;

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
