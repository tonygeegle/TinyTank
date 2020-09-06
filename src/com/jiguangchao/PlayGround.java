package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SingleRandom extends Random {
    public static SingleRandom INSTANCE = new SingleRandom();

    private SingleRandom() {
    }
}

class Globals {

}

enum Directions {
    CENTER, NORTH, SOUTH, WEST, EAST;
    // 随机获取一个 direction
    public Directions getNextRandomDirection() {
        Directions[] enumConstants = Directions.class.getEnumConstants();
        int index = SingleRandom.INSTANCE.nextInt(enumConstants.length);
        return enumConstants[index];
    }
}

class Tank extends Rectangle2D.Double {
    private static int NEXT_ID = 0;
    private int id = NEXT_ID++;
    private int HP = 100;
    private Directions direction = Directions.CENTER;
    private static final int SIDE_LENGTH = 50;

    public Tank(double x, double y) {
        super(x, y, SIDE_LENGTH, SIDE_LENGTH);
    }

    public Tank() {
        super(SIDE_LENGTH * NEXT_ID, 0, SIDE_LENGTH, SIDE_LENGTH);
    }

    public int getId() {
        return id;
    }

    public Directions getDirection() {
        return direction;
    }

    private Directions changeAndGetDirection() {
        direction = direction.getNextRandomDirection();
        return direction;
    }

    public void move() {

    }

    private void move(Directions direction, int steps) {
        for (int i = 0; i < steps; i++) {
            double curX = getX();
            double curY = getY();
            delay(DELAY);

            switch (direction) {
                case EAST -> {
                    curX += MOVE_DISTANCE;
                }
                case NORTH -> {
                    curY += MOVE_DISTANCE;
                }
                case WEST -> {
                    curX -= MOVE_DISTANCE;
                }
                case SOUTH -> {
                    curY -= MOVE_DISTANCE;
                }
                default -> {

                }
            }
            // 边界检测
            Tank rect = new Tank(curX , curY, SIDE_LENGTH, SIDE_LENGTH);
            if (isCollide(rect, tanks) || curX + SIDE_LENGTH > DEFAULT_WIDTH || curX < 0 || curY + SIDE_LENGTH > DEFAULT_HEIGHT || curY < 0) {
                direction = direction.getNextRandomDirection();
                continue;
            }
            // 碰撞检测
            //
            //wlock.lock();
            try {
                target.setFrame(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
            }
            finally {
                //wlock.unlock();
            }
            repaint();
        }
    }

    public void paintSelf(Graphics2D g2) {
        g2.draw(this);
        g2.drawString(String.valueOf(getId()),(float) (getX() + SIDE_LENGTH / 3), (float) (getY() + SIDE_LENGTH / 2));
    }

    @Override
    public String toString() {
        return "Tank{" +
                "id=" + id +
                ", HP=" + HP +
                ", direction=" + direction +
                '}';
    }
}

public class PlayGround extends JComponent {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MOVE_DISTANCE = 25;
    private static final int DELAY = 1000;
    private static final int SIDE_LENGTH = 50;

    private ArrayList<Tank> tanks;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rlock = rwLock.readLock();
    private final Lock wlock = rwLock.writeLock();

    private Random random = SingleRandom.INSTANCE;

    public PlayGround() {
        tanks = new ArrayList<>(10);
        addTanks(15);
    }


    private void addTanks(int num) {
        double x = 0, y = 0;
        double rangeX = DEFAULT_WIDTH - SIDE_LENGTH;
        double rangeY = DEFAULT_HEIGHT - SIDE_LENGTH;

        for (int i = 0; i < num; i++) {
//            x = random.nextDouble() * rangeX;
//            y = random.nextDouble() * rangeY;
            x = i * SIDE_LENGTH;
            var tank = new Tank(x, y, SIDE_LENGTH, SIDE_LENGTH);
            tanks.add(tank);
            Thread t = new Thread(new MoveTank(tank));
            t.start();
        }

    }

    private class MoveTank implements Runnable {
        private Tank target;

        public MoveTank(Tank target) {
            this.target = target;
        }

        private void delay(int milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean isCollide(Tank curRect, ArrayList<Tank> tanks) {
                //rlock.lock();
                try {
                    for (var tank: tanks) {
                        if (tank.getId() != target.getId() && curRect.intersects(tank)) {
                            System.out.println(target + "collide->" + tank);
                            return true;
                        }
                    }
                }
                finally {
                    //rlock.unlock();
                }
                return false;
        }

        private void move(Directions direction, int steps) {

            for (int i = 0; i < steps; i++) {
                Double curX = this.target.getX();
                Double curY = this.target.getY();
                delay(DELAY);

                switch (direction) {
                    case EAST -> {
                        curX += MOVE_DISTANCE;
                    }
                    case NORTH -> {
                        curY += MOVE_DISTANCE;
                    }
                    case WEST -> {
                        curX -= MOVE_DISTANCE;
                    }
                    case SOUTH -> {
                        curY -= MOVE_DISTANCE;
                    }
                    default -> {

                    }
                }
                // 边界检测
                Tank rect = new Tank(curX , curY, SIDE_LENGTH, SIDE_LENGTH);
                if (isCollide(rect, tanks) || curX + SIDE_LENGTH > DEFAULT_WIDTH || curX < 0 || curY + SIDE_LENGTH > DEFAULT_HEIGHT || curY < 0) {
                    direction = direction.getNextRandomDirection();
                    continue;
                }
                // 碰撞检测
                //
                //wlock.lock();
                try {
                    target.setFrame(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
                }
                finally {
                    //wlock.unlock();
                }
                repaint();
            }
        }

        @Override
        public void run() {
            while (true) {
                // 随机移动次数
                int steps = random.nextInt(DEFAULT_WIDTH / MOVE_DISTANCE);
                move(Directions.CENTER.getNextRandomDirection(), steps);
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
        for (Tank tank : tanks) {
            tank.paintSelf(g2);
        }
    }
}
