package Enterprise;

import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;
import Enterprise.gui.anime.controller.AddAnimeController;
import Enterprise.gui.anime.controller.AnimeController;
import Enterprise.gui.anime.controller.EditAnimeController;
import Enterprise.gui.anime.controller.ShowAnimeController;
import Enterprise.gui.book.controller.AddBookController;
import Enterprise.gui.book.controller.BookController;
import Enterprise.gui.book.controller.EditBookController;
import Enterprise.gui.book.controller.ShowBookController;
import Enterprise.gui.controller.Controller;
import Enterprise.gui.enterprise.controller.EnterpriseController;
import Enterprise.gui.manga.controller.AddMangaController;
import Enterprise.gui.manga.controller.EditMangaController;
import Enterprise.gui.manga.controller.MangaController;
import Enterprise.gui.manga.controller.ShowMangaController;
import Enterprise.gui.novel.controller.AddNovelController;
import Enterprise.gui.novel.controller.EditNovelController;
import Enterprise.gui.novel.controller.NovelController;
import Enterprise.gui.novel.controller.ShowNovelController;
import Enterprise.gui.series.controller.AddSeriesController;
import Enterprise.gui.series.controller.EditSeriesController;
import Enterprise.gui.series.controller.SeriesController;
import Enterprise.gui.series.controller.ShowSeriesController;

/**
 * Created by Dominik on 09.07.2017.
 * Part of OgameBot.
 */
public class ControlComm {
    private AddAnimeController addAnimeController;
    private AnimeController animeController;
    private EditAnimeController editAnimeController;
    private ShowAnimeController showAnimeController;

    private AddBookController addBookController;
    private BookController bookController;
    private EditBookController editBookController;
    private ShowBookController showBookController;

    private AddMangaController addMangaController;
    private MangaController mangaController;
    private EditMangaController editMangaController;
    private ShowMangaController showMangaController;

    private AddNovelController addNovelController;
    private NovelController novelController;
    private EditNovelController editNovelController;
    private ShowNovelController showNovelController;

    private AddSeriesController addSeriesController;
    private SeriesController seriesController;
    private EditSeriesController editSeriesController;
    private ShowSeriesController showSeriesController;

    private EnterpriseController enterpriseController;
    private static final ControlComm instance = new ControlComm();

    public static ControlComm getInstance() {
        return instance;
    }

    private ControlComm() {
        addAnimeController = new AddAnimeController();
        animeController = new AnimeController();
        editAnimeController = new EditAnimeController();
        showAnimeController = new ShowAnimeController();

        addBookController = new AddBookController();
        bookController = new BookController();
        editBookController = new EditBookController();
        showBookController = new ShowBookController();

        addMangaController = new AddMangaController();
        mangaController = new MangaController();
        editMangaController = new EditMangaController();
        showMangaController = new ShowMangaController();

        addNovelController = new AddNovelController();
        novelController = new NovelController();
        editNovelController = new EditNovelController();
        showNovelController = new ShowNovelController();

        addSeriesController = new AddSeriesController();
        seriesController = new SeriesController();
        editSeriesController = new EditSeriesController();
        showSeriesController = new ShowSeriesController();
    }

    public Controller getController(Module module, Mode mode) {
        Controller controller = null;
        switch (module) {
            case ANIME:
                switch (mode){
                    case ADD: controller = addAnimeController; break;
                    case CONTENT: controller = animeController; break;
                    case EDIT: controller = editAnimeController; break;
                    case SHOW: controller = showAnimeController; break;
                }
                break;
            case BOOK:
                switch (mode){
                    case ADD: controller = addBookController; break;
                    case CONTENT: controller = bookController; break;
                    case EDIT: controller = editBookController; break;
                    case SHOW: controller = showBookController; break;
                }
                break;
            case MANGA:
                switch (mode){
                    case ADD: controller = addMangaController; break;
                    case CONTENT: controller = mangaController; break;
                    case EDIT: controller = editMangaController; break;
                    case SHOW: controller = showMangaController; break;
                }
                break;
            case NOVEL:
                switch (mode){
                    case ADD: controller = addNovelController; break;
                    case CONTENT: controller = novelController; break;
                    case EDIT: controller = editNovelController; break;
                    case SHOW: controller = showNovelController; break;
                }
                break;
            case SERIES:
                switch (mode){
                    case ADD: controller = addSeriesController; break;
                    case CONTENT: controller = seriesController; break;
                    case EDIT: controller = editSeriesController; break;
                    case SHOW: controller = showSeriesController; break;
                }
                break;
            case ENTERPRISE:
                switch (mode){
                    case ADD:
                        throw new IllegalArgumentException("dafür nicht möglich!");
                    case CONTENT: controller = enterpriseController; break;
                    case EDIT:
                        throw new IllegalArgumentException("dafür nicht möglich!");
                    case SHOW:
                        throw new IllegalArgumentException("dafür nicht möglich!");
                }
                break;
            default:
                controller = null;
        }
        return controller;
    }

    public void setController(Controller controller) {
        if (controller instanceof AddAnimeController) {

            addAnimeController = (AddAnimeController) controller;
        } else if (controller instanceof AnimeController) {
            animeController = (AnimeController) controller;

        } else if (controller instanceof EditAnimeController) {
            editAnimeController = (EditAnimeController) controller;

        } else if (controller instanceof ShowAnimeController) {
            showAnimeController = (ShowAnimeController) controller;

        } else if (controller instanceof AddBookController) {
            addBookController = (AddBookController) controller;

        } else if (controller instanceof BookController) {
            bookController = (BookController) controller;

        } else if (controller instanceof EditBookController) {
            editBookController = (EditBookController) controller;

        } else if (controller instanceof ShowBookController) {
            showBookController = (ShowBookController) controller;

        } else if (controller instanceof AddMangaController) {
            addMangaController = (AddMangaController) controller;

        } else if (controller instanceof MangaController) {
            mangaController = (MangaController) controller;

        } else if (controller instanceof EditMangaController) {
            editMangaController = (EditMangaController) controller;

        } else if (controller instanceof ShowMangaController) {
            showMangaController = (ShowMangaController) controller;

        } else if (controller instanceof AddNovelController) {
            addNovelController = (AddNovelController) controller;

        } else if (controller instanceof NovelController) {
            novelController = (NovelController) controller;

        } else if (controller instanceof EditNovelController) {
            editNovelController = (EditNovelController) controller;

        } else if (controller instanceof ShowNovelController) {
            showNovelController = (ShowNovelController) controller;

        } else if (controller instanceof AddSeriesController) {
            addSeriesController = (AddSeriesController) controller;

        } else if (controller instanceof SeriesController) {
            seriesController = (SeriesController) controller;

        } else if (controller instanceof EditSeriesController) {
            editSeriesController = (EditSeriesController) controller;

        } else if (controller instanceof ShowSeriesController) {
            showSeriesController = (ShowSeriesController) controller;

        } else if (controller instanceof EnterpriseController){
            enterpriseController = (EnterpriseController) controller;

        }else{
            throw new IllegalArgumentException("Unbekannter Controller!");
        }

    }
}
