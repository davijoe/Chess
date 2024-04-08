package robot;

public class Board {

    char[][] board = new char[8][8];

    Move bestMove;

    char currentPlayer;

    public boolean isTileEmpty(int row, int col) {
        return board[row][col] == 0;
    }

}
