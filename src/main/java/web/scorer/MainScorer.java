package web.scorer;

/**
 *
 */
public class MainScorer extends StandardScorer {

    public MainScorer() {
        //positive characteristics
        addAttributeValue(true, "class", "main", "content", "contain");
        addAttributeValue(true, "id", "main", "content", "contain");
        addTags(true, "main");
        //negative characteristics
        addTags(false, "footer", "body", "header", "nav", "html", "head", "aside");
        addAttributeValue(false, "role", "nav");

        final String[] negAttrValues = {
                "foot", "nav", "side", "btn", "post",
                "article", "pager", "entry", "login",
                "archive", "credit", "copyright", "pagination",
                "menu", "collapsed", "dropdown", "child",
                "widget", "subscri", "item", "cookie",
                "meta", "follow", "next", "previous",
                "category", "likes", "facebook", "twitter",
                "disqus", "discord",
        };

        addAttributeValue(false, "class", negAttrValues);
        addAttributeValue(false, "id", negAttrValues);
        addText(false, "copyright");
    }

    public MainScorer(Scorer scorer) {
        super(scorer);
    }

    @Override
    public String getScoreKey() {
        return "main";
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        if (super.scoreHead(elementWrapper, value)) {
            return true;
        }

        int words = elementWrapper.getWords();

        int wordScore = 0;
        if (words <= 1) {
            wordScore = -1;
        } else {
            int rootWords = elementWrapper.getRoot().getWords();

            int percLimit = rootWords < 500 ? 20 : 60;

            if (elementWrapper.getWordPercentage() > percLimit) {
                wordScore++;
            } else {
                wordScore -= 5;
            }
        }

        int linkElementDensityScore = elementWrapper.getLinkElementDensity() > 0.4 ? -5 : 1;

        double uniqueness = elementWrapper.getUniqueness();

        uniqueness = 3 / uniqueness;

        value.addNonStackBoni(uniqueness);
        value.addNonStackBoni(linkElementDensityScore);
        value.addNonStackBoni(wordScore);

        return true;
    }

    @Override
    public void scoreTail(ElementWrapper elementWrapper, ScorerValue value) {
    }
}
