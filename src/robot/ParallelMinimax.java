package robot;

import java.util.concurrent.RecursiveTask;

public class ParallelMinimax extends RecursiveTask<int[]> {
    private Game game;
    private int depth;
    private int alpha;
    private int beta;
    private boolean maximizingPlayer;
    private int nodeCount;

    public ParallelMinimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        this.game = game;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
        this.maximizingPlayer = maximizingPlayer;
        this.nodeCount = 1;
    }
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    protected int[] compute() {
        if (depth == 0 || game.isGameFinished()) {
            return null;
        }

        game.generateMoves(game);

        if (game.generateMoveCounter == 0) {
            return null;
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < game.generateMoveCounter; i++) {
            int[] move = game.moves[i];
            Game newGame = new Game(game);
            newGame.makeMove(move[0], move[1], move[2], move[3]);

            ParallelMinimax task = new ParallelMinimax(newGame, depth - 1, alpha, beta, !maximizingPlayer);
            task.fork();
            int[] result = task.join();

            if (result != null) {
                nodeCount += task.getNodeCount();
            } else {
                nodeCount++;
            }

            int score = newGame.evaluate() + depth;


            if ((maximizingPlayer && score > bestScore) || (!maximizingPlayer && score < bestScore)) {
                bestScore = score;
                bestMove = move;
            }

            if (maximizingPlayer) {
                alpha = Math.max(alpha, bestScore);
            } else {
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return bestMove;
    }
}