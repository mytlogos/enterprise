package scrape;

import enterprise.data.intface.Creation;
import scrape.sources.Source;
import scrape.sources.posts.PostSearchEntry;

/**
 *
 */
public class ChapterSearchEntry extends SearchEntry implements Comparable<ChapterSearchEntry> {

    public ChapterSearchEntry(Creation creation, Source source) {
        super(creation, source);
        validateState();
    }

    private void validateState() {
        String message = "";
        if (source == null) {
            message = message + "comment is null, ";
        }
        if (creationKey == null) {
            message = message + "creation is null ";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public int hashCode() {
        int result = getCreation() != null ? getCreation().hashCode() : 0;
        result = 31 * result + getSource().hashCode();
        result = 31 * result + getCreation().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostSearchEntry that = (PostSearchEntry) o;

        return (getCreation() != null ?
                getCreation().equals(that.getCreation()) :
                that.getCreation() == null) &&
                getSource().equals(that.getSource());
    }

    @Override
    public int compareTo(ChapterSearchEntry o) {
        if (o == null) {
            return -1;
        }
        if (this == o) {
            return 0;
        }
        int compared = getSource().compareTo(o.getSource());

        if (compared == 0) {
            compared = getCreation().compareTo(o.getCreation());
        }
        return compared;
    }
}
