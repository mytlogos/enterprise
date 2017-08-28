package Enterprise.gui.enterprise.controller;

import Enterprise.ControlComm;
import Enterprise.data.concurrent.GetCall;
import Enterprise.data.concurrent.OnCloseRun;
import Enterprise.data.concurrent.UpdateService;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.anime.controller.AnimeController;
import Enterprise.gui.book.controller.BookController;
import Enterprise.gui.controller.Controller;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.ItemFactory;
import Enterprise.gui.general.Mode;
import Enterprise.gui.general.PostSingleton;
import Enterprise.gui.manga.controller.MangaController;
import Enterprise.gui.novel.controller.NovelController;
import Enterprise.gui.series.controller.SeriesController;
import Enterprise.modules.Anime;
import Enterprise.modules.Module;
import Enterprise.modules.Novel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import scrape.concurrent.ScheduledScraper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static Enterprise.modules.Module.*;

/**
 * This Class is the Controller of the Main Window of the File {@code Enterprise.fxml}.
 */
public class EnterpriseController implements Initializable, Controller {

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    {
        try {
            //creates a FileHandler for this Class and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + this.getClass().getSimpleName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @FXML
    private Tab animeTab;
    @FXML
    private Tab mangaTab;
    @FXML
    private Tab novelTab;
    @FXML
    private Tab seriesTab;
    @FXML
    private Tab bookTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private Pane animePane;
    @FXML
    private Pane mangaPane;
    @FXML
    private Pane novelPane;
    @FXML
    private Pane seriesPane;
    @FXML
    private Pane bookPane;
    @FXML
    private BorderPane root;
    @FXML
    private Menu viewMenu;
    @FXML
    private Menu columnMenu;

    @FXML
    private SeparatorMenuItem separator1;

    @FXML
    private SeparatorMenuItem separator2;
    @FXML
    private Button showPostsBtn;

    /**
     * Shows Posts scraped from {@link ScheduledScraper} in a new Window.
     */
    @FXML
    void showPosts() {
        PostView postView = new PostView();
        postView.open();
    }

    /**
     * Sets the Tabs to fill the Width of the Window.
     */
    //copied Method to let the Tab fill the remaining Space in the Header
    private void setTabPaneListeners() {

        tabPane.widthProperty().addListener((value, oldWidth, newWidth) -> {
            Side side = tabPane.getSide();
            int numTabs = tabPane.getTabs().size();
            if ((side == Side.BOTTOM || side == Side.TOP) && numTabs != 0) {
                tabPane.setTabMinWidth(newWidth.intValue() / numTabs - (20));
                tabPane.setTabMaxWidth(newWidth.intValue() / numTabs - (20));
            }
        });

        tabPane.heightProperty().addListener((value, oldHeight, newHeight) -> {
            Side side = tabPane.getSide();
            int numTabs = tabPane.getTabs().size();
            if ((side == Side.LEFT || side == Side.RIGHT) && numTabs != 0) {
                tabPane.setTabMinWidth(newHeight.intValue() / numTabs - (20));
                tabPane.setTabMaxWidth(newHeight.intValue() / numTabs - (20));
            }
        });

        tabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            Side side = tabPane.getSide();
            int numTabs = tabPane.getTabs().size();
            if (numTabs != 0) {
                if (side == Side.LEFT || side == Side.RIGHT) {
                    tabPane.setTabMinWidth(tabPane.heightProperty().intValue() / numTabs - (20));
                    tabPane.setTabMaxWidth(tabPane.heightProperty().intValue() / numTabs - (20));
                }
                if (side == Side.BOTTOM || side == Side.TOP) {
                    tabPane.setTabMinWidth(tabPane.widthProperty().intValue() / numTabs - (20));
                    tabPane.setTabMaxWidth(tabPane.widthProperty().intValue() / numTabs - (20));
                }
            }
        });
    }

