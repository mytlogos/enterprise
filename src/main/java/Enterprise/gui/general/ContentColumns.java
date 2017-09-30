package Enterprise.gui.general;

import Enterprise.data.intface.CreationEntry;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ContentColumns<E extends CreationEntry> {
    private Column<E> TITLE = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().titleProperty();
        }

        @Override
        public double getPrefWidth() {
            return 210;
        }

        @Override
        public String getName() {
            return "Titel";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> CREATORNAME = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().getCreator().nameProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return "Autor";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> PRESENTPORTIONS = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
            return param -> param.getValue().getCreation().numPortionProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return module == BasicModules.ANIME ? "Anzahl Episoden" :
                    module == BasicModules.BOOK ? "Anzahl Kapitel" :
                            module == BasicModules.MANGA ? "Anzahl Kapitel" :
                                    module == BasicModules.NOVEL ? "Anzahl Kapitel" :
                                            module == BasicModules.SERIES ? "Anzahl Episoden" :
                                                    "Fehler";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> PROCESSEDPORTIONS = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
            return param -> param.getValue().getUser().processedPortionProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return module == BasicModules.ANIME ? "Gesehen" :
                    module == BasicModules.BOOK ? "Gelesen" :
                            module == BasicModules.MANGA ? "Gelesen" :
                                    module == BasicModules.NOVEL ? "Gelesen" :
                                            module == BasicModules.SERIES ? "Gesehen" :
                                                    "Fehler";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> LASTPORTION = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().dateLastPortionProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return module == BasicModules.ANIME ? "Datum letzter Folge" :
                    module == BasicModules.BOOK ? "Datum letztes Kapitel" :
                            module == BasicModules.MANGA ? "Datum letztes Kapitel" :
                                    module == BasicModules.NOVEL ? "Datum letztes Kapitel" :
                                            module == BasicModules.SERIES ? "Datum letzter Folge" :
                                                    "Fehler";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> OWNSTATUS = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getUser().ownStatusProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return "Eigener Status";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> SERIES = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().seriesProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return "Reihe";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> RATING = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, Number>, ObservableValue<Number>> getCallBack() {
            return param -> param.getValue().getUser().ratingProperty();
        }

        @Override
        public double getPrefWidth() {
            return 80;
        }

        @Override
        public String getName() {
            return "Bewertung";
        }

        @Override
        public boolean getDefaultSelect() {
            return true;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> CREATORSORT = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().getCreator().sortNameProperty();
        }

        @Override
        public double getPrefWidth() {
            return 120;
        }

        @Override
        public String getName() {
            return "Autor/-ensortierung";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> WORKSTATUS = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().workStatusProperty();
        }

        @Override
        public double getPrefWidth() {
            return 120;
        }

        @Override
        public String getName() {
            return "Status";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> COMMENT = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getCreation().getCreator().sortNameProperty();
        }

        @Override
        public double getPrefWidth() {
            return 200;
        }

        @Override
        public String getName() {
            return "Kommentar";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };
    private Column<E> KEYWORDS = new Column<E>() {
        Module module;

        @Override
        public Callback<TableColumn.CellDataFeatures<E, String>, ObservableValue<String>> getCallBack() {
            return param -> param.getValue().getUser().keyWordsProperty();
        }

        @Override
        public double getPrefWidth() {
            return 120;
        }

        @Override
        public String getName() {
            return "Stichwörter";
        }

        @Override
        public boolean getDefaultSelect() {
            return false;
        }

        @Override
        public void setColumnModule(Module module) {
            this.module = module;
        }
    };

    /**
     * Returns the values of this {@code ContentColumns} as an {@code List}.
     *
     * @return list of the enum values
     */
    public List<Column<E>> asList() {
        List<Column<E>> columns = new ArrayList<>();
        columns.add(TITLE);
        columns.add(CREATORNAME);
        columns.add(PRESENTPORTIONS);
        columns.add(PROCESSEDPORTIONS);
        columns.add(LASTPORTION);
        columns.add(OWNSTATUS);
        columns.add(SERIES);
        columns.add(RATING);
        columns.add(CREATORSORT);
        columns.add(WORKSTATUS);
        columns.add(COMMENT);
        columns.add(KEYWORDS);
        return columns;
    }
}
