package enterprise.gui.controller.show;

import enterprise.gui.controller.SourceableShow;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;

/**
 * This class is the Controller for {@code showAnime.fxml} with
 * {@link BasicModule#ANIME} and {@link BasicMode#SHOW}.
 */
public class ShowAnime extends SourceableShow {

    @Override
    public BasicModule getModule() {
        return BasicModule.ANIME;
    }
}
