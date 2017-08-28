package Enterprise.gui.general;

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
        return module == Module.ANIME ? AnimeColumns.TITLE :
                module == Module.BOOK ? BookColumns.TITLE :
                        module == Module.MANGA ? MangaColumns.TITLE :
                                module == Module.NOVEL ? NovelColumns.TITLE :
                                        module == Module.SERIES ? SeriesColumns.TITLE :
                                                "";
    }

    /**
     * Gets the name of the series column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getSeries(Module module) {
        return module == Module.ANIME ? AnimeColumns.SERIES :
                module == Module.BOOK ? BookColumns.SERIES :
                        module == Module.MANGA ? MangaColumns.SERIES :
                                module == Module.NOVEL ? NovelColumns.SERIES :
                                        module == Module.SERIES ? SeriesColumns.SERIES :
                                                "";
    }

    /**
     * Gets the name of the lastPortion column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getLastPortion(Module module) {
        return module == Module.ANIME ? AnimeColumns.LASTEP :
                module == Module.BOOK ? BookColumns.LASTEP :
                        module == Module.MANGA ? MangaColumns.LASTEP :
                                module == Module.NOVEL ? NovelColumns.LASTPORTION :
                                        module == Module.SERIES ? SeriesColumns.LASTEP :
                                                "";
    }

    /**
     * Gets the name of the numPortion column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getNumPortion(Module module) {
        return module == Module.ANIME ? AnimeColumns.NUMEP :
                module == Module.BOOK ? BookColumns.NUMEP:
                        module == Module.MANGA ? MangaColumns.NUMEP:
                                module == Module.NOVEL ? NovelColumns.NUMPORTION:
                                        module == Module.SERIES ? SeriesColumns.NUMEP:
                                                "";
    }

    /**
     * Gets the name of the processed column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getProcessed(Module module) {
        return module == Module.ANIME ? AnimeColumns.SEENEP :
                module == Module.BOOK ? BookColumns.SEENEP:
                        module == Module.MANGA ? MangaColumns.SEENEP:
                                module == Module.NOVEL ? NovelColumns.SEENPORTION:
                                        module == Module.SERIES ? SeriesColumns.SEENEP:
                                                "";
    }

    /**
     * Gets the name of the rating column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getRating(Module module) {
        return module == Module.ANIME ? AnimeColumns.RATING :
                module == Module.BOOK ? BookColumns.RATING:
                        module == Module.MANGA ? MangaColumns.RATING:
                                module == Module.NOVEL ? NovelColumns.RATING:
                                        module == Module.SERIES ? SeriesColumns.RATING:
                                                "";
    }

    /**
     * Gets the name of the creatorName column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getCreatorName(Module module) {
        return module == Module.ANIME ? AnimeColumns.AUTHORNAME:
                module == Module.BOOK ? BookColumns.AUTHORNAME:
                        module == Module.MANGA ? MangaColumns.AUTHORNAME:
                                module == Module.NOVEL ? NovelColumns.AUTHORNAME:
                                        module == Module.SERIES ? SeriesColumns.AUTHORNAME:
                                                "";
    }

    /**
     * Gets the name of the creatorSort column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getCreatorSort(Module module) {
        return module == Module.ANIME ? AnimeColumns.AUTHORSORT :
                module == Module.BOOK ? BookColumns.AUTHORSORT:
                        module == Module.MANGA ? MangaColumns.AUTHORSORT:
                                module == Module.NOVEL ? NovelColumns.AUTHORSORT:
                                        module == Module.SERIES ? SeriesColumns.AUTHORSORT:
                                                "";
    }

    /**
     * Gets the name of the workStat column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getWorkStat(Module module) {
        return module == Module.ANIME ? AnimeColumns.AUTHORSTAT :
                module == Module.BOOK ? BookColumns.AUTHORSTAT:
                        module == Module.MANGA ? MangaColumns.WORKSTAT:
                                module == Module.NOVEL ? NovelColumns.WORKSTAT:
                                        module == Module.SERIES ? SeriesColumns.AUTHORSTAT:
                                                "";
    }

    /**
     * Gets the name of the ownStat column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getOwnStat(Module module) {
        return module == Module.ANIME ? AnimeColumns.OWNSTAT :
                module == Module.BOOK ? BookColumns.OWNSTAT:
                        module == Module.MANGA ? MangaColumns.OWNSTAT:
                                module == Module.NOVEL ? NovelColumns.OWNSTAT:
                                        module == Module.SERIES ? SeriesColumns.OWNSTAT:
                                                "";
    }

    /**
     * Gets the name of the comment column.
     *
     * @param module module to search for
     * @return string - name of the column
     */
    public static String getComment(Module module) {
        return module == Module.ANIME ? AnimeColumns.COMMENT :
                module == Module.BOOK ? BookColumns.COMMENT:
                        module == Module.MANGA ? MangaColumns.COMMENT:
                                module == Module.NOVEL ? NovelColumns.COMMENT:
                                        module == Module.SERIES ? SeriesColumns.COMMENT:
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
        return module == Module.ANIME ? AnimeColumns.TRANSLATOR :
                        module == Module.MANGA ? MangaColumns.TRANSLATOR:
                                module == Module.NOVEL ? NovelColumns.TRANSLATOR:
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
        return module == Module.ANIME ? AnimeColumns.KEYWORDS :
                        module == Module.MANGA ? MangaColumns.KEYWORDS:
                                module == Module.NOVEL ? NovelColumns.KEYWORDS:
                                                "";
    }

    /**
     * The data for the Columns of {@link Module#ANIME}.
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
     * The data for the Columns of {@link Module#BOOK}.
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
     * The data for the Columns of {@link Module#MANGA}.
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

    /**
     * The data for the Columns of {@link Module#NOVEL}.
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
     * The data for the Columns of {@link Module#SERIES}.
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
}
