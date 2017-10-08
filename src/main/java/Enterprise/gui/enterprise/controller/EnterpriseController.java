package Enterprise.gui.enterprise.controller;

import Enterprise.ControlComm;
import Enterprise.data.concurrent.OnCloseRun;
import Enterprise.data.concurrent.UpdateService;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.controller.ContentController;
import Enterprise.gui.controller.Controller;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.Columns.Column;
import Enterprise.gui.general.GuiPaths;
import Enterprise.misc.Log;
import Enterprise.modules.BasicModules;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.posts.PostManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Enterprise.modules.BasicModules.*;

/**
 * This Class is the Controller of the Main Window of the File {@code enterprise.fxml}.
 */
public class EnterpriseController implements Initializable, Controller {

    private Logger logger = Log.classLogger(this);

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
    @FXML
    private MenuItem saveItem;
    @FXML
    private MenuItem openListItem;
    @FXML
    private MenuItem newListItem;
    @FXML
    private MenuItem closeListItem;
    @FXML
    private MenuItem deleteListItem;
    @FXML
    private MenuItem closeItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private ToggleButton enableMoving;
    @FXML
    private ComboBox<String> moveToBox;

    private boolean openPostView = false;
    private Stage postView = null;
    private int indexFirstSep;
    private int indexSecondSep;
    private UpdateService service;

    /**
     * Shows Posts scraped from {@link ScheduledPostScraper} in a new Window.
     */
    @FXML
    void showPosts() {
        try {
            postView = PostView.getInstance().open(root.getScene().getWindow());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not open postView", e);
        }
        if (postView == null) {
            showPostsBtn.setText("Zeige Posts");
        } else {
            showPostsBtn.setText("Schließe Posts");
        }
    }

    @FXML
    void movingMode() {
        // TODO: 28.09.2017

    }

    /**
     *
     */
    @FXML
    void closeList() {
        // TODO: 28.09.2017

    }

    /**
     * Requests the window the close.
     */
    @FXML
    void closeProgram() {
        Window window = root.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     *
     */
    @FXML
    void deleteList() {
        // TODO: 28.09.2017

    }

    /**
     *
     */
    @FXML
    void newList() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.showAndWait();

        String newList = dialog.getResult();
        String tabName = tabPane.getSelectionModel().getSelectedItem().getText();

        if (newList != null) {
            for (BasicModules basicModules : BasicModules.values()) {
                if (basicModules.tabName().equals(tabName)) {
                    ContentController controller = (ContentController) ControlComm.getInstance().getController(basicModules, BasicModes.CONTENT);
                    controller.addList(newList);
                    break;
                }
            }
        }
    }

    /**
     *
     */
    @FXML
    void openLists() {
        // TODO: 28.09.2017

    }

    /**
     * Saves the data in the database.
     */
    @FXML
    void saveData() {
        service.start();
    }

    /**
     * Opens the {@code About} window.
     */
    @FXML
    void openAbout() {
        // TODO: 28.09.2017
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
     * Loads the content of the specified {@link BasicModules} to the specified {@link Pane}
     * and binds it to the Container Pane.
     *
     * @param module module to load
     * @param pane   Pane to load the Content in
     * @throws IOException if content could not be loaded
     */
    private void loadContent(BasicModules module, Pane pane) throws IOException {
        String location = GuiPaths.getPath(module, BasicModes.CONTENT);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location));

        Pane contentPane;
        loader.setController(ControlComm.getInstance().getController(module, BasicModes.CONTENT));
        contentPane = loader.load();

        contentPane.prefHeightProperty().bind(pane.heightProperty());
        contentPane.prefWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(contentPane);
    }

    /**
     * Executes tasks on a Window Close Request.
     * Cancels the {@link ScheduledPostScraper} and
     * the {@link UpdateService}.
     * Starts the {@link OnCloseRun}.
     */
    private void onCloseOperations() {
        Platform.runLater(() -> tabPane.getScene().getWindow().setOnCloseRequest(event -> {
            service.cancel();
            Thread thread = new Thread(new OnCloseRun());
            thread.start();
            PostManager.getInstance().cancelScheduledScraper();
            System.out.println("Fenster wird geschlossen!");
        }));
    }

    @Override
    public void open() {
        // TODO: 28.09.2017

    }

    public void paneFocus() {
        for (Node node : root.getChildren()) {
            if (node instanceof Pane) {
                node.setOnMouseClicked(event -> node.requestFocus());
            }
        }
        root.setOnMouseClicked(event -> root.requestFocus());
    }

    /**
     * Sets the MenuItems of the theme 'show/hide XYColumn' to the {@code viewMenu}.
     * Adds listener to dynamically change these items, depending of the opened tab.
     */
    private void setViewMenu() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedTab = newValue.getText();
            onNewTab(selectedTab);
        });
        tabPane.getSelectionModel().select(animeTab);
        List<CheckMenuItem> menuItems = getCheckMenuItems(BasicModules.ANIME);
        viewMenu.getItems().addAll(indexFirstSep + 1, menuItems);
    }

    /**
     * Sets the 'hide/show XYColumns' menuItems to the {@code viewMenu}
     * and removes the old ones from the {@code viewMenu}.
     *
     * @param selectedTab name of the selected Tab
     */
    private void onNewTab(String selectedTab) {
        for (BasicModules basicModule : BasicModules.values()) {
            if (selectedTab.equalsIgnoreCase(basicModule.tabName())) {
                clearViewMenu();
                List<CheckMenuItem> items = getCheckMenuItems(basicModule);

                viewMenu.getItems().addAll(indexFirstSep + 1, items);
            }
        }
    }

    private List<CheckMenuItem> getCheckMenuItems(BasicModules basicModule) {
        ContentController controller = (ContentController) ControlComm.getInstance().getController(basicModule, BasicModes.CONTENT);

        List<Column<? extends CreationEntry, ?>> set = controller.getColumnManager().getColumns();
        List<CheckMenuItem> items = new ArrayList<>();

        for (Column<? extends CreationEntry, ?> column : set) {
            CheckMenuItem item = column.getMenuItem();
            if (item == null) {
                System.out.println("NULL FOR");
                System.out.println(column.getName());
                System.out.println(column.getNodeName());
            }
            items.add(item);
        }
        return items;
    }

    /**
     * Clears the Space which 'belongs' to the 'hide/show XYColumns' menuItems.
     */
    private void clearViewMenu() {
        indexSecondSep = viewMenu.getItems().indexOf(separator2);
        viewMenu.getItems().remove(indexFirstSep + 1, indexSecondSep);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControlComm.getInstance().setEnterpriseController(this);

        //starts the UpdateService
        service = new UpdateService();
        // FIXME: 27.09.2017 out of service atm
//        service.start();
        //starts the ScheduledPostScraper
        PostManager.getInstance().startScheduledScraper();

        //ready the Graphical Content
        setTabPaneListeners();
        onCloseOperations();
        paneFocus();
        setTabs();

        calcViewMenuSeparator();
        Platform.runLater(() -> {
            loadPanes();
            setViewMenu();
        });
    }

    private void calcViewMenuSeparator() {
        //space 'reserved' for the 'show/hide XYColumn' checkMenuItems
        indexFirstSep = viewMenu.getItems().indexOf(separator1);
        indexSecondSep = viewMenu.getItems().indexOf(separator2);
    }

    private void loadPanes() {
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
    }

    public Stage getStage() {
        if (root != null) {
            return (Stage) root.getScene().getWindow();
        } else {
            throw new IllegalStateException();
        }
    }
}
