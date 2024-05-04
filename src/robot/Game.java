package robot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Game {

    int[][] board = new int[8][8];

    int[][] moves = new int[100][5];
    int generateMoveCounter = 0;

    int enPassant;

    boolean whiteLongCastle = true;

    boolean whiteShortCastle = true;
    boolean blackShortCastle = true;
    boolean blackLongCastle = true;
    private int kingRow;
    private int kingCol;
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

    public void generateMoves(Game game) {
        if (currentPlayer == 'w') {
            for (int row = 0; row<8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (game.board[row][col] == 1) {
                        generateRookMoves(row, col);
                    }

                    if (game.board[row][col] == 2) {
                        generateKnightMoves(row,col);
                    }

                    if (game.board[row][col] == 3) {
                        generateBishopMoves(row, col);
                    }

                    if (game.board[row][col] == 4) {
                        generateQueenMoves(row, col);
                    }

                    if (game.board[row][col] == 5) {
                        generateKingMoves(row, col);
                    }

                    if (game.board[row][col] == 6 || game.board[row][col]==7) {
                        generatePawnMoves(row, col);
                    }

                }
            }

        }
        if(currentPlayer == 'b') {
            for (int row = 0; row<8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (game.board[row][col] == 8) {
                        generateRookMoves(row, col);
                    }

                    if (game.board[row][col] == 9) {
                        generateKnightMoves(row,col);
                    }

                    if (game.board[row][col] == 10) {
                        generateBishopMoves(row, col);
                    }

                    if (game.board[row][col] == 11) {
                        generateQueenMoves(row, col);
                    }

                    if (game.board[row][col] == 12) {
                        generateKingMoves(row, col);
                    }
                    if (game.board[row][col] == 13 || game.board[row][col]== 14) {
                        generatePawnMoves(row, col);
                    }
                }
            }
        }
    }


    public void pieceMoveLogic(int[][] directions, int row, int col, boolean canSlide) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (0<=newRow && newRow<8 && 0<=newCol && newCol<8) {
                if(!isTileEmpty(newRow, newCol)) {
                    if(board[newRow][newCol] > 7 && currentPlayer == 'w' || board[newRow][newCol] <= 7 && currentPlayer == 'b') {
                        addMove(row,col,newRow,newCol,board[row][col]);
                    }
                    break;
                }
                else {
                    addMove(row,col,newRow,newCol,board[row][col]);
                }
                if(!canSlide) {
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
    }

    public void generateRookMoves(int row, int col) {
        int[][] directions = {{-1,0}, {1,0}, {0,1}, {0,-1}};
        pieceMoveLogic(directions,row,col,true);
    }

    public void generateBishopMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}};
        pieceMoveLogic(directions, row, col, true);
    }
    public void generateKnightMoves(int row, int col) {
        int[][] directions = {{2,1}, {2,-1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}};
        pieceMoveLogic(directions, row, col, false);
    }

    public void generateQueenMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}};
        pieceMoveLogic(directions, row, col,true);
    }
    public void generateKingMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}};
        pieceMoveLogic(directions,row,col,false);
    }
    public void generatePawnMoves(int row, int col) {
        int piece = board[row][col];
        int direction = (piece == 6) ? 1 : -1; // 1 for white, -1 for black

        if (isTileEmpty(row + direction, col)) {
            if ((direction == 1 && row == 1) || (direction == -1 && row == 6)) {
                if (isTileEmpty(row + 2 * direction, col)) {
                    addMove(row, col, row + 2 * direction, col, piece);
                }
            }
            addMove(row, col, row + direction, col, piece);
        }

        if (col + 1 < 8 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col + 1) && board[row + direction][col + 1] / 7 != piece / 7) {
            addMove(row, col, row + direction, col + 1, piece);
        }
        if (col - 1 >= 0 && row + direction >= 0 && row + direction < 8 && !isTileEmpty(row + direction, col - 1) && board[row + direction][col - 1] / 7 != piece / 7) {
            addMove(row, col, row + direction, col - 1, piece);
        }
    }
    private void addMove(int startRow, int startCol, int endRow, int endCol, int piece) {
        moves[generateMoveCounter][0] = startRow;
        moves[generateMoveCounter][1] = startCol;
        moves[generateMoveCounter][2] = endRow;
        moves[generateMoveCounter][3] = endCol;
        moves[generateMoveCounter][4] = piece;
        generateMoveCounter++;
    }

    public Game(Game currentGame) {
        for (int i = 0; i< board.length; i++) {
            for (int j = 0; j<board.length; j++) {
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
        }
        else {
            newGame.currentPlayer = 'w';
        }
        return newGame;
    }

    //Start på minimax, slet ikke færdig. Er ikke sikker på at den skal ligge her, eller i en main klasse
    public int minimax(int depth, boolean isMaximizing) {
        if(checkForWin()) {
            return Integer.MAX_VALUE;
        }
        if(checkDraw()) {
            return 0;
        }
        if(isMaximizing) {
            int bestValue = -100;
            for(int i = 0; i<moves.length; i++) {
               Game newState = updateGameState(i);
               heuristicValue = minimax(depth+1,false);
               if (heuristicValue > bestValue) {
                   bestValue = heuristicValue;
               }
            }
            return bestValue;

        } else {
            int bestValue= 100;
            for(int i = 0; i<moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth+1,true);
                if(heuristicValue < bestValue) {
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
        return isCheck() && !canEscapeCheck();
    }

    private boolean kingInCheck(int kingRow, int kingCol) {
        if(kingSeeRook(kingRow,kingCol)) {
            System.out.println("King is in check by rook or queen");
            return true;
        }
        else if(kingSeeBishop(kingRow,kingCol)) {
            System.out.println("King is in check by bishop or queen");
            return true;
        }
        else if(knightSeeKing(kingRow, kingCol)) {
            System.out.println("King is in check by knight");
            return true;
        }
        else return false;
    }

    public boolean kingSeeBishop(int kingRow, int kingCol) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0<=newRow && newRow<8 && 0<=newCol && newCol<8) {
                if(!isTileEmpty(newRow, newCol)) {
                    if((board[newRow][newCol] == 10 || board[newRow][newCol]==11) && currentPlayer == 'w' || (board[newRow][newCol]==3 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return true;
                    }
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return false;
    }

    public boolean knightSeeKing(int kingRow, int kingCol) {
        int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0 <= newRow && newRow < 8 && 0 <= newCol && newCol < 8) {
                if (board[newRow][newCol] == 2 && currentPlayer == 'b' || board[newRow][newCol]==9 && currentPlayer=='w' ) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    public boolean kingSeeRook(int kingRow, int kingCol) {
        int[][] directions = {{-1,0}, {1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            int newRow = kingRow + direction[0];
            int newCol = kingCol + direction[1];
            while (0<=newRow && newRow<8 && 0<=newCol && newCol<8) {
                if(!isTileEmpty(newRow, newCol)) {
                    if(((board[newRow][newCol] == 8 || board[newRow][newCol]==11) && currentPlayer == 'w')) return true;
                    if((board[newRow][newCol]==1 || board[newRow][newCol] == 4) && currentPlayer == 'b') {
                        return true;
                    }
                    break;
                }
                    newRow += direction[0];
                    newCol += direction[1];
            }
        }
        return false;
    }
    private boolean isCheck() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((currentPlayer == 'w' && board[row][col] > 7) ||
                        (currentPlayer == 'b' && board[row][col] <= 7 && board[row][col] != 0)) {
                    if (canCaptureKing(row, col, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean canCaptureKing(int attackerRow, int attackerCol, int kingRow, int kingCol) {
        int attacker = board[attackerRow][attackerCol];
        int dy = Math.abs(kingRow - attackerRow);
        int dx = Math.abs(kingCol - attackerCol);

        return switch (attacker) { // white pawn
            // white pawn
            // black pawn
            case 6, 7, 13, 14 -> // black pawn
                    (dy == 1 && dx == 1); // white knight
            case 2, 9 -> // black knight
                    (dy == 2 && dx == 1) || (dy == 1 && dx == 2); // white rook
            case 1, 8 -> // black rook
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0); // white bishop
            case 3, 10 -> // black bishop
                    (dy == dx); // white queen
            case 4, 11 -> // black queen
                    (dy == 0 && dx > 0) || (dx == 0 && dy > 0) || (dy == dx); // white king
            case 5, 12 -> // black king
                    (dy <= 1 && dx <= 1);
            default -> false;
        };
    }

    private boolean canEscapeCheck() {
        //need to be implemented
        return true;
    }
    private boolean onlyKingLeft() {
        int numWhitePieces = 0;
        int numBlackPieces = 0;

        for (int[] row : board) {
            for (int piece : row) {
                if (piece == 1 || piece == 2 || piece == 3 || piece == 4 || piece == 5 || piece == 6 ||piece == 7) {
                    numWhitePieces++;
                } else if (piece == 8 || piece == 9 || piece == 10 || piece == 11 || piece == 12 || piece == 13 || piece == 14) {
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
        switch (piece) {
            case 6: // white pawn
            case 7: // white pawn
            case 13: // black pawn
            case 14: // black pawn
                return 100;
            case 2:  // White knight
            case 9:  // Black knight
                return 300;
            case 3:  // White bishop
            case 10: // Black bishop
                return 300;
            case 1:  // White rook
            case 8:  // Black rook
                return 500;
            case 4:  // White queen
            case 11: // Black queen
                return 900;
            case 5:  // White king
            case 12: // Black king
                //king got no value from what i found
                return 0;
            default:
                return 0;
        }
    }

    private int getPiecePositionValue(int piece, int row, int col) {
        int value = 0;
        switch (piece) {
            case 6:  // White pawn
            case 7:  // White pawn
                value += row * 10;
                break;
            case 13: // Black pawn
            case 14: // Black pawn
                value += (7 - row) * 10;
                break;
            case 2:  // White knight
            case 9:  // Black knight
                //adds points in increment of 5 depending on distance from center
                value += (3 - Math.abs(row - 3)) * 5 + (3 - Math.abs(col - 3)) * 5;
                break;
            case 3:  // White bishop
            case 10: // Black bishop
                value += (3 - Math.abs(row - 3)) * 5 + (3 - Math.abs(col - 3)) * 5;
                break;
            case 1:  // White rook
            case 8:  // Black rook
                value += (Math.abs(row - 3) + Math.abs(col - 3)) * 5;
                break;
            case 4:  // White queen
            case 11: // Black queen
                value += 20;
                break;
            case 5:  // White king
            case 12: // Black king
                break;
        }
        return value;
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

    public static void main(String[] args) {

        Game game = new Game();

        //Rook = 1, Knight = 2, Bishop = 3
        game.board[0][0] = 1;
        game.board[5][5] = 5;

        //Introducing black pieces
        game.board[4][4] = 11;
        game.board[4][3] = 9;

        Random rnd = new Random();
        System.out.println();

        game.currentPlayer = 'w';
        System.out.println("White turn");
        game.generateMoves(game);
        game.kingInCheck(5,5);

        Game newState = game.updateGameState(2);
        System.out.println(newState.currentPlayer);
        newState.generateMoves(newState);
        game.kingInCheck(5,5);


        Game newnew = newState.updateGameState(1);

        System.out.println(newnew.currentPlayer);

        newnew.generateMoves(newnew);

    }


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
                if ((currentPlayer == 'w' && piece == 5) || (currentPlayer == 'b' && piece == 12)) {
                    kingRow = i;
                    kingCol = j;
                    return;
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

            case 'p' -> 13;  // black pawn
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

            case 8 -> 'r';  // black rook
            case 9 -> 'n';  // black knight
            case 10 -> 'b';  // black bishop
            case 11 -> 'q';  // black queen
            case 12 -> 'k';  // black king

            case 6, 7 -> 'P';  // white pawn
            case 1 -> 'R';  // white rook
            case 2 -> 'N';  // white knight
            case 3 -> 'B';  // white bishop
            case 4 -> 'Q';  // white queen
            case 5 -> 'K';  // white king
            default -> '.';  // empty square
        };
    }

    public void makeMove(int startRow, int startCol, int endRow, int endCol) {
        int piece = board[startRow][startCol];
        board[startRow][startCol] = 0;
        board[endRow][endCol] = piece;
        currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';

        if ((piece == 5 && currentPlayer == 'w') || (piece == 12 && currentPlayer == 'b')) {
            kingRow = endRow;
            kingCol = endCol;
        }
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
//        Game game = new Game();
//        game.initializeBoard(fen);
//        game.printBoard();
//
//        LocalDateTime startTime;
//        LocalDateTime endTime;
//        /*
//        startTime = LocalDateTime.now();
//        //single-threaded test
//        int[] bestMove = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//        endTime = LocalDateTime.now();
//
//        long singleThreadedTime = Duration.between(startTime, endTime).toMillis();
//        System.out.println("Single-threaded Minimax Time: " + singleThreadedTime + " milliseconds");
//         */
//
//        startTime = LocalDateTime.now();
//        //multi-threaded test
//        int[] result = parallelMinimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//
//        endTime = LocalDateTime.now();
//
//        long multiThreadedTime = Duration.between(startTime, endTime).toMillis();
//        System.out.println("Work Stealing Minimax Time: " + multiThreadedTime + " milliseconds");
//
//        System.out.println("Node count: " + result[0]);
//
//        System.out.println("bestmove: " + result[1]+ result[2]+ result[3]+ result[4]);
//        game.makeMove(result[1], result[2], result[3], result[4]);
//        game.printBoard();
//        String newFEN = game.getFEN();
//        System.out.println("New FEN string:");
//        System.out.println(newFEN);
//    }
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


    public static int[] minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer) {
    //does not take into account win or lose atm
        if (depth == 0 || game.isGameFinished()) {
            return null;
        }

        int[] bestMove = null;
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        game.generateMoves(game);

        for (int i = 0; i < game.generateMoveCounter; i++) {
            int[] move = game.moves[i];
            Game newGame = new Game(game);
            newGame.makeMove(move[0], move[1], move[2], move[3]);

            int[] result = minimax(newGame, depth - 1, alpha, beta, !maximizingPlayer);

            if (result == null) {
                int score = newGame.evaluate();
                if (maximizingPlayer && score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                } else if (!maximizingPlayer && score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            } else {
                if (maximizingPlayer) {
                    if (result[4] > bestScore) {
                        bestScore = result[4];
                        bestMove = move;
                    }
                    alpha = Math.max(alpha, bestScore);
                } else {
                    if (result[4] < bestScore) {
                        bestScore = result[4];
                        bestMove = move;
                    }
                    beta = Math.min(beta, bestScore);
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }

        return bestMove;
    }
}