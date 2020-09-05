package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class PlayGround extends JComponent {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private static final int SIDELENGTH = 50;
    private ArrayList<Rectangle2D> tanks;

    private Random random = new Random();

    public PlayGround() {
        tanks = new ArrayList<>(10);
        addTanks(10);
    }

    private void addTanks(int num) {
        double x, y;
        double rangeX = DEFAULT_WIDTH - SIDELENGTH;
        double rangeY = DEFAULT_HEIGHT - SIDELENGTH;

        for (int i = 0; i < num; i++) {
            x = random.nextDouble() * rangeX;
            y = random.nextDouble() * rangeY;
            var tank = new Rectangle2D.Double(x, y, SIDELENGTH, SIDELENGTH);
            tanks.add(tank);
            Thread t = new Thread(new MoveTank(tank));
            t.start();
        }

    }

    private class MoveTank implements Runnable {
        private Rectangle2D target;
        private int deltX = 2;
        private int deltY = 2;

        public MoveTank(Rectangle2D target) {
            this.target = target;
        }

        @Override
        public void run() {

            int direction = -1;

            while (true) {
                // 随机移动次数
                int x_distance = random.nextInt(DEFAULT_WIDTH / deltX);
                int y_distance = random.nextInt(DEFAULT_HEIGHT / deltY);
                // 变换移动方向
                direction *= -1;
                // X方向进行移动
                for (int i = 0; i < x_distance; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double newX = target.getX() + deltX * direction;
                    // 边界检测
                    if (newX + SIDELENGTH > DEFAULT_WIDTH || newX < 0) {
                        break;
                    }
                    // 碰撞检测
                    // to_do
                    target.setFrame(newX, target.getY(), SIDELENGTH, SIDELENGTH);
                    repaint();
                }
                // Y方向进行移动
                for (int i = 0; i < y_distance; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double newY = target.getY() + deltY * direction;
                    if (newY + SIDELENGTH > DEFAULT_HEIGHT || newY < 0) {
                        break;
                    }
                    target.setFrame(target.getX(), newY, SIDELENGTH, SIDELENGTH);
                    repaint();
                }
            }

        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        var g2 = (Graphics2D) g;
//        g2.setPaint(Color.GREEN);
        // draw all tanks
        for (Rectangle2D tank : tanks)
            g2.draw(tank);
    }
}
