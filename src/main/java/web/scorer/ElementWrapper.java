package web.scorer;

import org.jsoup.nodes.Element;

import java.util.*;

/**
 *
 */
public class ElementWrapper {
    private ElementWrapper parent;
    private ElementWrapper root;
    private List<ElementWrapper> children = new ArrayList<>();
    private int siblingIndex;

    private Element element;
    private double linkWordDensity;
    private double linkElementDensity;
    private double blockElementDensity;
    private int words;
    private double wordDensity;
    private double wordPercentage;
    private double uniqueness;
    private double depthIndexScore;

    private Map<String, ScorerValue> scoreMap = new HashMap<>();
    private int linkSize;
    private double linkPercentage;
    private double linkTextNodeDensity;
    private int overLimitNodes;
    private int textNodeSize;

    public ElementWrapper(Element element) {
        this.element = element;

        if (parent != null) {
            parent.children.add(this);
        } else {
            root = this;
        }
    }

    public double getDepthIndexScore() {
        return depthIndexScore;
    }

    public void setDepthIndexScore(double depthIndexScore) {
        this.depthIndexScore = depthIndexScore;
    }

    public double getLinkElementDensity() {
        return linkElementDensity;
    }

    public void setLinkElementDensity(double linkElementDensity) {
        this.linkElementDensity = linkElementDensity;
    }

    public Map<String, ScorerValue> getScoreMap() {
        return Collections.unmodifiableMap(scoreMap);
    }

    public void appendChildren(Collection<ElementWrapper> elementWrappers) {
        for (ElementWrapper elementWrapper : new ArrayList<>(elementWrappers)) {
            appendChild(elementWrapper);
        }
    }

    public void appendChild(ElementWrapper elementWrapper) {
        ElementWrapper parent = elementWrapper.parent();

        if (parent != null) {
            parent.removeChild(elementWrapper);
        }
        elementWrapper.root = root;
        elementWrapper.parent = this;
        elementWrapper.siblingIndex = children.size();
        this.children.add(elementWrapper);
    }

    public ElementWrapper parent() {
        return parent;
    }

    public void removeChild(ElementWrapper elementWrapper) {
        int siblingIndex = elementWrapper.siblingIndex;
        children.remove(elementWrapper);
        elementWrapper.root = null;
        elementWrapper.parent = null;

        for (int i = siblingIndex; i < children.size(); i++) {
            ElementWrapper child = children.get(i);
            child.siblingIndex--;
        }
    }

    public double getWordPercentage() {
        return wordPercentage;
    }

    public void setWordPercentage(double wordPercentage) {
        this.wordPercentage = wordPercentage;
    }

    public List<ElementWrapper> siblings() {
        return parent == null ? Collections.emptyList() : parent.children;
    }

    public void setHeadScore(Scorer scorer) {
        ScorerValue value = scoreMap.computeIfAbsent(scorer.getScoreKey(), ScorerValue::new);
        scorer.scoreHead(this, value);
    }

    public void setTailScore(Scorer scorer) {
        ScorerValue value = scoreMap.computeIfAbsent(scorer.getScoreKey(), ScorerValue::new);
        scorer.scoreTail(this, value);
    }

    public ScorerValue getScorerValue(String key) {
        return scoreMap.get(key);
    }

    public ScorerValue getMaxScore() {
        ScorerValue max = null;
        for (ScorerValue value : scoreMap.values()) {
            if (max == null) {
                max = value;
            } else if (max.getScore() < value.getScore()) {
                max = value;
            }
        }
        return max;
    }

    public ScorerValue getScorerValue(Scorer scorer) {
        return scoreMap.get(scorer.getScoreKey());
    }

    public Element getElement() {
        return element;
    }

    public double getLinkWordDensity() {
        return linkWordDensity;
    }

    public void setLinkWordDensity(double linkWordDensity) {
        this.linkWordDensity = linkWordDensity;
    }

    public double getBlockElementDensity() {
        return blockElementDensity;
    }

    public void setBlockElementDensity(double blockElementDensity) {
        this.blockElementDensity = blockElementDensity;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public double getWordDensity() {
        return wordDensity;
    }

    public void setWordDensity(double wordDensity) {
        this.wordDensity = wordDensity;
    }

    public ElementWrapper getRoot() {
        return root;
    }

    public double getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(double uniqueness) {
        this.uniqueness = uniqueness;
    }

    public int childNodeSize() {
        return children.size();
    }

    public ElementWrapper childNode(int i) {
        return children.get(i);
    }

    public ElementWrapper nextSibling() {
        if (parent == null) {
            return null;
        }
        List<ElementWrapper> children = parent.getChildren();

        int nextSibIndex = siblingIndex + 1;
        return nextSibIndex < children.size() ? children.get(nextSibIndex) : null;
    }

    public List<ElementWrapper> getChildren() {
        return children;
    }

    public List<ElementWrapper> parents() {
        ElementWrapper parent = parent();

        List<ElementWrapper> parents = new ArrayList<>();

        while (parent != null) {
            parents.add(parent);
            parent = parent.parent();
        }
        return parents;
    }

    public int getSiblingIndex() {
        return siblingIndex;
    }

    @Override
    public String toString() {
        return "ElementWrapper{" +
                "siblingIndex=" + siblingIndex +
                ", element=" + getElementName() +
                ", linkWordDensity=" + linkWordDensity +
                ", linkElementDensity=" + linkElementDensity +
                ", blockElementDensity=" + blockElementDensity +
                ", words=" + words +
                ", wordDensity=" + wordDensity +
                ", uniqueness=" + uniqueness +
                ", scoreMap=" + scoreMap +
                ", parent=" + (parent == null ? null : parent.getElementName()) +
                ", children=" + children.size() +
                '}';
    }

    public String getElementName() {
        return element.tagName() + " id=" + element.id() + " class=" + element.className();
    }

    public void setLinkSize(int linkSize) {
        this.linkSize = linkSize;
    }

    public int linkSize() {
        return linkSize;
    }

    public double getLinkPercentage() {
        return linkPercentage;
    }

    public void setLinkPercentage(double linkPercentage) {
        this.linkPercentage = linkPercentage;
    }

    public void setLinkTextDensity(double linkTextNodeDensity) {
        this.linkTextNodeDensity = linkTextNodeDensity;
    }

    public double getLinkTextNodeDensity() {
        return linkTextNodeDensity;
    }

    /**
     * Sets the number of TextNodes which are over
     * a arbitrary length limit.
     *
     * @param overLimitNodes number of textNodes over limit
     * @see web.finder.ScoreVisitor#collectTextNodes(Element, List)
     */
    public void setOverLimitTextNodes(int overLimitNodes) {
        this.overLimitNodes = overLimitNodes;
    }

    public int getTextNodeSize() {
        return textNodeSize;
    }

    public void setTextNodeSize(int textNodeSize) {
        this.textNodeSize = textNodeSize;
    }

    public int getOverLimitNodes() {
        return overLimitNodes;
    }
}
