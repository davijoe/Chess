package robot;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Game {

//region Global Variables and Constants
    int[][] board = new int[8][8];

    int[][] moves = new int[1000][6];
    int[][] movesd1 = new int[1000][6];
    int[][] movesd2 = new int[1000][6];
    int[][] movesd3 = new int[1000][6];
    int[][] movesd4 = new int[1000][6];
    int[][] movesd5 = new int[1000][6];
    int[][] movesd6 = new int[1000][6];
    int[][] movesd7 = new int[1000][6];

    int generateMoveCounter = 0;
    int enPassant;
    boolean whiteLongCastle, whiteShortCastle, blackShortCastle, blackLongCastle = true;
    private int kingRow, kingCol;
    char currentPlayer;
    int heuristicValue;

    public Game() {

    }
//endregion
//region Getter Setter
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
    //endregion

//region Moves
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
        int direction = (piece == 6 || piece == 7) ? 1 : -1; // * 1 for white, -1 for black

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
        int capturedPiece = board[endRow][endCol];
        int score = computeMoveScore(piece, capturedPiece);
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        moves[generateMoveCounter][5] = score;
        generateMoveCounter++;
    }

    private int computeMoveScore(int piece, int capturedPiece) {
        int pieceValue = getPieceMoveValue(piece);
        int captureValue = capturedPiece == 0 ? 0 : getPieceValue(capturedPiece);
        return captureValue * 10 + pieceValue;
    }

    private int getPieceMoveValue(int piece) {
        switch (piece) {
            case 1: case 8: return 5;   // Rook
            case 2: case 9: return 3;   // Knight
            case 3: case 10: return 3;  // Bishop
            case 4: case 11: return 9;  // Queen
            case 5: case 12: return 10; // King
            case 6: case 7: case 13: case 14: return 1; // Pawn
            default: return 0;
        }
    }

    public boolean isTileEmpty(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8 && board[row][col] == 0;
    }

    public void generateMoves(int[][] moves) {
        generateMoveCounter = 0;

        if (currentPlayer == 'w') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    switch (board[row][col]) {
                        case 1: generateRookMoves(row, col, moves); break;
                        case 2: generateKnightMoves(row, col, moves); break;
                        case 3: generateBishopMoves(row, col, moves); break;
                        case 4: generateQueenMoves(row, col, moves); break;
                        case 5: generateKingMoves(row, col, moves); break;
                        case 6: case 7: generatePawnMoves(row, col, moves); break;
                    }
                }
            }
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    switch (board[row][col]) {
                        case 8: generateRookMoves(row, col, moves); break;
                        case 9: generateKnightMoves(row, col, moves); break;
                        case 10: generateBishopMoves(row, col, moves); break;
                        case 11: generateQueenMoves(row, col, moves); break;
                        case 12: generateKingMoves(row, col, moves); break;
                        case 13: case 14: generatePawnMoves(row, col, moves); break;
                    }
                }
            }
        }

        Arrays.sort(moves, 0, generateMoveCounter, (move1, move2) -> Integer.compare(move2[5], move1[5]));
    }

    public int[][] filterValidMoves(int[][] moves) {
        int validMoveCount = 0;
        for (int i = 0; i < moves.length; i++) {
            if (moves[i][0] != 0 || moves[i][1] != 0 || moves[i][2] != 0 || moves[i][3] != 0 || moves[i][4] != 0) {
                validMoveCount++;
            }
        }

        int[][] validMoves = new int[validMoveCount][5];
        int index = 0;
        for (int i = 0; i < moves.length; i++) {
            if (moves[i][0] != 0 || moves[i][1] != 0 || moves[i][2] != 0 || moves[i][3] != 0 || moves[i][4] != 0) {
                validMoves[index++] = moves[i];
            }
        }

        return validMoves;
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

    public int makeMove(int startRow, int startCol, int endRow, int endCol) {
        int capturedPiece = board[endRow][endCol];
        int piece = board[startRow][startCol];
        board[startRow][startCol] = 0;
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
        if ((piece == 5 && currentPlayer == 'w') || (piece == 13 && currentPlayer == 'b')) {
            kingRow = endRow;
            kingCol = endCol;
        }
        return capturedPiece;
    }

    public void undoMove(int startRow, int startCol, int endRow, int endCol, int piece) {
        board[startRow][startCol] = board[endRow][endCol];
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
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
            default -> moves;
        };
    }
    //endregion
