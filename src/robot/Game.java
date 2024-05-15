package robot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Game {

    int[][] board = new int[8][8];

    int[][] moves = new int[1000][5];
    int[][] movesd1 = new int[1000][5];
    int[][] movesd2 = new int[1000][5];
    int[][] movesd3 = new int[1000][5];
    int[][] movesd4 = new int[1000][5];
    int[][] movesd5 = new int[1000][5];
    int[][] movesd6 = new int[1000][5];
    int[][] movesd7 = new int[1000][5];

    int generateMoveCounter = 0;

    int enPassant;

    boolean whiteLongCastle = true;

    boolean whiteShortCastle = true;
    boolean blackShortCastle = true;
    boolean blackLongCastle = true;
    private int whiteKingRow;
    private int whiteKingCol;
    private int blackKingRow;
    private int blackKingCol;
    char currentPlayer;

    int heuristicValue;
    boolean isMaximizing;

    public Game() {

    }

    /*
    public boolean isTileEmpty(int row, int col) {
        return board[row][col] == 0;
    }
    */

    public boolean isTileEmpty(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8 && board[row][col] == 0;
    }

    public void generateMoves(int[][] moves) {
        if (currentPlayer == 'w') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == 1) {
                        generateRookMoves(row, col, moves);
                    }

                    if (board[row][col] == 2) {
                        generateKnightMoves(row, col, moves);
                    }

                    if (board[row][col] == 3) {
                        generateBishopMoves(row, col, moves);
                    }

                    if (board[row][col] == 4) {
                        generateQueenMoves(row, col, moves);
                    }

                    if (board[row][col] == 5) {
                        generateKingMoves(row, col, moves);
                    }

                    if (board[row][col] == 6 || board[row][col] == 7) {
                        generatePawnMoves(row, col, moves);
                    }
                }
            }
        }
        if (currentPlayer == 'b') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == 8) {
                        generateRookMoves(row, col, moves);
                    }

                    if (board[row][col] == 9) {
                        generateKnightMoves(row, col, moves);
                    }

                    if (board[row][col] == 10) {
                        generateBishopMoves(row, col, moves);
                    }

                    if (board[row][col] == 11) {
                        generateQueenMoves(row, col, moves);
                    }

                    if (board[row][col] == 12) {
                        generateKingMoves(row, col, moves);
                    }
                    if (board[row][col] == 13 || board[row][col] == 14) {
                        generatePawnMoves(row, col, moves);
                    }
                }
            }
        }
    }


    public void pieceMoveLogic(int[][] directions, int row, int col, boolean canSlide, int[][] moves) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if (board[newRow][newCol] > 7 && currentPlayer == 'w' || board[newRow][newCol] <= 7 && currentPlayer == 'b') {
                        addMove(row, col, newRow, newCol, board[row][col], moves);
                    }
                    break;
                } else {
                    addMove(row, col, newRow, newCol, board[row][col], moves);
                }
                if (!canSlide) {
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
    }

    public void generateRookMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateBishopMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateKnightMoves(int row, int col, int[][] moves) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        pieceMoveLogic(directions, row, col, false, moves);
    }

    public void generateQueenMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, true, moves);
    }

    public void generateKingMoves(int row, int col, int[][] moves) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        pieceMoveLogic(directions, row, col, false, moves);
    }

    public void generatePawnMoves(int row, int col, int[][] moves) {
        int piece = board[row][col];
        int direction = (piece == 6) ? 1 : -1; // 1 for white, -1 for black

        if (isTileEmpty(row + direction, col)) {
            if ((direction == 1 && row == 1) || (direction == -1 && row == 6)) {
                if (isTileEmpty(row + 2 * direction, col)) {
                    addMove(row, col, row + 2 * direction, col, piece, moves);
                }
            }
            addMove(row, col, row + direction, col, piece, moves);
        }

        if (col + 1 < 8 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col + 1) && board[row + direction][col + 1] / 8 != piece / 8) {
            addMove(row, col, row + direction, col + 1, piece, moves);
        }
        if (col - 1 >= 0 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col - 1) && board[row + direction][col - 1] / 8 != piece / 8) {
            addMove(row, col, row + direction, col - 1, piece, moves);
        }
    }

    private void addMove(int startRow, int startCol, int endRow, int endCol, int piece, int[][] moves) {
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        generateMoveCounter++;
    }

    public Game(Game currentGame) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                this.board[i][j] = currentGame.board[i][j];
            }
        }
        this.currentPlayer = currentGame.currentPlayer;
        this.heuristicValue = currentGame.heuristicValue;
        this.whiteLongCastle = currentGame.whiteLongCastle;
        this.whiteShortCastle = currentGame.whiteShortCastle;
        this.blackLongCastle = currentGame.blackLongCastle;
        this.blackShortCastle = currentGame.blackShortCastle;
    }

    public Game updateGameState(int moveIndex) {
//        int oldRow = moves[moveIndex][0];
//        int oldCol = moves[moveIndex][1];
//        int newRow = moves[moveIndex][2];
//        int newCol = moves[moveIndex][3];
//        int piece = moves[moveIndex][4];

        Game newGame = new Game(this);
        newGame.board[moves[moveIndex][0]][moves[moveIndex][1]] = 0;
        newGame.board[moves[moveIndex][2]][moves[moveIndex][3]] = moves[moveIndex][4];

        if (currentPlayer == 'w') {
            newGame.currentPlayer = 'b';
        } else {
            newGame.currentPlayer = 'w';
        }
        return newGame;
    }

    //Start på minimax, slet ikke færdig. Er ikke sikker på at den skal ligge her, eller i en main klasse
    public int minimax(int depth, boolean isMaximizing) {
        if (checkForWin()) {
            return Integer.MAX_VALUE;
        }
        if (checkDraw()) {
            return 0;
        }
        if (isMaximizing) {
            int bestValue = -100;
            for (int i = 0; i < moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth + 1, false);
                if (heuristicValue > bestValue) {
                    bestValue = heuristicValue;
                }
            }
            return bestValue;

        } else {
            int bestValue = 100;
            for (int i = 0; i < moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth + 1, true);
                if (heuristicValue < bestValue) {
                    bestValue = heuristicValue;
                }
            }
            return bestValue;
        }

    }

    public boolean isGameFinished() {
        //return false;
        return isCheckmate() || checkDraw() || onlyKingLeft();
    }

    private boolean isCheckmate() {
        //is check can't escape
        int kingRow;
        int kingCol;
        if(currentPlayer=='w') {
            kingRow = whiteKingRow;
            kingCol = whiteKingCol;
        }
        else {
            kingRow = blackKingRow;
            kingCol = blackKingCol;
        }
        return kingInCheck(kingRow, kingCol) && !canEscapeCheck(kingRow, kingCol);
    }

    private boolean kingInCheck(int kingRow, int kingCol) {
        int[] attacker = {kingRow, kingCol};
        if (kingSeeRook(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeeBishop(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeeKnight(kingRow, kingCol) != attacker) {
            return true;
        } else if (kingSeePawn(kingRow, kingCol) != attacker) {
            return true;
        } else return false;
    }

    public int[] kingSeeBishop(int kingRow, int kingCol) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 11 || board[newRow][newCol] == 12) && currentPlayer == 'w' || (board[newRow][newCol] == 3 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return new int[]{kingRow, kingCol};
    }

    public int[] kingSeeKnight(int kingRow, int kingCol) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (board[newRow][newCol] == 2 && currentPlayer == 'b' || board[newRow][newCol] == 10 && currentPlayer == 'w') {
                    return new int[]{newRow, newCol};
                }
                break;
            }
        }
        return new int[]{kingRow, kingCol};
    }

    public int[] kingSeeRook(int kingRow, int kingCol) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 9 || board[newRow][newCol] == 12) && currentPlayer == 'w' || (board[newRow][newCol] == 1 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return new int[]{kingRow, kingCol};
    }

    public int[] kingSeePawn(int kingRow, int kingCol) {
        if (currentPlayer == 'w') {
            if (!isTileEmpty(kingRow + 1, kingCol + 1) || !isTileEmpty(kingRow + 1, kingCol - 1)) {

            }
        } else {

        }
        return new int[]{kingRow, kingCol};
    }

    private boolean canCaptureKing(int attackerRow, int attackerCol, int kingRow, int kingCol) {
        int attacker = board[attackerRow][attackerCol];
        int dy = Math.abs(kingRow - attackerRow);
        int dx = Math.abs(kingCol - attackerCol);

        return switch (attacker) { // white pawn
            // white pawn
            // black pawn
            case 6, 7, 14, 15 -> // black pawn
                    (dy == 1 && dx == 1); // white knight
            case 2, 10 -> // black knight
                    (dy == 2 && dx == 1) || (dy == 1 && dx == 2); // white rook
            case 1, 9 -> // black rook
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0); // white bishop
            case 3, 11 -> // black bishop
                    (dy == dx); // white queen
            case 4, 12 -> // black queen
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0) || (dy == dx); // white king
            case 5, 13 -> // black king
                    (dy <= 1 && dx <= 1);
            default -> false;
        };
    }

    private boolean canEscapeCheck(int kingRow, int kingCol) {
        int[] attacker = new int[2];
        attacker = kingSeeRook(kingRow, kingCol);

        generateMoves(moves);
        for (int[] move : moves) {
            if (move[2] == attacker[0] && move[3] == attacker[1]) {
                return true;
            }
            if (move[4] == board[kingRow][kingCol] && !kingInCheck(move[2], move[3])) {
                return true;
            } else {
                makeMove(move[0], move[1], move[2], move[3]);
                if (!kingInCheck(kingRow, kingCol)) {
                    undoMove(move[0],move[1],move[2],move[3],move[4]);
                    return true;
                }
                else undoMove(move[0],move[1],move[2],move[3],move[4]);
            }
        }
        return false;
    }

    private boolean onlyKingLeft() {
        int numWhitePieces = 0;
        int numBlackPieces = 0;

        for (int[] row : board) {
            for (int piece : row) {
                if (piece == 1 || piece == 2 || piece == 3 || piece == 4 || piece == 5 || piece == 6 || piece == 7) {
                    numWhitePieces++;
                } else if (piece == 9 || piece == 10 || piece == 11 || piece == 12 || piece == 13 || piece == 14 || piece == 15) {
                    numBlackPieces++;
                }
            }
        }

        return numWhitePieces <= 1 && numBlackPieces <= 1;
    }

    public int evaluate() {
        int whiteScore = 0;
        int blackScore = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece != 0) {
                    int pieceValue = getPieceValue(piece);
                    if (piece <= 6) {
                        whiteScore += pieceValue + getPiecePositionValue(piece, row, col);
                    } else {
                        blackScore += pieceValue + getPiecePositionValue(piece, row, col);
                    }
                }
            }
        }

        return whiteScore - blackScore;
    }

    private int getPieceValue(int piece) {
        return switch (piece) {
            case 6, 7, 14, 15 -> //pawns
                    100;
            case 2, 10 ->  //knights
                    300;
            case 3, 11 -> //bishop
                    320;
            case 1, 9 ->  //rook
                    540;
            case 4, 12 -> //queen
                    900;
            case 5, 13 -> //king
                    20000;
            default -> 0;
        };
    }

    private int getPiecePositionValue(int piece, int row, int col) {
        int value = 0;
        switch (piece) {
            case 6:  // White pawn
            case 7:  // White pawn
            case 13:  // Black pawn
            case 14:  // Black pawn
                value = pawnPositionValue(row, col);
                break;
            case 2:  // White knight
            case 9:  // Black knight
                value = knightPositionValue(row, col);
                break;
            case 3:  // White bishop
            case 10: // Black bishop
                value = bishopPositionValue(row, col);
                break;
            case 1:  // White rook
            case 8:  // Black rook
                value = rookPositionValue(row, col);
                break;
            case 4:  // White queen
            case 11: // Black queen
                value = queenPositionValue(row, col);
                break;
            case 5:  // White king
            case 12: // Black king
                value = kingPositionValue(row, col);
                break;
        }
        return value;
    }

    private int pawnPositionValue(int row, int col) {
        int[][] whitePawnPositionValues = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {50, 50, 50, 50, 50, 50, 50, 50},
                {10, 10, 20, 30, 30, 20, 10, 10},
                {5, 5, 10, 25, 25, 10, 5, 5},
                {0, 0, 0, 20, 20, 0, 0, 0},
                {5, -5, -10, 0, 0, -10, -5, 5},
                {5, 10, 10, -20, -20, 10, 10, 5},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        int[][] blackPawnPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackPawnPositionValues[i][j] = whitePawnPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whitePawnPositionValues[row][col] : blackPawnPositionValues[row][col];
    }

    private int knightPositionValue(int row, int col) {
        int[][] whiteKnightPositionValues = {
                {-50, -40, -30, -30, -30, -30, -40, -50},
                {-40, -20, 0, 0, 0, 0, -20, -40},
                {-30, 0, 10, 15, 15, 10, 0, -30},
                {-30, 5, 15, 20, 20, 15, 5, -30},
                {-30, 0, 15, 20, 20, 15, 0, -30},
                {-30, 5, 10, 15, 15, 10, 5, -30},
                {-40, -20, 0, 5, 5, 0, -20, -40},
                {-50, -40, -30, -30, -30, -30, -40, -50}
        };
        int[][] blackKnightPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackKnightPositionValues[i][j] = whiteKnightPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteKnightPositionValues[row][col] : blackKnightPositionValues[row][col];
    }

    private int bishopPositionValue(int row, int col) {
        int[][] whiteBishopPositionValues = {
                {-20, -10, -10, -10, -10, -10, -10, -20},
                {-10, 0, 0, 0, 0, 0, 0, -10},
                {-10, 0, 5, 10, 10, 5, 0, -10},
                {-10, 5, 5, 10, 10, 5, 5, -10},
                {-10, 0, 10, 10, 10, 10, 0, -10},
                {-10, 10, 10, 10, 10, 10, 10, -10},
                {-10, 5, 0, 0, 0, 0, 5, -10},
                {-20, -10, -10, -10, -10, -10, -10, -20}
        };
        int[][] blackBishopPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackBishopPositionValues[i][j] = whiteBishopPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteBishopPositionValues[row][col] : blackBishopPositionValues[row][col];
    }

    private int rookPositionValue(int row, int col) {
        int[][] whiteRookPositionValues = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {5, 10, 10, 10, 10, 10, 10, 5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {-5, 0, 0, 0, 0, 0, 0, -5},
                {0, 0, 0, 5, 5, 0, 0, 0}
        };
        int[][] blackRookPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackRookPositionValues[i][j] = whiteRookPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteRookPositionValues[row][col] : blackRookPositionValues[row][col];
    }

    private int queenPositionValue(int row, int col) {
        int[][] whiteQueenPositionValues = {
                {-20, -10, -10, -5, -5, -10, -10, -20},
                {-10, 0, 0, 0, 0, 0, 0, -10},
                {-10, 0, 5, 5, 5, 5, 0, -10},
                {-5, 0, 5, 5, 5, 5, 0, -5},
                {0, 0, 5, 5, 5, 5, 0, -5},
                {-10, 5, 5, 5, 5, 5, 0, -10},
                {-10, 0, 5, 0, 0, 0, 0, -10},
                {-20, -10, -10, -5, -5, -10, -10, -20}
        };
        int[][] blackQueenPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackQueenPositionValues[i][j] = whiteQueenPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteQueenPositionValues[row][col] : blackQueenPositionValues[row][col];
    }

    private int kingPositionValue(int row, int col) {
        int[][] whiteKingPositionValues = {
                {-50, -40, -30, -20, -20, -30, -40, -50},
                {-30, -20, -10, 0, 0, -10, -20, -30},
                {-30, -10, 20, 30, 30, 20, -10, -30},
                {-30, -10, 30, 40, 40, 30, -10, -30},
                {-30, -10, 30, 40, 40, 30, -10, -30},
                {-30, -10, 20, 30, 30, 20, -10, -30},
                {-30, -30, 0, 0, 0, 0, -30, -30},
                {-50, -30, -30, -30, -30, -30, -30, -50}
        };
        int[][] blackKingPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackKingPositionValues[i][j] = whiteKingPositionValues[7 - i][j];
            }
        }
        return (currentPlayer == 'w') ? whiteKingPositionValues[row][col] : blackKingPositionValues[row][col];
    }

    public boolean checkForWin() {
        return false;
    }

    public boolean checkDraw() {
        return false;
    }

    public int getHeuristicMoveValue() {
        return 0;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getGenerateMoveCounter() {
        return generateMoveCounter;
    }

    public void setGenerateMoveCounter(int generateMoveCounter) {
        this.generateMoveCounter = generateMoveCounter;
    }

    public int getEnPassant() {
        return enPassant;
    }

    public void setEnPassant(int enPassant) {
        this.enPassant = enPassant;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

//    public static void main(String[] args) {
//
//        Game game = new Game();
//
//        //Rook = 1, Knight = 2, Bishop = 3
//        game.board[0][0] = 1;
//        game.board[5][5] = 5;
//
//        //Introducing black pieces
//        game.board[4][4] = 12;
//        game.board[4][3] = 10;
//
//        Random rnd = new Random();
//        System.out.println();
//
//        game.currentPlayer = 'w';
//        System.out.println("White's turn");
//        game.generateMoves(game);
//        game.kingInCheck(5,5);
//
//        Game newState = game.updateGameState(2);
//        System.out.println(newState.currentPlayer);
//        newState.generateMoves(newState);
//        game.kingInCheck(5,5);
//
//
//        Game newnew = newState.updateGameState(1);
//
//        System.out.println(newnew.currentPlayer);
//
//        newnew.generateMoves(newnew);
//
//    }


    public void initializeBoard(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        for (int i = 0; i < 8; i++) {
            int col = 0;
            for (char c : rows[7 - i].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    int piece = pieceFromChar(c);
                    board[i][col] = piece;
                    col++;
                }
            }
        }

        currentPlayer = parts[1].charAt(0);
        findAndSetKing();
    }

    public void findAndSetKing() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = board[i][j];
                if (piece == 5) {
                    whiteKingRow = i;
                    whiteKingCol = j;
                }
                if(piece == 12) {
                    blackKingRow = i;
                    blackKingCol = j;
                }
            }
        }
    }

    public int pieceFromChar(char c) {
        return switch (c) {
            case 'P' -> 6;  // white pawn
            case 'R' -> 1;  // white rook
            case 'N' -> 2;  // white knight
            case 'B' -> 3;  // white bishop
            case 'Q' -> 4;  // white queens
            case 'K' -> 5;  // white king

            case 'p' -> 14;  // black pawn
            case 'r' -> 8;  // black rook
            case 'n' -> 9;  // black knight
            case 'b' -> 10; // black bishop
            case 'q' -> 11; // black queen
            case 'k' -> 12; // black king

            default -> 0;   // empty square
        };
    }

    public void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(pieceToChar(board[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public char pieceToChar(int piece) {
        return switch (piece) {
            case 13, 14 -> 'p';  // black pawn

            case 9 -> 'n';  // black knight
            case 10 -> 'b';  // black bishop
            case 11 -> 'q';  // black queen
            case 12 -> 'k';  // black king
            case 8 -> 'r';  // black rook

            case 6, 7 -> 'P';  // white pawn
            case 1 -> 'R';  // white rook
            case 2 -> 'N';  // white knight
            case 3 -> 'B';  // white bishop
            case 4 -> 'Q';  // white queen
            case 5 -> 'K';  // white king
            default -> '.';  // empty square
        };
    }

    public int makeMove(int startRow, int startCol, int endRow, int endCol) {
        int capturedPiece = board[endRow][endCol];
        int piece = board[startRow][startCol];
        board[startRow][startCol] = 0;
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
        if (piece == 5 && currentPlayer == 'w') {
            whiteKingRow = endRow;
            whiteKingCol = endCol;
        }
        if(piece == 13 && currentPlayer == 'b') {
            blackKingRow = endRow;
            blackKingCol = endCol;
        }
        return capturedPiece;
    }

    //    public void makeMove(int startRow, int startCol, int endRow, int endCol) {
//        if (isValidMove(startRow, startCol, endRow, endCol)) {
//            int piece = board[startRow][startCol];
//            board[startRow][startCol] = 0;
//            board[endRow][endCol] = piece;
//            currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
//
//            if ((piece == 5 && currentPlayer == 'w') || (piece == 13 && currentPlayer == 'b')) {
//                kingRow = endRow;
//                kingCol = endCol;
//            }
//        }
//    }
//
    public void undoMove(int startRow, int startCol, int endRow, int endCol, int piece) {
        if(currentPlayer=='w' && board[endRow][endCol]==5) {
            whiteKingRow = startRow;
            whiteKingCol = startCol;
        }
        else if(currentPlayer=='b' && board[endRow][endCol]==12) {
            blackKingRow = startRow;
            blackKingCol = startCol;
        }
        board[startRow][startCol] = board[endRow][endCol];
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
    }

    public void resetMoves(int[][] moves) {
        for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves[i].length; j++) {
                moves[i][j] = 0;
            }
        }
    }

    public int[][] createNewMoveLists() {
        return new int[1000][5];
    }

    public int[][] getMovesByDepth(int depth) {
        return switch (depth) {
            case 7 -> movesd7;  // black pawn
            case 6 -> movesd6;  // black knight
            case 5 -> movesd5;  // black bishop
            case 4 -> movesd4;  // black queen
            case 3 -> movesd3;  // black king
            case 2 -> movesd2;  // black rook
            case 1 -> movesd1;
            case 0 -> moves;
            default -> moves;
        };
    }

    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startRow >= 8 || startCol < 0 || startCol >= 8 ||
                endRow < 0 || endRow >= 8 || endCol < 0 || endCol >= 8) {
            return false;
        }
        return true;
    }

    public String getFEN() {
        StringBuilder fen = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(pieceToChar(board[i][j]));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i > 0) {
                fen.append('/');
            }
        }
        fen.append(" ");
        fen.append(currentPlayer);
        return fen.toString();
    }


