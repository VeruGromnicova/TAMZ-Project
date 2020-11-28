package com.example.tdproject;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public MainThread thread;
    Bitmap[] bmp;
    Path enemyMove;
    int width;
    int height;
    private Ship ship;
    private Tower tower;
    private ArrayList<Tower> towers;
    private ArrayList<Ship> ships;
    private ArrayList<Path> paths;
    int mapX = 15;
    int mapY = 20;
    double ratioX;
    double ratioY;
    private Context context;

    private int[] map = {

    };

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        //thread.run();
        setFocusable(true);
        //this.setWillNotDraw(false);
        init(context);
        ratioX = (float)Resources.getSystem().getDisplayMetrics().widthPixels / (float)getResources().getDrawable(R.drawable.map1).getIntrinsicWidth();
        ratioY = (float)Resources.getSystem().getDisplayMetrics().heightPixels / (float)getResources().getDrawable(R.drawable.map1).getIntrinsicHeight();
        towers = new ArrayList<Tower>();
        paths = new ArrayList<Path>();
        loadMaps();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void update() {
        ship.update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        //ship1 = new Ship1(BitmapFactory.decodeResource(getResources(), R.drawable.ship));
        ship = new Ship(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship), width, height, false), paths.get(0));

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawBitmap(bmp[0], null, new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels), null);

        /*
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                canvas.drawBitmap(bmp[map[i*10 + j]], null,
                        new Rect(j*width, i*height,(j+1)*width, (i+1)*height), null);
            }
        }
        */
       for(int i = 0; i < towers.size(); i++) {
            towers.get(i).draw(canvas);
        }
        ship.draw(canvas);
        //tower.draw(canvas);
        /*
        System.out.println("");
        System.out.println("T");
        System.out.println("");
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            canvas.drawRect(100, 100, 200, 200, paint);
        }*/
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / 10;
        height = h / 10;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float displayX = event.getX();
            float displayY = event.getY();
            int mapX = (int)(event.getX() / (float)ratioX);
            int mapY = (int)(event.getY() / (float)ratioY);

            System.out.println(displayX + " " + displayY + " " + mapX + " " + mapY);
            System.out.println("T" + (float)Resources.getSystem().getDisplayMetrics().heightPixels  + " " + (float)getResources().getDrawable(R.drawable.map1).getIntrinsicHeight());
            System.out.println("T" + (float)Resources.getSystem().getDisplayMetrics().widthPixels  + " " + (float)getResources().getDrawable(R.drawable.map1).getIntrinsicWidth());

            if (bmp[2].getPixel(mapX, mapY) != Color.BLACK) {
                System.out.println("Build");
                towers.add(new Tower(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bug), 100, 100, false), (int)displayX - 50, (int)displayY - 50, 0));

                for (int i = mapX - 100; i < mapX + 100; i++) {    // TODO: 10 nahradit sirkou (toweru / ratioX) -1
                    for (int j = mapY - 100; j < mapY + 100; j++) { // TODO: 10 nahradit vyskou (toweru / ratioY) -1
                        if (i >= 0 && j >= 0 && i < getResources().getDrawable(R.drawable.map1).getIntrinsicWidth() && j < getResources().getDrawable(R.drawable.map1).getIntrinsicHeight()) {
                            bmp[2].setPixel(i, j, Color.BLACK);
                            // TODO: Postavit Tower
                            //tower = new Tower(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bug), 100, 100, false), (int)displayX - 50, (int)displayY - 50, 0); // TODO: 50 nahradit sirkou/vyskou (toweru / ratioX / 2)
                            }
                    }
                }
            }

        }
        return true;
    }

    void init(Context context) {
        bmp = new Bitmap[3];
        bmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.map1); // Colored
        bmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.map1work); // Working
        bmp[2] = bmp[1].copy(bmp[1].getConfig(), true);

        //bmpColored = new Bitmap[6];

        //bmpColored[0] = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        //bmpColored[1] = BitmapFactory.decodeResource(getResources(), R.drawable.path);
        //bmpColored[2] = BitmapFactory.decodeResource(getResources(), R.drawable.stone);

        //enemyMove = new Path();
        //enemyMove.moveTo(0,0);
        //enemyMove.lineTo(0, 500);
        //enemyMove.lineTo(500, 0);
        //enemyMove.lineTo(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);



    }

    public void loadMaps() {

//        float pathRatioX = (float)getResources().getDrawable(R.drawable.map1).getIntrinsicWidth() / 400;
//        float pathRatioY = (float)getResources().getDrawable(R.drawable.map1).getIntrinsicHeight() / 600;
        float pathRatioX = (float)Resources.getSystem().getDisplayMetrics().widthPixels / 400;
        float pathRatioY = (float)Resources.getSystem().getDisplayMetrics().heightPixels / 600;

        AssetManager assetManager = context.getAssets();
        InputStream input;
        try {
            input = assetManager.open("maps.txt");
            int size = input.available();
            byte[] buffer = new byte[size];

            input.read(buffer);
            input.close();

            String text = new String(buffer);

            String[] arrOfLines = text.split("\n");
            //int[] coordinates = new int[arrOfLines.length * 2];
            //int k = 0;
            Path path = new Path();
            boolean moveTo = true;
            for (int i = 1; i < arrOfLines.length; i++) {
                if (arrOfLines[i].indexOf(',') == -1 && i > 0) {
                    paths.add(path);
                    path = new Path();
                    moveTo = true;
                }
                else{
                    String[] arr = arrOfLines[i].split(",");
                    if (moveTo){
                        path.moveTo((int)(Integer.parseInt(arr[0].trim()) * pathRatioX), (int)(Integer.parseInt(arr[1].trim()) * pathRatioY));
                        moveTo = false;
                    }
                    else {
                        path.lineTo((int)(Integer.parseInt(arr[0].trim()) * pathRatioX), (int)(Integer.parseInt(arr[1].trim()) * pathRatioY));
                    }
                    //coordinates[k++] = Integer.parseInt(arr[0]);
                    //coordinates[k++] = Integer.parseInt(arr[1]);
                }
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
