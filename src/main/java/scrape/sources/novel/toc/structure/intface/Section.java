package scrape.sources.novel.toc.structure.intface;

import java.util.List;

/**
 *
 */
public interface Section extends Node {
    void add(Portion branch);

    void addAll(List<? extends Portion> portions);
}