//    public static void main(String[] args) {
//        System.out.println(Runtime.getRuntime().availableProcessors());
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Enter FEN string:");
//        String fen = scanner.nextLine();
//
//        System.out.println("Enter search depth for Minimax:");
//        int depth = scanner.nextInt();
//
//
//        System.out.println("enter time:");
//        int time = scanner.nextInt();
//        int timeLimit = time * 1000;
//
//        Game game = new Game();
//        game.initializeBoard(fen);
//        game.printBoard();
//
//        boolean minimax = false;
//        if (game.currentPlayer == 'w') {
//            minimax = true;
//        }
//
//        LocalDateTime startTime;
//        LocalDateTime endTime;
//
//        startTime = LocalDateTime.now();
//        //single-threaded test
//
//        // iterative deepening test
//
//        int[] previousBestMove = null;
//        int[] bestMove = iterativeDeepening(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, minimax, timeLimit);
//
//        //int[] bestMove = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, minimax);
//        endTime = LocalDateTime.now();
//
//        long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
//        System.out.println("Single-threaded Minimax Time: " + singleThreadedTime + " milliseconds");
//        game.makeMove(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
//        game.printBoard();
//
//        String newFEN = game.getFEN();
//        System.out.println("New FEN string:");
//        System.out.println(newFEN);
//
//
//        /*
//        Game game2 = new Game();
//        game2.initializeBoard(fen);
//        game2.printBoard();
//
//        boolean minimax = false;
//        if (game2.currentPlayer == 'w'){
//            minimax = true;
//        }
//        LocalDateTime startTime;
//        LocalDateTime endTime;
//
//        startTime = LocalDateTime.now();
//        //multi-threaded test
//        int[] result = parallelMinimax(game2, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, minimax);
//
//        endTime = LocalDateTime.now();
//
//        long multiThreadedTime = Duration.between(startTime, endTime).toMillis();
//        System.out.println("Work Stealing Minimax Time: " + multiThreadedTime + " milliseconds");
//
//        System.out.println("Node count: " + result[0]);
//
//        System.out.println("bestmove: " + result[1]+ result[2]+ result[3]+ result[4]);
//        game2.makeMove(result[1], result[2], result[3], result[4]);
//        game2.printBoard();
//
//        String newFEN2 = game2.getFEN();
//        System.out.println("New FEN string:");
//        System.out.println(newFEN2);
//        */
//    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
        Scanner scanner = new Scanner(System.in);

