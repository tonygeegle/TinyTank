package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

enum Directions {
    NORTH, SOUTH, WEST, EAST;
}

public class PlayGround extends JComponent {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MOVE_DISTANCE = 2;
    private static final int DELAY = 50;
    private static final int SIDE_LENGTH = 50;
    private ArrayList<Rectangle2D> tanks;

    private Random random = new Random();

    public PlayGround() {
        tanks = new ArrayList<>(10);
        addTanks(10);
    }

    private void addTanks(int num) {
        double x, y;
        double rangeX = DEFAULT_WIDTH - SIDE_LENGTH;
        double rangeY = DEFAULT_HEIGHT - SIDE_LENGTH;

        for (int i = 0; i < num; i++) {
            x = random.nextDouble() * rangeX;
            y = random.nextDouble() * rangeY;
            var tank = new Rectangle2D.Double(x, y, SIDE_LENGTH, SIDE_LENGTH);
            tanks.add(tank);
            Thread t = new Thread(new MoveTank(tank));
            t.start();
        }

    }

    private class MoveTank implements Runnable {
        private Rectangle2D target;

        public MoveTank(Rectangle2D target) {
            this.target = target;
        }

        private void delay(int milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void move(Directions direction, int steps) {
            Double 
            for (int i = 0; i < steps; i++) {
                delay(DELAY);
                switch (direction) {
                    case EAST -> {
                        double newX = target.getX() + MOVE_DISTANCE;
                    }
                    case NORTH -> {
                        double new = target.get() + MOVE_DISTANCE;
                    }
                    case WEST -> {

                    }
                    case SOUTH -> {
                    }
                }

                double newX = target.getX() + MOVE_DISTANCE * direction;
                // 边界检测
                if (newX + SIDE_LENGTH > DEFAULT_WIDTH || newX < 0) {
                    break;
                }
                // 碰撞检测
                // to_do
                target.setFrame(newX, target.getY(), SIDE_LENGTH, SIDE_LENGTH);
                repaint();
            }
        }

        @Override
        public void run() {

            int direction = -1;

            while (true) {
                // 随机移动次数
                int x_distance = random.nextInt(DEFAULT_WIDTH / MOVE_DISTANCE);
                int y_distance = random.nextInt(DEFAULT_HEIGHT / MOVE_DISTANCE);
                // 变换移动方向
                direction *= -1;
                // X方向进行移动
                for (int i = 0; i < x_distance; i++) {
                    delay(DELAY);
                    double newX = target.getX() + MOVE_DISTANCE * direction;
                    // 边界检测
                    if (newX + SIDE_LENGTH > DEFAULT_WIDTH || newX < 0) {
                        break;
                    }
                    // 碰撞检测
                    // to_do
                    target.setFrame(newX, target.getY(), SIDE_LENGTH, SIDE_LENGTH);
                    repaint();
                }
                // Y方向进行移动
                for (int i = 0; i < y_distance; i++) {
                    delay(DELAY);
                    double newY = target.getY() + MOVE_DISTANCE * direction;
                    if (newY + SIDE_LENGTH > DEFAULT_HEIGHT || newY < 0) {
                        break;
                    }
                    target.setFrame(target.getX(), newY, SIDE_LENGTH, SIDE_LENGTH);
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
