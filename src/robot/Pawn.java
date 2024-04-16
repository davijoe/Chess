package robot;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    boolean canPassent;

    public Pawn (int col, int row, boolean isWhite, boolean canPassent) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
        this.canPassent = canPassent;

    }

    @Override
    public List<Move> generateMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] attackDirections = {{-1,1},{1,1}};
        //Other stuff, not done
        return moves;
    }
}
