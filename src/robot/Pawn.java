package robot;

import java.util.List;

public class Pawn extends Piece {

    public Pawn (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves() {
        return null;
    }
}