//        System.out.println("Enter FEN string:");
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

//        System.out.println("Enter search depth for Minimax:");
//        int depth = scanner.nextInt();
        int depth = 6;
        Game game = new Game();
        game.initializeBoard(fen);
        game.printBoard();

        LocalDateTime startTime;
        LocalDateTime endTime;

        startTime = LocalDateTime.now();
        //single-threaded test
//        int[] bestMove = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        endTime = LocalDateTime.now();

        long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
        System.out.println("Single-threaded Minimax Time: " + singleThreadedTime + " milliseconds");


//        startTime = LocalDateTime.now();
//        //multi-threaded test
//        int[] result = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//
//        endTime = LocalDateTime.now();
//
//        long multiThreadedTime = Duration.between(startTime, endTime).toMillis();
//        System.out.println("Work Stealing Minimax Time: " + multiThreadedTime + " milliseconds");

//        System.out.println("bestmove: " + bestMove[1] + bestMove[2] + bestMove[3] + bestMove[4]);
//        game.makeMove(bestMove[1], bestMove[2], bestMove[3], bestMove[4]);
        game.printBoard();
        String newFEN = game.getFEN();
        System.out.println("New FEN string:");
        System.out.println(newFEN);
    }

    public static int[] parallelMinimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int parallelism = Runtime.getRuntime().availableProcessors() * 16;
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        ParallelMinimax task = new ParallelMinimax(game, depth, alpha, beta, maximizingPlayer);
        int[] result = pool.invoke(task);
        int nodeCount = task.getNodeCount();
        return new int[]{nodeCount, result[0], result[1], result[2], result[3]};

        /*
        int parallelism = Runtime.getRuntime().availableProcessors() * 16;
        ForkJoinPool pool = new ForkJoinPool(parallelism);
        ParallelMinimax task = new ParallelMinimax(game, depth, alpha, beta, maximizingPlayer);
        return pool.invoke(task);
         */
