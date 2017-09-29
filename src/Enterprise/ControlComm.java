package Enterprise;

import Enterprise.gui.controller.Controller;
import Enterprise.gui.controller.add.*;
import Enterprise.gui.controller.content.*;
import Enterprise.gui.controller.edit.*;
import Enterprise.gui.controller.show.*;
import Enterprise.gui.enterprise.controller.EnterpriseController;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.Mode;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ControlComm {
    private Map<Module, Map<Mode, Controller>> moduleModeMap = new HashMap<>();

    private static final ControlComm instance = new ControlComm();


    public static ControlComm getInstance() {
        return instance;
    }

    private ControlComm() {
        initMap(BasicModules.class);
    }

    private void initMap(Class<BasicModules> basicModulesClass) {
        for (BasicModules basicModules : basicModulesClass.getEnumConstants()) {
            moduleModeMap.put(basicModules, new HashMap<>());
        }

        initPutInMap(new AddAnimeController(), BasicModes.ADD, BasicModules.ANIME);
        initPutInMap(new AnimeController(), BasicModes.CONTENT, BasicModules.ANIME);
        initPutInMap(new EditAnimeController(), BasicModes.EDIT, BasicModules.ANIME);
        initPutInMap(new ShowAnimeController(), BasicModes.SHOW, BasicModules.ANIME);

        initPutInMap(new AddBookController(), BasicModes.ADD, BasicModules.BOOK);
        initPutInMap(new BookController(), BasicModes.CONTENT, BasicModules.BOOK);
        initPutInMap(new EditBookController(), BasicModes.EDIT, BasicModules.BOOK);
        initPutInMap(new ShowBookController(), BasicModes.SHOW, BasicModules.BOOK);

        initPutInMap(new AddMangaController(), BasicModes.ADD, BasicModules.MANGA);
        initPutInMap(new MangaController(), BasicModes.CONTENT, BasicModules.MANGA);
        initPutInMap(new EditMangaController(), BasicModes.EDIT, BasicModules.MANGA);
        initPutInMap(new ShowMangaController(), BasicModes.SHOW, BasicModules.MANGA);

        initPutInMap(new AddNovelController(), BasicModes.ADD, BasicModules.NOVEL);
        initPutInMap(new NovelController(), BasicModes.CONTENT, BasicModules.NOVEL);
        initPutInMap(new EditNovelController(), BasicModes.EDIT, BasicModules.NOVEL);
        initPutInMap(new ShowNovelController(), BasicModes.SHOW, BasicModules.NOVEL);

        initPutInMap(new AddSeriesController(), BasicModes.ADD, BasicModules.SERIES);
        initPutInMap(new SeriesController(), BasicModes.CONTENT, BasicModules.SERIES);
        initPutInMap(new EditSeriesController(), BasicModes.EDIT, BasicModules.SERIES);
        initPutInMap(new ShowSeriesController(), BasicModes.SHOW, BasicModules.SERIES);

        moduleModeMap.put(GuiPaths.Main.ENTERPRISE, new HashMap<>());
        initPutInMap(new EnterpriseController(), BasicModes.CONTENT, GuiPaths.Main.ENTERPRISE);
    }

    private void initPutInMap(Controller controller, Mode basicModes, Module basicModules) {
        Map<Mode, Controller> map = moduleModeMap.get(basicModules);
        map.put(basicModes, controller);
    }

    public Controller getController(Module module, Mode mode) {
        return moduleModeMap.get(module).get(mode);
    }

    public void setController(Controller controller, Module module, Mode mode) {
        moduleModeMap.get(module).put(mode, controller);
    }

    public EnterpriseController getEnterpriseController() {
        return (EnterpriseController) moduleModeMap.get(GuiPaths.Main.ENTERPRISE).get(BasicModes.CONTENT);
    }

    public void setEnterpriseController(EnterpriseController controller) {
        moduleModeMap.get(GuiPaths.Main.ENTERPRISE).put(BasicModes.CONTENT, controller);
    }
}
