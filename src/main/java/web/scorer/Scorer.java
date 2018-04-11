package web.scorer;

/**
 * Scorer which evaluates {@link ElementWrapper} with in a
 * heuristic manner.
 * <p>
 * Used by {@link web.finder.ScoreVisitor} to evaluate each
 * {@code ElementWrapper} of a tree.
 */
public interface Scorer {

    /**
     * @param positive
     * @param attr
     * @param values
     * @return
     */
    void addAttributeValue(boolean positive, String attr, String... values);

    /**
     * @param positive
     * @param attr
     * @param values
     * @return
     */
    void addParentBonusAttributeValue(boolean positive, String attr, String... values);

    /**
     * @param positive
     * @param txt
     * @return
     */
    void addText(boolean positive, String... txt);

    /**
     * @param positive
     * @param tags
     * @return
     */
    void addTags(boolean positive, String... tags);

    /**
     * Evaluates the ElementWrapper in the {@link web.finder.ScoreVisitor#head(ElementWrapper, int)}
     * method.
     * <p>
     * Returns true if it exited prematurely due to a fast-fail.
     *
     * @param elementWrapper wrapper to evaluate
     * @param value          value to store the evaluation in
     * @return true if it fast-failed
     */
    boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value);

    /**
     * @return
     */
    String getScoreKey();

    /**
     * Evaluates the ElementWrapper in the {@link web.finder.ScoreVisitor#tail(ElementWrapper, int)}
     * method.
     *
     * @param elementWrapper
     * @param value
     */
    void scoreTail(ElementWrapper elementWrapper, ScorerValue value);

    /**
     * Tags to fast fail the {@link #scoreHead(ElementWrapper, ScorerValue)} method.
     *
     * @param tags
     * @return
     */
    void addFastFailTag(String... tags);
}
