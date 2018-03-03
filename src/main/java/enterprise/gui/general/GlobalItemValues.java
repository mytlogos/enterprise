package enterprise.gui.general;

import tools.SetList;

import java.util.Collection;
import java.util.List;

/**
 * // TODO: 25.08.2017 do the javadoc
 */
public class GlobalItemValues {
    private static final GlobalItemValues instance = new GlobalItemValues();
    private final List<String> creatorNames = new SetList<>();
    private final List<String> sortCreators = new SetList<>();
    private final List<String> collections = new SetList<>();
    private final List<String> ownStatus = new SetList<>();
    private final List<String> workStatus = new SetList<>();
    private final List<String> translators = new SetList<>();
    private final List<String> listNames = new SetList<>();

    private GlobalItemValues() {
        if (instance != null) {
            throw new IllegalStateException();
        }
    }

    public static GlobalItemValues getInstance() {
        return instance;
    }

    public List<String> getListNames() {
        return listNames;
    }

    public List<String> getTranslators() {
        return translators;
    }

    public List<String> getCreatorNames() {
        return creatorNames;
    }

    public List<String> getSortCreators() {
        return sortCreators;
    }

    public List<String> getCollections() {
        return collections;
    }

    public List<String> getOwnStatus() {
        return ownStatus;
    }

    public List<String> getWorkStatus() {
        return workStatus;
    }

    public boolean addTranslator(String translator) {
        return this.translators.add(translator);
    }

    public boolean addTranslator(Collection<String> translators) {
        return this.translators.addAll(translators);
    }

    public boolean addCreator(String author) {
        return this.creatorNames.add(author);
    }

    public boolean addCreator(Collection<String> author) {
        return this.creatorNames.addAll(author);
    }

    public boolean addSortAuthor(String sortAuthor) {
        return this.sortCreators.add(sortAuthor);
    }

    public boolean addSortAuthor(Collection<String> sortAuthor) {
        return this.sortCreators.addAll(sortAuthor);
    }

    public boolean addCollection(String series) {
        return this.collections.add(series);
    }

    public boolean addCollection(Collection<String> series) {
        return this.collections.addAll(series);
    }

    public boolean addOwnStatus(String ownStatus) {
        return this.ownStatus.add(ownStatus);
    }

    public boolean addOwnStatus(Collection<String> ownStatus) {
        return this.ownStatus.addAll(ownStatus);
    }

    public boolean addCreatorStatus(String authorStatus) {
        return this.workStatus.add(authorStatus);
    }

    public boolean addCreatorStatus(Collection<String> authorStatus) {
        return this.workStatus.addAll(authorStatus);
    }

    public boolean removeCreator(String author) {
        return this.creatorNames.remove(author);
    }

    public boolean removeCreator(Collection<String> author) {
        return this.creatorNames.removeAll(author);
    }

    public boolean removeSortAuthor(String sortAuthor) {
        return this.sortCreators.remove(sortAuthor);
    }

    public boolean removeSortAuthor(Collection<String> sortAuthor) {
        return this.sortCreators.removeAll(sortAuthor);
    }

    public boolean removeCollection(String series) {
        return this.collections.remove(series);
    }

    public boolean removeCollection(Collection<String> series) {
        return this.collections.removeAll(series);
    }

    public boolean removeOwnStatus(String ownStatus) {
        return this.ownStatus.remove(ownStatus);
    }

    public boolean removeOwnStatus(Collection<String> ownStatus) {
        return this.ownStatus.removeAll(ownStatus);
    }

    public boolean removeCreatorStatus(String authorStatus) {
        return this.workStatus.remove(authorStatus);
    }

    public boolean removeCreatorStatus(Collection<String> authorStatus) {
        return this.workStatus.removeAll(authorStatus);
    }

    public boolean removeTranslator(String translator) {
        return this.translators.remove(translator);
    }

    public boolean removeTranslator(Collection<String> translators) {
        return this.translators.removeAll(translators);
    }
}
