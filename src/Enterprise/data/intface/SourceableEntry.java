package Enterprise.data.intface;

/**
 *  DMO of a sourceable CreationEntry.
 *  It is of no english or german origin and content can be scraped over the internet.
 */
public interface SourceableEntry extends CreationEntry {

    /**
     * gets the Sourceable of this {@code SourceableEntry}
     *
     * @return sourceable - a {@code Sourceable}, not {@code null}
     */
    Sourceable getSourceable();

    /**
     * // TODO: 27.08.2017 do the doc
     * @return
     */
    boolean readySourceableRemoval();
}
