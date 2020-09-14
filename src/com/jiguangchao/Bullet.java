package com.jiguangchao;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jiguangchao.GlobalSettings.*;

public class Bullet extends Rectangle {
    private static int NEXT_ID = 0;
    private int id = NEXT_ID++;
    private Tank curTank;
    private int damage = 20;
    private int speed = 2;
    public static CopyOnWriteArrayList<Bullet> allBullets = new CopyOnWriteArrayList<>();
    private final Directions direction;

    public Bullet(Tank curTank, int starX, int starY, Directions direction) {
        super(starX, starY, BULLETSIZE,  BULLETSIZE);
        this.curTank = curTank;
        this.direction = direction;
        allBullets.add(this);
    }
    // 注意：进行 checkAll 的时候其它线程会同时进行add操作
    public static void checkAll() {
        //
        var destroyBullets  = new ArrayList<Bullet>(Bullet.allBullets.size());
        var iter = Bullet.allBullets.iterator();
        // 遍历每一个子弹
        while (iter.hasNext()) {
            var bullet = iter.next();
            // 对当前子弹进行边界检测
            if (bullet.getX() + BULLETSIZE > DEFAULT_WIDTH || bullet.getX() < 0 || bullet.getY() + BULLETSIZE > DEFAULT_HEIGHT || bullet.getY() < 0) {
                destroyBullets.add(bullet);
            }
            else {
                // 对当前子弹 和 所有坦克进行碰撞检测
                for (var tank : Tank.tankGroup) {
                    if (bullet.intersects(tank) && bullet.curTank != tank) {
                        tank.getAttack(bullet.damage);
                        destroyBullets.add(bullet);
                    }
                }
            }
        }
        Bullet.allBullets.removeAll(destroyBullets);
    }

    public void move() {
            int curX = (int)getX();
            int curY = (int)getY();

            switch (this.direction) {
                case EAST -> {
                    curX += MOVE_DISTANCE * speed;
                }
                case NORTH -> {
                    curY -= MOVE_DISTANCE * speed;
                }
                case WEST -> {
                    curX -= MOVE_DISTANCE * speed;
                }
                case SOUTH -> {
                    curY += MOVE_DISTANCE * speed;
                }
                default -> {
                    curY += MOVE_DISTANCE * speed;
                }
            }
            setLocation(curX, curY);
    }

    public void paintSelf(Graphics g) {
        var g2 = (Graphics2D)g;
        var center = ResourceMgr.bulletR.getWidth() / 2 - BULLETSIZE / 2;
//        g2.draw(this);
//        g2.drawString(String.valueOf(getId()),(float) (getX() + SIDE_LENGTH / 3), (float) (getY() + SIDE_LENGTH / 2));
        switch (direction) {
            case EAST -> {
                g.drawImage(ResourceMgr.bulletR, (int)getX() - center, (int)getY()- center, null);
            }
            case NORTH -> {
                g.drawImage(ResourceMgr.bulletU, (int)getX()- center, (int)getY()- center, null);
            }
            case WEST -> {
                g.drawImage(ResourceMgr.bulletL, (int)getX()- center, (int)getY()- center, null);
            }
            case SOUTH -> {
                g.drawImage(ResourceMgr.bulletD, (int)getX()- center, (int)getY()- center, null);
            }
            default -> {
                g.drawImage(ResourceMgr.bulletD, (int)getX()- center, (int)getY()- center, null);
            }
        }
    }

    @Override
    public String toString() {
        return "Bullet{" +
                "id=" + id +
                ", curTank=" + curTank +
                ", damage=" + damage +
                ", speed=" + speed +
                ", direction=" + direction +
                '}';
    }
}
