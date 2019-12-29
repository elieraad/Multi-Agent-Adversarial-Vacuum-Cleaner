package sample;

import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sample.agents.Agent;
import sample.agents.DirtAgent;
import sample.path.PathFinder;
import sample.path.SearchAlgorithm;
import sample.maze.DstMazeGenerator;
import sample.maze.PrimMazeGenerator;
import sample.maze.RecursiveDivision;

import javax.swing.*;

public class Main extends Application {

    // TextFields
    private TextField tfNBRows = new TextField();
    private TextField tfNBColumns = new TextField();
    private TextField tfRadius = new TextField();
    private TextField tfTimer = new TextField();


    private DigitalClock clock;
    private AnimationTimer animationTimer;

    // Labels
    private Label lbRows = new Label("Rows");
    private Label lbColumns = new Label("Columns");
    private Label lbTimer = new Label("Timer");
    private Label lbRadius = new Label("Radius");

    // ImageViews
    private ImageView ivAgent = new ImageView(new Image("sample/images/agent.jpg"));
    private ImageView ivDirtAgent = new ImageView(new Image("sample/images/dirt_agent.jpg"));
    private ImageView ivDirt = new ImageView(new Image("sample/images/dirt.jpg"));
    private ImageView ivKitchen = new ImageView(new Image("sample/images/kitchen.jpg"));

    // Buttons
    private Button btAddAgent = new Button("Add Agent");
    private Button btAddDirtAgent = new Button("Add Dirt Agent");
    private Button btAddWall = new Button("Add Wall");
    private Button btAddDirt = new Button("Add Dirt");
    private Button btStart = new Button("START");
    private Button btStop = new Button("STOP");
    private Button btIncRow = new Button("+");
    private Button btDecRow = new Button("-");
    private Button btIncCol = new Button("+");
    private Button btDecCol = new Button("-");
    private Button btIncTimer = new Button("+");
    private Button btDecTimer = new Button("-");
    private Button btIncRadius = new Button("+");
    private Button btDecRadius = new Button("-");
    private Button btClear = new Button("Clear Map");
    private Button btRandomDirt = new Button("Random Dirt");

    private VBox vBoxHeader = new VBox();
    private HBox hBoxColumns = new HBox();
    private HBox hBoxRows = new HBox();
    private HBox hBoxTimer = new HBox();
    private HBox hBoxRadius = new HBox();
    private HBox hBoxStartStop = new HBox();
    public static HBox hBoxKitchen = new HBox();

    // Combo Box
    private ComboBox<String> cbAlgorithm = new ComboBox<>();
    private ComboBox<String> cbMaze = new ComboBox<>();

    // Panes
    private BorderPane root = new BorderPane();
    private GridPane menu = new GridPane();
    private FlowPane flowPane = new FlowPane();

    public static boolean addDirtAgent, addWall, addAgent, addDirt;

    private int nbRows = 10;
    private int nbColumns = 10;
    private int count;
    private int timer = Integer.MAX_VALUE;
    public static int radius = 1;

    //performance
    private int nbofTiles = nbColumns * nbRows;
    public static int nbOfCleanedTiles = 0;
    public static int cleanPathSize = 0;
    public static int dirtPathSize = 0;

    public static final int empty = 0;
    public static final int wall = 1;
    public static final int dirt = 2;
    public static final int agent = 3;
    public static final int dirt_agent = 4;

    public static int tileSize = 25;
    public static final int maxRows = 20;
    public static final int maxColumns = 20;

    private ToggleGroup observability;
    private RadioButton rbFullyObservable;
    private RadioButton rbPartiallyObservable;

    private ToggleGroup environment;
    private RadioButton rbStatic;
    private RadioButton rbDynamic;

    private int[][] map = new int[maxRows][maxColumns];
    private List<Agent> agentList;
    private List<DirtAgent> dirtAgentList;
    private Grid grid;
    public static boolean running;
    public static SearchAlgorithm searchAlgorithm;
    public static boolean fullyObs;
    public static boolean dynamic;

