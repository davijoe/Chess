package robot;

public class Move {

    int[][] moves;

    public Move(int maxMoves) {
        this.moves = new int[maxMoves][5];
    }

    public int[][] getMoves() {
        return moves;
    }

    public void setMoves(int[][] moves) {
        this.moves = moves;
    }
}
