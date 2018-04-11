package enterprise.gui.controller.show;

import enterprise.gui.controller.SourceableShow;
import enterprise.gui.general.BasicMode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;

/**
 * This class is the Controller for {@code showSourceable.fxml} with
 * {@link BasicModule#NOVEL} and {@link BasicMode#SHOW}.
 */
public class ShowNovel extends SourceableShow {

    @Override
    public Module getModule() {
        return BasicModule.NOVEL;
    }
}
