package enterprise.gui.controller.show;

import enterprise.gui.controller.SourceableShow;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;

/**
 * Created on 25.06.2017.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class ShowManga extends SourceableShow {

    @Override
    public Module getModule() {
        return BasicModule.MANGA;
    }

}