    @Override
    public void start(Stage primaryStage) {

        agentList = new LinkedList<>();
        dirtAgentList = new LinkedList<>();
        grid = new Grid(this, map);

        observability = new ToggleGroup();
        rbFullyObservable = new RadioButton("Fully Observable");
        rbPartiallyObservable = new RadioButton("Partially Observable");
        rbFullyObservable.setToggleGroup(observability);
        rbPartiallyObservable.setToggleGroup(observability);

        observability.selectToggle(rbFullyObservable);

        fullyObs = true;

        observability.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            cbAlgorithm.getSelectionModel().clearSelection();
            cbAlgorithm.getItems().clear();

            if (observability.getSelectedToggle() == rbFullyObservable) {
                fullyObs = true;
                hBoxRadius.setVisible(false);
                lbRadius.setVisible(false);
                cbAlgorithm.getItems().addAll("Dijkstra", "Minimax", "Alpha Beta Pruning");
            } else if (observability.getSelectedToggle() == rbPartiallyObservable) {
                fullyObs = false;
                hBoxRadius.setVisible(true);
                lbRadius.setVisible(true);
                cbAlgorithm.getItems().addAll("Dijkstra");
            }

        });

        environment = new ToggleGroup();
        rbStatic = new RadioButton("Static");
        rbDynamic = new RadioButton("Dynamic");
        rbStatic.setToggleGroup(environment);
        rbDynamic.setToggleGroup(environment);

        environment.selectToggle(rbStatic);

        dynamic = false;

        environment.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {

            if (environment.getSelectedToggle() == rbStatic) {
                dynamic = false;
                lbTimer.setVisible(false);
                hBoxTimer.setVisible(false);
            } else if (environment.getSelectedToggle() == rbDynamic) {
                dynamic = true;
                lbTimer.setVisible(true);
                hBoxTimer.setVisible(true);
            }

        });

        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.getChildren().addAll(new Label("Observability"), rbFullyObservable, rbPartiallyObservable,
                new Label("Environment"), rbStatic, rbDynamic);

        cbAlgorithm.setId("comboBox");
        cbAlgorithm.getItems().addAll("Dijkstra", "Minimax", "Alpha Beta Pruning");
        cbAlgorithm.setPromptText("Select Algorithm");

        cbAlgorithm.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                switch (newValue) {
                    case "Dijkstra":
                        searchAlgorithm = SearchAlgorithm.Dijkstra;
                        break;
                    case "Minimax":
                        searchAlgorithm = SearchAlgorithm.Minimax;
                        break;
                    case "Alpha Beta Pruning":
                        searchAlgorithm = SearchAlgorithm.AlphaBetaPruning;
                        break;
                }
            } catch (Exception ignored) {

            }
        });

        cbAlgorithm.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText("Select Algorithm");
                else setText(item);
            }
        });

        cbMaze.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText("Select Pattern");
                else setText(item);
            }
        });

        cbMaze.setId("comboBox");
        cbMaze.getItems().addAll("Prim Algorithm", "Recursive Division", "Randomized DST", "Random Map");
        cbMaze.setPromptText("Select Pattern");

        cbMaze.getSelectionModel().selectedItemProperty()
                .addListener((v, oldValue, newValue) -> {
                    if (!cbMaze.getSelectionModel().isSelected(-1)) {
                        switch (newValue) {
                            case "Prim Algorithm":
                                PrimMazeGenerator.createMaze(map, nbRows, nbColumns);
                                break;
                            case "Recursive Division":
                                RecursiveDivision.createMaze(map, nbRows, nbColumns);
                                break;
                            case "Randomized DST":
                                DstMazeGenerator.createMaze(map, nbRows, nbColumns);
                                break;
                            case "Random Map":
                                randomMap();
                                break;
                            default:
                                break;
                        }
                    } else {
                        cbMaze.setPromptText("Select Pattern");
                    }
                    drawGrid();
                });

        Rectangle wallIcon = new Rectangle(30, 30);
        wallIcon.setFill(Color.BLACK);

        ivAgent.setFitHeight(30);
        ivAgent.setFitWidth(30);

        ivDirtAgent.setFitHeight(30);
        ivDirtAgent.setFitWidth(30);

        ivDirt.setFitHeight(30);
        ivDirt.setFitWidth(30);

        vBoxHeader.setSpacing(8);
        vBoxHeader.getChildren().addAll(cbAlgorithm, cbMaze, btRandomDirt, btClear);

        hBoxColumns.setSpacing(5);
        hBoxColumns.setPadding(new Insets(0, 0, 0, 5));
        hBoxColumns.getChildren().addAll(btDecCol, tfNBColumns, btIncCol);

        hBoxTimer.setSpacing(5);
        hBoxTimer.setPadding(new Insets(0, 0, 0, 5));
        hBoxTimer.getChildren().addAll(btDecTimer, tfTimer, btIncTimer);

        hBoxRadius.setSpacing(5);
        hBoxRadius.setPadding(new Insets(0, 0, 0, 5));
        hBoxRadius.getChildren().addAll(btDecRadius, tfRadius, btIncRadius);

        hBoxRows.setSpacing(5);
        hBoxRows.setPadding(new Insets(0, 0, 0, 5));
        hBoxRows.getChildren().addAll(btDecRow, tfNBRows, btIncRow);

        hBoxStartStop.setSpacing(10);
        hBoxStartStop.getChildren().addAll(btStart, btStop);

        menu.setHgap(2);
        menu.setVgap(2);
        menu.setPadding(new Insets(2, 2, 2, 6));

        menu.add(vBoxHeader, 0, 0, 2, 1);

        menu.add(lbRows, 0, 1);
        menu.add(hBoxRows, 1, 1);
        menu.add(lbColumns, 0, 2);
        menu.add(hBoxColumns, 1, 2);

        menu.add(lbTimer, 0, 3);
        menu.add(hBoxTimer, 1, 3);

        menu.add(lbRadius, 0, 4);
        menu.add(hBoxRadius, 1, 4);

        menu.add(ivAgent, 0, 5);
        menu.add(btAddAgent, 1, 5);

        menu.add(ivDirtAgent, 0, 6);
        menu.add(btAddDirtAgent, 1, 6);


        menu.add(ivDirt, 0, 7);
        menu.add(btAddDirt, 1, 7);
        menu.add(wallIcon, 0, 8);
        menu.add(btAddWall, 1, 8);

        menu.add(hBoxStartStop, 0, 15, 2, 1);

        hBoxStartStop.setAlignment(Pos.CENTER);

        flowPane.setMaxHeight(150);
        flowPane.setPrefWidth(198);
        ivKitchen.setFitHeight(150);
        flowPane.setPadding(new Insets(2, 2, 2, 8));
        flowPane.setVgap(5);

        hBoxKitchen.setPadding(new Insets(0, 0, 0, 0));
        hBoxKitchen.getChildren().addAll(flowPane, ivKitchen);

        // Initial Empty sample.Grid
        drawGrid();

        tfRadius.textProperty().addListener((observable, oldValue, newValue) -> {
            radius = Integer.parseInt(newValue.isEmpty() ? "1" : newValue);
        });

        tfTimer.textProperty().addListener((observable, oldValue, newValue) -> {
            timer = Integer.parseInt(newValue.isEmpty() ? "" + Integer.MAX_VALUE : newValue);
        });


        tfNBRows.textProperty().addListener((observable, oldValue, newValue) -> {
            tfNBRows.setStyle("-fx-text-inner-color: black;");
            nbRows = Integer.parseInt(newValue.isEmpty() ? "0" : newValue);
            nbofTiles = nbColumns * nbRows;
            if (nbRows >= maxRows) {
                tfNBRows.setStyle("-fx-text-inner-color: red;");
                nbRows = maxRows;
                tfNBRows.setText(nbRows + "");
            }
            drawGrid();
        });

        tfNBColumns.textProperty().addListener((observable, oldValue, newValue) -> {
            tfNBColumns.setStyle("-fx-text-inner-color: black;");
            nbColumns = Integer.parseInt(newValue.isEmpty() ? "0" : newValue);
            nbofTiles = nbColumns * nbRows;
            if (nbColumns >= maxColumns) {
                tfNBColumns.setStyle("-fx-text-inner-color: red;");
                nbColumns = maxColumns;
                tfNBColumns.setText(nbColumns + "");
            }
            drawGrid();
        });

        lbTimer.setVisible(false);
        hBoxTimer.setVisible(false);
        lbRadius.setVisible(false);
        hBoxRadius.setVisible(false);


        btClear.setOnAction(e -> {
            clearMap();
            drawGrid();
        });
        btRandomDirt.setOnAction(e -> {
            randomDirt();
            drawGrid();
        });

        btAddAgent.setOnAction(e -> {
            addDirtAgent = false;
            addAgent = true;
            addDirt = false;
            addWall = false;
        });
        btAddDirtAgent.setOnAction(e -> {
            addDirtAgent = true;
            addAgent = false;
            addDirt = false;
            addWall = false;
        });
        btAddWall.setOnAction(e -> {
            addDirtAgent = false;
            addAgent = false;
            addDirt = false;
            addWall = true;
        });
        btAddDirt.setOnAction(e -> {
            addDirtAgent = false;
            addAgent = false;
            addDirt = true;
            addWall = false;
        });

        btIncRow.setOnAction(e -> {
            if (nbRows < maxRows)
                nbRows++;
            tfNBRows.setText(nbRows + "");
            nbofTiles = nbColumns * nbRows;
            drawGrid();
        });
        btDecRow.setOnAction(e -> {
            if (nbRows > 0)
                nbRows--;
            tfNBRows.setText(nbRows + "");
            nbofTiles = nbColumns * nbRows;
            drawGrid();
        });
        btIncCol.setOnAction(e -> {
            if (nbColumns < maxColumns)
                nbColumns++;
            tfNBColumns.setText(nbColumns + "");
            nbofTiles = nbColumns * nbRows;
            drawGrid();
        });
        btDecCol.setOnAction(e -> {
            if (nbColumns > 0)
                nbColumns--;
            tfNBColumns.setText(nbColumns + "");
            nbofTiles = nbColumns * nbRows;
            drawGrid();
        });

        btStart.setOnAction(e -> playAnimation());
        btStop.setOnAction(e -> stopAnimation());

        btStart.setId("btStart");
        btStop.setId("btStop");
        btAddAgent.setId("btAddAgent");
        btAddDirtAgent.setId("btAddDirtAgent");
        btAddDirt.setId("btAddDirt");
        btAddWall.setId("btAddWall");
        btClear.setId("btClear");
        btRandomDirt.setId("btRandomDirt");

        tfNBRows.setText(nbRows + "");
        tfNBColumns.setText(nbColumns + "");

        root.setLeft(menu);
        root.setCenter(grid);
        root.setTop(hBoxKitchen);

        root.setAlignment(grid, Pos.TOP_LEFT);

        clock = new DigitalClock();
        vBoxHeader.getChildren().addAll(clock);

        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add("sample/application.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("State-space Search Vacuum Cleaner Agent");
        primaryStage.show();
    }

    private void randomMap() {
        Random rand = new Random();
        for (int row = 0; row < nbRows; row++)
            for (int col = 0; col < nbColumns; col++)
                map[row][col] = (int) (Math.abs(rand.nextGaussian())) == 0 ? 0 : 1;
    }

    private void randomDirt() {
        Random rand = new Random();
        int dirtCount = 0;
        while (dirtCount <= 20) {
            int row = rand.nextInt(nbRows);
            int col = rand.nextInt(nbColumns);
            if (map[row][col] == empty)
                map[row][col] = (int) (Math.abs(rand.nextGaussian())) == 0 ? 0 : 2;
            dirtCount++;
        }
    }

    private void clearMap() {
        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxColumns; col++) {
                map[row][col] = empty;
            }
        }
        cbMaze.getSelectionModel().clearSelection();
        cbMaze.setPromptText("Select Pattern");
        clock.refreshDigits("");

        agentList.clear();
        dirtAgentList.clear();

        nbOfCleanedTiles = 0;
        cleanPathSize = 0;
        dirtPathSize = 0;
    }

    private void disableButtons() {
        addWall = false;
        btAddWall.setDisable(true);
        btStart.setDisable(true);
        btRandomDirt.setDisable(true);
        btClear.setDisable(true);
        if (!dynamic) {
            addDirt = false;
            addAgent = false;
            addDirtAgent = false;
            btAddDirt.setDisable(true);
            btAddAgent.setDisable(true);
            btAddDirtAgent.setDisable(true);
        }
    }

    public void enableButtons() {
        btAddWall.setDisable(false);
        btAddAgent.setDisable(false);
        btAddDirtAgent.setDisable(false);
        btAddDirt.setDisable(false);
        btRandomDirt.setDisable(false);
        btClear.setDisable(false);
        btStart.setDisable(false);
    }

    public void drawGrid() {
        grid.getChildren().clear();
        ivKitchen.setFitWidth(nbColumns * tileSize);

        for (int row = 0; row < nbRows; row++) {
            for (int col = 0; col < nbColumns; col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize);
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(1.0);

                tile.setTranslateX(col * tileSize);
                tile.setTranslateY(row * tileSize);

                switch (map[row][col]) {
                    case wall:
                        tile.setFill(Color.BLACK);
                        break;
                    case dirt:
                        tile.setFill(new ImagePattern(new Image("sample/images/dirt.jpg")));
                        break;
                    case agent:
                        tile.setFill(new ImagePattern(new Image("sample/images/agent.jpg")));
                        break;
                    case dirt_agent:
                        tile.setFill(new ImagePattern(new Image("sample/images/dirt_agent.jpg")));
                        break;
                    default:
                        tile.setFill(Color.WHITE);
                }

                grid.getChildren().add(tile);
            }
        }
    }

    private void runClock() {
        running = true;
        new Thread(() -> {
            long last = System.nanoTime();
            double delta = 0;
            double ns = 1_000_000_000.0 / 1;
            count = 0;
            String str = String.format("Time Elapsed: %d s", count);
            clock.refreshDigits(str);

            while (running) {
                long now = System.nanoTime();
                delta += (now - last) / ns;
                last = now;

                while (delta >= 1) {
                    count++;
                    str = String.format("Time Elapsed: %d s", count);
                    clock.refreshDigits(str);
                    if (count == timer)
                        stopAnimation();

                    delta--;
                }
            }
        }).start();
    }


    private void playAnimation() {
        if (cbAlgorithm.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Please select algorithm.", "Warning!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (agentList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please add agent.", "Warning!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Agent agent : agentList)
            agent.initVisited(nbRows, nbColumns);
        for (DirtAgent agent : dirtAgentList)
            agent.initVisited(nbRows, nbColumns);
        disableButtons();

        runClock();
        System.out.println("New Animation Timer created");
        animationTimer = new AnimationTimer() {
            int fps = 6; //number of update per second.
            double tickPerSecond = 1_000_000_000.0 / fps;
            double delta = 0;
            long lastTime = System.nanoTime();

            @Override
            public void handle(long now) {
                now = System.nanoTime();
                delta += (now - lastTime) / tickPerSecond;
                lastTime = now;
                //System.out.printf("Delta %.2f%n", delta);

                if (delta >= 1) {

                    if (!dynamic && PathFinder.getDirtTilesFullyObs(map, nbRows, nbColumns).isEmpty()) {
                        stopAnimation();
                    }

                    //DONT CHANGE ORDER!!!
                    for (Agent agent : agentList)
                        agent.update(map, nbRows, nbColumns);

                    for (DirtAgent agent : dirtAgentList)
                        agent.update(map, nbRows, nbColumns);
                    //DONT CHANGE ORDER!!!

                    delta = 0;
                    drawGrid();

                }
            }
        };

        animationTimer.start();

    }

    public void stopAnimation() {
        try {
            running = false;
            animationTimer.stop();
            enableButtons();
            JOptionPane.showMessageDialog(null, "Number of Tiles: " + nbofTiles
                            + "\nNumber of Dirty Tiles: " + (nbOfCleanedTiles + PathFinder.getDirtTilesFullyObs(map, nbRows, nbColumns).size())
                            + "\nNumber of Cleaned Tiles: " + nbOfCleanedTiles
                            + "\nNumber of Remaining Tiles: " + PathFinder.getDirtTilesFullyObs(map, nbRows, nbColumns).size()
                            + "\nNumber of Steps of Cleaning Agent(s): " + cleanPathSize
                            + "\nNumber of Steps of Dirty Producer(s): " + dirtPathSize
                            + "\nTime Elapsed: " + count + "s",
                    "Performance", JOptionPane.INFORMATION_MESSAGE);
            nbOfCleanedTiles = 0;
            cleanPathSize = 0;
            dirtPathSize = 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Process didn't start yet!",
                    "Performance", JOptionPane.WARNING_MESSAGE);
        }

    }

    public int getNbColumns() {
        return nbColumns;
    }

    public int getNbRows() {
        return nbRows;
    }

    public GridPane getMenu() {
        return menu;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }

    public List<DirtAgent> getDirtAgentList() {
        return dirtAgentList;
    }

    public static void main(String[] args) {
        launch(args);
    }

}