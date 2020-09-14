package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// 引入全局常量
import static com.jiguangchao.GlobalSettings.*;

public class PlayGround extends JComponent implements KeyListener {

    boolean bL = false;
    boolean bU = false;
    boolean bR = false;
    boolean bD = false;

    Tank myTank = new Tank(DEFAULT_WIDTH / 2 - SIDE_LENGTH /2, DEFAULT_HEIGHT - SIDE_LENGTH, true);

    public PlayGround() {
        myTank.setDirection(Directions.NORTH);
        myTank.setMoving(false);
        addTanks(TANK_NUM);
        Thread paintThread = new Thread(()-> {
            for (;;) {
                UtilTools.delay(DELAY);
                repaint();
            }
        });
        paintThread.start();
        setBackground(new Color(0,128,0));
    }

    private void addTanks(int num) {
        for (int i = 0; i < num; i++) {
            var tank = new Tank();
            Thread t = new Thread(new MoveTank(tank));
            t.start();
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                bL = true;
                setMainTankDir();
                break;
            case KeyEvent.VK_UP:
                bU = true;
                setMainTankDir();
                break;
            case KeyEvent.VK_RIGHT:
                bR = true;
                setMainTankDir();
                break;
            case KeyEvent.VK_DOWN:
                bD = true;
                setMainTankDir();
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                bL = false;
                setMainTankDir();
                break;
            case KeyEvent.VK_UP:
                bU = false;
                setMainTankDir();
                break;
            case KeyEvent.VK_RIGHT:
                bR = false;
                setMainTankDir();
                break;
            case KeyEvent.VK_DOWN:
                bD = false;
                setMainTankDir();
                break;

            case KeyEvent.VK_SPACE:
                myTank.fire();
                break;

            default:
                break;
        }
    }

    private void setMainTankDir() {
        var lastDirection = myTank.getDirection();
        if (!bL && !bU && !bR && !bD) {
            myTank.setMoving(false);
            myTank.setDirection(lastDirection);
        } else {
            myTank.setMoving(true);
            if (bL)
                myTank.setDirection(Directions.WEST);
            if (bU)
                myTank.setDirection(Directions.NORTH);
            if (bR)
                myTank.setDirection(Directions.EAST);
            if (bD)
                myTank.setDirection(Directions.SOUTH);
        }
    }

    private class MoveTank implements Runnable {
        private Tank target;

        public MoveTank(Tank target) {
            this.target = target;
        }

        @Override
        public void run() {
            while (target.isAlive()) {
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
        g.setColor(getBackground());//设置当前颜色为背景色
        g.fillRect(0,0, getWidth(), getHeight());//填充

        g.setColor(Color.white);
        g.setFont( new Font("Serif",Font.BOLD,16));
        g.drawString("敌军坦克 " + Tank.tankGroup.size() + " / " +  Tank.tankGroup.stream().filter(tank -> tank.isAlive()).count(), DEFAULT_WIDTH  - 120, 30);
        g.drawString("我的坦克 " + myTank.getHP(), DEFAULT_WIDTH  - 120, 50);

        this.myTank.move(MOVE_DISTANCE);

        for (Tank tank : Tank.tankGroup) {
            tank.paintSelf(g);
        }
        //Tank.tankGroup.removeIf(tank -> !tank.isAlive());
        for (var bullet : Bullet.allBullets) {
            // 每刷新一下界面，每更新一下所有子弹的位置
            bullet.move();
            bullet.paintSelf(g);
        }
        Bullet.checkAll();
    }

}
