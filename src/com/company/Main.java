package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        //create frame
        JFrame f = new JFrame("Candy Crush");

        //set frame settings
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(576,475);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

        Menu menu = new Menu();
        menu.requestFocusInWindow();
        menu.setFocusable(true);
        f.add(menu);
        f.setVisible(true);
    }
}
