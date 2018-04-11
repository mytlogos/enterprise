package enterprise.gui.controller.show;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.controller.Show;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;

/**
 * Created on 25.06.2017.
 * // TODO: 25.08.2017 do the javadoc and functionality
 */
public class ShowSeries extends Show<CreationEntry> {

    @Override
    public Module getModule() {
        return BasicModule.SERIES;
    }

}
