package scrape.sources.toc.intface;

import java.util.List;

/**
 *
 */
public interface Portion extends Node {
    void add(SubPortion subPortion);

    void addAll(List<? extends SubPortion> subPortions);

    @Override
    List<? extends SubPortion> getChildren();

    void remove(SubPortion subPortion);
}
