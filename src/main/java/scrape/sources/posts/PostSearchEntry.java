package scrape.sources.posts;

import enterprise.data.intface.Creation;
import scrape.SearchEntry;
import scrape.sources.Source;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PostSearchEntry extends SearchEntry implements Comparable<PostSearchEntry> {
    private final List<String> keyWords;

    public PostSearchEntry(Creation creation, Source source, List<String> keyWords) {
        super(creation, source);
        this.keyWords = keyWords;
        validateState();
    }

    private void validateState() {
        String message = "";
        if (source == null) {
            message = message + "comment is null, ";
        }
        if (keyWords == null) {
            message = message + "keyWordList is null ";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public int hashCode() {
        int result = getCreation() != null ? getCreation().hashCode() : 0;
        result = 31 * result + getSource().hashCode();
        result = 31 * result + getKeyWords().hashCode();
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
                getSource().equals(that.getSource()) &&
                getKeyWords().equals(that.getKeyWords());
    }

    public List<String> getKeyWords() {
        return Collections.unmodifiableList(keyWords);
    }

    @Override
    public int compareTo(PostSearchEntry o) {
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
