package sample.path;

import sample.Main;
import sample.Tuple;
import sample.agents.Agent;

import java.util.LinkedList;
import java.util.List;

public class Min extends PathFinder {

    public static Tuple run(int[][] map, Tuple agent, List<Tuple> dirtAgent, int nbRows, int nbColumns, int depth, boolean maximizingPlayer) {
        int[][] backgroundMap = new int[nbRows][nbColumns];
        for (int i = 0; i < nbRows; i++)
            System.arraycopy(map[i], 0, backgroundMap[i], 0, nbColumns);

        int maxEval = Integer.MIN_VALUE;
        int minEval = Integer.MAX_VALUE;
        Tuple bestMove = new Tuple(-1, -1);
       /* if (maximizingPlayer) {
            System.out.println("Calculating Dirt Agent best move...");
            for (Tuple move : getChildren(backgroundMap, nbRows, nbColumns, dirtAgent)) {
                //make the move
                int temp = backgroundMap[move.row][move.col];
                backgroundMap[dirtAgent.row][dirtAgent.col] = Main.dirt;
                backgroundMap[move.row][move.col] = Main.dirt_agent;

                int agentPrevRow = dirtAgent.row;
                int agentPrevCol = dirtAgent.col;

                dirtAgent.row = move.row;
                dirtAgent.col = move.col;

                int eval = max(backgroundMap, agent, dirtAgent, nbRows, nbColumns, depth);
                System.out.printf("Current move: %s, Current eval %s, Minimum Eval: %s%n", move, eval, minEval);

                // undo the move
                backgroundMap[move.row][move.col] = temp;
                backgroundMap[dirtAgent.row][dirtAgent.col] = Main.dirt_agent;

                dirtAgent.row = agentPrevRow;
                dirtAgent.col = agentPrevCol;

                if (maxEval < eval) {
                    bestMove.row = move.row;
                    bestMove.col = move.col;
                    maxEval = eval;
                    System.out.printf("This is our new best move: %s%n", bestMove);
                }

            }
        } else {
*/
        System.out.println("Calculating Agent best move...");
        for (Tuple move : getChildren(backgroundMap, nbRows, nbColumns, agent)) {

            //make the move
            int temp = backgroundMap[move.row][move.col];
            backgroundMap[agent.row][agent.col] = Main.empty;
            backgroundMap[move.row][move.col] = Main.agent;

            int agentPrevRow = agent.row;
            int agentPrevCol = agent.col;

            agent.row = move.row;
            agent.col = move.col;

            int eval = min(backgroundMap, agent, dirtAgent, nbRows, nbColumns, depth);
            System.out.printf("Current move: %s, Current eval %s, Minimum Eval: %s%n", move, eval, minEval);

            // undo the move
            backgroundMap[move.row][move.col] = temp;
            backgroundMap[agent.row][agent.col] = Main.agent;

            agent.row = agentPrevRow;
            agent.col = agentPrevCol;

            if (minEval > eval) {
                bestMove.row = move.row;
                bestMove.col = move.col;
                minEval = eval;
                System.out.printf("This is our new best move: %s%n", bestMove);
            }
        }
//        }
        System.out.println();
        return bestMove;
    }

    public static int max(int[][] map, Tuple agent, List<Tuple> dirtAgentList, int nbRows, int nbColumns, int depth) {
        if (depth == 0) {
            return getDirtTilesFullyObs(map, nbRows, nbColumns).size();
        }

        int maxEval = Integer.MIN_VALUE;
        for (Tuple dirtAgent : dirtAgentList) {
            List<Tuple> moves = new LinkedList<>();
            List<Integer> temp = new LinkedList<>();
            for (Tuple move : getChildren(map, nbRows, nbColumns, dirtAgent)) {

                //make the move
                temp.add(map[move.row][move.col]);

                map[dirtAgent.row][dirtAgent.col] = Main.dirt;
                map[move.row][move.col] = Main.dirt;

                moves.add(move);
            }

            int eval = min(map, agent, moves, nbRows, nbColumns, depth - 1);

            if (maxEval < eval) {
                maxEval = eval;
            }

            for (int i = 0; i < temp.size(); i++) {
                // undo the move
                Tuple move = moves.get(i);
                map[move.row][move.col] = temp.get(i);
                map[dirtAgent.row][dirtAgent.col] = Main.dirt_agent;
            }

        }

        return maxEval;
    }

    public static int min(int[][] map, Tuple agent, List<Tuple> dirtAgentList, int nbRows, int nbColumns, int depth) {
        if (depth == 0)
            return getDirtTilesFullyObs(map, nbRows, nbColumns).size();


        int minEval = Integer.MAX_VALUE;
        for (Tuple move : getChildren(map, nbRows, nbColumns, agent)) {

            //make the move
            int temp = map[move.row][move.col];
            map[agent.row][agent.col] = Main.empty;
            map[move.row][move.col] = Main.agent;

            int eval = max(map, move, dirtAgentList, nbRows, nbColumns, depth - 1);

            if (minEval > eval) {
                minEval = eval;
            }

            // undo the move
            map[move.row][move.col] = temp;
            map[agent.row][agent.col] = Main.agent;

        }
        return minEval;
    }


}
