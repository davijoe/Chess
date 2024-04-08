package main;

import robot.Bishop;
import robot.Rook;
import robot.Move;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
        //Below is just testing
        robot.Board cboard = new robot.Board();
        Bishop wb = new Bishop(1,1,true);

        Rook rook = new Rook(2,3,true);
        List<Move> test = rook.generateMoves(cboard);
        int nr = 0;
        //Just testing rook moves
        for(Move move : test) {

            System.out.println("Move nr: "+nr);
            System.out.println(move.toString());
            nr++;
        }

        frame.setVisible(true);
    }
}
