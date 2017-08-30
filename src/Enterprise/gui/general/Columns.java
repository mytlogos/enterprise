package Enterprise.gui.general;

import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;

/**
 * This class provides the column names for the
 * graphical user interface of the corresponding
 * {@link Module}.
 */
public class Columns {

    /**
     * Gets the name of the title column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getTitle(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.TITLE :
                module == BasicModules.BOOK ? BookColumns.TITLE :
                        module == BasicModules.MANGA ? MangaColumns.TITLE :
                                module == BasicModules.NOVEL ? NovelColumns.TITLE :
                                        module == BasicModules.SERIES ? SeriesColumns.TITLE :
                                                "";
    }

    /**
     * Gets the name of the series column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getSeries(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.SERIES :
                module == BasicModules.BOOK ? BookColumns.SERIES :
                        module == BasicModules.MANGA ? MangaColumns.SERIES :
                                module == BasicModules.NOVEL ? NovelColumns.SERIES :
                                        module == BasicModules.SERIES ? SeriesColumns.SERIES :
                                                "";
    }

    /**
     * Gets the name of the lastPortion column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getLastPortion(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.LASTEP :
                module == BasicModules.BOOK ? BookColumns.LASTEP :
                        module == BasicModules.MANGA ? MangaColumns.LASTEP :
                                module == BasicModules.NOVEL ? NovelColumns.LASTPORTION :
                                        module == BasicModules.SERIES ? SeriesColumns.LASTEP :
                                                "";
    }

    /**
     * Gets the name of the numPortion column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getNumPortion(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.NUMEP :
                module == BasicModules.BOOK ? BookColumns.NUMEP :
                        module == BasicModules.MANGA ? MangaColumns.NUMEP :
                                module == BasicModules.NOVEL ? NovelColumns.NUMPORTION :
                                        module == BasicModules.SERIES ? SeriesColumns.NUMEP :
                                                "";
    }

    /**
     * Gets the name of the processed column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getProcessed(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.SEENEP :
                module == BasicModules.BOOK ? BookColumns.SEENEP :
                        module == BasicModules.MANGA ? MangaColumns.SEENEP :
                                module == BasicModules.NOVEL ? NovelColumns.SEENPORTION :
                                        module == BasicModules.SERIES ? SeriesColumns.SEENEP :
                                                "";
    }

    /**
     * Gets the name of the rating column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getRating(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.RATING :
                module == BasicModules.BOOK ? BookColumns.RATING :
                        module == BasicModules.MANGA ? MangaColumns.RATING :
                                module == BasicModules.NOVEL ? NovelColumns.RATING :
                                        module == BasicModules.SERIES ? SeriesColumns.RATING :
                                                "";
    }

    /**
     * Gets the name of the creatorName column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getCreatorName(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.AUTHORNAME :
                module == BasicModules.BOOK ? BookColumns.AUTHORNAME :
                        module == BasicModules.MANGA ? MangaColumns.AUTHORNAME :
                                module == BasicModules.NOVEL ? NovelColumns.AUTHORNAME :
                                        module == BasicModules.SERIES ? SeriesColumns.AUTHORNAME :
                                                "";
    }

    /**
     * Gets the name of the creatorSort column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getCreatorSort(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.AUTHORSORT :
                module == BasicModules.BOOK ? BookColumns.AUTHORSORT :
                        module == BasicModules.MANGA ? MangaColumns.AUTHORSORT :
                                module == BasicModules.NOVEL ? NovelColumns.AUTHORSORT :
                                        module == BasicModules.SERIES ? SeriesColumns.AUTHORSORT :
                                                "";
    }

    /**
     * Gets the name of the workStat column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getWorkStat(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.AUTHORSTAT :
                module == BasicModules.BOOK ? BookColumns.AUTHORSTAT :
                        module == BasicModules.MANGA ? MangaColumns.WORKSTAT :
                                module == BasicModules.NOVEL ? NovelColumns.WORKSTAT :
                                        module == BasicModules.SERIES ? SeriesColumns.AUTHORSTAT :
                                                "";
    }

    /**
     * Gets the name of the ownStat column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getOwnStat(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.OWNSTAT :
                module == BasicModules.BOOK ? BookColumns.OWNSTAT :
                        module == BasicModules.MANGA ? MangaColumns.OWNSTAT :
                                module == BasicModules.NOVEL ? NovelColumns.OWNSTAT :
                                        module == BasicModules.SERIES ? SeriesColumns.OWNSTAT :
                                                "";
    }

    /**
     * Gets the name of the comment column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getComment(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.COMMENT :
                module == BasicModules.BOOK ? BookColumns.COMMENT :
                        module == BasicModules.MANGA ? MangaColumns.COMMENT :
                                module == BasicModules.NOVEL ? NovelColumns.COMMENT :
                                        module == BasicModules.SERIES ? SeriesColumns.COMMENT :
                                                "";
    }

    /**
     * Gets the name of the translator column,
     * for {@code Modules} which are defined as
     * {@link Enterprise.data.intface.Sourceable}.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getTranslator(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.TRANSLATOR :
                module == BasicModules.MANGA ? MangaColumns.TRANSLATOR :
                        module == BasicModules.NOVEL ? NovelColumns.TRANSLATOR :
                                                "";
    }

    /**
     * Gets the name of the keyWords column,
     * for {@code Modules} which are defined as
     * {@link Enterprise.data.intface.Sourceable}.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getKeyWords(Module module) {
        return module == BasicModules.ANIME ? AnimeColumns.KEYWORDS :
                module == BasicModules.MANGA ? MangaColumns.KEYWORDS :
                        module == BasicModules.NOVEL ? NovelColumns.KEYWORDS :
                                                "";
    }

    /**
     * The data for the Columns of {@link BasicModules#ANIME}.
     */
    private static class AnimeColumns {
        private static final String TITLE = "Titel";
        private static final String SERIES = "Reihe";
        private static final String LASTEP = "Datum letzter Episode";
        private static final String NUMEP = "Anzahl Episoden";
        private static final String SEENEP = "Gesehene Folgen";
        private static final String RATING = "Bewertung";
        private static final String AUTHORNAME = "Autor/-en";
        private static final String AUTHORSORT = "Autorensortierung";
        private static final String AUTHORSTAT = "Status des Autors";
        private static final String OWNSTAT = "Eigener Status";
        private static final String COMMENT = "Kommentar";
        private static final String TRANSLATOR = "SubGruppe";
        private static final String KEYWORDS = "Stichwörter";
    }

