package robot;

public class Board {

    int[][] board = new int[8][8];

    int generateMoveCounter = 0;

    int enPassant = 1;

    int longCastle = 2;

    int shortCastle = 3;

    Move moves = new Move(100);
    char currentPlayer;

    public Board() {

    }

    public boolean isTileEmpty(int row, int col) {
        return board[row][col] == 0;
    }

    public void generateMoves(Board board) {

        if (currentPlayer == 'w') {
            for (int row = 0; row<8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board.board[row][col] == 1) {
                        generateRookMoves(row, col);
                    }

                    if (board.board[row][col] == 2) {
                        generateKnightMoves(row,col);
                    }

                    if (board.board[row][col] == 3) {
                        generateBishopMoves(row, col);
                    }

                    if (board.board[row][col] == 4) {
                        generateQueenMoves(row, col);
                    }

                    if (board.board[row][col] == 5) {
                        generateKingMoves(row, col);
                    }

                    if (board.board[row][col] == 6) {
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
                    if(board[rowNew][colNew] > 6 && currentPlayer == 'w' || board[rowNew][colNew] < 6 && currentPlayer == 'b') {
                        moves.moves[generateMoveCounter][0] = row;
                        moves.moves[generateMoveCounter][1] = col;
                        moves.moves[generateMoveCounter][2] = rowNew;
                        moves.moves[generateMoveCounter][3] = colNew;
                        System.out.println("\nRook Move nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
                        System.out.println("New Row, Col: (" + rowNew+", "+colNew+")");
                        moves.moves[generateMoveCounter][4] = 1;
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
                    moves.moves[generateMoveCounter][4] = 1;
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
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 3;
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
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 2;
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
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 4;
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
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 5;
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

        Board board = new Board();

        //Rook = !, Knight = 2, Bishop = 3
        board.board[0][0] = 1;
        board.board[0][1] = 1;
        board.board[0][2] = 2;
        board.board[0][3] = 3;
        board.board[4][5] = 1;
        board.board[5][6] = 2;
        board.board[7][6] = 3;

        //Introducing black pieces
        board.board[5][0] = 7;


        board.currentPlayer = 'w';
        board.generateMoves(board);

    }
}
