package sample.agents;

import sample.Main;
import sample.path.*;
import sample.Tuple;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Agent extends Tuple {

    private List<Tuple> pathToRandomLocation;
    private List<Tuple> pathToTile;
    private List<Tuple> path;

    private int radius = Main.radius;
    private Main main;

    private boolean[][] visited;

    public Agent(Main main, int row, int col) {
        super(row, col);
        this.main = main;

        pathToRandomLocation = new LinkedList<>();
        pathToTile = new LinkedList<>();
        path = new LinkedList<>();
    }

    public void update(int[][] map, int nbRows, int nbColumns) {
        if (!Main.running) {
            return;
        }
        int stuck = 0;
        //if fully observable
        if (Main.fullyObs) {

            List<Tuple> moveAsList = new LinkedList<>();
            Tuple move;
            List<Tuple> dirtAgentList = new LinkedList<>(main.getDirtAgentList());

            switch (Main.searchAlgorithm) {
                case Dijkstra:
                    List<Tuple> neighbors = PathFinder.getDirtTilesFullyObs(map, nbRows, nbColumns);
                    path = Dijkstra.run(map, nbRows, nbColumns, new Tuple(row, col), neighbors);
                    break;
                case Minimax:
                    if (main.getAgentList().size() == 1 && main.getDirtAgentList().size() == 1)
                        move = Minimax.run(map, this, dirtAgentList.get(0), nbRows, nbColumns, 6, false);
                    else
                        move = Min.run(map, this, dirtAgentList, nbRows, nbColumns, 6, false);
                    moveAsList.add(move);
                    path = moveAsList;
                    break;
                case AlphaBetaPruning:
                    if (main.getAgentList().size() == 1 && main.getDirtAgentList().size() == 1)
                        move = AlphaBetaPruning.run(map, this, dirtAgentList.get(0), nbRows, nbColumns, 10, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    else
                        move = AlphaBetaMin.run(map, this, dirtAgentList, nbRows, nbColumns, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

                    moveAsList.add(move);
                    path = moveAsList;
                    break;
            }

            //if path is empty stop the agent and return
            if (path == null || path.isEmpty()) {
                return;
            }

        } else {    //if partially observable

            //if there is no dirt tile in
            List<Tuple> neighbors = PathFinder.getDirtTilesPartObs(map, nbRows, nbColumns, this, radius);
            pathToTile = Dijkstra.run(map, nbRows, nbColumns, this, neighbors);

            if (pathToTile == null || pathToTile.isEmpty()) {
                //check agent memory for a path to random location
                if (pathToRandomLocation == null || pathToRandomLocation.isEmpty()) {
                    do {

                        stuck++;
                        if (stuck > nbColumns * nbRows) {
                            System.out.println("DUMP");
                            main.stopAnimation();
                            return;
                        }

                        //get an available location
                        Tuple location = PathFinder.getAvailableLocation(map, visited);
                        //if no available location found stop the agent and return
                        if (location == null) {
                            main.stopAnimation();
                            return;
                        }

                        //if an available location was found get path to it and load it in agents memory
                        List<Tuple> locationAsList = new LinkedList<>();
                        locationAsList.add(location);
                        pathToRandomLocation = Dijkstra.run(map, nbRows, nbColumns, this, locationAsList);
                    } while (pathToRandomLocation.isEmpty());

                }

                // make the agent go in random direction
                path = pathToRandomLocation;

            } else {    // a dirt tile in sight
                //make the agent go after the dirt tile and delete the random path from its memory
                path = pathToTile;
                pathToRandomLocation.clear();
            }

        }

        map[row][col] = Main.empty;

        Tuple nextStep = path.remove(0);
        if (row != nextStep.row || col != nextStep.col)
            Main.cleanPathSize++;

        row = nextStep.row;
        col = nextStep.col;

        if (!Main.fullyObs) {
            visited[row][col] = true;
            if (map[row][col] == Main.dirt) {
                System.out.println("Visited tiles erased from memory!");
                initVisited(nbRows, nbColumns);
            }
        }

        if (map[row][col] == Main.dirt)
            Main.nbOfCleanedTiles++;

        map[row][col] = Main.agent;
        main.drawGrid();
    }

    public void initVisited(int nbRows, int nbColumns) {
        //System.out.println("initVisited");
        visited = new boolean[nbRows][nbColumns];
        for (boolean[] rows : visited)
            Arrays.fill(rows, false);

    }
}
