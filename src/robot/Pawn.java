package robot;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] attackDirections = {{-1,1},{1,1}};
        //Other stuff, not done
        return moves;
    }
}
