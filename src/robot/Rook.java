package robot;

import java.util.List;

public class Rook extends Piece {

    public Rook (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves() {
        return null;
    }
}
