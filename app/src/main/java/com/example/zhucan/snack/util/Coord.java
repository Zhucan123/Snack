package com.example.zhucan.snack.util;

public class Coord{
    private int x;
    private int y;
    Coord next;
    Coord head;
    public Coord(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}