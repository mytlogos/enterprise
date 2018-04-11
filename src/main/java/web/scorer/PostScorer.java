package web.scorer;

/**
 *
 */
public class PostScorer extends StandardScorer {

    public PostScorer() {
        //positive characteristics
        addAttributeValue(true, "class", "post", "article");
        addAttributeValue(true, "id", "post", "article");
        addTags(true, "article");
        //negative characteristics
        addTags(false, "footer", "body", "header", "nav", "home", "html", "head", "aside", "main");
        addAttributeValue(false, "role", "nav");
        addAttributeValue(false, "class", "main", "header", "nav", "footer", "side");
        addAttributeValue(false, "id", "main", "header", "nav", "footer", "side");
        addText(false, "copyright");
    }


    @Override

    public String getScoreKey() {
        return "post";
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        if (super.scoreHead(elementWrapper, value)) {
            return true;
        }

        double uniqueness = elementWrapper.getUniqueness();
        value.addNonStackBoni(uniqueness);

        return false;
    }
}
