package com.example.tdproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Tower {

    private Bitmap image;
    private int x,y;
    private double maxlen;
    private ArrayList<Ship> ships;
    private int type;
    private int maxLen;
    private long fireDelay;
    private long lastFire;
    private GameView gv;
    private Timer timer;

    public Tower(GameView gv, Bitmap image, int x, int y, int type, int price) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.type = type;
        this.gv = gv;
        this.lastFire = System.currentTimeMillis();

        if (this.type == 0) {
            this.maxlen = 200;
            this.fireDelay = 900;
        }
        else if (this.type == 1) {
            this.maxlen = 230;
            this.fireDelay = 700;
        }
        else {
            this.maxlen = 220;
            this.fireDelay = 500;
        }

        this.gv.decreaseCoins(price);
    }



    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);

    }

    public void update() {
        checkRank();
    }

    public void checkRank(){
        if ( (System.currentTimeMillis() - lastFire) > fireDelay){
            lastFire = System.currentTimeMillis();
            ships = gv.getShips();

            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).isActive()) {
                    double len = Math.sqrt((ships.get(i).getCenterX() - this.getCenterX()) * (ships.get(i).getCenterX() - this.getCenterX())
                            + (ships.get(i).getCenterY() - this.getCenterY()) * (ships.get(i).getCenterY() - this.getCenterY()));
                    if (len < maxlen) {
                        Projectile pr = new Projectile(this.getCenterX(), this.getCenterY(), ships.get(i).getCenterX(), ships.get(i).getCenterY());

                        gv.addToProjectiles(pr);
                        return;
                    }
                }
            }
        }
    }

    public int getType(){
        return type;
    }

    public int getCenterX(){
        return this.x + (int)(image.getWidth()/2);
    }

    public int getCenterY(){
        return this.y + (int)(image.getHeight()/2);
    }
}
