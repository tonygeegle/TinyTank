package com.jiguangchao;

import javax.swing.*;


public class TankFrame extends JFrame {
    public TankFrame() {

    }

    public static void main(String[] args) {
        var frame = new TankFrame();
        frame.setTitle("坦克大战");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PlayGround());
        frame.pack();
        frame.setVisible(true);
    }
}
