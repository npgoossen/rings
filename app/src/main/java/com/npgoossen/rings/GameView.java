package com.npgoossen.rings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View implements View.OnTouchListener, SensorEventListener {

    private static Random randomGenerator = new Random();

    private static boolean scoreChanged;
    private static int score = 0;
    private static boolean gameOver = false;
    private static int finalScore;
    private static boolean highScoreSet;
    private static int highScore;

    public static List<Integer> loopColors = new ArrayList<>();
    private static Loop mainLoop;
    private static int loopSpeed = 9;

    private static float sensorMultiplier = 1.25f;
    private static SensorManager gameSensorManager;
    private static Sensor motionSensor;

    private final RectF arcWedge;
    private final float radius;
    private final float innerRadius;
    private Ball curBall;

    private Resources res = getResources();
    private Paint paint = new Paint();
    private int bgColor = res.getColor(R.color.mainBG);

    private int windowHeight;
    private int windowWidth;

    private int gameOverColor;

    private static final int originalLoopSpeed = 9;
    private static final float originalSensorMultiplier = 1.25f;

    private boolean sensorSwitch;

    /*
       Constructor for the game view
     */
    public GameView(Context context){
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.windowHeight = size.y;
        this.windowWidth = size.x;

        radius = (float) (windowWidth * 7.0 / 16.0);
        innerRadius = (float) (windowWidth * 2.0/5.0);

        gameSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        motionSensor = gameSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        arcWedge = new RectF((float)(windowWidth / 2.0 - radius),
                (float)(windowHeight / 2.0 - radius),
                (float)(windowWidth / 2.0 + radius),
                (float)(windowHeight / 2.0 + radius));

        mainLoop = new Loop(this.paint, this.arcWedge,
                this.radius, this.innerRadius, this.bgColor);

        this.startGame();
    }

    @Override
    public void onDraw(Canvas canvas){
        if(!gameOver) {
            canvas.drawColor(this.bgColor);
            mainLoop.draw(canvas);
            mainLoop.move(loopSpeed);

            this.curBall.draw(canvas);
            this.checkCollision();

            paint.setColor(loopColors.get(0));
            paint.setTextSize(70.0f);
            paint.setFakeBoldText(true);
            canvas.drawText(String.valueOf(score), this.windowWidth - 100, 100, this.paint);

            if(sensorSwitch) {
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Switch Incoming", this.arcWedge.centerX(), this.windowHeight - 100,
                        this.paint);
            }
            this.updateLoop();
        } else {

            this.drawGameOver(canvas);
        }

        invalidate();
    }

    public boolean onTouch(View view, MotionEvent event){
        if(!gameOver){
            return true;
        } else {
            if(event.getY() < (this.windowHeight - 250) ) {
                System.out.println("play again");
                this.resetGame();
            } else{
                this.resetGame();
                Intent mainIntent = new Intent(getContext(), MainMenu.class);
                getContext().startActivity(mainIntent);
                ((Activity) getContext()).finish();
                return true;
            }

            return true;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int tmp){

    }

    /*
        Handles all the sensor changes.
        moves the ball according to the velocity of the
        accelerometers
     */
    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !gameOver) {
            float velX = event.values[0] * sensorMultiplier;
            float velY = event.values[1] * sensorMultiplier;

            this.curBall.move(velX, velY);
        }
    }


    /*
        Checks for a collision in between the ball and the loop,
        also checks if the color matches the ball and loop segment
     */
    public boolean checkCollision(){

        float xDif = this.curBall.positionX - this.arcWedge.centerX();
        float yDif = this.curBall.positionY - this.arcWedge.centerY();
        float disSquared = (xDif * xDif) + (yDif * yDif);
        boolean collision = disSquared < (this.innerRadius - this.curBall.radius) *
                (this.innerRadius - this.curBall.radius);

        if(!collision) {
            if (this.colorMatched()) {
                score++;
                scoreChanged = true;
                this.curBall.reset();
                finalScore = score;
            }
            else{
                gameOver = true;
            }
        }

        return true;
    }

    public static int getRandBallColor(){
        int tmpIndex = Math.abs(randomGenerator.nextInt() % mainLoop.segments.size());

        return mainLoop.segments.get(tmpIndex).color;
    }

    public static int getRandLoopColor(){
        int tmpIndex = Math.abs(randomGenerator.nextInt() % loopColors.size());

        return loopColors.get(tmpIndex);
    }

    protected boolean colorMatched(){
        for(LoopSegment tmpSeg : mainLoop.segments){
            if(tmpSeg.containsPoint(this.curBall.positionX, this.curBall.positionY)){
                if(tmpSeg.color == this.curBall.color) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateLoop(){
        if(score % 5 == 0 && scoreChanged ) {
            while(true){
                if(mainLoop.addSegment(getRandLoopColor()))
                    break;
            }
            if(score % 35 == 0){
                sensorMultiplier *= -1f;
            }
            loopSpeed += 4;
            sensorMultiplier += 0.45;
            scoreChanged = false;
        } else {
            sensorSwitch = score % 34 == 0 && score != 0;
        }
    }

    /*
        Adds a bunch of colors to the possible colors
        for the loop and ball
     */
    private void initLoopColors(){
        loopColors.add(res.getColor(R.color.gameGreen));
        loopColors.add(res.getColor(R.color.gameOrange));
        loopColors.add(res.getColor(R.color.gameRed));
        loopColors.add(res.getColor(R.color.gameBlue));
        loopColors.add(res.getColor(R.color.gamePink));
        loopColors.add(res.getColor(R.color.gamePurple));
        loopColors.add(res.getColor(R.color.gameYellow));
        loopColors.add(res.getColor(R.color.gameGrey));
        loopColors.add(res.getColor(R.color.gameBlack));
        loopColors.add(res.getColor(R.color.gameTeal));
        loopColors.add(res.getColor(R.color.gameBrown));
        loopColors.add(res.getColor(R.color.gameBrightGreen));
    }

    private void resetGame(){
        mainLoop.clearLoop();
        int tmp = 0;

        while(tmp < 2) {
            if (mainLoop.addSegment(getRandLoopColor())) {
                tmp++;
            }
        }
        this.curBall.reset();
        score = 0;
        finalScore = 0;
        gameOver = false;
        scoreChanged = false;

        sensorMultiplier = originalSensorMultiplier;
        loopSpeed = originalLoopSpeed;
    }

    private void startGame(){
        this.initLoopColors();

        scoreChanged = false;
        gameOver = false;
        highScoreSet = false;

        int tmp = 0;
        while(tmp < 2){
            if(mainLoop.addSegment(getRandLoopColor()))
                tmp++;
        }

        this.curBall = new Ball((float)(radius/5.0), arcWedge.centerX(), arcWedge.centerY(),
                this.paint, getRandBallColor(),
                windowHeight, windowWidth);

        gameOverColor = loopColors.get(randomGenerator.nextInt(loopColors.size()));

        gameSensorManager.registerListener(this, motionSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void drawGameOver(Canvas canvas){
        canvas.drawColor(this.bgColor);
        mainLoop.draw(canvas);
        this.curBall.draw(canvas);

        canvas.drawARGB(200, 255, 250, 250);

        if(!highScoreSet){
            SharedPreferences prefs = getContext().getSharedPreferences("highScore",
                    Context.MODE_PRIVATE);
            highScore = prefs.getInt("highScore", 0);

            if(finalScore > highScore){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highScore", finalScore);
                editor.apply();
            }

            highScoreSet = false;
        }


        paint.setColor(this.gameOverColor);
        paint.setTextSize(100.0f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("game over... :\'(", this.arcWedge.centerX(), 150, paint);

        paint.setFakeBoldText(true);
        paint.setTextSize(300.0f);
        canvas.drawText(String.valueOf(finalScore), this.arcWedge.centerX(),
                this.arcWedge.centerY(), paint);

        paint.setTextSize(100.0f);
        canvas.drawText("play again?", this.arcWedge.centerX(), this.arcWedge.centerY() + 150,
                paint);

        canvas.drawText("main menu", this.arcWedge.centerX(), this.windowHeight - 120,
                paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(80.0f);
        canvas.drawText("high score: " + String.valueOf(highScore), this.arcWedge.centerX(), this.arcWedge.centerY() + 250,
                paint);

    }
}
