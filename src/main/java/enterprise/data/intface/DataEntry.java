package enterprise.data.intface;

import gorgon.external.GorgonEntry;

/**
 * Basic interface for persisting objects in a database.
 */
public interface DataEntry extends GorgonEntry {

    int getId();

    void setId(int id);

}
