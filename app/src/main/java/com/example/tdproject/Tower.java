package com.example.tdproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Tower {

    private Bitmap image;
    private int x,y;
    private double maxlen;
    private ArrayList<Ship> units;
    private int type;

    public Tower(Bitmap image, int x, int y, int type) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(image, x, y, null);
    }

    public void checkRank(){

    }

    public int getType(){
        return type;
    }

    public double getCenterX(){
        return this.x + (image.getWidth()/2);
    }

    public double getCenterY(){
        return this.y + (image.getHeight()/2);
    }
}
