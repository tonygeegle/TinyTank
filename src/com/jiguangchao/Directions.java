package com.jiguangchao;

public enum Directions {
    CENTER, NORTH, SOUTH, WEST, EAST;
    // 随机获取一个 direction
    public Directions getNextRandomDirection() {
//        Directions[] enumConstants = Directions.class.getEnumConstants();
        Directions[] enumConstants = Directions.values();
        int index = SingleRandom.INSTANCE.nextInt(enumConstants.length);
        return enumConstants[index];
    }
}