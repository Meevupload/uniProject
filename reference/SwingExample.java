import javax.swing.*;
import java.awt.*;

class gui {
    public static void main(String args[]) {

        //Creating the Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar(); //menu bar at the top
        JMenu m1 = new JMenu("FILE"); //menu element
        JMenu m2 = new JMenu("Help"); 
        mb.add(m1); //add both of these to the JMenuBar object
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Open"); // menu drop down items
        JMenuItem m22 = new JMenuItem("Save as");
        m1.add(m11); // adding these drop down items to the menu element
        m1.add(m22);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter Text");
        JTextField tf = new JTextField(10); // accepts upto 10 characters
        JButton send = new JButton("Send");
        JButton reset = new JButton("Reset");
        panel.add(label); // Components Added using Flow Layout
        panel.add(tf);
        panel.add(send);
        panel.add(reset);

        // Text Area at the Center
        JTextArea ta = new JTextArea();

        //There are 5 areas: NORTH, SOUTH, LEFT, RIGHT, CENTER
        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true); // make the frame visible
    }
}