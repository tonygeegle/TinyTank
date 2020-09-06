package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
// 引入全局常量
import static com.jiguangchao.GlobalSettings.*;

class SingleRandom extends Random {
    public static SingleRandom INSTANCE = new SingleRandom();

    private SingleRandom() {
    }
}

class UtilTools {
    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
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
    // Tank 类自己维护本 tank 所属的集团军
    public static CopyOnWriteArrayList<Tank> tankGroup = new CopyOnWriteArrayList<Tank>();
    // Graphics2D
    private Graphics2D g;

    private Directions direction = Directions.CENTER;

    public Tank(double x, double y) {
        super(x, y, SIDE_LENGTH, SIDE_LENGTH);
        tankGroup.add(this);
    }

    public Tank() {
        this(SIDE_LENGTH * NEXT_ID, 0);
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

    private boolean isCollide(Rectangle2D.Double tempoRect) {
        for (var tank: tankGroup) {
            // 锁定目标tank,防止比较过程中目标tank的位置被其它线程修改
//            synchronized (tank) {
                if (tank.getId() != id && tempoRect.intersects(tank)) {
                    System.out.println(this + "collide->" + tank);
                    return true;
                }
//            }
        }
        return false;
    }

    public void move() {
        int steps = SingleRandom.INSTANCE.nextInt(DEFAULT_WIDTH / MOVE_DISTANCE);
        move(changeAndGetDirection(), steps);
    }

    public void move(Directions direction, int steps) {
        for (int i = 0; i < steps; i++) {
            double curX = getX();
            double curY = getY();

            UtilTools.delay(DELAY);

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
            // 碰撞检测 & 边界检测
            var tempoRect = new Rectangle2D.Double(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
            if (isCollide(tempoRect) || curX + SIDE_LENGTH > DEFAULT_WIDTH || curX < 0 || curY + SIDE_LENGTH > DEFAULT_HEIGHT || curY < 0) {
                changeAndGetDirection();
                continue;
            }

            // 没有其它线程访问当前对象时候才进行修改！
//            synchronized (this) {
                setFrame(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
//            }
            repaint();
        }
    }

    private void repaint() {
        g.draw(this);
        g.drawString(String.valueOf(getId()),(float) (getX() + SIDE_LENGTH / 3), (float) (getY() + SIDE_LENGTH / 2));
    }


    public void paintSelf(Graphics2D g2) {
        this.g = g2;
        repaint();
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
//    private ArrayList<Tank> tanks;
//    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
//    private final Lock rlock = rwLock.readLock();
//    private final Lock wlock = rwLock.writeLock();

//    private Random random = SingleRandom.INSTANCE;

    public PlayGround() {
        addTanks(5);
    }


    private void addTanks(int num) {
        double x = 0, y = 0;
        double rangeX = DEFAULT_WIDTH - SIDE_LENGTH;
        double rangeY = DEFAULT_HEIGHT - SIDE_LENGTH;

        for (int i = 0; i < num; i++) {
//            x = random.nextDouble() * rangeX;
//            y = random.nextDouble() * rangeY;
            x = i * SIDE_LENGTH;
            var tank = new Tank(x, y);
            System.out.println(tank);
            Thread t = new Thread(new MoveTank(tank));
            t.start();
        }

    }

    private class MoveTank implements Runnable {
        private Tank target;

        public MoveTank(Tank target) {
            this.target = target;
        }


        @Override
        public void run() {
            while (true) {
                System.out.println(target);
                target.move();
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
        for (Tank tank : Tank.tankGroup) {
            tank.paintSelf(g2);
        }
    }
}
