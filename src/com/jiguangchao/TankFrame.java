package com.jiguangchao;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class TankFrame extends JFrame {
    public TankFrame() {

    }

    public static void main(String[] args) {
        var frame = new TankFrame();
        var pg = new PlayGround();
        frame.setTitle("坦克大战");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(pg);
        frame.addKeyListener(pg);
        frame.pack();
        frame.setVisible(true);
    }
}
