package scrape.sources.toc.structure.novel;

import scrape.sources.toc.structure.Leaf;
import scrape.sources.toc.structure.intface.SubPortion;

/**
 *
 */
public class SubChapter extends Leaf implements SubPortion {

    public SubChapter(String title) {
        this(title, false, 0, 0);
    }

    public SubChapter(String title, boolean isExtra, double index, double partIndex) {
        super(title, "Part", isExtra, index, partIndex);
    }

    @Override
    public Chapter getParent() {
        return (Chapter) super.getParent();
    }
}
