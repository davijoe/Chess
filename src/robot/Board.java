package robot;

public class Board {

    char[][] board = new char[8][8];

    Move moves = new Move(100);
    char currentPlayer;

    public boolean isTileEmpty(int row, int col) {
        return board[row][col] == 0;
    }

    public void generateMoves() {


        
    }



    public void generateRookMoves() {

    }

    public void generateBishopMoves() {

    }
    public void generateKnightMoves() {

    }
    public void generateQueenMoves() {

    }
    public void generateKingMoves() {

    }

    public void generatePawnMoves() {

    }

}
