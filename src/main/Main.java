package main;

import robot.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        // Create a new JFrame to hold the game
        JFrame frame = new JFrame("AlphaNaN - Chess Game"); // Create JFrame with title "AlphaNull - Chess Game"
        frame.setLayout(new GridBagLayout()); // GridBagLayout class is a layout manager
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the frame is closed
        frame.setSize(1000, 1000); // Set the size of the frame
        frame.setLocationRelativeTo(null); // Center the frame

        // Add the board to the frame
        Board board = new Board();
        frame.add(board);

        Game gameboard = new Game();

        int[][] fisk = gameboard.getBoard();

        fisk[0][0] = 1;

        gameboard.setBoard(fisk);

        for (int[] row : fisk) {
            for (int col : row) {
                System.out.println(col);
            }
        }

        frame.setVisible(true);
    }
}
