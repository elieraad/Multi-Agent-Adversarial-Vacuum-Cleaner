package sample.path;

import javafx.scene.Parent;
import sample.Main;
import sample.Tuple;

import java.util.*;

public class PathFinder extends Parent {


    public static List<Tuple> getChildren(int[][] map, int nbRows, int nbColumns, Tuple tuple) {
        List<Tuple> children = new ArrayList<>();

        /*row - 1, col-1, row+1, col+1*/
        if (checkTile(map, tuple.row - 1, tuple.col, nbRows, nbColumns))
            children.add(new Tuple(tuple.row - 1, tuple.col));
        if (checkTile(map, tuple.row + 1, tuple.col, nbRows, nbColumns))
            children.add(new Tuple(tuple.row + 1, tuple.col));
        if (checkTile(map, tuple.row, tuple.col - 1, nbRows, nbColumns))
            children.add(new Tuple(tuple.row, tuple.col - 1));
        if (checkTile(map, tuple.row, tuple.col + 1, nbRows, nbColumns))
            children.add(new Tuple(tuple.row, tuple.col + 1));

        children.add(new Tuple(tuple.row, tuple.col));
        Collections.shuffle(children);
        return children;
    }

    private static boolean checkTile(int[][] map, int row, int col, int nbRows, int nbColumns) {
        return row >= 0 && row <= nbRows - 1 && col >= 0 && col <= nbColumns - 1
                && map[row][col] != Main.wall
                && map[row][col] != Main.agent
                && map[row][col] != Main.dirt_agent;
    }

    public static Tuple getAvailableLocation(int[][] map, boolean[][] visited) {
        List<Tuple> availableLocations = new LinkedList<>();
        //System.out.printf("length(%d, %d)", visited.length, visited[0].length);
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++) {
                //System.out.printf("(%d, %d) = %b", row, col, visited[row][col]);
                if (!visited[i][j] && map[i][j] != Main.wall) {
                    availableLocations.add(new Tuple(i, j));
                }
            }
            //System.out.println();
        }

        if (availableLocations.isEmpty())
            return null;
        Collections.shuffle(availableLocations);
        return availableLocations.get(0);
    }

    public static List<Tuple> getDirtTilesFullyObs(int[][] map, int nbRows, int nbColumns) {
        List<Tuple> tuplesOfInterest = new LinkedList<>();

        for (int row = 0; row < nbRows; row++) {
            for (int col = 0; col < nbColumns; col++) {
                if (map[row][col] == Main.dirt)
                    tuplesOfInterest.add(new Tuple(row, col));
            }
        }
        return tuplesOfInterest;
    }

    public static List<Tuple> getDirtTilesPartObs(int[][] map, int nbRows, int nbColumns, Tuple agent, int radius) {
        List<Tuple> tuplesOfInterest = new LinkedList<>();
        int agentX = agent.col;
        int agentY = agent.row;
        //System.out.printf("Agent at: %d, %d%n", agentX, agentY);

        int startRow = Math.max(agentY - radius, 0);
        int endRow = Math.min(agentY + radius, nbRows);

        int startCol = Math.max(agentX - radius, 0);
        int endCol = Math.min(agentX + radius, nbColumns);

        //System.out.printf("Bounding box (%d, %d) -> (%d, %d)%n", startCol, startRow, endCol, endRow);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                //System.out.printf("(%d, %d) = [%d]", col, row, map[row][col]);
                if (map[row][col] == Main.dirt) {
                    //System.out.printf("Dirt at: %d, %d%n", col, row);
                    tuplesOfInterest.add(new Tuple(row, col));
                }
                //System.out.printf("%n");
            }
        }
        //System.out.printf("Tuple of Interest: %s%n", tuplesOfInterest);
        return tuplesOfInterest;
    }

}