    /**
     * Loads the content of the specified {@link Module} to the specified {@link Pane}
     * and binds it to the Container Pane.
     *
     * @param module module to load
     * @param pane Pane to load the Content in
     * @throws IOException if content could not be loaded
     */
    private void loadContent(Module module, Pane pane) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getPath(module, Mode.CONTENT)));

        Pane pane1;
        pane1 = loader.load();

        pane1.prefHeightProperty().bind(pane.heightProperty());
        pane1.prefWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(pane1);
    }

    /**
     * Executes tasks on a Window Close Request.
     * Cancels the {@link ScheduledScraper} and
     * the {@link UpdateService}.
     * Starts the {@link OnCloseRun}.
     */
    private void onCloseOperations() {
        Platform.runLater(()-> tabPane.getScene().getWindow().setOnCloseRequest(event -> {
            service.cancel();
            Thread thread = new Thread(new OnCloseRun());
            thread.start();
            PostSingleton.getInstance().cancelScheduledScraper();
            System.out.println("Fenster wird geschlossen!");
        }));
    }

    @Override
    public void open() {

    }

    @Override
    public void paneFocus() {
        for (Node node : root.getChildren()) {
            if (node instanceof Pane) {
                node.setOnMouseClicked(event -> node.requestFocus());
            }
        }
        root.setOnMouseClicked(event -> root.requestFocus());
    }

    @Override
    public void setModuleEntry() {

    }

    private int indexFirstSep;
    private int indexSecondSep;

    /**
     * Sets the MenuItems of the theme 'show/hide XYColumn' to the {@code viewMenu}.
     * Adds listener to dynamically change these items, depending of the opened tab.
     */
    private void setViewMenu() {
        ItemFactory factory = new ItemFactory();
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedTab = newValue.getText();
            String oldTab = oldValue.getText();

            onNewTab(factory, selectedTab);
            onOldTab(oldTab);

        });
        tabPane.getSelectionModel().select(animeTab);
        viewMenu.getItems().addAll(indexFirstSep +1, factory.getAnimeMenuItems());
    }

    /**
     * Clears all columns except the {@code indexColumn} of the {@code entryTable}
     * of the specified String.
     * @param oldTab string representing a tabName of {@link Module}.
     * @see Module#tabName()
     */
    private void onOldTab(String oldTab) {
        if (ANIME.tabName().equalsIgnoreCase(oldTab)) {
            AnimeController controller = (AnimeController) ControlComm.getInstance().getController(ANIME, Mode.CONTENT);
            controller.clearColumns();
        }
        if (BOOK.tabName().equalsIgnoreCase(oldTab)) {
            BookController controller = (BookController) ControlComm.getInstance().getController(BOOK, Mode.CONTENT);
            //controller.clearColumns();
            // TODO: 30.07.2017 implement BOOK, MANGA and SERIES first
        }
        if (MANGA.tabName().equalsIgnoreCase(oldTab)) {
            MangaController controller = (MangaController) ControlComm.getInstance().getController(MANGA, Mode.CONTENT);
            //controller.clearColumns();
        }
        if (NOVEL.tabName().equalsIgnoreCase(oldTab)) {
            NovelController controller = (NovelController) ControlComm.getInstance().getController(NOVEL, Mode.CONTENT);
            controller.clearColumns();
        }
        if (SERIES.tabName().equalsIgnoreCase(oldTab)) {
            SeriesController controller = (SeriesController) ControlComm.getInstance().getController(SERIES, Mode.CONTENT);
            //controller.clearColumns();
        }
    }

    /**
     * Sets the 'hide/show XYColumns' menuItems to the {@code viewMenu}
     * and removes the old ones from the {@code viewMenu}.
     * @param factory menuItemFactory for the 'hide/show XYColumns'
     * @param selectedTab name of the selected Tab
     */
    private void onNewTab(ItemFactory factory, String selectedTab) {
        if (selectedTab.equalsIgnoreCase(ANIME.tabName())) {
            clearViewMenu();
            viewMenu.getItems().addAll(indexFirstSep +1, factory.getAnimeMenuItems());
        }
        if (selectedTab.equalsIgnoreCase(BOOK.tabName())) {
            clearViewMenu();
        }
        if (selectedTab.equalsIgnoreCase(MANGA.tabName())) {
            clearViewMenu();
        }
        if (selectedTab.equalsIgnoreCase(NOVEL.tabName())) {
            clearViewMenu();
            viewMenu.getItems().addAll(indexFirstSep +1, factory.getNovelMenuItems());
        }
        if (selectedTab.equalsIgnoreCase(SERIES.tabName())) {
            clearViewMenu();
        }
    }

    /**
     * Clears the Space which 'belongs' to the 'hide/show XYColumns' menuItems.
     */
    private void clearViewMenu() {
        indexSecondSep = viewMenu.getItems().indexOf(separator2);
        viewMenu.getItems().remove(indexFirstSep +1 ,indexSecondSep);
    }

    /**
     * Sets the name of the Tabs.
     */
    private void setTabs() {
        animeTab.setText(ANIME.tabName());
        bookTab.setText(BOOK.tabName());
        mangaTab.setText(MANGA.tabName());
        novelTab.setText(NOVEL.tabName());
        seriesTab.setText(SERIES.tabName());
    }

    /**
     * Starts the {@link GetCall} to get all {@link CreationEntry}s from the source of data.
     * Separates the Entries according to their {@link Module}s.
     * Makes the {@link Enterprise.data.intface.Sourceable} available for the {@link ScheduledScraper}.
     */
    private void getDataFromDB() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<? extends CreationEntry>> future = executor.submit(new GetCall());

        List<SourceableEntry> animeEntries = Anime.getInstance().getEntries();
        List<SourceableEntry> novelEntries = Novel.getInstance().getEntries();

        List<? extends CreationEntry> futureEntries;
        try {
            futureEntries = future.get();

                for (CreationEntry creationEntry : futureEntries) {

                    if (creationEntry instanceof SourceableEntry && creationEntry.getModule() == Module.ANIME) {
                        animeEntries.add((SourceableEntryImpl) creationEntry);
                    }
                    if (creationEntry instanceof SourceableEntry && creationEntry.getModule() == Module.NOVEL) {
                        novelEntries.add((SourceableEntryImpl) creationEntry);
                    }
                }

            novelEntries.forEach(novelEntry -> PostSingleton.getInstance().addSearchEntries(novelEntry.getSourceable()));
            animeEntries.forEach(animeEntry -> PostSingleton.getInstance().addSearchEntries(animeEntry.getSourceable()));

        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "error occurred while getting data from the database", e);
            e.printStackTrace();
        }
    }

    private UpdateService service;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setController(this);
        getDataFromDB();

        //starts the UpdateService
        service = new UpdateService();
        //service.start();
        // FIXME: 15.08.2017 uncomment the scheduled Services
        //starts the ScheduledScraper
        //PostSingleton.getInstance().startScheduledScraper();

        //ready the Graphical Content
        setTabPaneListeners();
        onCloseOperations();
        paneFocus();
        setTabs();

        //space 'reserved' for the 'show/hide XYColumn' checkMenuItems
        indexFirstSep = viewMenu.getItems().indexOf(separator1);
        indexSecondSep = viewMenu.getItems().indexOf(separator2);

        try {
            loadContent(ANIME, animePane);
            loadContent(BOOK, bookPane);
            loadContent(MANGA, mangaPane);
            loadContent(NOVEL, novelPane);
            loadContent(SERIES, seriesPane);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not load content", e);
            e.printStackTrace();
        }

        Platform.runLater(this::setViewMenu);

    }
}
