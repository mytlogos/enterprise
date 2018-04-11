package web;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.sources.SourceAccessor;
import scrape.sources.TestSources;
import web.finder.ScorerUser;
import web.scorer.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Viewer extends Application {
    private final ListView<String> view = new ListView<>();
    private final TableView<ElementViewWrapper> wrapperView = new TableView<>();
    private final TabPane tabPane = new TabPane();

    private FilteredList<ElementViewWrapper> items;
    private ObservableList<ElementViewWrapper> unfilteredItems;

    private ObservableMap<String, ScorerUser> dataFinder = FXCollections.observableMap(new TreeMap<>());
    private ObservableMap<String, Map<String, List<ElementViewWrapper>>> linkScorerData = FXCollections.observableMap(new TreeMap<>());

    private BooleanProperty finishedLoading = new SimpleBooleanProperty();
    private IntegerProperty limit = new SimpleIntegerProperty(10);

    private String scorer = "Scorer";
    private String page = "Page";

    private BorderPane box;
    private TableColumn<ElementViewWrapper, String> linkColumn;
    private TableColumn<ElementViewWrapper, String> scorerColumn;
    private Pattern filterPattern = Pattern.compile("\\[(\\w+)=?(\\w+)?]?|(?:\\s)?(\\w+)|(?:#)(\\w+)|(?:\\.)(\\w+)");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeData(oldValue, newValue));

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(Side.LEFT);
        tabPane.getTabs().add(new Tab(scorer));
        tabPane.getTabs().add(new Tab(page));

        tabPane.setBackground(new Background(new BackgroundFill(Color.CHOCOLATE, null, null)));

        StackPane pane = new StackPane();
        MaskerPane maskerPane = new MaskerPane();
        maskerPane.visibleProperty().bind(finishedLoading.not());
        pane.getChildren().addAll(tabPane, maskerPane);

        primaryStage.setScene(new Scene(pane));
        primaryStage.setMaximized(true);
        primaryStage.show();


        dataFinder.addListener((MapChangeListener<? super String, ? super ScorerUser>) observable -> {
            if (observable.wasAdded()) {
                String link = observable.getKey();

                ScorerUser user = observable.getMap().get(link);
                List<ElementWrapper> wrappers = user.wrappers();
                Map<String, List<ElementViewWrapper>> set = linkScorerData.computeIfAbsent(link, k -> new TreeMap<>());

                List<String> scorerKeys = getScorer().stream().map(Scorer::getScoreKey).collect(Collectors.toList());

                for (ElementWrapper wrapper : wrappers) {

                    for (String scorerKey : scorerKeys) {
                        set.computeIfAbsent(scorerKey, k -> new ArrayList<>()).add(new ElementViewWrapper(wrapper, scorerKey, link));
                    }
                }
            }
        });
        finishedLoading.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                view.getSelectionModel().selectFirst();
                changeFinder(view.getSelectionModel().getSelectedItem());
            }
        });

        limit.addListener((observable, oldValue, newValue) -> changeFinder(view.getSelectionModel().getSelectedItem()));
        new Thread(this::load).start();
    }

    private void changeData(Tab oldValue, Tab current) {
        if (box == null) {
            initPane();
        }

        if (oldValue != null) {
            oldValue.setContent(null);
        }
        current.setContent(box);

        if (current.getText().equals(scorer)) {
            view.getItems().setAll(getScorer().stream().map(Scorer::getScoreKey).collect(Collectors.toList()));
            addScorerColumns();
        } else if (current.getText().equals(page)) {
            view.getItems().setAll(TestSources.testLinks().stream().map(Viewer::getHost).collect(Collectors.toList()));
            view.getItems().sort(Comparator.naturalOrder());
            addPageColumn();
        }
        view.getSelectionModel().selectFirst();
    }

    private void initPane() {
        box = new BorderPane();
        box.setPadding(new Insets(5));
        box.setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));

        view.setPrefWidth(150);

        WebView webView = new WebView();
        webView.setPrefWidth(300);

        final ObservableList<ElementViewWrapper> items = wrapperView.getItems();

        this.items = items.filtered(null);
        this.unfilteredItems = items;

        final SortedList<ElementViewWrapper> sortedItems = this.items.sorted();
        sortedItems.comparatorProperty().bind(wrapperView.comparatorProperty());

        wrapperView.setItems(sortedItems);

        addStandardColumns(wrapperView);

        wrapperView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeView(newValue, webView));
        view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeFinder(newValue));

        HBox top = new HBox();
        top.setSpacing(5);
        top.setPadding(new Insets(0, 0, 5, 0));

        final TextField field = new TextField();
        field.setPromptText("Set Display Limit");
        field.setText(String.valueOf(limit.get()));
        field.textProperty().addListener((observable, oldValue, newValue) -> updateLimit(field));

        final TextField filterField = new TextField();
        filterField.setPromptText("Filter Class, Id, Tag or Attributes");
        filterField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter(newValue));

        top.getChildren().addAll(field, filterField);

        box.setLeft(view);
        box.setCenter(wrapperView);
        box.setRight(webView);
        box.setTop(top);
    }

    private void addPageColumn() {
        if (scorerColumn == null) {
            scorerColumn = new TableColumn<>("Scorer");
            scorerColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().key));
        }

        final ObservableList<TableColumn<ElementViewWrapper, ?>> columns = this.wrapperView.getColumns();
        columns.remove(linkColumn);
        columns.add(2, scorerColumn);
    }

    private void changeView(ElementViewWrapper newValue, WebView webView) {
        if (newValue != null) {
            String html = newValue.wrapper.getElement().outerHtml();
            webView.getEngine().loadContent(html);
        }
    }

    private void changeFinder(String item) {
        if (item == null) {
            return;
        }

        final String text = tabPane.getSelectionModel().getSelectedItem().getText();
        if (text.equals(scorer)) {
            changeScorerFinder(item);

        } else if (text.equals(page)) {
            changePageFinder(item);
        }
    }

    private void changePageFinder(String newValue) {
        Map<String, List<ElementViewWrapper>> map = linkScorerData.get(newValue);


        final List<ElementViewWrapper> items = unfilteredItems;
        items.clear();

        if (map == null) {
            return;
        }

        for (List<ElementViewWrapper> wrappers : new ArrayList<>(map.values())) {

            wrappers.sort(Comparator.comparingDouble(view -> view.value.getScore()));

            final int limit;
            int topNumber = this.limit.get();

            if (topNumber < 0 || wrappers.size() < topNumber) {
                limit = 0;
            } else {
                limit = wrappers.size() - topNumber;
            }

            for (int i = wrappers.size() - 1; i >= limit; i--) {
                ElementViewWrapper wrapper = wrappers.get(i);
                items.add(wrapper);
            }
        }
        wrapperView.getSelectionModel().selectFirst();
    }

    private void updateLimit(TextField field) {
        final String number = field.getText();
        final int limit;

        if (number.isEmpty()) {
            limit = 10;
        } else if (number.length() == 1 && !Character.isDigit(number.charAt(0))) {
            limit = this.limit.get();
        } else {
            if (new BigDecimal(number).compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) <= 0) {
                limit = Integer.MAX_VALUE;
            } else {
                limit = Integer.parseInt(number);
            }
        }
        this.limit.set(limit);
    }

    /**
     * Filters Elements which do not have any of the
     * search parameters.
     *
     * <p>
     * The search for attributes is not clean, it searches
     * for attributes which contains, not equals the given attribute
     * key or value.
     * </p>
     *
     * @param text text with filter parameter
     * @see #filterPattern
     */
    private void updateFilter(String text) {
        items.setPredicate(wrapper -> {
            if (wrapper == null) {
                return false;
            }
            if (text.isEmpty()) {
                return true;
            }

            final Matcher matcher = filterPattern.matcher(text);

            boolean foundNothing = true;

            while (matcher.find()) {
                foundNothing = false;

                final String attr = matcher.group(1);
                final String value = matcher.group(2);
                final String tag = matcher.group(3);
                final String id = matcher.group(4);
                final String className = matcher.group(5);


                if (tag != null) {
                    if (wrapper.wrapper.getElement().tagName().contains(tag)) {
                        return true;
                    }
                }
                if (id != null) {
                    if (wrapper.wrapper.getElement().id().contains(id)) {
                        return true;
                    }
                }
                if (className != null) {
                    if (wrapper.wrapper.getElement().className().contains(className)) {
                        return true;
                    }
                }

                if (attr != null && value == null) {
                    final List<Attribute> attributes = wrapper.wrapper.getElement().attributes().asList();
                    for (Attribute attribute : attributes) {
                        if (attribute.getKey().contains(attr)) {
                            return true;
                        }
                    }
                } else if (attr != null) {
                    final List<Attribute> attributes = wrapper.wrapper.getElement().attributes().asList();
                    for (Attribute attribute : attributes) {
                        if (attribute.getKey().contains(attr) && attribute.getValue().contains(value)) {
                            return true;
                        }
                    }
                }
            }
            return foundNothing;
        });
    }

    private void addScorerColumns() {
        if (linkColumn == null) {
            linkColumn = new TableColumn<>("Link");
            linkColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().link));
        }

        final ObservableList<TableColumn<ElementViewWrapper, ?>> columns = this.wrapperView.getColumns();
        columns.remove(scorerColumn);
        columns.add(2, linkColumn);
    }

    private void addStandardColumns(TableView<ElementViewWrapper> wrapperView) {
        wrapperView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ElementViewWrapper, Number> score = new TableColumn<>("Score");
        score.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().value.getScore())));

        TableColumn<ElementViewWrapper, String> element = new TableColumn<>("Element");
        element.setCellValueFactory(param -> {
            Element node = param.getValue().wrapper.getElement();
            return new SimpleStringProperty(node.tagName() + node.attributes());
        });

        TableColumn<ElementViewWrapper, Number> weight = new TableColumn<>("Weight");
        weight.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().value.getElementWeight())));

        TableColumn<ElementViewWrapper, Number> words = new TableColumn<>("Words");
        words.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getWords())));

        TableColumn<ElementViewWrapper, Number> wordD = new TableColumn<>("WD");
        wordD.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getWordDensity())));

        TableColumn<ElementViewWrapper, Number> wordPer = new TableColumn<>("WP");
        wordPer.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getWordPercentage())));

        TableColumn<ElementViewWrapper, Number> linkPer = new TableColumn<>("LP");
        linkPer.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getLinkPercentage())));

        TableColumn<ElementViewWrapper, Number> linkED = new TableColumn<>("LED");
        linkED.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getLinkElementDensity())));

        TableColumn<ElementViewWrapper, Number> linkWD = new TableColumn<>("LWD");
        linkWD.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getLinkWordDensity())));

        TableColumn<ElementViewWrapper, Number> linkTND = new TableColumn<>("LTND");
        linkTND.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getLinkTextNodeDensity())));

        TableColumn<ElementViewWrapper, Number> DIScore = new TableColumn<>("DIS");
        DIScore.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getDepthIndexScore())));

        TableColumn<ElementViewWrapper, Number> TNOLSize = new TableColumn<>("TNOL");
        TNOLSize.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getOverLimitNodes())));

        TableColumn<ElementViewWrapper, Number> TNSize = new TableColumn<>("TNSize");
        TNSize.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getTextNodeSize())));

        TableColumn<ElementViewWrapper, Number> unique = new TableColumn<>("Unique");
        unique.setCellValueFactory(param -> new SimpleDoubleProperty(round(param.getValue().wrapper.getUniqueness())));

        wrapperView.getColumns().addAll(score, element, weight, words, wordD, TNOLSize, TNSize, wordPer, linkED, linkPer, linkTND, linkWD, DIScore, unique);
    }

    private double round(double value) {
        return BigDecimal.valueOf(Double.isInfinite(value) ? 0 : Double.isNaN(value) ? 0 : value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private void changeScorerFinder(String newScorer) {
        List<ElementViewWrapper> items = unfilteredItems;
        items.clear();

        for (Map.Entry<String, Map<String, List<ElementViewWrapper>>> entry : new ArrayList<>(linkScorerData.entrySet())) {
            Map<String, List<ElementViewWrapper>> scorerMap = entry.getValue();
            List<ElementViewWrapper> wrappers = scorerMap.get(newScorer);

            wrappers.sort(Comparator.comparingDouble(view -> view.value.getScore()));

            final int limit;
            int topNumber = this.limit.get();

            if (topNumber < 0 || wrappers.size() < topNumber) {
                limit = 0;
            } else {
                limit = wrappers.size() - topNumber;
            }

            for (int i = wrappers.size() - 1; i >= limit; i--) {
                ElementViewWrapper wrapper = wrappers.get(i);
                items.add(wrapper);
            }
        }
        wrapperView.getSelectionModel().selectFirst();
    }

    private void load() {
        List<String> links = TestSources.testLinks();
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (String link : links) {
            service.submit(() -> {
                try {
                    Document document = new PreProcessor().cleanWhole(SourceAccessor.getDocument(link));
                    ScorerUser finder = new ScorerUser(document);
                    finder.setMaxScorerVisitor(false);

                    List<Scorer> scorer = getScorer();
                    finder.addScorers(scorer.toArray(new Scorer[0]));
                    finder.execute();

                    System.out.println("found no problem for " + link);
                    dataFinder.put(getHost(link), finder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        service.shutdown();
        while (!service.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Platform.runLater(() -> finishedLoading.set(true));
        System.out.println("nothing left");
    }

    private List<Scorer> getScorer() {
        return Arrays.asList(new NavigationScorer(), new MainScorer(), new SideBarScorer(), new PostScorer(), new ArchiveScorer());
    }

    private static String getHost(String s) {
        Matcher matcher = Pattern.compile("https?://(\\w{0,3}\\.)?([\\w-]+)\\..+").matcher(s);
        if (matcher.find()) {
            return matcher.group(2);
        } else {
            throw new IllegalStateException("no host found for " + s);
        }
    }

    private static class ElementViewWrapper implements Comparable<ElementViewWrapper> {
        private ScorerValue value;
        private ElementWrapper wrapper;
        private String link;
        private String key;

        ElementViewWrapper(ElementWrapper wrapper, String key, String link) {
            this.wrapper = wrapper;
            this.link = link;
            this.key = key;
            this.value = wrapper.getScorerValue(key);
        }

        @Override
        public int compareTo(ElementViewWrapper o) {
            int i = link.length() - o.link.length();

            if (i == 0) {
                i = ((int) value.getScore()) - ((int) value.getScore());
            }

            if (i == 0) {
                i = wrapper.getElement().hashCode() - o.wrapper.getElement().hashCode();
            }
            return i;
        }
    }
}