//region Minimax and Iterative Deepening


    public static int[][] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, int[] previousBestMove) {
        if (depth == 0) {
            int score = game.evaluate();
            return new int[][]{{}, {score}};
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (previousBestMove != null) {
            int previousMove;
            previousMove = game.makeMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            game.undoMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3],previousMove);
            int score = result[1][0] - (maximizingPlayer ? depth : +depth);

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
        game.generateMoves(moves);
        int[][] validMoves = game.filterValidMoves(moves);

        for (int i = 0; i < validMoves.length; i++) {
            int[] move = validMoves[i];
            int previousMove = game.makeMove(move[0], move[1], move[2], move[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            game.undoMove(move[0],move[1],move[2],move[3],previousMove);
            int score = result[1][0] - (maximizingPlayer ? depth : +depth);

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

    public static int[] iterativeDeepening(Game game, int maxDepth, int alpha, int beta, boolean maximizingPlayer, long timeLimit) {
        int[][] bestMove = {null};

        long startTime = System.currentTimeMillis();

        for (int depth = 1; depth <= maxDepth; depth++) {
            final int currentDepth = depth;
            System.out.println("current depth: " + currentDepth);
            Game newGame = new Game(game);
            Thread searchThread = new Thread(() -> {
                int[][] result = minimax(newGame, currentDepth, alpha, beta, maximizingPlayer, bestMove[0]);
                bestMove[0] = result[0];
            });
            searchThread.start();

            try {
                searchThread.join(timeLimit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (searchThread.isAlive()) {
                searchThread.interrupt();
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeLimit) {
                break;
            }
        }

        return bestMove[0];
    }
//endregion
//region Heuristic Position Values
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
private int mgPawnPositionValue(int row, int col) {
    int[][] whitePawnPositionValues = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {98, 134, 61, 95, 68, 126, 34, -11},
            {-6, 7, 26, 31, 65, 56, 25, -20},
            {-14, 13, 6, 21, 23, 12, 17, -23},
            {-27, -2, -5, 12, 17, 6, 10, -25},
            {-26, -4, -4, -10, 3, 3, 33, -12},
            {-35, -1, -20, -23, -15, 24, 38, -22},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    int[][] blackPawnPositionValues = new int[8][8];
    for (int i = 0; i < 8; i++) {
        System.arraycopy(whitePawnPositionValues[7 - i], 0, blackPawnPositionValues[i], 0, 8);
    }
    return (currentPlayer == 'w') ? whitePawnPositionValues[row][col] : blackPawnPositionValues[row][col];
}
    private int egPawnPositionValue(int row, int col) {
        int[][] whitePawnPositionValues = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {178, 173, 158, 134, 147, 132, 165, 187},
                {94, 100, 85, 67, 56, 53, 82, 84},
                {32, 24, 13, 5, -2, 4, 17, 17},
                {13, 9, -3, -7, -7, -8, 3, -1},
                {4, 7, -6, 1, 0, -5, -1, -8},
                {13, 8, 8, 10, 13, 0, 2, -7},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        int[][] blackPawnPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whitePawnPositionValues[7 - i], 0, blackPawnPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whitePawnPositionValues[row][col] : blackPawnPositionValues[row][col];
    }

    private int mgKnightPositionValue(int row, int col) {
        int[][] whiteKnightPositionValues = {
                {-167, -89, -34, -49, 61, -97, -15, -107},
                {-73, -41, 72, 36, 23, 62, 7, -17},
                {-47, 60, 37, 65, 84, 129, 73, 44},
                {-9, 17, 19, 53, 37, 69, 18, 22},
                {-13, 4, 16, 13, 28, 19, 21, -8},
                {-23, -9, 12, 10, 19, 17, 25, -16},
                {-29, -53, -12, -3, -1, 18, -14, -19},
                {-105, -21, -58, -33, -17, -28, -19, -23}
        };
        int[][] blackKnightPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteKnightPositionValues[7 - i], 0, blackKnightPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteKnightPositionValues[row][col] : blackKnightPositionValues[row][col];
    }

    private int egKnightPositionValue(int row, int col) {
        int[][] whiteKnightPositionValues = {
                {-58, -38, -13, -28, -31, -27, -63, -99},
                {-25, -8, -25, -2, -9, -25, -24, -52},
                {-24, -20, 10, 9, -1, -9, -19, -41},
                {-17, 3, 22, 22, 22, 11, 8, -18},
                {-18, -6, 16, 25, 16, 17, 4, -18},
                {-23, -3, -1, 15, 10, -3, -20, -22},
                {-42, -20, -10, -5, -2, -20, -23, -44},
                {-29, -51, -23, -15, -22, -18, -50, -64}
        };
        int[][] blackKnightPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteKnightPositionValues[7 - i], 0, blackKnightPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteKnightPositionValues[row][col] : blackKnightPositionValues[row][col];
    }

    private int mgBishopPositionValue(int row, int col) {
        int[][] whiteBishopPositionValues = {
                {-29, 4, -82, -37, -25, -42, 7, -8},
                {-26, 16, -18, -13, 30, 59, 18, -47},
                {-16, 37, 43, 40, 35, 50, 37, -2},
                {-4, 5, 19, 50, 37, 37, 7, -2},
                {-6, 13, 13, 26, 34, 12, 10, 4},
                {0, 15, 15, 15, 14, 27, 18, 10},
                {4, 15, 16, 0, 7, 21, 33, 1},
                {-33, -3, -14, -21, -13, -12, -39, -21}
        };
        int[][] blackBishopPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteBishopPositionValues[7 - i], 0, blackBishopPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteBishopPositionValues[row][col] : blackBishopPositionValues[row][col];
    }

    private int egBishopPositionValue(int row, int col) {
        int[][] whiteBishopPositionValues = {
                {-14, -21, -11, -8, -7, -9, -17, -24},
                {-8, -4, 7, -12, -3, -13, -4, -14},
                {2, -8, 0, -1, -2, 6, 0, 4},
                {-3, 9, 12, 9, 14, 10, 3, 2},
                {-6, 3, 13, 19, 7, 10, -3, -9},
                {-12, -3, 8, 10, 13, 3, -7, -15},
                {-14, -18, -7, -1, 4, -9, -15, -27},
                {-23, -9, -23, -5, -9, -16, -5, -17}
        };
        int[][] blackBishopPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteBishopPositionValues[7 - i], 0, blackBishopPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteBishopPositionValues[row][col] : blackBishopPositionValues[row][col];
    }
    private int mgRookPositionValue(int row, int col) {
        int[][] whiteRookPositionValues = {
                {32, 42, 32, 51, 63, 9, 31, 43},
                {27, 32, 58, 62, 80, 67, 26, 44},
                {-5, 19, 26, 36, 17, 45, 61, 16},
                {-24, -11, 7, 26, 24, 35, -8, -20},
                {-36, -26, -12, -1, 9, -7, 6, -23},
                {-45, -25, -16, -17, 3, 0, -5, -33},
                {-44, -16, -20, -9, -1, 11, -6, -71},
                {-19, -13, 1, 17, 16, 7, -37, -26}
        };
        int[][] blackRookPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteRookPositionValues[7 - i], 0, blackRookPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteRookPositionValues[row][col] : blackRookPositionValues[row][col];
    }

    private int egRookPositionValue(int row, int col) {
        int[][] whiteRookPositionValues = {
                {13, 10, 18, 15, 12, 12, 8, 5},
                {11, 13, 13, 11, -3, 3, 8, 3},
                {7, 7, 7, 5, 4, -3, -5, -3},
                {4, 3, 13, 1, 2, 1, -1, 2},
                {3, 5, 8, 4, -5, -6, -8, -11},
                {-4, 0, -5, -1, -7, -12, -8, -16},
                {-6, -6, 0, 2, -9, -9, -11, -3},
                {-9, 2, 3, -1, -5, -13, 4, -20}
        };
        int[][] blackRookPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteRookPositionValues[7 - i], 0, blackRookPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteRookPositionValues[row][col] : blackRookPositionValues[row][col];
    }
    private int mgQueenPositionValue(int row, int col) {
        int[][] whiteQueenPositionValues = {
                {-28, 0, 29, 12, 59, 44, 43, 45},
                {-24, -39, -5, 1, -16, 57, 28, 54},
                {-13, -17, 7, 8, 29, 56, 47, 57},
                {-27, -27, -16, -16, -1, 17, -2, 1},
                {-9, -26, -9, -10, -2, -4, 3, -3},
                {-14, 2, -11, -2, -5, 2, 14, 5},
                {-35, -8, 11, 2, 8, 15, -3, 1},
                {-1, -18, -9, 10, -15, -25, -31, -50}
        };
        int[][] blackQueenPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteQueenPositionValues[7 - i], 0, blackQueenPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteQueenPositionValues[row][col] : blackQueenPositionValues[row][col];
    }

    private int egQueenPositionValue(int row, int col) {
        int[][] whiteQueenPositionValues = {
                {-9, 22, 22, 27, 27, 19, 10, 20},
                {-17, 20, 32, 41, 58, 25, 30, 0},
                {-20, 6, 9, 49, 47, 35, 19, 9},
                {3, 22, 24, 45, 57, 40, 57, 36},
                {-18, 28, 19, 47, 31, 34, 39, 23},
                {-16, -27, 15, 6, 9, 17, 10, 5},
                {-22, -23, -30, -16, -16, -23, -36, -32},
                {-33, -28, -22, -43, -5, -32, -20, -41}
        };
        int[][] blackQueenPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteQueenPositionValues[7 - i], 0, blackQueenPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteQueenPositionValues[row][col] : blackQueenPositionValues[row][col];
    }
    private int mgKingPositionValue(int row, int col) {
        int[][] whiteKingPositionValues = {
                {-65, 23, 16, -15, -56, -34, 2, 13},
                {29, -1, -20, -7, -8, -4, -38, -29},
                {-9, 24, 2, -16, -20, 6, 22, -22},
                {-17, -20, -12, -27, -30, -25, -14, -36},
                {-49, -1, -27, -39, -46, -44, -33, -51},
                {-14, -14, -22, -46, -44, -30, -15, -27},
                {1, 7, -8, -64, -43, -16, 9, 8},
                {-15, 36, 12, -54, 8, -28, 24, 14}
        };
        int[][] blackKingPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteKingPositionValues[7 - i], 0, blackKingPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteKingPositionValues[row][col] : blackKingPositionValues[row][col];
    }
    private int egKingPositionValue(int row, int col) {
        int[][] whiteKingPositionValues = {
                {-74, -35, -18, -18, -11, 15, 4, -17},
                {-12, 17, 14, 17, 17, 38, 23, 11},
                {10, 17, 23, 15, 20, 45, 44, 13},
                {-8, 22, 24, 27, 26, 33, 26, 3},
                {-18, -4, 21, 24, 27, 23, 9, -11},
                {-19, -3, 11, 21, 23, 16, 7, -9},
                {-27, -11, 4, 13, 14, 4, -5, -17},
                {-53, -34, -21, -11, -28, -14, -24, -43}
        };
        int[][] blackKingPositionValues = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(whiteKingPositionValues[7 - i], 0, blackKingPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteKingPositionValues[row][col] : blackKingPositionValues[row][col];
    }
    //endregion
//region Evaluation and Heuristics Methods

    public int evaluate() {
        int whiteScore = 0;
        int blackScore = 0;
        int pieceCount = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece != 0) {
                    pieceCount++;
                    int pieceValue = getPieceValue(piece);
                    int positionValue = (pieceCount > 16)
                            ? getMidgamePiecePositionValue(piece, row, col)
                            : getEndgamePiecePositionValue(piece, row, col);

                    if (piece <= 7) {
                        whiteScore += pieceValue + positionValue;
                    } else {
                        blackScore += pieceValue + positionValue;
                    }
                }
            }
        }

        return whiteScore - blackScore;
    }

    private int getPieceValue(int piece) {
        return switch (piece) {
            case 6, 7, 13, 14 -> // PAWNS
                    100;
            case 2, 9 ->  // KNIGHTS
                    300;
            case 3, 10 -> // BISHOPS
                    320;
            case 1, 8 ->  // ROOKS
                    540;
            case 4, 11 -> // QUEENS
                    900;
            case 5, 12 -> // KINGS
                    20000;
            default -> 0;
        };
    }

    private int getEndgamePiecePositionValue(int piece, int row, int col) {
        int value = 0;
        switch (piece) {
            case 6, 7, 13, 14 -> // PAWNS
                    value = pawnPositionValue(row, col);
            case 2, 9 -> // KNIGHTS
                    value = knightPositionValue(row, col);
            case 3, 10 -> // BISHOPS
                    value = bishopPositionValue(row, col);
            case 1, 8 -> // ROOKS
                    value = rookPositionValue(row, col);
            case 4, 11 -> // QUEENS
                    value = queenPositionValue(row, col);
            case 5, 12 -> // KINGS
                    value = egKingPositionValue(row, col);
        }
        return value;
    }

    private int getMidgamePiecePositionValue(int piece, int row, int col) {
        int value = 0;
        switch (piece) {
            case 6, 7, 13, 14 -> // PAWNS
                    value = pawnPositionValue(row, col);
            case 2, 9 -> // KNIGHTS
                    value = knightPositionValue(row, col);
            case 3, 10 -> // BISHOPS
                    value = bishopPositionValue(row, col);
            case 1, 8 -> // ROOKS
                    value = rookPositionValue(row, col);
            case 4, 11 -> // QUEENS
                    value = queenPositionValue(row, col);
            case 5, 12 -> // KINGS
                    value = mgKingPositionValue(row, col);
        }
        return value;
    }
//endregion

//region Board
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
    public void printBoard() {
        System.out.println("---a-b-c-d-e-f-g-h--");
        for (int i = 7; i >= 0; i--) {
            System.out.print((i + 1) + "| ");
            for (int j = 0; j < 8; j++) {
                System.out.print(pieceToChar(board[i][j]) + " ");
            }
            System.out.println("|" + (i + 1));
        }
        System.out.println("---a-b-c-d-e-f-g-h---");
        System.out.println();
    }
    //endregion
//region Pieces
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
//endregion
//region King Methods
public void findAndSetKing() {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            int piece = board[i][j];
            if ((currentPlayer == 'w' && piece == 5) || (currentPlayer == 'b' && piece == 12)) {
                kingRow = i;
                kingCol = j;
                return;
            }
        }
    }
}

    private boolean kingInCheck(int kingRow, int kingCol) {
        return kingSeeRook(kingRow, kingCol) != null ||
                kingSeeBishop(kingRow, kingCol) != null ||
                kingSeeKnight(kingRow, kingCol) != null ||
                kingSeePawn(kingRow, kingCol) != null;
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
        return null;
    }
    public int[] kingSeeKnight(int kingRow, int kingCol) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            if (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if ((board[newRow][newCol] == 2 && currentPlayer == 'b') ||
                        (board[newRow][newCol] == 10 && currentPlayer == 'w')) {
                    return new int[]{newRow, newCol};
                }
            }
        }
        return null;
    }
    public int[] kingSeeRook(int kingRow, int kingCol) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 9 || board[newRow][newCol] == 12) && currentPlayer == 'w' ||
                            (board[newRow][newCol] == 1 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return new int[]{newRow, newCol};
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return null;
    }
    public int[] kingSeePawn(int kingRow, int kingCol) {
        if (currentPlayer == 'w') {
            if (isEnemyPawn(kingRow + 1, kingCol + 1) || isEnemyPawn(kingRow + 1, kingCol - 1)) {
                return new int[]{kingRow + 1, kingCol + 1}; // Example; adjust based on which pawn detected
            }
        } else {
            if (isEnemyPawn(kingRow - 1, kingCol + 1) || isEnemyPawn(kingRow - 1, kingCol - 1)) {
                return new int[]{kingRow - 1, kingCol + 1}; // Example; adjust based on which pawn detected
            }
        }
        return null;
    }
    private boolean isEnemyPawn(int row, int col) {
        return (board[row][col] == 1 && currentPlayer == 'b') || (board[row][col] == 9 && currentPlayer == 'w');
    }
    private boolean canCaptureKing(int attackerRow, int attackerCol, int kingRow, int kingCol) {
        int attacker = board[attackerRow][attackerCol];
        int dy = Math.abs(kingRow - attackerRow);
        int dx = Math.abs(kingCol - attackerCol);

        return switch (attacker) {
            case 6, 7, 13, 14 -> // PAWNS
                    (dy == 1 && dx == 1);
            case 2, 9 -> // KNIGHTS
                    (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
            case 1, 8 -> // ROOK
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0);
            case 3, 10 -> // BISHOPS
                    (dy == dx);
            case 4, 11 -> // QUEENS
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0) || (dy == dx);
            case 5, 12 -> // KINGS
                    (dy <= 1 && dx <= 1);
            default -> false;
        };
    }

    private boolean canEscapeCheck(int kingRow, int kingCol) {
        int[] attacker;

        // Generate possible moves
        generateMoves(moves);

        // Check if the king can move to a safe spot
        for (int[] move : moves) {
            makeMove(move[0], move[1], move[2], move[3]);
            if (!kingInCheck(kingRow, kingCol)) {
                undoMove(move[0], move[1], move[2], move[3], move[4]);
                return true;
            }
            undoMove(move[0], move[1], move[2], move[3], move[4]);
        }

        // Check if any piece can block or capture the attacker
        for (int[] move : moves) {
            attacker = identifyAttacker(kingRow, kingCol);
            if (move[2] == attacker[0] && move[3] == attacker[1]) {
                return true;
            }
        }

        return false;
    }
    private int[] identifyAttacker(int kingRow, int kingCol) {
        int[] attacker = kingSeeRook(kingRow, kingCol);
        if (attacker != null) return attacker;

        attacker = kingSeeBishop(kingRow, kingCol);
        if (attacker != null) return attacker;

        attacker = kingSeeKnight(kingRow, kingCol);
        if (attacker != null) return attacker;

        attacker = kingSeePawn(kingRow, kingCol);
        return attacker;
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
//endregion

//region Game Logic and Helper Methods
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

    private static char reverseNumber(char c) {
        Map<Character, Character> mapping = new HashMap<>();
        mapping.put('1', '8');
        mapping.put('2', '7');
        mapping.put('3', '6');
        mapping.put('4', '5');
        mapping.put('5', '4');
        mapping.put('6', '3');
        mapping.put('7', '2');
        mapping.put('8', '1');

        return mapping.get(c);
    }

    public Game(Game currentGame) {
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(currentGame.board[i], 0, this.board[i], 0, board.length);
        }
        this.currentPlayer = currentGame.currentPlayer;
        this.heuristicValue = currentGame.heuristicValue;
        this.whiteLongCastle = currentGame.whiteLongCastle;
        this.whiteShortCastle = currentGame.whiteShortCastle;
        this.blackLongCastle = currentGame.blackLongCastle;
        this.blackShortCastle = currentGame.blackShortCastle;
    }

    public Game updateGameState(int moveIndex) {
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
//endregion
//region Game Win Conditions
public boolean checkForWin() {
    return false;
}
    public boolean checkDraw() {
        return false;
    }

    public boolean isGameFinished() {
        //return false;
        return isCheckmate() || checkDraw() || onlyKingLeft();
    }
    private boolean isCheckmate() {
        // In check -> can't escape
        return kingInCheck(kingRow, kingCol) && !canEscapeCheck(kingRow, kingCol);
    }

    //endregion

//region Logging
    private static void logToCSV(String message) {
        try (FileWriter fw = new FileWriter("performance_log.csv", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logHeader() {
        try (FileWriter fw = new FileWriter("performance_log.csv", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("Logical Processors,Start Time,End Time,Execution Time (ms),Depth,Best Move,New FEN string");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logEntry(int logicalProcessors, LocalDateTime startTime, LocalDateTime endTime, long duration, int depth, String bestMove, String newFEN) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String entry = String.format("%d,%s,%s,%d,%d,%s,%s",
                logicalProcessors,
                startTime.format(formatter),
                endTime.format(formatter),
                duration,
                depth,
                bestMove,
                newFEN);
        logToCSV(entry);
    }
//endregion

//region main
    public static void main(String[] args) {
        int logicalProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Logical Processors: " + logicalProcessors);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter FEN string:");
        String fen = scanner.nextLine();

        int depth = 6;
        Game game = new Game();
        game.initializeBoard(fen);

        int moveCounter = 0;

        while (true) {
            if (moveCounter < 1) {
                moveCounter++;
                game.printBoard();
            }
            System.out.println("Searching in depth " + depth + "...");

            LocalDateTime startTime = LocalDateTime.now();

            // Single-threaded test
            boolean minimax = false;
            if (game.currentPlayer == 'w'){
                minimax = true;
            }
            int[] bestMove = iterativeDeepening(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, minimax,30000);
            LocalDateTime endTime = LocalDateTime.now();

            long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
            System.out.println("Execution Time: " + singleThreadedTime + " milliseconds");

            String bestMoveString = bestMove[0] + "" + bestMove[1] + bestMove[2] + bestMove[3];
            System.out.println("Best move: " + bestMoveString);

            game.makeMove(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
            game.printBoard();

            String newFEN = game.getFEN();
            System.out.println("New FEN string: " + newFEN);

            logEntry(logicalProcessors, startTime, endTime, singleThreadedTime, depth, bestMoveString, newFEN);

            System.out.println("Enter the next move for black (e.g., d7d5) or type 'exit' to end:");
            String userMove = scanner.nextLine();

            if (userMove.equalsIgnoreCase("exit")) {
                break;
            }

            if (userMove.length() == 4) {

                char fromXChar = userMove.charAt(0);
                char fromYChar = reverseNumber(userMove.charAt(1));
                char toXChar = userMove.charAt(2);
                char toYChar = reverseNumber(userMove.charAt(3));

                int fromX = fromXChar - 'a';
                int fromY = 8 - Character.getNumericValue(fromYChar);
                int toX = toXChar - 'a';
                int toY = 8 - Character.getNumericValue(toYChar);

                game.makeMove(fromY, fromX, toY, toX);
                game.printBoard();
                System.out.println("Updated FEN string: " + game.getFEN());

            } else {
                System.out.println("Invalid input format. Please enter the move in the format 'e7e5'.");
            }
        }
        scanner.close();
    }
//endregion

}