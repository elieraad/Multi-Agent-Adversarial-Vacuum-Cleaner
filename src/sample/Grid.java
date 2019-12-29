package sample;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import sample.agents.Agent;
import sample.agents.DirtAgent;

public class Grid extends GridPane {

    public Grid(Main main, int[][] map) {
//        setMaxSize(500,500);
//        setAlignment(Pos.CENTER);

        setOnMouseDragged(e -> {
            int row = (int) ((e.getSceneY() - getLayoutY()) / Main.tileSize);
            int col = (int) ((e.getSceneX() - getLayoutX()) / Main.tileSize);

            if (e.getButton() == MouseButton.PRIMARY) {
                if (Main.addDirt) {
                    map[row][col] = Main.dirt;
                } else if (Main.addWall) {
                    map[row][col] = Main.wall;
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {

                if (map[row][col] == Main.agent) {
                    main.getAgentList().remove(new Agent(main, row, col));
                } else if (map[row][col] == Main.dirt_agent) {
                    main.getDirtAgentList().remove(new DirtAgent(main, row, col));
                }
                map[row][col] = Main.empty;
            }
            main.drawGrid();
        });


        setOnMouseClicked(e -> {
            int row = (int) ((e.getSceneY() - getLayoutY()) / Main.tileSize);
            int col = (int) ((e.getSceneX() - getLayoutX()) / Main.tileSize);
            if (row <= main.getNbRows() && col <= main.getNbColumns()) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (Main.addAgent) {
                        Agent agent = new Agent(main, row, col);
                        main.getAgentList().add(agent);
                        map[row][col] = Main.agent;
                    } else if (Main.addDirtAgent) {
                        DirtAgent agent = new DirtAgent(main, row, col);
                        main.getDirtAgentList().add(agent);
                        map[row][col] = Main.dirt_agent;
                    } else if (Main.addDirt) {
                        map[row][col] = Main.dirt;
                    } else if (Main.addWall) {
                        map[row][col] = Main.wall;
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if (map[row][col] == Main.agent) {
                        Agent agent = new Agent(main, row, col);
                        main.getAgentList().remove(agent);
                    } else if (map[row][col] == Main.dirt_agent) {
                        DirtAgent agent = new DirtAgent(main, row, col);
                        main.getDirtAgentList().remove(agent);
                    }
                    map[row][col] = Main.empty;
                }
                main.drawGrid();
            }
        });

    }

}
