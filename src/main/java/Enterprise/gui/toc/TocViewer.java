package Enterprise.gui.toc;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scrape.sources.toc.CreationRoot;
import scrape.sources.toc.Leaf;
import scrape.sources.toc.intface.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public class TocViewer {

    @FXML
    private Button changeView;

    @FXML
    private AnchorPane viewAnchorPane;

    @FXML
    private TableView<Node> portionsTable;

    private TreeView<Node> portionsTree;

    private ViewMode viewMode = ViewMode.TABLE;
    private CreationRoot rootNode;

    @FXML
    void changeView() {
        viewMode = viewMode.getNext();
        viewMode.change(this);
    }

    public void view(CreationRoot node) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TocViewer.fxml"));
        Parent root;
        try {
            root = loader.load();

            TocViewer controller = loader.getController();

            if (node != null) {
                controller.rootNode = node;
                controller.viewMode.addRoot(controller);
            }

            Stage primaryStage = new Stage();

            primaryStage.setTitle("Enterprise");
            primaryStage.setScene(new Scene(root));
            primaryStage.sizeToScene();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private enum ViewMode {
        TABLE("Baumansicht") {
            @Override
            void change(TocViewer viewer) {
                viewer.viewAnchorPane.getChildren().clear();
                viewer.viewAnchorPane.getChildren().add(viewer.portionsTable);
                viewer.changeView.setText(this.text);
            }

            @Override
            void addRoot(TocViewer viewer) {
                CreationRoot rootNode = viewer.rootNode;
                handleColumns(viewer, rootNode);
                addData(viewer, rootNode);
            }

            private void addData(TocViewer viewer, CreationRoot rootNode) {
                List<? extends Node> collect;

                if (rootNode.hasSubPortion()) {
                    collect = getSecondLevelLeafs(rootNode);
                } else {
                    collect = getFirstLevelLeafs(rootNode);
                }
                viewer.portionsTable.getItems().addAll(collect);
            }

            private void handleColumns(TocViewer viewer, CreationRoot rootNode) {
                List<? extends Node> children = rootNode.getChildren();

                if (!children.isEmpty()) {
                    List<TableColumn<Node, ?>> columns = new ArrayList<>();

                    addNodeColumn(rootNode, columns, rootNode.getSectionType(), CreationRoot::hasSection, node -> node.getParent().getParent());
                    addNodeColumn(rootNode, columns, rootNode.getPortionType(), CreationRoot::hasPortion, Node::getParent);
                    addNodeColumn(rootNode, columns, rootNode.getSubPortionType(), CreationRoot::hasSubPortion, node -> node);

                    addIconColumn(columns, Leaf::getLocalUri, "img/pc.png");
                    addIconColumn(columns, Leaf::getInternetUri, "img/www.png");
                    viewer.portionsTable.getColumns().addAll(columns);
                }
            }

            private void addIconColumn(List<TableColumn<Node, ?>> columns, Function<Leaf, String> function, String imagePath) {
                TableColumn<Node, ImageView> imageColumn = new TableColumn<>();
                imageColumn.setMaxWidth(25);
                imageColumn.setMinWidth(25);
                imageColumn.setCellValueFactory(param -> {
                    Image image = null;

                    String link = function.apply(((Leaf) param.getValue()));
                    if (link != null) {
                        image = new Image(new File(imagePath).toURI().toString());
                    }
                    // TODO: 03.11.2017 implement the function of opening the links, local or internet
                    ImageView view = new ImageView(image);
                    view.setFitHeight(20);
                    view.setFitWidth(20);
                    return new SimpleObjectProperty<>(view);
                });
                columns.add(imageColumn);
            }

            private void addNodeColumn(CreationRoot rootNode, List<TableColumn<Node, ?>> columns, String type, Predicate<CreationRoot> nodePredicate, Function<Node, Node> function) {
                if (nodePredicate.test(rootNode)) {
                    TableColumn<Node, String> column = getNodeColumn(type);
                    column.setCellValueFactory(param -> function.apply(param.getValue()).titleProperty());
                    columns.add(column);
                }
            }

            private List<Node> getFirstLevelLeafs(Node rootNode) {
                return rootNode.
                        getChildren().
                        stream().
                        flatMap(node -> node.getChildren().stream()).
                        collect(Collectors.toList());
            }

            private List<? extends Node> getSecondLevelLeafs(CreationRoot rootNode) {
                return getFirstLevelLeafs(rootNode).
                        stream().
                        flatMap(node -> node.getChildren().stream()).
                        collect(Collectors.toList());
            }

            private TableColumn<Node, String> getNodeColumn(String type) {
                TableColumn<Node, String> column = new TableColumn<>();
                column.setText(type);
                return column;
            }

        },
        TREE("Tabellenansicht") {
            @Override
            void change(TocViewer viewer) {
                viewer.viewAnchorPane.getChildren().clear();

                if (viewer.portionsTree == null) {
                    viewer.portionsTree = new TreeView<>();
                    addRoot(viewer);

                    AnchorPane.setLeftAnchor(viewer.portionsTree, 0d);
                    AnchorPane.setBottomAnchor(viewer.portionsTree, 0d);
                    AnchorPane.setRightAnchor(viewer.portionsTree, 0d);
                    AnchorPane.setRightAnchor(viewer.portionsTree, 0d);
                }

                viewer.viewAnchorPane.getChildren().add(viewer.portionsTree);
                viewer.changeView.setText(this.text);
            }

            @Override
            void addRoot(TocViewer viewer) {
                TreeItem<Node> item = new TreeItem<>(viewer.rootNode);
                item.setExpanded(true);

                resolveTree(viewer.rootNode, item);

                viewer.portionsTree.setRoot(item);
            }

            private void resolveTree(Node node, TreeItem<Node> item) {
                if (!node.getChildren().isEmpty()) {
                    node.getChildren().forEach(nodeConsumer -> {
                        TreeItem<Node> treeItem = new TreeItem<>(nodeConsumer);

                        item.getChildren().add(treeItem);
                        resolveTree(nodeConsumer, treeItem);
                    });
                }
            }
        };

        final String text;

        ViewMode(String text) {
            this.text = text;
        }

        abstract void change(TocViewer viewer);

        ViewMode getNext() {
            ViewMode[] values = ViewMode.values();
            for (int i = 0; i < values.length; i++) {
                ViewMode value = values[i];
                if (value == this) {
                    if (i + 1 == values.length) {
                        return values[0];
                    } else {
                        return values[i + 1];
                    }
                }
            }
            return TABLE;
        }

        abstract void addRoot(TocViewer viewer);
    }

}
