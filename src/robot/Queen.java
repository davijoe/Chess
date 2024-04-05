package robot;

import java.util.List;

public class Queen extends Piece {

    public Queen (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves() {
        return null;
    }
}
