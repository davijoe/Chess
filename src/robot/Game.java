package robot;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game {

    //region Global Variables and Constants
    int[][] board = new int[8][8];
    private Map<Long, int[]> transpositionTable = new HashMap<>();

    int[][] moves = new int[181][6];
    int[][] movesd1 = new int[181][6];
    int[][] movesd2 = new int[181][6];
    int[][] movesd3 = new int[181][6];
    int[][] movesd4 = new int[181][6];
    int[][] movesd5 = new int[181][6];
    int[][] movesd6 = new int[181][6];
    int[][] movesd7 = new int[181][6];
    int[][] movesd8 = new int[181][6];
    int[][] movesd9 = new int[181][6];
    int[][] movesd10 = new int[181][6];
    int[][] movesd11 = new int[181][6];
    int[][] movesd12 = new int[181][6];
    int nodecount = 1;
    int[][] checkMoves = new int[1000][6];

    int[][] validMovesInCheck = new int[1000][6];

    int validCheckMovesCounter = 0;

    int generateMoveCounter = 0;
    int enPassant;
    boolean whiteLongCastle, whiteShortCastle, blackShortCastle, blackLongCastle = true;

    public int whiteKingRow;
    public int whiteKingCol;
    public int blackKingRow;
    public int blackKingCol;
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

    private static final int[][] ROOK_DIRECTIONS = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    private static final int[][] BISHOP_DIRECTIONS = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
    private static final int[][] KNIGHT_DIRECTIONS = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
    private static final int[][] QUEEN_DIRECTIONS = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    private static final int[][] PAWN_CAPTURE_DIRECTIONS_WHITE = {{1, 1}, {1, -1}};
    private static final int[][] PAWN_CAPTURE_DIRECTIONS_BLACK = {{-1, 1}, {-1, -1}};

    public void generateRookMoves(int row, int col, int[][] moves) {
        pieceMoveLogic(ROOK_DIRECTIONS, row, col, true, moves);
    }
    public void generateRookCaptureMoves(int row, int col, int[][] moves) {
        pieceCaptureOnlyLogic(ROOK_DIRECTIONS, row, col, true, moves);
    }

    public void generateBishopMoves(int row, int col, int[][] moves) {
        pieceMoveLogic(BISHOP_DIRECTIONS, row, col, true, moves);
    }

    public void generateBishopCaptureMoves(int row, int col, int[][] moves) {
        pieceCaptureOnlyLogic(BISHOP_DIRECTIONS, row, col, true, moves);
    }

    public void generateKnightMoves(int row, int col, int[][] moves) {
        pieceMoveLogic(KNIGHT_DIRECTIONS, row, col, false, moves);
    }
    public void generateKnightCaptureMoves(int row, int col, int[][] moves) {
        pieceCaptureOnlyLogic(KNIGHT_DIRECTIONS, row, col, false, moves);
    }

    public void generateQueenMoves(int row, int col, int[][] moves) {
        pieceMoveLogic(QUEEN_DIRECTIONS, row, col, true, moves);
    }
    public void generateQueenCaptureMoves(int row, int col, int[][] moves) {
        pieceCaptureOnlyLogic(QUEEN_DIRECTIONS, row, col, true, moves);
    }


    public void generateKingMoves(int row, int col, int[][] moves) {
        pieceMoveLogic(QUEEN_DIRECTIONS, row, col, false, moves);
    }
    public void generateKingCaptureMoves(int row, int col, int[][] moves) {
        pieceCaptureOnlyLogic(QUEEN_DIRECTIONS, row, col, false, moves);
    }

    public void generatePawnMoves(int row, int col, int[][] moves) {
        int piece = board[row][col];
        int direction = (piece == 6 || piece == 7) ? 1 : -1; // * 1 for white, -1 for black

        if (isTileEmpty(row + direction, col)) {
            addMove(row, col, row + direction, col, piece, moves);
            if ((direction == 1 && row == 1) || (direction == -1 && row == 6)) {
                if (isTileEmpty(row + 2 * direction, col)) {
                    addMove(row, col, row + 2 * direction, col, piece, moves);
                }
            }
        }

        int[][] pawnCaptureDirections = (piece == 6 || piece == 7) ? PAWN_CAPTURE_DIRECTIONS_WHITE : PAWN_CAPTURE_DIRECTIONS_BLACK;
        for (int[] dir : pawnCaptureDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8 && !isTileEmpty(newRow, newCol) && board[newRow][newCol] / 8 != piece / 8) {
                addMove(row, col, newRow, newCol, piece, moves);
            }
        }
    }

    public void generatePawnCaptureMoves(int row, int col, int[][] moves) {
        int piece = board[row][col];
        int direction = (piece == 6 || piece == 7) ? 1 : -1; // * 1 for white, -1 for black

        int[][] pawnCaptureDirections = (piece == 6 || piece == 7) ? PAWN_CAPTURE_DIRECTIONS_WHITE : PAWN_CAPTURE_DIRECTIONS_BLACK;
        for (int[] dir : pawnCaptureDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8 && !isTileEmpty(newRow, newCol) && board[newRow][newCol] / 8 != piece / 8) {
                addMove(row, col, newRow, newCol, piece, moves);
            }
        }
    }

    private void addMove(int startRow, int startCol, int endRow, int endCol, int piece, int[][] moves) {
        int capturedPiece = board[endRow][endCol];
        int score = computeMoveScore(piece, endRow, endCol, capturedPiece);
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        moves[generateMoveCounter][5] = score;
        generateMoveCounter++;
    }

    //values for {Rook, Knight, Bishop, Queen, King, Pawn, Pawn, Rook, Knight, Bishop, Queen, King, Pawn, Pawn}
    static final int[] PIECE_VALUES = {50, 32, 35, 90, 100, 10, 10, 50, 32, 35, 90, 100, 10, 10};
    private int computeMoveScore(int piece, int endRow, int endCol, int capturedPiece) {
        int pieceValue = 0;
        if (piece != 0){
            if (piece > 0) {
                pieceValue = PIECE_VALUES[(piece) - 1];
            } else {
                pieceValue = PIECE_VALUES[(piece) + 6];
            }
        }
        int captureValue = capturedPiece == 0 ? 0 : pieceValue;

        return (captureValue * 12) + pieceValue + (endRow >= 2 && endRow <= 5 && endCol >= 2 && endCol <= 5 ? 5 : 0);
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
                        case 1:
                            generateRookMoves(row, col, moves);
                            break;
                        case 2:
                            generateKnightMoves(row, col, moves);
                            break;
                        case 3:
                            generateBishopMoves(row, col, moves);
                            break;
                        case 4:
                            generateQueenMoves(row, col, moves);
                            break;
                        case 5:
                            generateKingMoves(row, col, moves);
                            break;
                        case 6:
                        case 7:
                            generatePawnMoves(row, col, moves);
                            break;
                    }
                }
            }
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    switch (board[row][col]) {
                        case 8:
                            generateRookMoves(row, col, moves);
                            break;
                        case 9:
                            generateKnightMoves(row, col, moves);
                            break;
                        case 10:
                            generateBishopMoves(row, col, moves);
                            break;
                        case 11:
                            generateQueenMoves(row, col, moves);
                            break;
                        case 12:
                            generateKingMoves(row, col, moves);
                            break;
                        case 13:
                        case 14:
                            generatePawnMoves(row, col, moves);
                            break;
                    }
                }
            }
        }

        Arrays.sort(moves, 0, generateMoveCounter, (move1, move2) -> Integer.compare(move2[5], move1[5]));
    }

    public int[][] getCaptureMoves() {
        int[][] captureMoves = new int[181][6];
        generateMoveCounter = 0;

        if (currentPlayer == 'w') {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    switch (board[row][col]) {
                        case 1 -> generateRookCaptureMoves(row, col, captureMoves);
                        case 2 -> generateKnightCaptureMoves(row, col, captureMoves);
                        case 3 -> generateBishopCaptureMoves(row, col, captureMoves);
                        case 4 -> generateQueenCaptureMoves(row, col, captureMoves);
                        case 5 -> generateKingCaptureMoves(row, col, captureMoves);
                        case 6, 7 -> generatePawnCaptureMoves(row, col, captureMoves);
                    }
                }
            }
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    switch (board[row][col]) {
                        case 8 -> generateRookCaptureMoves(row, col, captureMoves);
                        case 9 -> generateKnightCaptureMoves(row, col, captureMoves);
                        case 10 -> generateBishopCaptureMoves(row, col, captureMoves);
                        case 11 -> generateQueenCaptureMoves(row, col, captureMoves);
                        case 12 -> generateKingCaptureMoves(row, col, captureMoves);
                        case 13, 14 -> generatePawnCaptureMoves(row, col, captureMoves);
                    }
                }
            }
        }

        int[][] filteredMoves = new int[generateMoveCounter][6];
        int filteredMoveCounter = 0;
        for (int i = 0; i < generateMoveCounter; i++) {
            if (captureMoves[i][5] > 120) {
                filteredMoves[filteredMoveCounter++] = captureMoves[i];
            }
        }

        int[][] result = new int[filteredMoveCounter][6];
        System.arraycopy(filteredMoves, 0, result, 0, filteredMoveCounter);
        return result;
    }

    public int[][] filterValidMoves(int[][] moves) {
        int left = 0;
        int right = moves.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (isZeroMove(moves[mid])) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        int validMoveCount = left;

        int[][] validMoves = new int[validMoveCount][5];
        for (int i = 0; i < validMoveCount; i++) {
            validMoves[i] = moves[i];
        }

        return validMoves;
    }

    private boolean isZeroMove(int[] move) {
        return move[0] == 0 && move[1] == 0 && move[2] == 0 && move[3] == 0 && move[4] == 0;
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
    public void pieceCaptureOnlyLogic(int[][] directions, int row, int col, boolean canSlide, int[][] moves) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if (board[newRow][newCol] > 7 && currentPlayer == 'w' || board[newRow][newCol] <= 7 && currentPlayer == 'b') {
                        addMove(row, col, newRow, newCol, board[row][col], moves);
                    }
                    break;
                }
                if (!canSlide) {
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
    }
    private boolean kingInCheck(int kingRow, int kingCol) {
        int[] attacker = {kingRow, kingCol};
        if (kingSeeRook(kingRow, kingCol)!=null) {
            return true;
        } else if (kingSeeBishop(kingRow, kingCol)!=null) {
            return true;
        } else if (kingSeeKnight(kingRow, kingCol)!=null) {
            return true;
        } else if (kingSeePawn(kingRow, kingCol)!=null) {
            return true;
        } else return false;
    }
    public int[] kingSeeBishop(int kingRow, int kingCol) {
        for (int[] direction : BISHOP_DIRECTIONS) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 10 || board[newRow][newCol] == 11) && board[kingRow][kingCol]==5 || (board[newRow][newCol] == 3 || board[newRow][newCol] == 4) && board[kingRow][kingCol]==12) {
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
        for (int[] direction : KNIGHT_DIRECTIONS) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (board[newRow][newCol] == 2 && board[kingRow][kingCol]==12 || board[newRow][newCol] == 9 && board[kingRow][kingCol]==5) {
                    return new int[]{newRow, newCol};
                }
                break;
            }
        }
        return null;
    }
    public int[] kingSeeRook(int kingRow, int kingCol) {
        for (int[] direction : ROOK_DIRECTIONS) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (!isTileEmpty(newRow, newCol)) {
                    if ((board[newRow][newCol] == 8 || board[newRow][newCol] == 11) && board[kingRow][kingCol]==5 || (board[newRow][newCol] == 1 || board[newRow][newCol] == 4) && board[kingRow][kingCol]==12) {
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
        if (board[kingRow][kingCol]==5) {
            if (kingRow + 1 < 7) {
                if (kingCol + 1 < 8 && (board[kingRow + 1][kingCol + 1] == 13 || board[kingRow + 1][kingCol + 1] == 14)) {
                    return new int[]{kingRow + 1, kingCol + 1};
                }
                if (0 <= kingCol - 1 && (board[kingRow + 1][kingCol - 1] == 13 || board[kingRow + 1][kingCol - 1] == 14)) {
                    return new int[]{kingRow + 1, kingCol - 1};
                }
            }
        }
        else if (kingRow-1 >= 0) {
            if (kingCol + 1 < 8 && (board[kingRow-1][kingCol+1] == 6 || board[kingRow-1][kingCol+1] == 7)) {
                return new int[]{kingRow-1, kingCol+1};
            }
            if (0 <= kingCol - 1 && (board[kingRow-1][kingCol - 1] == 6 || board[kingRow-1][kingCol - 1] == 7)) {
                return new int[]{kingRow-1, kingCol-1};
            }
        }
        return null;
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

    public void resetMoves(int[][] moves) {
        for (int i = 0; i < moves.length; i++) {
            for (int j = 0; j < moves[i].length; j++) {
                moves[i][j] = 0;
            }
        }
    }

    private boolean canEscapeCheck(int kingRow, int kingCol) {
        int originalMoveCounter = generateMoveCounter;
        validCheckMovesCounter = 0;
        generateMoveCounter = 0;
        resetMoves(validMovesInCheck);
        resetMoves(checkMoves);
        generateMoves(checkMoves);
        for (int i = 0; i<generateMoveCounter; i++ ) {
            int[] move = checkMoves[i];
            if(currentPlayer=='w') {
                int[] capturedPiece = makeMove(move[0],move[1],move[2],move[3]);
                if (!kingInCheck(whiteKingRow, whiteKingCol)) {
                    addMove(move[0], move[1], move[2], move[3], capturedPiece[1], validMovesInCheck);
                    undoMove(move[0], move[1], move[2], move[3], capturedPiece);
                    validCheckMovesCounter++;
                    break;
                }
                undoMove(move[0], move[1], move[2], move[3], capturedPiece);
            }
            if(currentPlayer=='b') {
                int[] capturedPiece = makeMove(move[0],move[1],move[2],move[3]);
                if (!kingInCheck(blackKingRow, blackKingCol)) {
                    addMove(move[0], move[1], move[2], move[3], capturedPiece[1], validMovesInCheck);
                    undoMove(move[0], move[1], move[2], move[3], capturedPiece);
                    validCheckMovesCounter++;
                    break;
                }
                undoMove(move[0], move[1], move[2], move[3], capturedPiece);
            }
        }
        generateMoveCounter = originalMoveCounter;
        if(validCheckMovesCounter>0) {
            return true;
        }
        return false;
    }

    public int[][] getMovesByDepth(int depth) {
        return switch (depth) {
            case 12 -> movesd12;
            case 11 -> movesd11;
            case 10 -> movesd10;
            case 9 -> movesd9;
            case 8 -> movesd8;
            case 7 -> movesd7;
            case 6 -> movesd6;
            case 5 -> movesd5;
            case 4 -> movesd4;
            case 3 -> movesd3;
            case 2 -> movesd2;
            case 1 -> movesd1;
            default -> moves;
        };
    }
    //endregion


//region Minimax and Iterative Deepening

    public static long calculateHash(Game game) {
        int[][] board = game.getBoard();
        long hash = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int piece = board[i][j];
                hash ^= ZOBRIST_KEYS[i][j][piece];
            }
        }

        return hash;
    }

    static final long[][][] ZOBRIST_KEYS = new long[8][8][15];

    static {
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 15; k++) {
                    ZOBRIST_KEYS[i][j][k] = random.nextLong();
                }
            }
        }
    }
    public static int[][] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, int[] previousBestMove, boolean checkCapture) {
        //++game.nodecount;
        long hash = calculateHash(game);
        if(game.transpositionTable.containsKey(hash)){
            return new int[][]{{}, game.transpositionTable.get(hash)};
        }
        if (game.kingInCheck(game.whiteKingRow, game.whiteKingCol) || game.kingInCheck(game.blackKingRow, game.blackKingCol)) {
            if (game.isCheckmate()) {
                int score = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                game.transpositionTable.put(hash, new int[]{score});
                return new int[][]{{}, {score}};
            }
        }
        if(game.checkDraw()) {
            game.transpositionTable.put(hash, new int[]{0});
            return new int[][] {{},{0}};
        }
        if (depth == 0) {
            int score = game.evaluate();
            if (checkCapture) {
                int[][] captureMoves = game.getCaptureMoves();
                for (int[] captureMove : captureMoves) {
                    int[] previousMove = game.makeMove(captureMove[0], captureMove[1], captureMove[2], captureMove[3]);
                    int[][] result = minimax(game, depth, alpha, beta, !maximizingPlayer, null, true);
                    game.undoMove(captureMove[0], captureMove[1], captureMove[2], captureMove[3], previousMove);
                    int captureScore = result[1][0];

                    if ((maximizingPlayer && captureScore > score) || (!maximizingPlayer && captureScore < score)) {
                        score = captureScore;
                    }

                    if (maximizingPlayer) {
                        alpha = Math.max(alpha, score);
                    } else {
                        beta = Math.min(beta, score);
                    }

                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            game.transpositionTable.put(hash, new int[]{score});
            return new int[][]{{}, {score}};
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (previousBestMove != null) {
            int[] previousMove;
            previousMove = game.makeMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3]);
            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null, checkCapture);
            game.undoMove(previousBestMove[0], previousBestMove[1], previousBestMove[2], previousBestMove[3],previousMove);
            int score = result[1][0];

            if ((maximizingPlayer && score > bestScore) || (!maximizingPlayer && score < bestScore)) {
                bestScore = score;
                bestMove = previousBestMove;
            }

            if (maximizingPlayer) {
                alpha = Math.max(alpha, bestScore);
            } else {
                beta = Math.min(beta, bestScore);
            }
        }

        int[][] moves = game.getMovesByDepth(depth);
        game.generateMoves(moves);
        int[][] validMoves = game.filterValidMoves(moves);
        //System.out.println("Depth: " + depth + ", Valid Moves: " + Arrays.deepToString(validMoves));

        for (int i = 0; i < validMoves.length; i++) {
            int[] move = validMoves[i];
            int[] previousMove = game.makeMove(move[0], move[1], move[2], move[3]);

            int[][] result = minimax(game, depth - 1, alpha, beta, !maximizingPlayer, null, checkCapture);
            game.undoMove(move[0],move[1],move[2],move[3],previousMove);
            int score = result[1][0];

            //System.out.println("Depth: " + depth + ", Move: " + Arrays.toString(move) + ", Score: " + score);

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

        //System.out.println("Returning from Depth: " + depth + ", Best Move: " + Arrays.toString(bestMove) + ", Best Score: " + bestScore);
        //game.transpositionTable.put(hash, new int[]{bestScore});
        return new int[][]{bestMove, {bestScore}};
    }

    public static int[] iterativeDeepening(Game game, int maxDepth, int alpha, int beta, boolean maximizingPlayer, long timeLimit) {
        int[][] bestMove = {null};

        long startTime = System.currentTimeMillis();
        AtomicBoolean timeUp = new AtomicBoolean(false);

        for (int depth = 1; depth <= maxDepth; depth++) {
            final int currentDepth = depth;
            boolean checkCapture = false;
            if (currentDepth > 7){
                checkCapture = true;
            }
            System.out.println("current depth: " + currentDepth);
            Game newGame = new Game(game);
            boolean finalCheckCapture = checkCapture;
            Thread searchThread = new Thread(() -> {
                int[][] result = minimax(newGame, currentDepth, alpha, beta, maximizingPlayer, bestMove[0], finalCheckCapture);
                bestMove[0] = result[0];
                //System.out.println(newGame.nodecount);
            });
            searchThread.start();

            long remainingTime = timeLimit - (System.currentTimeMillis() - startTime);
            try {
                searchThread.join(remainingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (searchThread.isAlive()) {
                timeUp.set(true);
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
private static int[][] reverse(int[][] table) {
    int[][] reversedTable = new int[8][8];
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            reversedTable[i][j] = table[7 - i][j];
        }
    }
    return reversedTable;
}
    private static final int[][] pawnPositionValue = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static final int[][] blackPawnPositionValue = reverse(pawnPositionValue);

    private static final int[][] knightPositionValue = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static final int[][] blackKnightPositionValue = reverse(knightPositionValue);

    private static final int[][] bishopPositionValue = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    private static final int[][] blackBishopPositionValue = reverse(bishopPositionValue);

    private static final int[][] rookPositionValue = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };

    private static final int[][] blackRookPositionValue = reverse(rookPositionValue);

    private static final int[][] queenPositionValue = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
    };

    private static final int[][] blackQueenPositionValue = reverse(queenPositionValue);

    private static final int[][] mgPawnPositionValue = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {98, 134, 61, 95, 68, 126, 34, -11},
            {-6, 7, 26, 31, 65, 56, 25, -20},
            {-14, 13, 6, 21, 23, 12, 17, -23},
            {-27, -2, -5, 12, 17, 6, 10, -25},
            {-26, -4, -4, -10, 3, 3, 33, -12},
            {-35, -1, -20, -23, -15, 24, 38, -22},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    private static final int[][] blackmgPawnPositionValue = reverse(mgPawnPositionValue);

    private static final int[][] egPawnPositionValue = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {178, 173, 158, 134, 147, 132, 165, 187},
            {94, 100, 85, 67, 56, 53, 82, 84},
            {32, 24, 13, 5, -2, 4, 17, 17},
            {13, 9, -3, -7, -7, -8, 3, -1},
            {4, 7, -6, 1, 0, -5, -1, -8},
            {13, 8, 8, 10, 13, 0, 2, -7},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    private static final int[][] blackegPawnPositionValue = reverse(egPawnPositionValue);

    private static final int[][] mgKnightPositionValue = {
            {-167, -89, -34, -49, 61, -97, -15, -107},
            {-73, -41, 72, 36, 23, 62, 7, -17},
            {-47, 60, 37, 65, 84, 129, 73, 44},
            {-9, 17, 19, 53, 37, 69, 18, 22},
            {-13, 4, 16, 13, 28, 19, 21, -8},
            {-23, -9, 12, 10, 19, 17, 25, -16},
            {-29, -53, -12, -3, -1, 18, -14, -19},
            {-105, -21, -58, -33, -17, -28, -19, -23}
    };
    private static final int[][] blackmgKnightPositionValue = reverse(mgKnightPositionValue);

    private static final int[][] egKnightPositionValue = {
            {-58, -38, -13, -28, -31, -27, -63, -99},
            {-25, -8, -25, -2, -9, -25, -24, -52},
            {-24, -20, 10, 9, -1, -9, -19, -41},
            {-17, 3, 22, 22, 22, 11, 8, -18},
            {-18, -6, 16, 25, 16, 17, 4, -18},
            {-23, -3, -1, 15, 10, -3, -20, -22},
            {-42, -20, -10, -5, -2, -20, -23, -44},
            {-29, -51, -23, -15, -22, -18, -50, -64}
    };
    private static final int[][] blackegKnightPositionValue = reverse(egKnightPositionValue);

    private static final int[][] mgBishopPositionValue = {
            {-29, 4, -82, -37, -25, -42, 7, -8},
            {-26, 16, -18, -13, 30, 59, 18, -47},
            {-16, 37, 43, 40, 35, 50, 37, -2},
            {-4, 5, 19, 50, 37, 37, 7, -2},
            {-6, 13, 13, 26, 34, 12, 10, 4},
            {0, 15, 15, 15, 14, 27, 18, 10},
            {4, 15, 16, 0, 7, 21, 33, 1},
            {-33, -3, -14, -21, -13, -12, -39, -21}
    };
    private static final int[][] blackmgBishopPositionValue = reverse(mgBishopPositionValue);

    private static final int[][] egBishopPositionValue = {
            {-14, -21, -11, -8, -7, -9, -17, -24},
            {-8, -4, 7, -12, -3, -13, -4, -14},
            {2, -8, 0, -1, -2, 6, 0, 4},
            {-3, 9, 12, 9, 14, 10, 3, 2},
            {-6, 3, 13, 19, 7, 10, -3, -9},
            {-12, -3, 8, 10, 13, 3, -7, -15},
            {-14, -18, -7, -1, 4, -9, -15, -27},
            {-23, -9, -23, -5, -9, -16, -5, -17}
    };
    private static final int[][] blackegBishopPositionValue = reverse(egBishopPositionValue);

    private static final int[][] mgRookPositionValue = {
            {32, 42, 32, 51, 63, 9, 31, 43},
            {27, 32, 58, 62, 80, 67, 26, 44},
            {-5, 19, 26, 36, 17, 45, 61, 16},
            {-24, -11, 7, 26, 24, 35, -8, -20},
            {-36, -26, -12, -1, 9, -7, 6, -23},
            {-45, -25, -16, -17, 3, 0, -5, -33},
            {-44, -16, -20, -9, -1, 11, -6, -71},
            {-19, -13, 1, 17, 16, 7, -37, -26}
    };
    private static final int[][] blackmgRookPositionValue = reverse(mgRookPositionValue);

    private static final int[][] egRookPositionValue = {
            {13, 10, 18, 15, 12, 12, 8, 5},
            {11, 13, 13, 11, -3, 3, 8, 3},
            {7, 7, 7, 5, 4, -3, -5, -3},
            {4, 3, 13, 1, 2, 1, -1, 2},
            {3, 5, 8, 4, -5, -6, -8, -11},
            {-4, 0, -5, -1, -7, -12, -8, -16},
            {-6, -6, 0, 2, -9, -9, -11, -3},
            {-9, 2, 3, -1, -5, -13, 4, -20}
    };
    private static final int[][] blackegRookPositionValue = reverse(egRookPositionValue);
    //
    private static final int[][] mgQueenPositionValue = {
            {-28, 0, 29, 12, 59, 44, 43, 45},
            {-24, -39, -5, 1, -16, 57, 28, 54},
            {-13, -17, 7, 8, 29, 56, 47, 57},
            {-27, -27, -16, -16, -1, 17, -2, 1},
            {-9, -26, -9, -10, -2, -4, 3, -3},
            {-14, 2, -11, -2, -5, 2, 14, 5},
            {-35, -8, 11, 2, 8, 15, -3, 1},
            {-1, -18, -9, 10, -15, -25, -31, -50}
    };
    private static final int[][] blackmgQueenPositionValue = reverse(mgQueenPositionValue);

    private static final int[][] egQueenPositionValue = {
            {-9, 22, 22, 27, 27, 19, 10, 20},
            {-17, 20, 32, 41, 58, 25, 30, 0},
            {-20, 6, 9, 49, 47, 35, 19, 9},
            {3, 22, 24, 45, 57, 40, 57, 36},
            {-18, 28, 19, 47, 31, 34, 39, 23},
            {-16, -27, 15, 6, 9, 17, 10, 5},
            {-22, -23, -30, -16, -16, -23, -36, -32},
            {-33, -28, -22, -43, -5, -32, -20, -41}
    };
    private static final int[][] blackegQueenPositionValue = reverse(egQueenPositionValue);

    private static final int[][] mgKingPositionValue = {
            {-65, 23, 16, -15, -56, -34, 2, 13},
            {29, -1, -20, -7, -8, -4, -38, -29},
            {-9, 24, 2, -16, -20, 6, 22, -22},
            {-17, -20, -12, -27, -30, -25, -14, -36},
            {-49, -1, -27, -39, -46, -44, -33, -51},
            {-14, -14, -22, -46, -44, -30, -15, -27},
            {1, 7, -8, -64, -43, -16, 9, 8},
            {-15, 36, 12, -54, 8, -28, 24, 14}
    };
    private static final int[][] blackmgKingPositionValue = reverse(mgKingPositionValue);

    private static final int[][] egKingPositionValue = {
            {-74, -35, -18, -18, -11, 15, 4, -17},
            {-12, 17, 14, 17, 17, 38, 23, 11},
            {10, 17, 23, 15, 20, 45, 44, 13},
            {-8, 22, 24, 27, 26, 33, 26, 3},
            {-18, -4, 21, 24, 27, 23, 9, -11},
            {-19, -3, 11, 21, 23, 16, 7, -9},
            {-27, -11, 4, 13, 14, 4, -5, -17},
            {-53, -34, -21, -11, -28, -14, -24, -43}
    };
    private static final int[][] blackegKingPositionValue = reverse(egKingPositionValue);


    private int getPiecePositionValue(int piece, int row, int col, boolean endgame) {
        return switch (piece) {
            case 6, 7, 13, 14 -> (currentPlayer == 'w')
                    ? (endgame ? egPawnPositionValue[row][col] : mgPawnPositionValue[row][col])
                    : (endgame ? blackegPawnPositionValue[row][col] : blackmgPawnPositionValue[row][col]);
            case 2, 9 -> (currentPlayer == 'w')
                    ? (endgame ? egKnightPositionValue[row][col] : mgKnightPositionValue[row][col])
                    : (endgame ? blackegKnightPositionValue[row][col] : blackmgKnightPositionValue[row][col]);
            case 3, 10 -> (currentPlayer == 'w')
                    ? (endgame ? egBishopPositionValue[row][col] : mgBishopPositionValue[row][col])
                    : (endgame ? blackegBishopPositionValue[row][col] : blackmgBishopPositionValue[row][col]);
            case 1, 8 -> (currentPlayer == 'w')
                    ? (endgame ? egRookPositionValue[row][col] : mgRookPositionValue[row][col])
                    : (endgame ? blackegRookPositionValue[row][col] : blackmgRookPositionValue[row][col]);
            case 4, 11 -> (currentPlayer == 'w')
                    ? (endgame ? egQueenPositionValue[row][col] : mgQueenPositionValue[row][col])
                    : (endgame ? blackegQueenPositionValue[row][col] : blackmgQueenPositionValue[row][col]);
            case 5, 12 -> (currentPlayer == 'w')
                    ? (endgame ? egKingPositionValue[row][col] : mgKingPositionValue[row][col])
                    : (endgame ? blackegKingPositionValue[row][col] : blackmgKingPositionValue[row][col]);
            default -> 0;
        };
    }


    /*
    private int getPiecePositionValue(int piece, int row, int col, boolean endgame) {
        return switch (piece) {
            case 6, 7, 13, 14 -> (currentPlayer == 'w') ? pawnPositionValue[row][col] : blackPawnPositionValue[row][col];
            case 2, 9 -> (currentPlayer == 'w') ? knightPositionValue[row][col] : blackKnightPositionValue[row][col];
            case 3, 10 -> (currentPlayer == 'w') ? bishopPositionValue[row][col] : blackBishopPositionValue[row][col];
            case 1, 8 -> (currentPlayer == 'w') ? rookPositionValue[row][col] : blackRookPositionValue[row][col];
            case 4, 11 -> (currentPlayer == 'w') ? queenPositionValue[row][col] : blackQueenPositionValue[row][col];
            case 5, 12 -> (currentPlayer == 'w')
                    ? (endgame ? egKingPositionValue[row][col] : mgKingPositionValue[row][col])
                    : (endgame ? blackegKingPositionValue[row][col] : blackmgKingPositionValue[row][col]);
            default -> 0;
        };
    }
     */


    //endregion
//region Evaluation and Heuristics Methods
    // empty, rook, knight, bishop, queen, king, pawn, pawn, rook, knight, bishop, queen, king, pawn, pawn
    private final int[] pieceValues = {0, 540, 300, 320, 900, 20000, 100, 100, 540, 300, 320, 900, 20000, 100, 100};
    public int evaluate() {
        int whiteScore = 0;
        int blackScore = 0;
        int pieceCount = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece != 0) {
                    pieceCount++;
                    int pieceValue = pieceValues[piece];
                    boolean endgame = pieceCount <= 16;
                    int positionValue = getPiecePositionValue(piece, row, col, endgame);

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
    public int[] makeMove(int startRow, int startCol, int endRow, int endCol) {
        int capturedPiece = board[endRow][endCol];
        int piece = board[startRow][startCol];
        int queen;
        board[startRow][startCol] = 0;

        if ((piece == 6 && endRow == 7) || (piece == 7 && endRow == 0)) {
            queen = (currentPlayer == 'w') ? 4 : 11;
            board[endRow][endCol] = queen;
        } else {
            board[endRow][endCol] = piece;
        }

        if (piece == 5) {
            whiteKingRow = endRow;
            whiteKingCol = endCol;
        }
        if(piece==12) {
            blackKingRow = endRow;
            blackKingCol = endCol;
        }
        currentPlayer = (currentPlayer=='w') ? 'b' : 'w';
        return new int[] {piece,capturedPiece};
    }

    public void undoMove(int startRow, int startCol, int endRow, int endCol, int[] moveInfo) {
        int piece = moveInfo[0];
        int capturedPiece = moveInfo[1];

        if ((piece == 4 && startRow == 6 && endRow == 7) || (piece == 11 && startRow == 1 && endRow == 0)) {
            piece = (currentPlayer == 'w') ? 6 : 13;
        }

        if(board[endRow][endCol]==5) {
            whiteKingRow = startRow;
            whiteKingCol = startCol;
        }
        if(board[endRow][endCol]==12) {
            blackKingRow = startRow;
            blackKingCol = startCol;
        }

        board[startRow][startCol] = piece;
        board[endRow][endCol] = capturedPiece;
        currentPlayer = (currentPlayer=='w') ? 'b' : 'w';

    }

    private boolean isEnemyPawn(int row, int col) {
        return (board[row][col] == 1 && currentPlayer == 'b') || (board[row][col] == 9 && currentPlayer == 'w');
    }
//    private boolean canCaptureKing(int attackerRow, int attackerCol, int kingRow, int kingCol) {
//        int attacker = board[attackerRow][attackerCol];
//        int dy = Math.abs(kingRow - attackerRow);
//        int dx = Math.abs(kingCol - attackerCol);
//
//        return switch (attacker) {
//            case 6, 7, 13, 14 -> // PAWNS
//                    (dy == 1 && dx == 1);
//            case 2, 9 -> // KNIGHTS
//                    (dy == 2 && dx == 1) || (dy == 1 && dx == 2);
//            case 1, 8 -> // ROOK
//                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0);
//            case 3, 10 -> // BISHOPS
//                    (dy == dx);
//            case 4, 11 -> // QUEENS
//                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0) || (dy == dx);
//            case 5, 12 -> // KINGS
//                    (dy <= 1 && dx <= 1);
//            default -> false;
//        };
//    }

//    private boolean canEscapeCheck(int kingRow, int kingCol) {
//        int[] attacker;
//
//        // Generate possible moves
//        generateMoves(moves);
//
//        // Check if the king can move to a safe spot
//        for (int[] move : moves) {
//            makeMove(move[0], move[1], move[2], move[3]);
//            if (!kingInCheck(kingRow, kingCol)) {
//                undoMove(move[0], move[1], move[2], move[3], move[4]);
//                return true;
//            }
//            undoMove(move[0], move[1], move[2], move[3], move[4]);
//        }
//
//        // Check if any piece can block or capture the attacker
//        for (int[] move : moves) {
//            attacker = identifyAttacker(kingRow, kingCol);
//            if (move[2] == attacker[0] && move[3] == attacker[1]) {
//                return true;
//            }
//        }
//
//        return false;
//    }
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
        if(!kingInCheck(kingRow,kingCol)) {
            return !canEscapeCheck(kingRow, kingCol);
        }
        return false;
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
        if(kingInCheck(kingRow,kingCol)) {
            if(!canEscapeCheck(kingRow,kingCol)) {
                return true;
            }
            return false;
        }
        return false;
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

        int depth = 12;
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
            int[] bestMove = iterativeDeepening(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, minimax, 40000);
            LocalDateTime endTime = LocalDateTime.now();

            long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
            System.out.println("Execution Time: " + singleThreadedTime + " milliseconds");

            String bestMoveString = bestMove[0] + "" + bestMove[1] + bestMove[2] + bestMove[3];
            System.out.println("Best move: " + bestMoveString);

            game.makeMove(bestMove[0],bestMove[1],bestMove[2],bestMove[3]);
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