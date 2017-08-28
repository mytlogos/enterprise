package Enterprise.gui.general;

import Enterprise.ControlComm;
import Enterprise.gui.controller.ModuleController;
import Enterprise.gui.controller.SourceableModuleCont;
import Enterprise.modules.Module;
import Enterprise.gui.anime.controller.AnimeController;
import Enterprise.gui.novel.controller.NovelController;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class generates {@link CheckMenuItem}s with several default values.
 * The created {@code CheckMenuItems} are intended to be used for the {@code viewMenu}
 * of the {@link Enterprise.gui.enterprise.controller.EnterpriseController} in the
 * 'hide/show XYColumns' area.
 */
public class ItemFactory {

    /**
     * Generates a {@code List} of {@link CheckMenuItem}s with specifications for
     * the {@link Module#ANIME} and default behavior in the form of
     * listeners are added to the {@link CheckMenuItem#selectedProperty()}.
     *
     * @return items {@code List} of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getAnimeMenuItems() {
        AnimeController controller = (AnimeController) ControlComm.getInstance().getController(Module.ANIME, Mode.CONTENT);

        List<CheckMenuItem> items = getCheckMenuItems(controller, Module.ANIME);
        getSourceableCheckMenuItems(controller, items, Module.ANIME);

        return items;
    }

    /**
     * Generates a {@code List} of {@link CheckMenuItem}s with specifications for
     * the {@link Module#BOOK} and default behavior in the form of
     * listeners are added to the {@link CheckMenuItem#selectedProperty()}.
     *
     * @return items {@code List} of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getBookMenuItems() {
        return new ArrayList<>();
    }

    /**
     * Generates a {@code List} of {@link CheckMenuItem}s with specifications for
     * the {@link Module#MANGA} and default behavior in the form of
     * listeners are added to the {@link CheckMenuItem#selectedProperty()}.
     *
     * @return items {@code List} of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getMangaMenuItems() {
        return new ArrayList<>();
    }

    /**
     * Generates a {@code List} of {@link CheckMenuItem}s with specifications for
     * the {@link Module#NOVEL} and default behavior in the form of
     * listeners are added to the {@link CheckMenuItem#selectedProperty()}.
     *
     * @return items {@code List} of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getNovelMenuItems() {
        NovelController controller = (NovelController) ControlComm.getInstance().getController(Module.NOVEL, Mode.CONTENT);


        List<CheckMenuItem> items = getCheckMenuItems(controller, Module.NOVEL);
        getSourceableCheckMenuItems(controller, items, Module.NOVEL);

        return items;
    }

    /**
     * Generates a {@code List} of {@link CheckMenuItem}s with specifications for
     * the {@link Module#SERIES} and default behavior in the form of
     * listeners are added to the {@link CheckMenuItem#selectedProperty()}.
     *
     * @return items {@code List} of {@code CheckMenuItems}
     */
    public List<CheckMenuItem> getSeriesMenuItems() {
        return new ArrayList<>();
    }

    /**
     * Adds the {@link CheckMenuItem}s inherent to {@link Enterprise.data.intface.Sourceable}s
     * to the specified {@code List}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param items {@code List} of items to add to
     * @param module {@code Module} to specify the text of the items
     */
    private void getSourceableCheckMenuItems(SourceableModuleCont controller, List<CheckMenuItem> items, Module module) {
        CheckMenuItem tlGroup = getTranslatorItem(controller, Columns.getTranslator(module));
        CheckMenuItem keyWords = getKeyWordsItem(controller, Columns.getKeyWords(module));

        items.add(tlGroup);
        items.add(keyWords);
    }

