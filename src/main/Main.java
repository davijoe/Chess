package main;

import robot.Bishop;

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
        Bishop wb = new Bishop(1,1,true);

        wb.generateMoves();

        frame.setVisible(true);
    }
}