/*
        ForkJoinPool pool = new ForkJoinPool();
        ParallelMinimax task = new ParallelMinimax(game, depth, alpha, beta, maximizingPlayer);
        return pool.invoke(task);
 */

    }

//    public static int[] iterativeDeepening(Game game, int maxDepth, int alpha, int beta, boolean maximizingPlayer, long timeLimit) {
//        int[][] bestMove = {null};
//
//        long startTime = System.currentTimeMillis();
//
//        for (int depth = 1; depth <= maxDepth; depth++) {
//            final int currentDepth = depth;
//            System.out.println("current depth: " + currentDepth);
//            Thread searchThread = new Thread(() -> {
//                int[][] result = minimax(game, currentDepth, alpha, beta, maximizingPlayer, bestMove[0]);
//                bestMove[0] = result[0];
//            });
//            searchThread.start();
//
//            try {
//                searchThread.join(timeLimit);
//            } catch (InterruptedException e) {
//            }
//
//            if (searchThread.isAlive()) {
//                searchThread.interrupt();
//            }
//
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - startTime >= timeLimit) {
//                break;
//            }
//        }
//
//        return bestMove[0];
//    }

    public static int[][] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, int[] previousBestMove) {
        if (depth == 0) {
            return new int[][]{{}, {game.evaluate()}};
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (previousBestMove != null) {
            int previousMove = game.makeMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            int score = result[1][0] + depth;

            if ((maximizingPlayer && score > bestScore) || (!maximizingPlayer && score < bestScore)) {
                bestScore = score;
                bestMove = previousBestMove;
            }

            if (maximizingPlayer) {
                alpha = Math.max(alpha, bestScore);
            } else {
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                return new int[][]{bestMove, {bestScore}};
            }
        }
        int[][] moves = game.getMovesByDepth(depth);
        game.generateMoveCounter = 0;
        game.generateMoves(moves);

        for (int i = 0; i < game.generateMoveCounter; i++) {
            int[] move = moves[i];

            int previousMove = game.makeMove(move[0], move[1], move[2], move[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            game.undoMove(move[0], move[1], move[2], move[3], previousMove);
            int score = result[1][0] + depth;

            if ((maximizingPlayer && score > bestScore) || (!maximizingPlayer && score < bestScore)) {
                bestScore = score;
                bestMove = move;
            }

            if (maximizingPlayer) {
                alpha = Math.max(alpha, bestScore);
            } else {
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return new int[][]{bestMove, {bestScore}};
    }

}

//    public static int[] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
//        //does not take into account win or lose atm
//        if (depth == 0)  {
//            return new int[] {game.evaluate()};
//        }
//
//        int[] bestMove = null;
//        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
//
//        int[][] moves = game.getMovesByDepth(depth);
//        game.generateMoveCounter = 0;
//        game.generateMoves(moves);
//
//        for (int i = 0; i < game.generateMoveCounter; i++) {
//            int[] move = moves[i];
//            int previousMove;
//            previousMove = game.makeMove(move[0], move[1], move[2], move[3]);
//
//            int[] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer);
//            game.undoMove(move[0],move[1],move[2],move[3],previousMove);
//
//
//            if (result == null) {
//                int score = game.evaluate();
//                if (maximizingPlayer && score > bestScore) {
//                    bestScore = score;
//                    bestMove = move;
//                } else if (!maximizingPlayer && score < bestScore) {
//                    bestScore = score;
//                    bestMove = move;
//                }
//            } else {
//
//                if (maximizingPlayer) {
//                    if (result[0] > bestScore) {
//                        bestScore = result[0];
//                        bestMove = move;
//                    }
//                    alpha = Math.max(alpha, bestScore);
//                } else {
//                    if (result[0] < bestScore) {
//                        bestScore = result[0];
//                        bestMove = move;
//                    }
//                    beta = Math.min(beta, bestScore);
//                }
//                if (beta <= alpha) {
//                    break;
//                }
//            }
//        }
//        return bestMove;
//    }
//}