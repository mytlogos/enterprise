package web.scorer;

/**
 *
 */
public class SideBarScorer extends StandardScorer {

    public SideBarScorer() {
        //positive characteristics
        addTags(true, "aside");
        addAttributeValue(true, "class", "side", "secondary", "tertiary", "widget-area");
        addAttributeValue(true, "id", "side", "secondary", "tertiary", "widget-area");
        addText(true, "archive");
        addParentBonusAttributeValue(true, "class", "widget", "archive");
        addParentBonusAttributeValue(true, "id", "widget", "archive");
        //negative characteristics
        addFastFailTag("head", "html", "body", "td", "tr", "th", "tbody", "table", "header", "nav", "main", "article", "footer");

        final String[] negAttrValues = {
                "main", "content", "foot", "nav", "head",
                "btn", "page", "post", "article",
                "pager", "entry", "login", "archive",
                "credit", "copyright", "pagination", "pager",
                "subscri", "meta", "cookie", "follow",
                "next", "previous", "category", "likes",
                "archive", "facebook", "twitter", "disqus",
                "discord", "search"
        };

        addAttributeValue(false, "role", "nav");
        addAttributeValue(false, "class", negAttrValues);
        addAttributeValue(false, "id", negAttrValues);
        addText(false, "copyright", "home");
    }

    public SideBarScorer(Scorer scorer) {
        super(scorer);
    }

    @Override
    public String getScoreKey() {
        return "sidebar";
    }

    @Override
    public boolean scoreHead(ElementWrapper elementWrapper, ScorerValue value) {
        if (super.scoreHead(elementWrapper, value)) {
            return true;
        }

        if (elementWrapper.linkSize() < 1) {
            value.addNonStackBoni(-5);
        }

        return false;
    }

    @Override
    public void scoreTail(ElementWrapper elementWrapper, ScorerValue value) {
    }
}
