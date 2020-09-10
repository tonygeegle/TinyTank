package com.jiguangchao;

import java.util.Random;

public class SingleRandom extends Random {
    public static SingleRandom INSTANCE = new SingleRandom();

    private SingleRandom() {
    }
}
