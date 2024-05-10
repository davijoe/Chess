package robot;

public class ChessMain {

    Game state = new Game();

    public ChessMain(Game state) {
        this.state = state;
    }


    public static void main(String[] args) {
        ChessMain game = new ChessMain(new Game());
        game.minmax(Integer.MIN_VALUE,Integer.MAX_VALUE,true,3);
    }


    public int minmax(int alpha, int beta, boolean isMaximizing, int depth) {

        if(depth == 0) {
            int score = state.evaluate();
        }

        int[] bestMove = null;
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[][] moves = state.getMovesByDepth(depth);

        state.generateMoves(moves);
        if(isMaximizing) {
            while(alpha<beta) {
                for (int[] move : state.moves) {
                    state.makeMove(move[0],move[1],move[2],move[3]);
                    int value = minmax(alpha,beta,false,depth-1);
                    if(value > alpha) {
                        alpha = value;
                        bestMove = move;
                    }
                }
            }
            return alpha;
        }
        else {
            while(alpha<beta) {
                for (int[] move : moves) {
                    state.makeMove(move[0],move[1],move[2],move[3]);
                    int value = minmax(alpha,beta,true,depth-1);
                    if(value < beta) {
                        beta= value;
                        bestMove = move;
                    }
                }
            }
            return beta;
        }
    }
}