    /**
     * Creates a {@code List} of all {@link CheckMenuItem}s corresponding to the columns
     * inherent to all {@link ModuleController}s.
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param module {@code Module} to specify the text of the items
     * @return list of {@code CheckMenuItems}
     */
    private List<CheckMenuItem> getCheckMenuItems(ModuleController controller, Module module) {
        CheckMenuItem title = getTitleItem(controller, Columns.getTitle(module));
        CheckMenuItem series = getSeriesItem(controller, Columns.getSeries(module));
        CheckMenuItem lastEp = getLastPortionItem(controller, Columns.getLastPortion(module));

        CheckMenuItem numEp = getPresentItem(controller, Columns.getNumPortion(module));
        CheckMenuItem seenEp = getProcessedItem(controller, Columns.getProcessed(module));
        CheckMenuItem rating = getRatingItem(controller, Columns.getRating(module));

        CheckMenuItem creatorName = getCreatorNameItem(controller, Columns.getCreatorName(module));
        CheckMenuItem creatorSortName = getCreatorSortItem(controller, Columns.getCreatorSort(module));
        CheckMenuItem creatorStatus = getWorkStatItem(controller, Columns.getWorkStat(module));

        CheckMenuItem ownStatus = getOwnStatItem(controller, Columns.getOwnStat(module));
        CheckMenuItem comment = getCommentItem(controller, Columns.getComment(module));

        List<CheckMenuItem> items = new ArrayList<>();
        items.add(title);
        items.add(series);
        items.add(lastEp);
        items.add(numEp);
        items.add(seenEp);
        items.add(rating);
        items.add(creatorName);
        items.add(creatorSortName);
        items.add(creatorStatus);
        items.add(ownStatus);
        items.add(comment);
        return items;
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getTitleItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showTitleColumn();
                    } else {
                        controller.hideTitleColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getSeriesItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showSeriesColumn();
                    } else {
                        controller.hideSeriesColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getLastPortionItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showLastPortionColumn();
                    } else {
                        controller.hideLastPortionColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getPresentItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showPresentColumn();
                    } else {
                        controller.hidePresentColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getProcessedItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showProcessedColumn();
                    } else {
                        controller.hideProcessedColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getRatingItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showRatingColumn();
                    } else {
                        controller.hideRatingColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getCreatorNameItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showCreatorColumn();
                    } else {
                        controller.hideCreatorColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getCreatorSortItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showCreatorSortColumn();
                    } else {
                        controller.hideCreatorSortColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getWorkStatItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showWorkStatColumn();
                    } else {
                        controller.hideWorkStatColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getOwnStatItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, true,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showOwnStatusColumn();
                    } else {
                        controller.hideOwnStatusColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getCommentItem(ModuleController controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showCommentColumn();
                    } else {
                        controller.hideCommentColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getTranslatorItem(SourceableModuleCont controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showTranslatorColumn();
                    } else {
                        controller.hideTranslatorColumn();
                    }
                });
    }

    /**
     * Returns a {@link CheckMenuItem} with default setting and specific behaviour.
     * If checked, it will show the specified column,
     * else 'hide' it (remove it from the tableView).
     * Sets the text of the {@code CheckMenuItem} to the provided {@code String}.
     *
     * @param controller to specify specific behaviour for the {@link CheckMenuItem#selectedProperty()}
     * @param itemName text of the item
     * @return item - the complete {@code CheckMenuItem}
     */
    private CheckMenuItem getKeyWordsItem(SourceableModuleCont controller, String itemName) {
        return checkMenuItemFactory(itemName, false,
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        controller.showKeyWordsColumn();
                    } else {
                        controller.hideKeyWordsColumn();
                    }
                });
    }

    /**
     * The Factory Method for creating a {@link CheckMenuItem} with the values provided
     * by the parameter.
     *
     * @param text {@code String} to set as visible text of the {@code CheckMenuItem}
     * @param defaultSel the default selection
     * @param changeListener the behaviour on a selection change
     * @return item - a complete {@code CheckMenuItem}
     */
    private CheckMenuItem checkMenuItemFactory(String text, boolean defaultSel, ChangeListener<Boolean> changeListener) {
        CheckMenuItem item = new CheckMenuItem();
        item.setText(text);
        item.selectedProperty().addListener(changeListener);
        item.setSelected(defaultSel);
        return item;
    }
}
