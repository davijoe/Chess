package robot;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

    int col, row;
    boolean isWhite;

    List<Move> moves = new ArrayList<>();


    public abstract List<Move> generateMoves(Game game);


}
