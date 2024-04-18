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
        int counter = 0;
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                if(isTileEmpty(rowNew,colNew)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    System.out.println("\nMove nr: " + generateMoveCounter + "\nRow, Col: (" + row+", "+col+")");
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
        int counter = 0;
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(isTileEmpty(row,col)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 3;
                    generateMoveCounter++;
                }
                rowNew += direction[0];
                colNew += direction[1];
            }
        }


    }
    public void generateKnightMoves(int row, int col) {
        int[][] directions = {{2,1}, {2,-1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}};
        int counter = 0;
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            if (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(isTileEmpty(row,col)) {
                    moves.moves[generateMoveCounter][0] = row;
                    moves.moves[generateMoveCounter][1] = col;
                    moves.moves[generateMoveCounter][2] = rowNew;
                    moves.moves[generateMoveCounter][3] = colNew;
                    moves.moves[generateMoveCounter][4] = 2;
                    generateMoveCounter++;
                }
            }
        }

    }
    public void generateQueenMoves(int row, int col) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1}, {-1,-1}, {-1,0}, {1,0}, {0,1}, {0,-1}};
        int counter = 0;
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(isTileEmpty(row,col)) {
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
        int counter = 0;
        for (int[] direction : directions) {
            int rowNew = row + direction[0];
            int colNew = col + direction[1];
            if (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
                //Checking if im dumb
                System.out.println(counter);
                counter++;
                if(isTileEmpty(row,col)) {
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


//        int[][] directions = new int[2][2];
//        if (board[row][col] == 6) {
//            directions[0][0] = 1;
//        }
//        int counter = 0;
//        for (int[] direction : directions) {
//            int rowNew = row + direction[0];
//            int colNew = col + direction[1];
//            while (0<=rowNew && rowNew<8 && 0<=colNew && colNew<8) {
//                //Checking if im dumb
//                System.out.println(counter);
//                counter++;
//                if(isTileEmpty(row,col)) {
//                    moves.moves[generateMoveCounter][0] = row;
//                    moves.moves[generateMoveCounter][1] = col;
//                    moves.moves[generateMoveCounter][2] = rowNew;
//                    moves.moves[generateMoveCounter][3] = colNew;
//                    moves.moves[generateMoveCounter][4] = 2;
//                    generateMoveCounter++;
//                }
//            }
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

        board.board[0][0] = 1;
        board.board[0][1] = 1;
//        board.board[0][2] = 1;
//        board.board[0][3] = 1;
//        board.board[0][4] = 1;
//        board.board[0][5] = 1;
//        board.board[0][6] = 1;


        board.currentPlayer = 'w';
        board.generateMoves(board);

    }
}
