package com.example.tdproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

public class Projectile {
    private int x, y;
    private Path path;
    private boolean active;
    private int iCurStep = 0;

    public Projectile(int x, int y, int posShipX, int posShipY) {
        this.x = x;
        this.y = y;
        this.active = true;

        this.path = new Path();
        path.moveTo(x, y);
        path.lineTo(posShipX, posShipY);
        move();
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        canvas.drawCircle(x,y,5, paint);
        //canvas.drawBitmap(image, x, y, null);
    }

    public void update() {
        move();
    }

    public void move() {
        //double len = Math.sqrt((posShipX-x)*(posShipX-x) + (posShipY-y)*(posShipY-y));

        PathMeasure pm = new PathMeasure(path, false);
        int countPoints = (int)(pm.getLength() / 16);

        float fSegmentLen = pm.getLength() / countPoints;//we'll get 20 points from path to animate the circle
        float afP[] = {0f, 0f};

        if (iCurStep <= countPoints) {
            pm.getPosTan(fSegmentLen * iCurStep, afP, null);

            x = (int)afP[0];
            y = (int)afP[1];
            iCurStep++;
        } else {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }
    public void deactivate() {
        active = false;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