    /**
     * The data for the Columns of {@link BasicModules#BOOK}.
     */
    private static class BookColumns {
        private static final String TITLE = "Titel";
        private static final String SERIES = "Reihe";
        private static final String LASTEP = "Datum letztes Buch";
        private static final String NUMEP = "Anzahl Kapitel";
        private static final String SEENEP = "Gelesene Kapitel";
        private static final String RATING = "Bewertung";
        private static final String AUTHORNAME = "Autor/-en";
        private static final String AUTHORSORT = "Autorensortierung";
        private static final String AUTHORSTAT = "Status des Autors";
        private static final String OWNSTAT = "Eigener Status";
        private static final String COMMENT = "Kommentar";
    }

    /**
     * The data for the Columns of {@link BasicModules#NOVEL}.
     */
    private static class NovelColumns {
        private static final String TITLE = "Titel";
        private static final String SERIES = "Reihe";
        private static final String LASTPORTION = "Datum letztes Kapitels";
        private static final String NUMPORTION = "Anzahl Kapitel";
        private static final String SEENPORTION = "Gelesene Kapitel";
        private static final String RATING = "Bewertung";
        private static final String AUTHORNAME = "Autor/-en";
        private static final String AUTHORSORT = "Autorensortierung";
        private static final String WORKSTAT = "Status des Werkes";
        private static final String OWNSTAT = "Eigener Status";
        private static final String COMMENT = "Kommentar";
        private static final String TRANSLATOR = "Übersetzer";
        private static final String KEYWORDS = "Stichwörter";
    }

    /**
     * The data for the Columns of {@link BasicModules#SERIES}.
     */
    private static class SeriesColumns {
        private static final String TITLE = "Titel";
        private static final String SERIES = "Reihe";
        private static final String LASTEP = "Datum letzter Episode";
        private static final String NUMEP = "Anzahl Episoden";
        private static final String SEENEP = "Gesehene Folgen";
        private static final String RATING = "Bewertung";
        private static final String AUTHORNAME = "Autor/-en";
        private static final String AUTHORSORT = "Autorensortierung";
        private static final String AUTHORSTAT = "Status des Autors";
        private static final String OWNSTAT = "Eigener Status";
        private static final String COMMENT = "Kommentar";
    }

    /**
     * The data for the Columns of {@link BasicModules#MANGA}.
     */
    private class MangaColumns {
        private static final String TITLE = "Titel";
        private static final String SERIES = "Reihe";
        private static final String LASTEP = "Datum letztes Kapitels";
        private static final String NUMEP = "Anzahl Kapitel";
        private static final String SEENEP = "Gelesene Kapitel";
        private static final String RATING = "Bewertung";
        private static final String AUTHORNAME = "Autor/-en";
        private static final String AUTHORSORT = "Autorensortierung";
        private static final String WORKSTAT = "Status des Autors";
        private static final String OWNSTAT = "Eigener Status";
        private static final String COMMENT = "Kommentar";
        private static final String TRANSLATOR = "Übersetzer";
        private static final String KEYWORDS = "Stichwörter";
    }
}
