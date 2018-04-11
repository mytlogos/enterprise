package web.scorer;

/**
 *
 */
public class ArticleScorer extends StandardScorer {
    @Override
    public String getScoreKey() {
        return "article";
    }
}
