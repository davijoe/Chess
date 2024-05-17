package robot;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Game {

//region Global Variables and Constants
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
    boolean whiteLongCastle, whiteShortCastle, blackShortCastle, blackLongCastle = true;
    private int kingRow, kingCol;
    char currentPlayer;
    int heuristicValue;

    public Game() {

    }
//endregion
//region Minimax and Iterative Deepening
    public static int[][] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, int[] previousBestMove) {
        if (depth == 0) {
            return new int[][]{{}, {game.evaluate()}};
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (previousBestMove != null) {
            int previousMove;
            previousMove = game.makeMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            game.undoMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3],previousMove);
            int score = result[1][0] + (maximizingPlayer ? depth : -depth);

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
            int previousMove;
            previousMove = game.makeMove(move[0], move[1], move[2], move[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null);
            game.undoMove(move[0],move[1],move[2],move[3],previousMove);
            int score = result[1][0] + (maximizingPlayer ? depth : -depth);

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
//region Evaluation and Heuristics Methods
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
            case 6, 7, 14, 15 -> // PAWNS
                    100;
            case 2, 10 ->  // KNIGHTS
                    300;
            case 3, 11 -> // BISHOPS
                    320;
            case 1, 9 ->  // ROOKS
                    540;
            case 4, 12 -> // QUEENS
                    900;
            case 5, 13 -> // KINGS
                    20000;
            default -> 0;
        };
    }

    private int getPiecePositionValue(int piece, int row, int col) {
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
                    value = kingPositionValue(row, col);
        }
        return value;
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
            System.arraycopy(whitePawnPositionValues[7 - i], 0, blackPawnPositionValues[i], 0, 8);
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
            System.arraycopy(whiteKnightPositionValues[7 - i], 0, blackKnightPositionValues[i], 0, 8);
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
            System.arraycopy(whiteBishopPositionValues[7 - i], 0, blackBishopPositionValues[i], 0, 8);
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
            System.arraycopy(whiteRookPositionValues[7 - i], 0, blackRookPositionValues[i], 0, 8);
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
            System.arraycopy(whiteQueenPositionValues[7 - i], 0, blackQueenPositionValues[i], 0, 8);
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
            System.arraycopy(whiteKingPositionValues[7 - i], 0, blackKingPositionValues[i], 0, 8);
        }
        return (currentPlayer == 'w') ? whiteKingPositionValues[row][col] : blackKingPositionValues[row][col];
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
        System.out.println("");
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
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        generateMoveCounter++;
    }

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
        } else {
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
//region main
    public static void main(String[] args) {
        int logicalProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Logical Processors: " + logicalProcessors);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter FEN string:");
        String fen = scanner.nextLine();

        int depth = 5;
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

            String bestMoveString = bestMove[0] + "" + bestMove[1] + "" + bestMove[2] + "" + bestMove[3];
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