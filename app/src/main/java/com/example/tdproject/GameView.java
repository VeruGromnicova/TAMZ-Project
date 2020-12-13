package com.example.tdproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public MainThread thread;
    public static SharedPreferences sp;
    Bitmap[] bmp;
    Bitmap[] bmpMaps;
    Bitmap[] bmpMapsWork;
    Bitmap[] bmpBtns;
    Paint[] paints;
    int width;
    int height;
    private ArrayList<Tower> towers;
    private ArrayList<Ship> ships;
    private ArrayList<Path> paths;
    private ArrayList<Projectile> pros;
    private ArrayList<String> shipSeq;
    private ArrayList<String[]> delays;
    private ArrayList<String[]> scoreList;
    double ratioX;
    double ratioY;
    private Context context;
    private int shipsCount;
    private int coins;
    private int hearts;
    private int screenWidth;
    private int screenHeight;
    private int unitWidth;
    private int unitHeight;
    private int barHeight;
    private int mapHeight;
    private int shipWidth;
    private int shipHeight;
    private int towerSize;
    private int coinHeartTextSize;
    private int selectedTower;
    private int[] pricesTower;
    private boolean menu;
    private int level;
    private boolean win;
    private boolean gameOver;
    private boolean bestScores;
    private long lastDraw;
    private long firstDraw;
    private long gameTime;
    private long pause;
    private int delay;
    private int activeShips;
    private String playTime;
    private SharedPreferences.Editor editor;
    private boolean newRecord;
    private SoundPool sounds;
    private int winSound;
    private int gameOverSound;
    private MediaPlayer mediaPlayer;
    private int availableLevels;


    private int[] map = {

    };

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        sp = this.context.getSharedPreferences("keys", Context.MODE_PRIVATE);

        this.menu = true;
        this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.towers = new ArrayList<Tower>();
        this.ships = new ArrayList<Ship>();
        this.paths = new ArrayList<Path>();
        this.pros = new ArrayList<Projectile>();
        this.shipSeq = new ArrayList<String>();
        this.delays = new ArrayList<String[]>();
        this.pricesTower = new int[3];
        this.pricesTower[0] = 50;
        this.pricesTower[1] = 80;
        this.pricesTower[2] = 120;
        this.unitWidth = (int)(screenWidth/80);
        this.unitHeight = (int)(screenHeight/80);
        this.barHeight = unitHeight*12;
        this.mapHeight = screenHeight-barHeight;
        this.shipWidth = unitWidth*4;
        this.shipHeight = unitHeight*4;
        this.towerSize = unitWidth*8;
        this.coinHeartTextSize = unitWidth*6;
        this.menu = true;
        this.ratioX = (float)(screenWidth) / (float)getResources().getDrawable(R.drawable.map1x).getIntrinsicWidth();
        this.ratioY = (float)(mapHeight) / (float)getResources().getDrawable(R.drawable.map1x).getIntrinsicHeight();
        this.activeShips = 0;
        this.editor = sp.edit();
        this.newRecord = false;
        this.availableLevels = sp.getInt("availableLevels", 1);

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        winSound = sounds.load(context, R.raw.win, 1);
        gameOverSound = sounds.load(context, R.raw.gameover, 1);


        mediaPlayer = MediaPlayer.create(context, R.raw.bgmusic);
        mediaPlayer.setLooping(true);

        //restartGame(1);
        init(context);
        loadMaps();

    }

    /*
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(keyString, valueString);
    editor.commit();
     */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void update() {
        if (!gameOver) {
            for (int i = 0; i < towers.size(); i++) {
                towers.get(i).update();
            }
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).isActive()) {
                    ships.get(i).update();
                }
            }
            for (int i = 0; i < pros.size(); i++) {
                if (pros.get(i).isActive()) {
                    pros.get(i).update();
                }
            }
        }
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
        mediaPlayer.start();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (menu) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawBitmap(bmpBtns[9], 22*unitWidth, 46*unitHeight, null);
            canvas.drawBitmap(bmpBtns[10], 22*unitWidth, 55*unitHeight, null);
            canvas.drawBitmap(bmpBtns[11], 22*unitWidth, 64*unitHeight, null);

            canvas.drawText("Levels:", 33*unitWidth, 10*unitHeight, paints[5]);

            int row = -1;
            int column;

            for (int i = 0; i < 9; i++) {
                column = i % 3;
                if (i % 3 == 0) {
                    row++;
                }
                if (i < availableLevels) {
                    canvas.drawBitmap(bmpBtns[i], 15*unitWidth + 20*column*unitWidth, 15*unitHeight + 9*row*unitHeight, null);
                }
                else{
                    canvas.drawBitmap(bmpBtns[15], 15*unitWidth + 20*column*unitWidth, 15*unitHeight + 9*row*unitHeight, null);
                }
            }
        }
        else if (gameOver) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawText("Game Over!", 18*unitWidth, 25*unitHeight, paints[6]);
            canvas.drawBitmap(bmpBtns[13], 22*unitWidth, 35*unitHeight, null);
            canvas.drawBitmap(bmpBtns[14], 22*unitWidth, 45*unitHeight, null);
        }
        else if (win) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawText("You won!", 24*unitWidth, 25*unitHeight, paints[7]);
            canvas.drawBitmap(bmpBtns[12], 22*unitWidth, 40*unitHeight, null);
            canvas.drawBitmap(bmpBtns[14], 22*unitWidth, 50*unitHeight, null);
            canvas.drawText("Time: " + playTime, 29*unitWidth, 32*unitHeight, paints[12]);

            if (newRecord) {
                canvas.drawText("New record", 25*unitWidth, 70*unitHeight, paints[13]);
            }
        }
        else if (bestScores) {
            canvas.drawBitmap(bmp[8], 0,0, null);

            canvas.drawBitmap(bmpBtns[14], 22*unitWidth, 71*unitHeight, null);

            for (int i = 0; i < paths.size()-1; i++) {
                canvas.drawText("Level " + (i+1), 10*unitWidth, 5*unitHeight + (8*i*unitHeight), paints[12]);

                if (scoreList.get(i)[0] != "") {
                    canvas.drawText("Lives: " + scoreList.get(i)[0] + ",  " + "Coins: " + scoreList.get(i)[2] + ",  " + "Time: " + scoreList.get(i)[1] , 15*unitWidth, 8*unitHeight + (8*i)*unitHeight, paints[14]);
                }
            }

        }
        else {
            canvas.drawBitmap(bmpMaps[level-1], null, new Rect(0, 0, screenWidth, mapHeight), null);
            canvas.drawBitmap(bmp[11], 10, 10, null);
            canvas.drawText("Level " + Integer.toString(this.level), screenWidth-220, 65, paints[1]);

            if (System.currentTimeMillis() - this.lastDraw > 1500 && ships.size() < shipsCount && System.currentTimeMillis() - this.firstDraw > this.pause) {

                this.lastDraw = System.currentTimeMillis();
                if (Integer.parseInt(delays.get(level-1)[delay].trim()) == ships.size()){
                    this.firstDraw = System.currentTimeMillis();
                    this.pause = 5000;
                    delay++;
                }

                switch (shipSeq.get(level-1).charAt(ships.size())){
                    case '0':
                        ships.add(new Ship(this, bmp[7], paths.get(level - 1), 0));
                        break;
                    case '1':
                        ships.add(new Ship(this, bmp[15], paths.get(level - 1), 1));
                        break;
                    case '2':
                        ships.add(new Ship(this, bmp[16], paths.get(level - 1), 2));
                        break;
                    case '3':
                        ships.add(new Ship(this, bmp[17], paths.get(level - 1), 3));
                        break;
                }
                //activeShips++;
            }

            for(int i = 0; i < towers.size(); i++) {
                towers.get(i).draw(canvas);
            }
            for(int i = 0; i < ships.size(); i++) {
                if (ships.get(i).isActive()) {
                    ships.get(i).draw(canvas);
                }
            }
            for(int i = 0; i < pros.size(); i++) {
                if (pros.get(i).isActive()) {
                    pros.get(i).draw(canvas);
                }
            }

            //canvas.drawRect(0, mapHeight, screenWidth, screenHeight, paints[0]);
            canvas.drawBitmap(bmp[9], 0, mapHeight, null);
            canvas.drawBitmap(bmp[3], 5, mapHeight+5 , null);
            canvas.drawText(Integer.toString(this.coins), coinHeartTextSize+25, mapHeight+coinHeartTextSize-5, paints[1]);
            canvas.drawBitmap(bmp[4], 5, mapHeight+coinHeartTextSize+15 , null);
            canvas.drawText(Integer.toString(this.hearts), coinHeartTextSize+25, mapHeight+(2*coinHeartTextSize)+5, paints[1]);

            canvas.drawBitmap(bmp[5], screenWidth-(int)(screenWidth/9)-coinHeartTextSize, screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[2]);
            canvas.drawBitmap(bmp[13], screenWidth-(int)(screenWidth/9)-(4*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[3]);
            canvas.drawBitmap(bmp[14], screenWidth-(int)(screenWidth/9)-(7*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[4]);

            //canvas.drawBitmap(bmp[13], screenWidth-(int)(screenWidth/9), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[9]);
            //canvas.drawBitmap(bmp[13], screenWidth-(int)(screenWidth/9)-(4*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[10]);
            //canvas.drawBitmap(bmp[13], screenWidth-(int)(screenWidth/9)-(7*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[11]);

            canvas.drawText(Integer.toString(pricesTower[0]), screenWidth-(int)(screenWidth/9)-(int)(coinHeartTextSize/2),screenHeight-20, paints[8]);
            canvas.drawText(Integer.toString(pricesTower[1]), screenWidth-(int)(screenWidth/9)-(int)(3.5*coinHeartTextSize),screenHeight-20, paints[8]);
            canvas.drawText(Integer.toString(pricesTower[2]), screenWidth-(int)(screenWidth/9)-(int)(6.5*coinHeartTextSize),screenHeight-20, paints[8]);
            canvas.drawBitmap(bmp[12], screenWidth-(int)(screenWidth/9)+coinHeartTextSize/4, screenHeight-55 , null);
            canvas.drawBitmap(bmp[12], screenWidth-(int)(screenWidth/9)-(int)(2.75*coinHeartTextSize), screenHeight-55 , null);
            canvas.drawBitmap(bmp[12], screenWidth-(int)(screenWidth/9)-(int)(5.75*coinHeartTextSize), screenHeight-55 , null);


            if (System.currentTimeMillis() - this.firstDraw < this.pause && delay == 0) {
                canvas.drawText("Remaining: " + Long.toString((this.pause - (System.currentTimeMillis() - this.firstDraw))/1000) + " s", 20, screenHeight-30, paints[8]);
                //System.out.println(Long.toString((this.pause - (System.currentTimeMillis() - this.firstDraw))/1000));
            }

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = (int)(w / 14);
        height = (int)(h / 14);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float displayX = event.getX();
        float displayY = event.getY();

        if (menu) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 46*unitHeight && displayY < 52*unitHeight) { //
                    System.out.println("reset");
                    restartGame(1);
                    availableLevels = 1;
                    editor.putInt("availableLevels", availableLevels);
                    editor.commit();
                    for (int i = 0; i < 9; i++) {
                        editor.putString("level" + (i+1), "");
                        editor.commit();
                    }
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 55*unitHeight && displayY < 61*unitHeight) {
                    System.out.println("best scores");
                    scoreList = new ArrayList<String[]>();
                    for (int i = 1; i <= paths.size(); i++) {
                        this.scoreList.add(parseRecord(i));
                        if (parseRecord(i)[0].trim() != "") {
                            this.scoreList.get(i-1)[1] = getPlayTime(Long.parseLong(scoreList.get(i-1)[1]));
                        }
                    }
                    this.bestScores = true;
                    this.menu = false;
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 64*unitHeight && displayY < 70*unitHeight) { //QUIT
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
                else if (displayX > 15*unitWidth && displayX < 28*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    restartGame(1);
                }
                else if (displayX > 35*unitWidth && displayX < 48*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    gameOver = false;
                    restartGame(2);
                }
                else if (displayX > 55*unitWidth && displayX < 68*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    restartGame(3);
                }
                else if (displayX > 15*unitWidth && displayX < 68*unitWidth && displayY > 24*unitHeight && displayY < 30*unitHeight) {
                    restartGame(4);
                }
                else if (displayX > 35*unitWidth && displayX < 68*unitWidth && displayY > 24*unitHeight && displayY < 30*unitHeight) {
                    restartGame(5);
                }
                else if (displayX > 55*unitWidth && displayX < 68*unitWidth && displayY > 24*unitHeight && displayY < 30*unitHeight) {
                    restartGame(6);
                }
                else if (displayX > 15*unitWidth && displayX < 68*unitWidth && displayY > 33*unitHeight && displayY < 39*unitHeight) {
                    restartGame(7);
                }
                else if (displayX > 35*unitWidth && displayX < 68*unitWidth && displayY > 33*unitHeight && displayY < 39*unitHeight) {
                    restartGame(8);
                }
                else if (displayX > 55*unitWidth && displayX < 68*unitWidth && displayY > 33*unitHeight && displayY < 39*unitHeight) {
                    restartGame(9);
                }
                /*
                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                 */
            }
        }
        else if (gameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 35*unitHeight && displayY < 41*unitHeight) {
                    restartGame(this.level);
                    System.out.println("RESTART");
                    gameOver = false;
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 45*unitHeight && displayY < 51*unitHeight) {
                    menu = true;
                    gameOver = false;

                }
                System.out.println(gameOver);
            }
        }
        else if (win) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 40*unitHeight && displayY < 46*unitHeight) {
                    level++;
                    System.out.println("RESTART");
                    restartGame(level);
                    win = false;
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 50*unitHeight && displayY < 56*unitHeight) {
                    menu = true;
                    win = false;
                }
            }
        }
        else if (bestScores) {
            //22*unitWidth, 75*unitHeight

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 71*unitHeight && displayY < 77*unitHeight) {
                    bestScores = false;
                    menu = true;
                }
            }
        }
        else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                //canvas.drawBitmap(bmp[11], 10, 10, null);
                if (displayX > 10 && displayX < (8*unitWidth)+10 && displayY > 10 && displayY < (8*unitWidth)+10) {
                    menu = true;
                }
                if (displayY <= mapHeight && selectedTower >= 0) {
                    if ((bmp[0].getPixel((int)displayX, (int)displayY) != Color.BLACK) && coins >= pricesTower[selectedTower] ) {
                        int indexBmp = -1;
                        if (selectedTower == 0) indexBmp = 1;
                        else if (selectedTower == 1) indexBmp = 2;
                        else if (selectedTower == 2) indexBmp = 6;

                        towers.add(new Tower(this, bmp[indexBmp], (int)displayX - towerSize/2, (int)displayY - towerSize/2, selectedTower, pricesTower[selectedTower]));
                        paints[2].setAlpha(255); paints[3].setAlpha(255); paints[4].setAlpha(255);
                        selectedTower = -1;
                        for (int i = (int)displayX - towerSize; i < (int)displayX + towerSize; i++) {
                            for (int j = (int)displayY - towerSize; j < (int)displayY + towerSize; j++) {
                                if (i >= 0 && j >= 0 && i < screenWidth && j < mapHeight) {
                                    bmp[0].setPixel(i, j, Color.BLACK);
                                }
                            }
                        }
                    }
                } else {
                    if (displayX > screenWidth-(int)(screenWidth/9)-coinHeartTextSize && displayX < screenWidth-(int)(screenWidth/9)+coinHeartTextSize &&
                            displayY > screenHeight-(barHeight/2)-coinHeartTextSize && displayY < screenHeight-(barHeight/2)+coinHeartTextSize) {
                        paints[2].setAlpha(140);
                        paints[3].setAlpha(255);
                        paints[4].setAlpha(255);
                        //paints[9].setAlpha(100);
                        //paints[10].setAlpha(0);
                        //paints[11].setAlpha(0);
                        selectedTower = 0;
                    }
                    else if (displayX > screenWidth-(int)(screenWidth/9)-(4*coinHeartTextSize) && displayX < screenWidth-(int)(screenWidth/9)-(2*coinHeartTextSize) &&
                            displayY > screenHeight-(barHeight/2)-coinHeartTextSize && displayY < screenHeight-(barHeight/2)+coinHeartTextSize) {
                        paints[2].setAlpha(255);
                        paints[3].setAlpha(140);
                        paints[4].setAlpha(255);
                        //paints[9].setAlpha(0);
                        //paints[10].setAlpha(100);
                        //paints[11].setAlpha(0);
                        selectedTower = 1;
                    }
                    else if (displayX > screenWidth-(int)(screenWidth/9)-(7*coinHeartTextSize) && displayX < screenWidth-(int)(screenWidth/9)-(5*coinHeartTextSize) &&
                            displayY > screenHeight-(barHeight/2)-coinHeartTextSize && displayY < screenHeight-(barHeight/2)+coinHeartTextSize) {
                        paints[2].setAlpha(255);
                        paints[3].setAlpha(255);
                        paints[4].setAlpha(140);
                        //paints[9].setAlpha(0);
                        //paints[10].setAlpha(0);
                        //paints[11].setAlpha(100);
                        selectedTower = 2;
                    }
                }
            }
        }
        return true;
    }

    void init(Context context) {
        bmp = new Bitmap[20];
        bmpMaps = new Bitmap[10];
        bmpMapsWork = new Bitmap[10];
        bmpBtns = new Bitmap[16];
        bmpMaps[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[0]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1work), screenWidth, mapHeight, false); // Working

        bmpMaps[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map2x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[1]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map2work), screenWidth, mapHeight, false); // Working

        bmpMaps[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map3x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[2]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map3work), screenWidth, mapHeight, false); // Working

        bmpMaps[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map4x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[3]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map4work), screenWidth, mapHeight, false); // Working

        bmpMaps[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map5x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[4]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map5work), screenWidth, mapHeight, false); // Working

        bmpMaps[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[5]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1work), screenWidth, mapHeight, false); // Working

        bmpMaps[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map2x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[6]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map2work), screenWidth, mapHeight, false); // Working

        bmpMaps[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map3x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[7]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map3work), screenWidth, mapHeight, false); // Working

        bmpMaps[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map4x), screenWidth, mapHeight, false); // Colored
        bmpMapsWork[8]  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map4work), screenWidth, mapHeight, false); // Working

        bmp[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower2), towerSize, towerSize, false);
        bmp[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower3), towerSize, towerSize, false);
        bmp[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower1), towerSize, towerSize, false);

        bmp[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coin), coinHeartTextSize, coinHeartTextSize, false);
        bmp[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.heart), coinHeartTextSize, coinHeartTextSize, false);

        bmp[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower2), 2*coinHeartTextSize, 2*coinHeartTextSize, false);
        bmp[13] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower3), 2*coinHeartTextSize, 2*coinHeartTextSize, false);
        bmp[14] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tower1), 2*coinHeartTextSize, 2*coinHeartTextSize, false);

        bmp[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship1), shipWidth, shipHeight, false);
        bmp[15] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2), shipWidth, shipHeight, false);
        bmp[16] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship3), shipWidth, shipHeight, false);
        bmp[17] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.boss), (int)(1.5*shipWidth), (int)(1.5*shipHeight), false);

        bmp[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.menubg), screenWidth, screenHeight, false);

        bmp[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bar), screenWidth, barHeight, false);

        bmpBtns[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel1), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel2), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel3), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel4), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel5), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel6), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel7), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel8), 13*unitWidth, 6*unitHeight, false);
        bmpBtns[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel9), 13*unitWidth, 6*unitHeight, false);

        bmpBtns[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenurestart), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[10] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenuscoret), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[11] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenuquit), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[12] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenunext), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[13] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenurestart), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[14] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenu), 40*unitWidth, 6*unitHeight, false);
        bmpBtns[15] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevellock), 13*unitWidth, 6*unitHeight, false);

        bmp[11] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.menuicon), 8*unitWidth, 8*unitWidth, false);
        bmp[12] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coin), coinHeartTextSize/2, coinHeartTextSize/2, false);
        //bmp[13] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle1), 180*2, 180*2, false);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mintmolly.ttf");

        paints = new Paint[15];
        paints[0] = new Paint();
        paints[1] = new Paint();
        paints[2] = new Paint();
        paints[3] = new Paint();
        paints[4] = new Paint();
        paints[5] = new Paint();
        paints[6] = new Paint();
        paints[7] = new Paint();
        paints[8] = new Paint();
        paints[9] = new Paint();
        paints[10] = new Paint();
        paints[11] = new Paint();
        paints[12] = new Paint();
        paints[13] = new Paint();
        paints[14] = new Paint();

        paints[0].setColor(Color.BLUE);
        paints[1].setColor(Color.BLACK);
        paints[1].setTextSize(coinHeartTextSize);
        paints[1].setTypeface(typeface);
        paints[5].setTextSize((int)(1.3 * coinHeartTextSize));
        paints[5].setColor(Color.WHITE);
        paints[5].setTypeface(typeface);
        paints[6].setTextSize(2*coinHeartTextSize);
        paints[6].setColor(Color.RED);
        paints[6].setTypeface(typeface);
        //paints[6].setTypeface(Typeface.DEFAULT_BOLD);
        paints[7].setColor(Color.GREEN);
        paints[7].setTextSize(2*coinHeartTextSize);
        paints[7].setTypeface(typeface);
        //paints[7].setTypeface(Typeface.DEFAULT_BOLD);
        paints[8].setColor(Color.BLACK);
        paints[8].setTextSize((int)(coinHeartTextSize*0.6));
        paints[8].setTypeface(typeface);
        //paints[9].setAlpha(0);
        //paints[10].setAlpha(0);
        //paints[11].setAlpha(0);

        paints[12].setColor(Color.WHITE);
        paints[12].setTextSize(coinHeartTextSize);
        paints[12].setTypeface(typeface);

        paints[13].setColor(Color.GREEN);
        paints[13].setTextSize((int)(1.5*coinHeartTextSize));
        paints[13].setTypeface(typeface);

        paints[14].setColor(Color.WHITE);
        paints[14].setTextSize((int)(0.8*coinHeartTextSize));
        paints[14].setTypeface(typeface);
    }

    public void loadMaps() {

        float pathRatioX = (float)screenWidth / 400;
        float pathRatioY = (float)screenHeight / 600;

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

            Path path = new Path();
            boolean moveTo = true;
            for (int i = 1; i < arrOfLines.length; i++) {
                if (arrOfLines[i].indexOf(',') == -1 && i > 0) {
                    if (arrOfLines[i].charAt(0) == 'S'){
                        shipSeq.add(arrOfLines[i].substring(1));
                    }
                    else if(arrOfLines[i].charAt(0) == 'P'){
                        delays.add(arrOfLines[i].substring(1).split(";"));
                    }
                    else {
                        paths.add(path);
                        path = new Path();
                        moveTo = true;
                    }

                }
                else{
                    String[] arr = arrOfLines[i].split(",");
                    if (moveTo){
                        path.moveTo((int)(Integer.parseInt(arr[0].trim()) * pathRatioX), (int)((Integer.parseInt(arr[1].trim()) * pathRatioY)/1.18));
                        moveTo = false;
                    }
                    else {
                        path.lineTo((int)(Integer.parseInt(arr[0].trim()) * pathRatioX), (int)((Integer.parseInt(arr[1].trim()) * pathRatioY)/1.18));
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Ship> getShips() {
        return this.ships;
    }
    public ArrayList<Projectile> getProjectiles() {
        return this.pros;
    }

    public void addToProjectiles(Projectile pr) {
        this.pros.add(pr);
    }

    public void increaseCoins(int sum) {
        coins += sum;
    }

    public void decreaseCoins(int sum) {
        coins -= sum;
    }

    public void decreaseHearts(int sum) {
        hearts -= sum;
        if(hearts < 1){
            sounds.play(gameOverSound, 1.0f, 1.0f, 0, 0, 1.5f);
            gameOver = true;
        }
    }

    public void restartGame(int level) {
        bmp[0] = bmpMapsWork[level - 1].copy(bmpMapsWork[level - 1].getConfig(), true);
        this.towers = new ArrayList<Tower>();
        this.ships = new ArrayList<Ship>();
        //this.paths = new ArrayList<Path>();
        this.pros = new ArrayList<Projectile>();
        this.selectedTower = -1;
        this.level = level;
        this.hearts = 5;
        if (level == 1) {
            this.coins = 100;
        }
        else if (level == 2) {
            this.coins = 120;
        }
        else {
            this.coins = 120;
        }
        this.lastDraw = System.currentTimeMillis();
        this.firstDraw = System.currentTimeMillis();
        this.shipsCount = shipSeq.get(level-1).length()-1;
        this.menu = false;
        this.pause = 10000;
        this.gameTime = System.currentTimeMillis() + this.pause;
        this.delay = 0;
        this.activeShips = this.shipsCount;
        this.paints[2].setAlpha(140);
        this.paints[3].setAlpha(255);
        this.paints[4].setAlpha(255);
        //editor.putString("level" + this.level, "");
        //editor.commit();

    }

    public int getScreenWidth() {
        return this.screenWidth;
    }
    public int getScreenHeight() {
        return this.screenHeight;
    }

    public void decreaseActiveShips() {
        this.activeShips--;
        if (activeShips == 0) {
            this.win = true;
            sounds.play(winSound, 1.0f, 1.0f, 0, 0, 1.5f);
            if (this.level + 1 > availableLevels) {
                editor.putInt("availableLevels", availableLevels++);
                editor.commit();
            }
            this.gameTime = System.currentTimeMillis() - this.gameTime;
            this.playTime = getPlayTime(this.gameTime);
            this.newRecord = checkBestScores();
        }
    }

    public String getPlayTime(long gameTime) {
        long minutes = (gameTime  / 1000) / 60;
        long seconds = (gameTime  / 1000) % 60;
        long millis = ((gameTime  / 1000) % 60) & 100;

        return Long.toString(minutes) + ":" + Long.toString(seconds) + "." + Long.toString(millis);
    }

    public boolean checkBestScores() {
        if (parseRecord(this.level)[0].trim() == ""){
            editor.putString("level" + this.level, Integer.toString(this.hearts) + ";" + Long.toString(this.gameTime) + ";" + Integer.toString(this.coins));
            editor.commit();
            return true;
        }

        int bestLives = Integer.parseInt(parseRecord(this.level)[0].trim());

        if (bestLives < this.hearts) {
            editor.putString("level" + this.level, Integer.toString(this.hearts) + ";" + Long.toString(this.gameTime) + ";" + Integer.toString(this.coins));
            editor.commit();
            //this.newRecord = true;
            return true;
        }
        else if (bestLives == this.hearts) {
            long bestTime = Long.parseLong(parseRecord(this.level)[1].trim());
            if (this.gameTime < bestTime) {
                editor.putString("level" + this.level, Integer.toString(this.hearts) + ";" + Long.toString(this.gameTime) + ";" + Integer.toString(this.coins));
                editor.commit();
                //this.newRecord = true;
                return true;
            }
            else if (bestTime == this.gameTime) {
                int bestCoins = Integer.parseInt(parseRecord(this.level)[2].trim());
                if (bestCoins < this.coins) {
                    editor.putString("level" + this.level, Integer.toString(this.hearts) + ";" + Long.toString(this.gameTime) + ";" + Integer.toString(this.coins));
                    editor.commit();
                    //this.newRecord = true;
                    return true;
                }
            }
        }
        return false;
    }

    public String[] parseRecord(int level) {
        String bestRecord = sp.getString("level" + level, "");

        if(bestRecord == ""){
            String[] newRecord = {""};
            return  newRecord;
        }
        else{
            return bestRecord.split(";");
        }

    }
    /*
    nastavit zamek na levely
    odladit
     */
}
