package robot;

import java.util.List;

public class King extends Piece{

    public King (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves(Game game) {
        return null;
    }
}
