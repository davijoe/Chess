package robot;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    boolean hasCastle;

    public Rook (int col, int row, boolean isWhite) {
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;
    }

    @Override
    public List<Move> generateMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{-1,0}, {1,0}, {0,1}, {0,-1}};
        int counter = 0;

        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(board.isTileEmpty(row,col)) {

                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }
        return moves;
    }


}
