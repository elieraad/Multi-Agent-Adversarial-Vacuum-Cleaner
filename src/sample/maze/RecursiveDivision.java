package sample.maze;

import sample.Main;

public class RecursiveDivision {

    public static void createMaze(int[][] map, int nbRows, int nbColumns) {
        initFreshMaze(map, nbRows, nbColumns);
        breakWall(map, 0, nbRows / 2, 0, nbColumns / 2);
    }

    private static void initFreshMaze(int[][] map, int nbRows, int nbColumns) {
        for (int r = 0; r < nbRows; r++) {
            for (int c = 0; c < nbColumns; c++) {
                if (r % 2 == 1 && c % 2 == 1) {
                    map[r][c] = Main.empty;
                } else {
                    map[r][c] = Main.wall;
                }
            }
        }
    }

    private static void breakWall(int[][] map, int ra, int rb, int ca, int cb) {
        if (cb - ca <= 1 && rb - ra <= 1) return;
        if (rb - ra >= cb - ca) { //height > width, horizontal cut
            int midR = (ra + rb) / 2;
            int offC = (int) (Math.random() * (cb - ca)) + ca;
            map[midR * 2][offC * 2 + 1] = Main.empty;
            breakWall(map, ra, midR, ca, cb);
            breakWall(map, midR, rb, ca, cb);
        } else {    //width > height, vertical cut
            int midC = (ca + cb) / 2;
            int offR = (int) (Math.random() * (rb - ra)) + ra;
            map[offR * 2 + 1][midC * 2] = Main.empty;
            breakWall(map, ra, rb, ca, midC);
            breakWall(map, ra, rb, midC, cb);
        }
    }
}


