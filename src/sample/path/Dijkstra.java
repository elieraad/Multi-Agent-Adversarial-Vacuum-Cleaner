package sample.path;

import sample.Main;
import sample.Tuple;

import java.util.*;

public class Dijkstra extends PathFinder {

    public static List<Tuple> run(int[][] map, int n, int m, Tuple source, List<Tuple> destinations) {
        Map<Integer, Tuple> toReturn = new TreeMap<>();

        // If source is a wall skip
        if (map[source.row][source.col] == Main.wall) {
            //System.out.println("Source is a wall");
            return null;
        }

        // If destination is empty skip
        int numVertices = n * m;
        if (destinations.isEmpty()) {
            //System.out.println("No destination");
            return null;
        }

        //System.out.printf("%-20s %-15s%s%n", "Vertex", "Distance", "Path");
        for (Tuple destination : destinations) {
            int[][] distances = new int[n][m];
            boolean[][] visited = new boolean[n][m];
            Tuple[][] path = new Tuple[n][m];

            //Initialize the distances to INF and visited to false
            for (int i = 0; i < n; i++) {
                Arrays.fill(distances[i], Integer.MAX_VALUE);
                Arrays.fill(visited[i], false);
            }

            distances[source.row][source.col] = 0; //distance to source node is zero
            path[source.row][source.col] = null; //no path from source node to source

            for (int i = 0; i < numVertices; i++) {

                Tuple nearestVertex = null;
                int shortestDistance = Integer.MAX_VALUE;

                for (int row = 0; row < n; row++) {
                    for (int col = 0; col < m; col++) {

                        //pick the nearest vertex that's not yet visited
                        if (!visited[row][col] && distances[row][col] < shortestDistance) {
                            nearestVertex = new Tuple(row, col);
                            shortestDistance = distances[row][col];
                        }
                    }
                }

                //Mark the picked vertex as visited
                if (nearestVertex != null) {
                    if (nearestVertex.equals(destination)) {
                        Tuple t = returnSolution(source, nearestVertex, distances, path);
                        if (t != null)
                            toReturn.put(t.getPath().size(), t);
                        break;
                    }

                    visited[nearestVertex.row][nearestVertex.col] = true;

                    List<Tuple> children = getChildren(map, n, m, nearestVertex);
                    for (Tuple child : children) {

                        if (shortestDistance + 1 < distances[child.row][child.col]) {
                            path[child.row][child.col] = nearestVertex;
                            distances[child.row][child.col] = shortestDistance + 1;
                        }
                    }
                }

            }
        }
        //System.out.printf("toReturn %s%n", toReturn);
        //System.out.println();

        int shortestPathKey = toReturn.keySet().stream().findFirst().orElse(0);
        if (shortestPathKey != 0) {
            List<Tuple> path = toReturn.get(shortestPathKey).getPath();
            path.remove(0);
            return path;
        }
        return new LinkedList<>();
    }

    private static Tuple returnSolution(Tuple source, Tuple destination, int[][] distances, Tuple[][] path) {
        List<Tuple> pathToDest = new LinkedList<>();
        Tuple child = null;
        if (!source.equals(destination) && distances[destination.row][destination.col] < Integer.MAX_VALUE) {
            //System.out.printf("%-20s %-15s", source + " -> " + destination, distances[destination.row][destination.col]);
            returnPath(destination, path, pathToDest);
            child = new Tuple(destination.row, destination.col, distances[destination.row][destination.col], pathToDest);
            source.addChild(child);
            //System.out.println();
        }

        return child;
    }

    private static void returnPath(Tuple currentVertex, Tuple[][] path, List<Tuple> children) {

        if (currentVertex == null) {
            return;
        }

        returnPath(path[currentVertex.row][currentVertex.col], path, children);
        children.add(currentVertex);
        //System.out.print(currentVertex + " ");
    }

}
