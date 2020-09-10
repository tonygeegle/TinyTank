package com.jiguangchao;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jiguangchao.GlobalSettings.*;
import static com.jiguangchao.GlobalSettings.SIDE_LENGTH;

public class Tank extends Rectangle {
    private static int NEXT_ID = 0;
    private int id = NEXT_ID++;
    private int HP = 100;
    // Tank 类自己维护本 tank 所属的集团军
    public static CopyOnWriteArrayList<Tank> tankGroup = new CopyOnWriteArrayList<>();
    // Graphics2D
    private Graphics g;

    private Directions direction = Directions.CENTER;

    public Tank(int x, int y) {
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
        System.out.println(this);
        return direction;
    }

    private boolean isCollide(Rectangle tempoRect) {
        for (var tank: tankGroup) {
            // 锁定目标tank,防止比较过程中目标tank的位置被其它线程修改
            synchronized (tank) {
                if (tank.getId() != id && tempoRect.intersects(tank)) {
                    System.out.println(this + "collide->" + tank);
                    return true;
                }
            }
        }
        return false;
    }

    public void move() {
        int steps = SingleRandom.INSTANCE.nextInt(DEFAULT_WIDTH / MOVE_DISTANCE);
        System.out.println(this + "" + steps);
        changeAndGetDirection();
        move(steps);
    }

    public void move(int steps) {
        for (int i = 0; i < steps; i++) {
            int curX = (int)getX();
            int curY = (int)getY();

            UtilTools.delay(DELAY);

            switch (this.direction) {
                case EAST -> {
                    curX += MOVE_DISTANCE;
                }
                case NORTH -> {
                    curY -= MOVE_DISTANCE;
                }
                case WEST -> {
                    curX -= MOVE_DISTANCE;
                }
                case SOUTH -> {
                    curY += MOVE_DISTANCE;
                }
                default -> {

                }
            }
            // 碰撞检测 & 边界检测
            var tempoRect = new Rectangle(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
            if (isCollide(tempoRect) || curX + SIDE_LENGTH > DEFAULT_WIDTH || curX < 0 || curY + SIDE_LENGTH > DEFAULT_HEIGHT || curY < 0) {
                System.out.printf("i= %d, curX %d curY %d %s\n", i, curX, curY, getDirection());
                changeAndGetDirection();
                continue;
            }

            // 没有其它线程访问当前对象时候才进行修改！
            synchronized (this) {
//                setFrame(curX, curY, SIDE_LENGTH, SIDE_LENGTH);
                setLocation(curX, curY);
            }
            //repaint();
        }
    }

    public void paintSelf(Graphics g) {
//        g.draw(this);
        var g2 = (Graphics2D)g;
        g2.draw(this);
//        g2.drawString(String.valueOf(getId()),(float) (getX() + SIDE_LENGTH / 3), (float) (getY() + SIDE_LENGTH / 2));
        switch (direction) {
            case EAST -> {
                g.drawImage(ResourceMgr.goodTankR, (int)getX(), (int)getY(), null);
            }
            case NORTH -> {
                g.drawImage(ResourceMgr.goodTankU, (int)getX(), (int)getY(), null);
            }
            case WEST -> {
                g.drawImage(ResourceMgr.goodTankL, (int)getX(), (int)getY(), null);
            }
            case SOUTH -> {
                g.drawImage(ResourceMgr.goodTankD, (int)getX(), (int)getY(), null);
            }
            default -> {
                g.drawImage(ResourceMgr.goodTankL, (int)getX(), (int)getY(), null);
            }
        }
    }

    @Override
    public String toString() {
        return "Tank{" +
                "id=" + id +
                ", HP=" + HP +
                ", direction=" + direction + super.toString() +
                '}';
    }
}