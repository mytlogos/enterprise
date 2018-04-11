package web.scorer;

/**
 *
 */
public class ScorerValue {
    private final String scorerKey;
    private double elementWeight;
    private double boni;
    private double childrenWeight;
    private double parentBoni;
    private double nonStackingBoni;

    public ScorerValue(String scorerKey) {
        this.scorerKey = scorerKey;
    }

    public String getScorerKey() {
        return scorerKey;
    }

    public void addChildrenWeight(ScorerValue value) {
        this.childrenWeight += (value.getStackingScore() * 0.25);
        this.childrenWeight += value.parentBoni;
    }

    private double getStackingScore() {
        return elementWeight + boni + childrenWeight;
    }

    public void addNonStackBoni(double bonus) {
        nonStackingBoni += bonus;
    }

    public void addParentBoni(double bonus) {
        this.parentBoni += bonus;
    }

    public double getBoni() {
        return boni;
    }

    public void addStackBoni(double boni) {
        this.boni += boni;
    }

    public double getChildrenWeight() {
        return childrenWeight;
    }

    public void setChildrenWeight(double childrenWeight) {
        this.childrenWeight = childrenWeight;
    }

    public double getElementWeight() {
        return elementWeight;
    }

    public void setElementWeight(double elementWeight) {
        this.elementWeight = elementWeight;
    }

    public double getScore() {
        return getStackingScore() + nonStackingBoni;
    }
}
