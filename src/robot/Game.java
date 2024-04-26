package robot;

import java.util.Random;

public class Game {

    int[][] board = new int[8][8];

    int generateMoveCounter = 0;

    int enPassant = 1;

    boolean whiteLongCastle = true;

    boolean whiteShortCastle = true;
    boolean blackShortCastle = true;
    boolean blackLongCastle = true;

    Move moves = new Move(100);
    char currentPlayer;

    int heuristicValue;
    boolean isMaximizing;

    public Game() {

    }

    public boolean isTileEmpty(int row, int col) {
        return board[row][col] == 0;
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



    public void generateRookMoves(int row, int col) {
        int[][] directions = {{-1,0}, {1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if (!isTileEmpty(rowNew, colNew)) {
                    if(board[rowNew][colNew] > 7 && currentPlayer == 'w' || board[rowNew][colNew] <= 7 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = board[row][col];
                        generateMoveCounter++;
                    }
                    break;
                }
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                    System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                    moves.moves[generateMoveCounter][4] = board[row][col];
                    generateMoveCounter++;
                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }

    }

    public void generateBishopMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}};
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if (!isTileEmpty(rowNew, colNew)) {
                    if(board[rowNew][colNew] > 7 && currentPlayer == 'w' || board[rowNew][colNew] <= 7 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nBishop Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = board[row][col];
                        generateMoveCounter++;
                    }
                    break;
                }
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = board[row][col];
                    System.out.println("\nBishop Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                    System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                    generateMoveCounter++;
                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }


    }
    public void generateKnightMoves(int row, int col) {
        int[][] directions = {{2,1}, {2,-1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}};
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            if (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if (!isTileEmpty(rowNew, colNew)) {
                    if(board[rowNew][colNew] > 7 && currentPlayer == 'w' || board[rowNew][colNew] <= 7 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = board[row][col];
                        generateMoveCounter++;
                    }
                    break;
                }
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = board[row][col];
                    System.out.println("\nKnight Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                    System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                    generateMoveCounter++;
                }
            }
        }

    }
    public void generateQueenMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if (!isTileEmpty(rowNew, colNew)) {
                    if(board[rowNew][colNew] > 7 && currentPlayer == 'w' || board[rowNew][colNew] <= 7 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = board[row][col];
                        generateMoveCounter++;
                    }
                    break;
                }
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = board[row][col];
                    generateMoveCounter++;
                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }

    }
    public void generateKingMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            if (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if (!isTileEmpty(rowNew, colNew)) {
                    if(board[rowNew][colNew] > 7 && currentPlayer == 'w' || board[rowNew][colNew] <= 7 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = board[row][col];
                        generateMoveCounter++;
                    }
                    break;
                }
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = board[row][col];
                    generateMoveCounter++;
                }
            }
        }
    }

    public void generatePawnMoves(int row, int col) {
        if (board[row][col]==6) {
            if(isTileEmpty(row+1,col)) {
                if(isTileEmpty(row+2,col) && row==1) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = row+2;
                    moves.moves[generateMoveCounter][3] = col;
                    moves.moves[generateMoveCounter][4] = 6;
                    generateMoveCounter++;
                }
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row+1;
                moves.moves[generateMoveCounter][3] = col;
                moves.moves[generateMoveCounter][4] = 6;
                generateMoveCounter++;
            }
            if(!isTileEmpty(row+1,col+1) && board[row+1][col+1] > 6) {
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row+1;
                moves.moves[generateMoveCounter][3] = col+1;
                moves.moves[generateMoveCounter][4] = 6;
                generateMoveCounter++;
            }
            if(!isTileEmpty(row+1,col-1) && board[row+1][col-1] > 6) {
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row+1;
                moves.moves[generateMoveCounter][3] = col-1;
                moves.moves[generateMoveCounter][4] = 6;
                generateMoveCounter++;
            }
        }

        if(board[row][col]==12) {
            if(isTileEmpty(row-1,col)) {
                if(isTileEmpty(row-2,col) && row==6) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = row-2;
                    moves.moves[generateMoveCounter][3] = col;
                    generateMoveCounter++;
                }
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row-1;
                moves.moves[generateMoveCounter][3] = col;
                generateMoveCounter++;
            }
            if(!isTileEmpty(row-1,col+1) && board[row-1][col+1] <= 6) {
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row-1;
                moves.moves[generateMoveCounter][3] = col+1;
                moves.moves[generateMoveCounter][4] = 12;
                generateMoveCounter++;
            }
            if(!isTileEmpty(row-1,col-1) && board[row-1][col-1] <= 6) {
                moves.moves[generateMoveCounter][0] = row;
                moves.moves[generateMoveCounter][1] = col;
                moves.moves[generateMoveCounter][2] = row-1;
                moves.moves[generateMoveCounter][3] = col-1;
                moves.moves[generateMoveCounter][4] = 12;
                generateMoveCounter++;
            }
        }
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
//        int oldRow = moves.moves[moveIndex][0];
//        int oldCol = moves.moves[moveIndex][1];
//        int newRow = moves.moves[moveIndex][2];
//        int newCol = moves.moves[moveIndex][3];
//        int piece = moves.moves[moveIndex][4];

        Game newGame = new Game(this);
        newGame.board[moves.moves[moveIndex][0]][moves.moves[moveIndex][1]] = 0;
        newGame.board[moves.moves[moveIndex][2]][moves.moves[moveIndex][3]] = moves.moves[moveIndex][4];

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

        }
        if(isMaximizing) {
            int bestValue = -100;
            for(int i = 0; i<moves.moves.length; i++) {
               Game newState = updateGameState(i);
               heuristicValue = minimax(depth+1,false);
               if (heuristicValue > bestValue) {
                   bestValue = heuristicValue;
               }
            }
            return bestValue;

        }
        if(!isMaximizing) {
            int bestValue= 100;
            for(int i = 0; i<moves.moves.length; i++) {
                Game newState = updateGameState(i);
                heuristicValue = minimax(depth+1,true);
                if(heuristicValue < bestValue) {
                    bestValue = heuristicValue;
                }
            }
            return bestValue;
        }

        return 0;
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

    public int getLongCastle() {
        return longCastle;
    }

    public void setLongCastle(int longCastle) {
        this.longCastle = longCastle;
    }

    public int getShortCastle() {
        return shortCastle;
    }

    public void setShortCastle(int shortCastle) {
        this.shortCastle = shortCastle;
    }

    public Move getMoves() {
        return moves;
    }

    public void setMoves(Move moves) {
        this.moves = moves;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public static void main(String[] args) {

        Game game = new Game();

        //Rook = !, Knight = 2, Bishop = 3
        game.board[0][0] = 1;

        //Introducing black pieces
        game.board[5][0] = 8;

        Random rnd = new Random();
        System.out.println();

        game.currentPlayer = 'b';
        System.out.println("Blacks turn");
        game.generateMoves(game);

        Game newState = game.updateGameState(2);
        System.out.println(newState.currentPlayer);
        newState.generateMoves(newState);


        Game newnew = newState.updateGameState(1);

        System.out.println(newnew.currentPlayer);

        newnew.generateMoves(newnew);

    }
}
