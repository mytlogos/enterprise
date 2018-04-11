package enterprise;

import enterprise.gui.controller.Controller;
import enterprise.gui.controller.Show;
import enterprise.gui.controller.SourceableShow;
import enterprise.gui.controller.add.*;
import enterprise.gui.controller.content.*;
import enterprise.gui.controller.edit.*;
import enterprise.gui.enterprise.EnterpriseController;
import enterprise.gui.general.BasicMode;
import enterprise.gui.general.GuiPaths;
import enterprise.gui.general.Mode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ControlComm {
    private static final ControlComm instance = new ControlComm();
    private final Map<Module, Map<Mode, Controller>> moduleModeMap = new HashMap<>();


    private ControlComm() {
        initMap();
    }

    private void initMap() {
        initPutInMap(new AddAnime(), BasicMode.ADD, BasicModule.ANIME);
        initPutInMap(new AnimeContent(), BasicMode.CONTENT, BasicModule.ANIME);
        initPutInMap(new EditAnime(), BasicMode.EDIT, BasicModule.ANIME);
        initPutInMap(new SourceableShow(BasicModule.ANIME), BasicMode.SHOW, BasicModule.ANIME);

        initPutInMap(new AddBook(), BasicMode.ADD, BasicModule.BOOK);
        initPutInMap(new BookContent(), BasicMode.CONTENT, BasicModule.BOOK);
        initPutInMap(new EditBook(), BasicMode.EDIT, BasicModule.BOOK);
        initPutInMap(new Show<>(BasicModule.BOOK), BasicMode.SHOW, BasicModule.BOOK);

        initPutInMap(new AddManga(), BasicMode.ADD, BasicModule.MANGA);
        initPutInMap(new MangaContent(), BasicMode.CONTENT, BasicModule.MANGA);
        initPutInMap(new EditManga(), BasicMode.EDIT, BasicModule.MANGA);
        initPutInMap(new Show<>(BasicModule.MANGA), BasicMode.SHOW, BasicModule.MANGA);

        initPutInMap(new AddNovel(), BasicMode.ADD, BasicModule.NOVEL);
        initPutInMap(new NovelContent(), BasicMode.CONTENT, BasicModule.NOVEL);
        initPutInMap(new EditNovel(), BasicMode.EDIT, BasicModule.NOVEL);
        initPutInMap(new SourceableShow(BasicModule.NOVEL), BasicMode.SHOW, BasicModule.NOVEL);

        initPutInMap(new AddSeries(), BasicMode.ADD, BasicModule.SERIES);
        initPutInMap(new SeriesContent(), BasicMode.CONTENT, BasicModule.SERIES);
        initPutInMap(new EditSeries(), BasicMode.EDIT, BasicModule.SERIES);
        initPutInMap(new Show<>(BasicModule.SERIES), BasicMode.SHOW, BasicModule.SERIES);

        initPutInMap(new EnterpriseController(), BasicMode.CONTENT, GuiPaths.Main.ENTERPRISE);
    }

    private void initPutInMap(Controller controller, Mode basicModes, Module basicModules) {
        Map<Mode, Controller> map = moduleModeMap.computeIfAbsent(basicModules, k -> new HashMap<>());
        map.put(basicModes, controller);
    }

    public static ControlComm get() {
        return instance;
    }

    public Controller getController(Module module, Mode mode) {
        return moduleModeMap.get(module).get(mode);
    }

    public void setController(Controller controller, Module module, Mode mode) {
        moduleModeMap.get(module).put(mode, controller);
    }

    public EnterpriseController getEnterpriseController() {
        return (EnterpriseController) moduleModeMap.get(GuiPaths.Main.ENTERPRISE).get(BasicMode.CONTENT);
    }

}
