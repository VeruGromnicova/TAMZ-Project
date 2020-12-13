package com.example.tdproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;

import java.util.ArrayList;

public class Ship {

    private Bitmap image;
    private Bitmap imageOrg;
    private int x,y;
    private int iCurStep = 0;
    private Path shipPath;
    private int speed;
    private Canvas canvas;
    private GameView gv;
    private String direction = "N";
    private int lives = 5;
    private int reward;
    private int damage = 1;
    private boolean active = true;
    private PathMeasure pm;
    private float fSegmentLen;
    private ArrayList<Projectile> pros;
    private int type;

    public Ship(GameView gv, Bitmap bmp, Path sPath, int type) {
        this.gv = gv;
        image = bmp;
        imageOrg = bmp;
        shipPath = sPath;
        x = gv.getScreenWidth();
        y = gv.getScreenHeight();
        this.type = type;
        // Move

        if (type == 0) {
            reward = 20;
            damage = 1;
            lives = 3;
            speed = 1200;
        }
        else if (type == 1) {
            reward = 30;
            damage = 1;
            lives = 5;
            speed = 1200;
        }
        else if (type == 2) {
            reward = 30;
            damage = 2;
            lives = 6;
            speed = 1200;
        }
        else {
            reward = 50;
            lives = 30;
            damage = 5;
            speed = 2000;
        }
        pm = new PathMeasure(shipPath, false);
        fSegmentLen = pm.getLength() / this.speed;//we'll get 20 points from path to animate the circle
    }

    public void draw(Canvas canvas) {
        this.canvas = canvas;
        canvas.drawBitmap(image, x, y, null);
    }

    public void update() {
        float afP[] = {0f, 0f};

        if (iCurStep <= this.speed) {
            pm.getPosTan(fSegmentLen * iCurStep, afP, null);
            afP[0] = (int)(afP[0] - (int)(image.getWidth() / 2));
            afP[1] = (int)(afP[1] - (int)(image.getHeight() / 2));

            if (rotateShip(x, y, (int)afP[0], (int)afP[1])) {
                pm.getPosTan(fSegmentLen * iCurStep, afP, null);
                afP[0] = (int) (afP[0] - (int) (image.getWidth() / 2));
                afP[1] = (int) (afP[1] - (int) (image.getHeight() / 2));
            }

            x = (int)afP[0];
            y = (int)afP[1];
            iCurStep++;
        } else {
            if (active) {
                active = false;
                this.gv.decreaseHearts(this.damage);
            }
        };
        // CheckCollision
        pros = this.gv.getProjectiles();
        checkCollision();
    }

    private void checkCollision() {
        for (int i = 0; i < pros.size(); i++) {

            if (pros.get(i).isActive() && isActive()) {
                if (intersects(pros.get(i).getX(), pros.get(i).getY())) {
                    pros.get(i).deactivate();
                    lives--;

                    if (lives == 0) {
                        active = false;
                        this.gv.increaseCoins(reward);
                        this.gv.decreaseActiveShips();
                    }
                }
            }
        }
    }

    private boolean intersects(int prox, int proy){
        if (Math.abs(prox - getCenterX()) < image.getWidth()/2 && Math.abs(proy - getCenterY()) < image.getHeight()/2){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean rotateShip(int x, int y, int newx, int newy) {

        if(y-newy > 0 && !direction.equals("N")) {
            Matrix matrix = new Matrix();
            image = Bitmap.createBitmap(imageOrg, 0,0, imageOrg.getWidth(), imageOrg.getHeight(), matrix, true);
            direction = "N";
            return true;
        }
        else if(y-newy < 0 && !direction.equals("S")) {
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            image = Bitmap.createBitmap(imageOrg, 0,0, imageOrg.getWidth(), imageOrg.getHeight(), matrix, true);
            direction = "S";
            return true;
        }
        else if(x-newx < 0 && !direction.equals("E")) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            image = Bitmap.createBitmap(imageOrg, 0,0, imageOrg.getWidth(), imageOrg.getHeight(), matrix, true);
            direction = "E";
            return true;
        }
        else if(x-newx > 0 && !direction.equals("W")) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            image = Bitmap.createBitmap(imageOrg, 0,0, imageOrg.getWidth(), imageOrg.getHeight(), matrix, true);
            direction = "W";
            return true;
        }
        return false;
    }

    public int getCenterX() {
        int centerX = x + (int)(image.getWidth()/2);
        return centerX;
    }

    public int getCenterY() {
        int centerY = y + (int)(image.getHeight()/2);
        return centerY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
