package Enterprise.gui.controller;

import Enterprise.gui.general.Mode;
import Enterprise.modules.Module;

/**
 *
 */
abstract class AbstractController<E extends Enum<E> & Module, R extends Enum<R> & Mode> implements Controller {
    protected E module;
    protected R mode;

    AbstractController() {
        setMode();
        setModule();
    }

    protected abstract void setMode();

    protected abstract void setModule();
}
