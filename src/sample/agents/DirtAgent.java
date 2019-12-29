package sample.agents;

import sample.Main;
import sample.path.*;
import sample.Tuple;
import sample.path.Minimax;

import java.util.*;

public class DirtAgent extends Tuple {

    private List<Tuple> path;
    private Tuple destination;

    private boolean[][] visited;
    private List<Tuple> destinationAsList;
    private int lastStep;
    private Main main;

    public DirtAgent(Main main, int row, int col) {
        super(row, col);
        path = new LinkedList<>();
        lastStep = Main.empty;
        this.main = main;
        initVisited(main.getNbRows(), main.getNbColumns());
    }

    public void update(int[][] map, int nbRows, int nbColumns) {
        if (!Main.running)
            return;

        if (path.isEmpty()) {
            do {
                destination = PathFinder.getAvailableLocation(map, visited);
                if (destination == null) {
                    initVisited(nbRows, nbColumns);
                    destination = PathFinder.getAvailableLocation(map, visited);
                }
                destinationAsList = new LinkedList<>();
                destinationAsList.add(destination);
            } while (destination == null);
        }

        List<Tuple> moveAsList = new LinkedList<>();
        Tuple move;
        List<Tuple> agentList = new LinkedList<>(main.getAgentList());

        switch (Main.searchAlgorithm) {
            case Dijkstra:
                path = Dijkstra.run(map, nbRows, nbColumns, this, destinationAsList);
                break;
            case Minimax:
                if (main.getAgentList().size() == 1 && main.getDirtAgentList().size() == 1)
                    move = Minimax.run(map, main.getAgentList().get(0), this, nbRows, nbColumns, 6, true);
                else
                    move = Max.run(map, agentList, this, nbRows, nbColumns, 6, true);

                moveAsList.add(move);
                path = moveAsList;
                destination = path.get(0);
                break;
            case AlphaBetaPruning:
                if (main.getAgentList().size() == 1 && main.getDirtAgentList().size() == 1)
                    move = AlphaBetaPruning.run(map, main.getAgentList().get(0), this, nbRows, nbColumns, 10, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                else
                    move = AlphaBetaMax.run(map, agentList, this, nbRows, nbColumns, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

                moveAsList.add(move);
                path = moveAsList;
                destination = path.get(0);
                break;

        }

        if (path.isEmpty())
            initVisited(nbRows, nbColumns);
        else {

            map[row][col] = lastStep;


            Tuple nextStep = path.remove(0);
            if (row != nextStep.row || col != nextStep.col)
                Main.dirtPathSize++;
            row = nextStep.row;
            col = nextStep.col;


            if (destination.row == row && destination.col == col)
                lastStep = Main.dirt;
            else lastStep = map[row][col];


            visited[row][col] = true;
            map[row][col] = Main.dirt_agent;

            main.drawGrid();

        }

    }

    public void initVisited(int nbRows, int nbColumns) {
        //System.out.println("initVisited");
        visited = new boolean[nbRows][nbColumns];
        for (boolean[] rows : visited)
            Arrays.fill(rows, false);

    }

}
