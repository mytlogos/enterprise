package Enterprise.modules;

/**
 * Created by Dominik on 10.07.2017.
 * Part of OgameBot.
 */
public enum Module {
    ANIME,
    BOOK,
    MANGA,
    NOVEL,
    SERIES,
    ENTERPRISE;

    public String tabName() {
        String tabName;
        switch (this) {
            case ANIME:
                tabName = "anime";
                break;
            case BOOK:
                tabName = "Bücher";
                break;
            case MANGA:
                tabName = "manga";
                break;
            case NOVEL:
                tabName = "Novel";
                break;
            case SERIES:
                tabName = "Serien";
                break;
            default:
                tabName = "N/A";
        }
        return tabName;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
