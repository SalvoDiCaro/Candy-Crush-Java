package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class Menu extends JPanel implements ActionListener, MouseListener{

    private Timer timer;

    public Menu(){
        super.addMouseListener(this);
        //definition of timer delay to update paint
        this.timer = new Timer(50,this);
        this.timer.start();

        JButton button = new JButton("NEW GAME");
        this.setLayout(null);
        button.setBounds(238, 380, 100, 30);
        this.add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame("Candy Crush");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(576,475);
                f.setLocationRelativeTo(null);
                f.setResizable(false);

                //create a game and add it in the frame
                CandyCrush game = new CandyCrush();
                game.requestFocusInWindow();
                game.setFocusable(true);
                f.add(game);
                f.setVisible(true);
                getTopLevelAncestor().setVisible(false);
            }
        } );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("img/bgImage.png"));
        } catch (IOException e){
            e.printStackTrace();
        }
        g.drawImage(bgImage,0,0,576,475, null);
    }
}
