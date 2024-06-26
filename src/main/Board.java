package main;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Board extends JPanel {

    public int tileSize = 100;

    int col = 8;
    int row = 8;

    ArrayList<Piece> pieces = new ArrayList<>();

    public Piece selectedPiece;

    Input input = new Input(this);

    public Board() {
        this.setPreferredSize(new Dimension(col * tileSize, row * tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);

        addPiece();

    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieces) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        capturePiece(move);
    }

    public void capturePiece(Move move){
        pieces.remove(move.capturedPiece);
    }

    public boolean isValidMove(Move move) {

        if (sameTeam(move.piece, move.capturedPiece)){
            return false;
        }

        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2){
        if (p1 == null || p2 == null){
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    public void addPiece() {
        pieces.add(new Rook(this, 0, 0, false));
        pieces.add(new Knight(this, 1, 0, false));
        pieces.add(new Bishop(this, 2, 0, false));
        pieces.add(new Queen(this, 3, 0, false));
        pieces.add(new King(this, 4, 0, false));
        pieces.add(new Bishop(this, 5, 0, false));
        pieces.add(new Knight(this, 6, 0, false));
        pieces.add(new Rook(this, 7, 0, false));

        pieces.add(new Pawn(this, 0, 1, false));
        pieces.add(new Pawn(this, 1, 1, false));
        pieces.add(new Pawn(this, 2, 1, false));
        pieces.add(new Pawn(this, 3, 1, false));
        pieces.add(new Pawn(this, 4, 1, false));
        pieces.add(new Pawn(this, 5, 1, false));
        pieces.add(new Pawn(this, 6, 1, false));
        pieces.add(new Pawn(this, 7, 1, false));

        pieces.add(new Rook(this, 0, 7, true));
        pieces.add(new Knight(this, 1, 7, true));
        pieces.add(new Bishop(this, 2, 7, true));
        pieces.add(new Queen(this, 3, 7, true));
        pieces.add(new King(this, 4, 7, true));
        pieces.add(new Bishop(this, 5, 7, true));
        pieces.add(new Knight(this, 6, 7, true));
        pieces.add(new Rook(this, 7, 7, true));

        pieces.add(new Pawn(this, 0, 6, true));
        pieces.add(new Pawn(this, 1, 6, true));
        pieces.add(new Pawn(this, 2, 6, true));
        pieces.add(new Pawn(this, 3, 6, true));
        pieces.add(new Pawn(this, 4, 6, true));
        pieces.add(new Pawn(this, 5, 6, true));
        pieces.add(new Pawn(this, 6, 6, true));
        pieces.add(new Pawn(this, 7, 6, true));
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the board tiles
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                g2d.setColor((c + r) % 2 == 0 ? new Color(99, 55, 12) : new Color(255, 220, 189));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }

            for (Piece piece : pieces) {
                piece.paint(g2d);

            }
        }
    }
}
