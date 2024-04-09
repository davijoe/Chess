package robot;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves(Board board) {

        List<Move> moves = new ArrayList<>();
        int[][] directions = {{-1,0}, {-1,1}, {1,0}, {1,1}, {0,1}, {1,-1}, {0,-1}, {-1,-1}};

        int counter = 0;

        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(board.isTileEmpty(row,col)) {
                    Move move = new Move(row,col,rowNew,colNew);
                    moves.add(move);
                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }
        return moves;
    }
}
