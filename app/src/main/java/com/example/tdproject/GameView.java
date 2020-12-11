package com.example.tdproject;

import android.content.Context;
import android.content.Intent;
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
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public MainThread thread;
    Bitmap[] bmp;
    Paint[] paints;
    Path enemyMove;
    int width;
    int height;
    private Tower tower;
    private ArrayList<Tower> towers;
    private ArrayList<Ship> ships;
    private ArrayList<Path> paths;
    private ArrayList<Projectile> pros;
    private ArrayList<String> shipSeq;
    private ArrayList<String[]> delays;
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
    private long lastDraw;
    private long firstDraw;
    private long pause;
    private int delay;
    private int activeShips;

    private int[] map = {

    };

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);

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
        this.pricesTower[0] = 40;
        this.pricesTower[1] = 60;
        this.pricesTower[2] = 50;
        this.unitWidth = (int)(screenWidth/80);
        this.unitHeight = (int)(screenHeight/80);
        this.barHeight = unitHeight*12;
        this.mapHeight = screenHeight-barHeight;
        this.shipWidth = unitWidth*4;
        this.shipHeight = unitHeight*4;
        this.towerSize = unitWidth*8;
        this.coinHeartTextSize = unitWidth*6;
        this.menu = true;
        this.ratioX = (float)(screenWidth) / (float)getResources().getDrawable(R.drawable.map1).getIntrinsicWidth();
        this.ratioY = (float)(mapHeight) / (float)getResources().getDrawable(R.drawable.map1).getIntrinsicHeight();
        this.activeShips = 0;
        //restartGame(1);
        init(context);
        loadMaps();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void update() {
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
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (menu) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 46*unitHeight, null);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 55*unitHeight, null);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 64*unitHeight, null);

            canvas.drawText("Levels:", 30*unitWidth, 10*unitHeight, paints[5]);
            canvas.drawBitmap(bmp[10], 15*unitWidth, 15*unitHeight, null);
            canvas.drawBitmap(bmp[10], 35*unitWidth, 15*unitHeight, null);
            canvas.drawBitmap(bmp[10], 55*unitWidth, 15*unitHeight, null);
            canvas.drawBitmap(bmp[10], 15*unitWidth, 24*unitHeight, null);
            canvas.drawBitmap(bmp[10], 35*unitWidth, 24*unitHeight, null);
            canvas.drawBitmap(bmp[10], 55*unitWidth, 24*unitHeight, null);
            canvas.drawBitmap(bmp[10], 15*unitWidth, 33*unitHeight, null);
            canvas.drawBitmap(bmp[10], 35*unitWidth, 33*unitHeight, null);
            canvas.drawBitmap(bmp[10], 55*unitWidth, 33*unitHeight, null);

        }
        else if (gameOver) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawText("Game Over!", 10*unitWidth, 25*unitHeight, paints[6]);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 35*unitHeight, null);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 45*unitHeight, null);
        }
        else if (win) {
            canvas.drawBitmap(bmp[8], 0,0, null);
            canvas.drawText("You won!", 18*unitWidth, 25*unitHeight, paints[7]);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 35*unitHeight, null);
            canvas.drawBitmap(bmp[9], 22*unitWidth, 45*unitHeight, null);
        }
        else {
            canvas.drawBitmap(bmp[0], null, new Rect(0, 0, screenWidth, mapHeight), null);
            canvas.drawBitmap(bmp[11], 10, 10, null);

            if (System.currentTimeMillis() - this.lastDraw > 1000 && ships.size() < shipsCount && System.currentTimeMillis() - this.firstDraw > this.pause) {

                this.lastDraw = System.currentTimeMillis();
                if (Integer.parseInt(delays.get(level-1)[delay].trim()) == ships.size()){
                    this.firstDraw = System.currentTimeMillis();
                    this.pause = 4000;
                    delay++;

                    System.out.println("T");
                }

                switch (shipSeq.get(level-1).charAt(ships.size())){
                    case '0':
                        ships.add(new Ship(this, bmp[7], paths.get(level - 1), 0));
                        break;
                    case '1':
                        ships.add(new Ship(this, bmp[7], paths.get(level - 1), 1));
                        break;
                    case '2':
                        ships.add(new Ship(this, bmp[7], paths.get(level - 1), 2));
                        break;
                    case '3':
                        ships.add(new Ship(this, bmp[6], paths.get(level - 1), 3));
                        break;
                }
                activeShips++;
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

            canvas.drawRect(0, mapHeight, screenWidth, screenHeight, paints[0]);
            canvas.drawBitmap(bmp[3], 5, mapHeight+5 , null);
            canvas.drawText(Integer.toString(this.coins), coinHeartTextSize+25, mapHeight+coinHeartTextSize-5, paints[1]);
            canvas.drawBitmap(bmp[4], 5, mapHeight+coinHeartTextSize+15 , null);
            canvas.drawText(Integer.toString(this.hearts), coinHeartTextSize+25, mapHeight+(2*coinHeartTextSize)+5, paints[1]);
            canvas.drawBitmap(bmp[5], screenWidth-(int)(screenWidth/9)-coinHeartTextSize, screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[2]);
            canvas.drawBitmap(bmp[5], screenWidth-(int)(screenWidth/9)-(4*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[3]);
            canvas.drawBitmap(bmp[5], screenWidth-(int)(screenWidth/9)-(7*coinHeartTextSize), screenHeight-((int)(barHeight/1.8))-coinHeartTextSize, paints[4]);
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
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 46*unitHeight && displayY < 52*unitHeight) {
                    System.out.println("reset");
                    restartGame(1);
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 55*unitHeight && displayY < 61*unitHeight) {
                    System.out.println("start");

                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 64*unitHeight && displayY < 70*unitHeight) {
                    System.out.println("quit");
                    /*
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);*/
                }
                /*
                *   canvas.drawBitmap(bmp[10], 15*unitWidth, 15*unitHeight, null);
                    canvas.drawBitmap(bmp[10], 35*unitWidth, 15*unitHeight, null);
                    canvas.drawBitmap(bmp[10], 55*unitWidth, 15*unitHeight, null);
                * */
                else if (displayX > 15*unitWidth && displayX < 28*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    restartGame(1);
                    System.out.println("level 1");
                }
                else if (displayX > 35*unitWidth && displayX < 48*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    //restartGame(1);
                    System.out.println("level 2");
                }
                else if (displayX > 55*unitWidth && displayX < 68*unitWidth && displayY > 15*unitHeight && displayY < 21*unitHeight) {
                    //restartGame(1);
                    System.out.println("level 3");
                }
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
            }
        }
        else if (win) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 35*unitHeight && displayY < 41*unitHeight) {
                    level++;
                    System.out.println("RESTART");
                    restartGame(level);
                    win = false;
                }
                else if (displayX > 22*unitWidth && displayX < 62*unitWidth && displayY > 45*unitHeight && displayY < 51*unitHeight) {
                    menu = true;
                    win = false;
                }
            }
        }
        else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int mapX = (int)(event.getX() / (float)ratioX);
                int mapY = (int)(event.getY() / (float)ratioY);

                //canvas.drawBitmap(bmp[11], 10, 10, null);
                if (displayX > 10 && displayX < (8*unitWidth)+10 && displayY > 10 && displayY < (8*unitWidth)+10) {
                    menu = true;
                }
                if (displayY <= mapHeight && selectedTower >= 0) {
                    if ((bmp[2].getPixel(mapX, mapY) != Color.BLACK) && coins >= pricesTower[selectedTower] ) {
                        towers.add(new Tower(this, bmp[6], (int)displayX - towerSize/2, (int)displayY - towerSize/2, selectedTower, pricesTower[selectedTower]));
                        paints[2].setAlpha(255); paints[3].setAlpha(255); paints[4].setAlpha(255);
                        selectedTower = -1;
                        for (int i = mapX - towerSize; i < mapX + towerSize; i++) {
                            for (int j = mapY - towerSize; j < mapY + towerSize; j++) {
                                if (i >= 0 && j >= 0 && i < getResources().getDrawable(R.drawable.map1).getIntrinsicWidth() && j < getResources().getDrawable(R.drawable.map1).getIntrinsicHeight()) {
                                    bmp[2].setPixel(i, j, Color.BLACK);
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
                        selectedTower = 0;
                    }
                    else if (displayX > screenWidth-(int)(screenWidth/9)-(4*coinHeartTextSize) && displayX < screenWidth-(int)(screenWidth/9)-(2*coinHeartTextSize) &&
                            displayY > screenHeight-(barHeight/2)-coinHeartTextSize && displayY < screenHeight-(barHeight/2)+coinHeartTextSize) {
                        paints[2].setAlpha(255);
                        paints[3].setAlpha(140);
                        paints[4].setAlpha(255);
                        selectedTower = 1;
                    }
                    else if (displayX > screenWidth-(int)(screenWidth/9)-(7*coinHeartTextSize) && displayX < screenWidth-(int)(screenWidth/9)-(5*coinHeartTextSize) &&
                            displayY > screenHeight-(barHeight/2)-coinHeartTextSize && displayY < screenHeight-(barHeight/2)+coinHeartTextSize) {
                        paints[2].setAlpha(255);
                        paints[3].setAlpha(255);
                        paints[4].setAlpha(140);
                        selectedTower = 2;
                    }
                }
            }
        }
        return true;
    }

    void init(Context context) {
        bmp = new Bitmap[13];
        bmp[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1), screenWidth, mapHeight, false); // Colored
        bmp[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map1work), screenWidth, mapHeight, false); // Working
        bmp[2] = bmp[1].copy(bmp[1].getConfig(), true);
        bmp[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coin), coinHeartTextSize, coinHeartTextSize, false);
        bmp[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.heart), coinHeartTextSize, coinHeartTextSize, false);
        bmp[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bug), 2*coinHeartTextSize, 2*coinHeartTextSize, false);
        bmp[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bug), towerSize, towerSize, false);
        bmp[7] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship), shipWidth, shipHeight, false);
        bmp[8] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.menubg), screenWidth, screenHeight, false);
        bmp[9] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttmenu), 40*unitWidth, 6*unitHeight, false);
        bmp[10] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.buttlevel), 13*unitWidth, 6*unitHeight, false);
        bmp[11] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.menuicon), 8*unitWidth, 8*unitWidth, false);

        paints = new Paint[8];
        paints[0] = new Paint();
        paints[1] = new Paint();
        paints[2] = new Paint();
        paints[3] = new Paint();
        paints[4] = new Paint();
        paints[5] = new Paint();
        paints[6] = new Paint();
        paints[7] = new Paint();

        paints[0].setColor(Color.BLUE);
        paints[1].setColor(Color.BLACK);
        paints[1].setTextSize(coinHeartTextSize);
        paints[5].setTextSize((int)(1.3 * coinHeartTextSize));
        paints[5].setColor(Color.WHITE);
        paints[5].setTypeface(Typeface.SANS_SERIF);
        paints[6].setTextSize(2*coinHeartTextSize);
        paints[6].setColor(Color.RED);
        paints[6].setTypeface(Typeface.SANS_SERIF);
        paints[6].setTypeface(Typeface.DEFAULT_BOLD);
        paints[7].setColor(Color.GREEN);
        paints[7].setTextSize(2*coinHeartTextSize);
        paints[7].setTypeface(Typeface.SANS_SERIF);
        paints[7].setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void loadMaps() {

//        float pathRatioX = (float)getResources().getDrawable(R.drawable.map1).getIntrinsicWidth() / 400;
//        float pathRatioY = (float)getResources().getDrawable(R.drawable.map1).getIntrinsicHeight() / 600;
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
            gameOver = true;
        }
    }

    public void restartGame(int level) {
        this.towers = new ArrayList<Tower>();
        this.ships = new ArrayList<Ship>();
        //this.paths = new ArrayList<Path>();
        this.pros = new ArrayList<Projectile>();
        this.selectedTower = -1;
        this.level = level;
        this.hearts = 15;
        if (level == 1) {
            this.coins = 100;
        }
        else if (level == 2) {
            this.coins = 120;
        }
        else {
            this.coins = 150;
        }
        this.lastDraw = System.currentTimeMillis();
        this.firstDraw = System.currentTimeMillis();
        this.shipsCount = shipSeq.get(level-1).length()-1;
        this.menu = false;
        this.pause = 10000;
        this.delay = 0;
        this.activeShips = 0;

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
            win = true;
        }
    }

}

/*
* Odpocet
* menu ikonka
* prepinani mezi levely
*
*    new CountDownTimer(10000, 1000) {

        public void onTick(long millisUntilFinished) {

            //time = "seconds remaining: " + millisUntilFinished / 1000;
        }

        public void onFinish() {
            //mTextField.setText("done!");
        }
    }.start();
*
*
* */