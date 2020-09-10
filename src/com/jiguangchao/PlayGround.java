package com.jiguangchao;

import javax.swing.*;
import java.awt.*;
// 引入全局常量
import static com.jiguangchao.GlobalSettings.*;

public class PlayGround extends JComponent {

    public PlayGround() {
        addTanks(TANK_NUM);
        Thread paintThread = new Thread(()-> {
            for (;;) {
                UtilTools.delay(DELAY);
                repaint();
            }
        });
        paintThread.start();
    }


    private void addTanks(int num) {
        int x = 0, y = 0;
        int rangeX = DEFAULT_WIDTH - SIDE_LENGTH;
        int rangeY = DEFAULT_HEIGHT - SIDE_LENGTH;

        for (int i = 0; i < num; i++) {
//            x = random.nextDouble() * rangeX;
//            y = random.nextDouble() * rangeY;
            x = i * SIDE_LENGTH;
            var tank = new Tank(x, y);
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
//                System.out.println("in class MoveTank run " + target);
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
        for (Tank tank : Tank.tankGroup) {
            tank.paintSelf(g);
        }
    }
}
