package scrape.sources.novel.toc.structure;

/**
 *
 */
public abstract class CreationContent extends NodeImpl {
    private final boolean isExtra;
    private final double index;

    protected CreationContent(String title, String type, boolean isExtra, double index) {
        super(title, type);
        this.isExtra = isExtra;
        this.index = index;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public double getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return super.toString() + ", isExtra=" + isExtra + ", index=" + index;
    }
}
