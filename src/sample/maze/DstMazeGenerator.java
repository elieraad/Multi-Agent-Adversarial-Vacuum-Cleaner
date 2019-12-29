package sample.maze;

import sample.Main;

import java.util.*;

public class DstMazeGenerator {

    public static void createMaze(int[][] map, int nbRows, int nbColumns) {
        // Initialize
        for (int i = 0; i < nbRows; i++)
            for (int j = 0; j < nbColumns; j++)
                map[i][j] = Main.empty;

        Random rand = new Random();
        // row for row、col for column
        // Generate random row
        int r = rand.nextInt(nbRows);
        while (r % 2 == 0) {
            r = rand.nextInt(nbRows);
        }
        // Generate random col
        int c = rand.nextInt(nbColumns);
        while (c % 2 == 0) {
            c = rand.nextInt(nbColumns);
        }
        // Starting cell
        map[r][c] = 0;

        //　Allocate the map with recursive method
        recursion(map, nbColumns, nbColumns, r, c);

    }

    private static void recursion(int[][] map, int nbColumns, int nbRows, int r, int c) {
        // 4 random directions
        int[] randDirs = generateRandomDirections();
        // Examine each direction
        for (int randDir : randDirs) {

            switch (randDir) {
                case 1: // Up
                    //　Whether 2 cells up is out or not
                    if (r - 2 <= 0)
                        continue;
                    if (map[r - 2][c] != Main.wall) {
                        map[r - 2][c] = Main.wall;
                        map[r - 1][c] = Main.wall;
                        recursion(map, nbColumns, nbColumns, r - 2, c);
                    }
                    break;
                case 2: // Right
                    // Whether 2 cells to the right is out or not
                    if (c + 2 >= nbColumns - 1)
                        continue;
                    if (map[r][c + 2] != Main.wall) {
                        map[r][c + 2] = Main.wall;
                        map[r][c + 1] = Main.wall;
                        recursion(map, nbColumns, nbColumns, r, c + 2);
                    }
                    break;
                case 3: // Down
                    // Whether 2 cells down is out or not
                    if (r + 2 >= nbRows - 1)
                        continue;
                    if (map[r + 2][c] != Main.wall) {
                        map[r + 2][c] = Main.wall;
                        map[r + 1][c] = Main.wall;
                        recursion(map, nbColumns, nbColumns, r + 2, c);
                    }
                    break;
                case 4: // Left
                    // Whether 2 cells to the left is out or not
                    if (c - 2 <= 0)
                        continue;
                    if (map[r][c - 2] != Main.wall) {
                        map[r][c - 2] = Main.wall;
                        map[r][c - 1] = Main.wall;
                        recursion(map, nbColumns, nbColumns, r, c - 2);
                    }
                    break;
            }
        }
    }

    private static int[] generateRandomDirections() {
        List<Integer> randoms = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Collections.shuffle(randoms);
        int[] result = new int[4];
        for (int i = 0; i < result.length; i++)
            result[i] = randoms.get(i);
        return result;
    }


}