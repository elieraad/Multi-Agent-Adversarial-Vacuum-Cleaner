package sample.maze;

import sample.Main;
import sample.Tuple;

import java.util.ArrayList;
import java.util.Arrays;

public class PrimMazeGenerator {

    public static void createMaze(int[][] map, int nbRows, int nbColumns) {
        // build map and initialize with only walls
        for (int[] row : map) {
            Arrays.fill(row, Main.wall);
        }
        Tuple parent = new Tuple((int) (Math.random() * nbRows), (int) (Math.random() * nbColumns), null);

        // iterate through direct neighbors of node
        ArrayList<Tuple> frontier = new ArrayList<>();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (map[parent.row + x][parent.col + y] == Main.empty)
                        continue;
                } catch (Exception e) { // ignore ArrayIndexOutOfBounds
                    continue;
                }
                // add eligible Tuples to frontier
                frontier.add(new Tuple(parent.row + x, parent.col + y, parent));
            }

        while (!frontier.isEmpty()) {

            // pick current node at random
            Tuple currNode = frontier.remove((int) (Math.random() * frontier.size()));
            Tuple oppsiteNode = currNode.opposite();
            try {
                // if both node and its opposite are walls
                if (map[currNode.row][currNode.col] == Main.wall && map[oppsiteNode.row][oppsiteNode.col] == Main.wall) {

                    // open path between the nodes
                    map[currNode.row][currNode.col] = Main.empty;
                    map[oppsiteNode.row][oppsiteNode.col] = Main.empty;

                    // iterate through direct neighbors of node, same as earlier
                    for (int x = -1; x <= 1; x++)
                        for (int y = -1; y <= 1; y++) {
                            if (x == 0 && y == 0 || x != 0 && y != 0)
                                continue;
                            try {
                                if (map[oppsiteNode.row + x][oppsiteNode.col + y] == Main.empty)
                                    continue;
                            } catch (Exception e) {
                                continue;
                            }
                            frontier.add(new Tuple(oppsiteNode.row + x, oppsiteNode.col + y, oppsiteNode));
                        }
                }

            } catch (Exception e) {
                // ignore NullPointerException and ArrayIndexOutOfBounds
            }
        }
    }

}
