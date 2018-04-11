package web.scorer;

/**
 *
 */
public class ArchiveScorer extends StandardScorer {

    public ArchiveScorer() {
        //positive characteristics
        addAttributeValue(true, "class", "archive");
        addAttributeValue(true, "id", "archive");
        addAttributeValue(true, "role", "archive");
        addText(true, "update");
        //negative characteristics
        addTags(false, "main", "footer", "body", "header", "nav", "home", "html", "head");
        addAttributeValue(false, "role", "nav");
        addAttributeValue(false, "class", "main", "content", "header", "nav", "footer");
        addAttributeValue(false, "id", "main", "content", "header", "nav", "footer");
        addText(false, "copyright");
    }

    public ArchiveScorer(Scorer scorer) {
        super(scorer);
    }

    @Override
    public String getScoreKey() {
        return "archive";
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        if (super.scoreHead(elementWrapper, value)) {
            return true;
        }

        double uniqueness = elementWrapper.getUniqueness();

        uniqueness = 1 / uniqueness;

        value.addNonStackBoni(uniqueness);

        return false;
    }

    @Override
    public void scoreTail(ElementWrapper elementWrapper, ScorerValue value) {
    }
}
