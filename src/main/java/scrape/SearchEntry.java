package scrape;

import Enterprise.data.intface.Creation;
import scrape.sources.Source;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SearchEntry implements Comparable<SearchEntry> {
    private Creation creationKey;
    private Source source;
    private List<String> keyWords;

    public SearchEntry(Creation creation, Source source, List<String> keyWords) {
        this.creationKey = creation;
        this.source = source;
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

    public Creation getCreationKey() {
        return creationKey;
    }

    public Source getSource() {
        return source;
    }

    public List<String> getKeyWords() {
        return Collections.unmodifiableList(keyWords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchEntry that = (SearchEntry) o;

        return (getCreationKey() != null ?
                getCreationKey().equals(that.getCreationKey()) :
                that.getCreationKey() == null) &&
                getSource().equals(that.getSource()) &&
                getKeyWords().equals(that.getKeyWords());
    }

    @Override
    public int hashCode() {
        int result = getCreationKey() != null ? getCreationKey().hashCode() : 0;
        result = 31 * result + getSource().hashCode();
        result = 31 * result + getKeyWords().hashCode();
        return result;
    }

    @Override
    public int compareTo(SearchEntry o) {
        if (o == null) {
            return -1;
        }
        if (this == o) {
            return 0;
        }
        int compared = getSource().compareTo(o.getSource());

        if (compared == 0) {
            compared = getCreationKey().compareTo(o.getCreationKey());
        }
        return compared;
    }
}
