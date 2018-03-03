package enterprise.gui.enterprise;

import enterprise.ControlComm;
import enterprise.data.Default;
import enterprise.data.concurrent.Updater;
import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Content;
import enterprise.gui.controller.Controller;
import enterprise.gui.general.BasicMode;
import enterprise.gui.general.Columns.Column;
import enterprise.gui.general.GuiPaths;
import enterprise.gui.general.Mode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.posts.PostManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enterprise.modules.BasicModule.*;

/**
 * This Class is the Controller of the Main Window of the File {@code enterprise.fxml}.
 */
public class EnterpriseController implements Controller {

    private final Logger logger = Default.LOGGER;

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
    private ToggleButton enableMoving;
    @FXML
    private ComboBox<String> moveToBox;
    @FXML
    private Text message;
    @FXML
    private ProgressBar progressBar;

    private boolean openPostView = false;
    private Stage postView = null;
    private int indexFirstSep;
    private int indexSecondSep;

    @Override
    public void initialize() {
        Controller.super.initialize();

        //starts the ScheduledPostScraper
        PostManager.getInstance().startScheduledScraper();

        //ready the Graphical Content
        setTabPaneListeners();
        paneFocus();
        setTabs();
        readyStatusBar();

        calcViewMenuSeparator();

        Platform.runLater(() -> {
            loadPanes();
            setViewMenu();
        });
    }

    @Override
    public Module getModule() {
        return GuiPaths.Main.ENTERPRISE;
    }

    @Override
    public Mode getMode() {
        return BasicMode.CONTENT;
    }

    /**
     * Sets the Tabs to fill the Width of the Window.
     * Copied from <a href="https://stackoverflow.com/a/31124890">
     * Answer to Question about Tab filling the width of the header.</a>
     */
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

    public void paneFocus() {
        for (Node node : root.getChildren()) {
            if (node instanceof Pane) {
                node.setOnMouseClicked(event -> node.requestFocus());
            }
        }
        root.setOnMouseClicked(event -> root.requestFocus());
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

    private void readyStatusBar() {
        Tasks tasks = Tasks.get();
        message.textProperty().bind(tasks.messageProperty());
        progressBar.progressProperty().bind(tasks.workDoneProperty());

        progressBar.progressProperty().addListener(
                (observable, oldValue, newValue) ->
                        progressBar.setVisible(!(newValue.intValue() == 1 && tasks.isEmpty())));
    }

    private void calcViewMenuSeparator() {
        //space 'reserved' for the 'show/hide XYColumn' checkMenuItems
        indexFirstSep = viewMenu.getItems().indexOf(separator1);
        indexSecondSep = viewMenu.getItems().indexOf(separator2);
    }

    private void loadPanes() {
        try {
            loadContent(ANIME, animeTab);
            loadContent(BOOK, bookTab);
            loadContent(MANGA, mangaTab);
            loadContent(NOVEL, novelTab);
            loadContent(SERIES, seriesTab);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not load content", e);
            e.printStackTrace();
        }
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
        List<CheckMenuItem> menuItems = getCheckMenuItems(BasicModule.ANIME);
        viewMenu.getItems().addAll(indexFirstSep + 1, menuItems);
    }

    /**
     * Loads the content of the specified {@link BasicModule} to the specified {@link Pane}
     * and binds it to the Container Pane.
     *
     * @param module module to load
     * @param tab    tab to load the Content in
     * @throws IOException if content could not be loaded
     */
    private void loadContent(BasicModule module, Tab tab) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/content.fxml"));

        loader.setController(ControlComm.get().getController(module, BasicMode.CONTENT));
        Pane contentPane = loader.load();
        tab.setContent(contentPane);
    }

    /**
     * Sets the 'hide/show XYColumns' menuItems to the {@code viewMenu}
     * and removes the old ones from the {@code viewMenu}.
     *
     * @param selectedTab name of the selected Tab
     */
    private void onNewTab(String selectedTab) {
        for (BasicModule basicModule : BasicModule.values()) {
            if (selectedTab.equalsIgnoreCase(basicModule.tabName())) {
                clearViewMenu();
                List<CheckMenuItem> items = getCheckMenuItems(basicModule);

                viewMenu.getItems().addAll(indexFirstSep + 1, items);
            }
        }
    }

    private List<CheckMenuItem> getCheckMenuItems(BasicModule basicModule) {
        Content controller = (Content) ControlComm.get().getController(basicModule, BasicMode.CONTENT);

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

    public Stage getStage() {
        if (root != null) {
            return (Stage) root.getScene().getWindow();
        } else {
            throw new IllegalStateException();
        }
    }

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
            for (BasicModule basicModules : BasicModule.values()) {
                if (basicModules.tabName().equals(tabName)) {
                    Content controller = (Content) ControlComm.get().getController(basicModules, BasicMode.CONTENT);
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
        Updater.runOnce();
    }

    /**
     * Opens the {@code About} window.
     */
    @FXML
    void openAbout() {
        // TODO: 28.09.2017
    }
}
