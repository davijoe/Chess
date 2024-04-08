package robot;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves(Board board) {
        System.out.println("fisk");
        return new ArrayList<>();
    }
}
