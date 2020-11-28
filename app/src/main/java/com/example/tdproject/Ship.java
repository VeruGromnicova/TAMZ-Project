package com.example.tdproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Path;
import android.graphics.PathMeasure;

public class Ship {

    private Bitmap image;
    private int x,y;
    private int iCurStep = 0;
    private Path shipPath;
    private int xVelocity = 10;
    private int yVelocity = 5;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    //private Movie bug;
    //private long mMovieStart;
    //private static final boolean DECODE_STREAM = true;

    public Ship(Bitmap bmp, Path sPath) {

        image = bmp;
        shipPath = sPath;
        x = 100;
        y = 100;
    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(image, x, y, null);
    }

    public void update() {

        PathMeasure pm = new PathMeasure(shipPath, false);
        float fSegmentLen = pm.getLength() / 600;//we'll get 20 points from path to animate the circle
        float afP[] = {0f, 0f};

        if (iCurStep <= 600) {
            pm.getPosTan(fSegmentLen * iCurStep, afP, null);
            //rotateShip(x, y, (int)afP[0], (int)afP[1]);


            x = (int)afP[0];
            y = (int)afP[1];
            iCurStep++;
        } else {
            iCurStep = 0;
        };

        /*
        x += xVelocity;
        y += yVelocity;
        if ((x > screenWidth - image.getWidth()) || (x < 0)) {
            xVelocity = xVelocity * -1;
        }
        if ((y > screenHeight - image.getHeight()) || (y < 0)) {
            yVelocity = yVelocity * -1;
        }
*/
    }

    private void rotateShip(int x, int y, int newx, int newy){
        if (newx != x && newy!= y) {
            int deltaX = newx - x;
            int deltaY = newy - y;
            double rad = Math.atan2(deltaY, deltaX); // In radians
            float deg = (float) (rad * (180 / Math.PI));

            Matrix matrix = new Matrix();
            matrix.postRotate(deg);
            image = Bitmap.createBitmap(image, x, y, image.getWidth(), image.getHeight(), matrix, false);
        }
    }
}
