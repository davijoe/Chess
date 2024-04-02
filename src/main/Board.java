package main;

import pieces.Knight;
import pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Board extends JPanel {

    public int tileSize = 100;

    int col = 8;
    int row = 8;

    ArrayList<Piece> pieces = new ArrayList<>();

    public Board() {
        this.setPreferredSize(new Dimension(col * tileSize, row * tileSize));
        addPiece();

    }

    public void addPiece() {
        pieces.add(new Knight(this, 1, 0, false));
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
