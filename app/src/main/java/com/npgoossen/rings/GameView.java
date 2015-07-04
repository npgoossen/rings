package com.npgoossen.rings;

import android.content.Context;
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

    public static List<Integer> loopColors = new ArrayList<>();
    private static Loop mainLoop;
    private static int loopSpeed = 4;

    private static float sensorMultiplier = 1.0f;
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

        this.addColorsToLoop();

        mainLoop.addSegment(loopColors.get(Math.abs(randomGenerator.nextInt() % loopColors.size())));
        mainLoop.addSegment(loopColors.get(Math.abs(randomGenerator.nextInt() % loopColors.size())));

        scoreChanged = false;
        gameOverColor = loopColors.get(Math.abs(randomGenerator.nextInt() % loopColors.size()));

        this.curBall = new Ball((float)(radius/5.0), arcWedge.centerX(), arcWedge.centerY(),
                this.paint, getRandBallColor(),
                windowHeight, windowWidth);

        gameSensorManager.registerListener(this, motionSensor,
                SensorManager.SENSOR_DELAY_GAME);
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
            paint.setTextSize(50.0f);
            paint.setFakeBoldText(true);
            canvas.drawText(String.valueOf(score), this.windowWidth - 100, 100, this.paint);
            this.updateLoop();
        } else {
            canvas.drawColor(this.bgColor);
            mainLoop.draw(canvas);
            this.curBall.draw(canvas);

            canvas.drawARGB(180, 255, 250, 250);

            paint.setColor(this.gameOverColor);
            paint.setTextSize(100.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("game over... :\'(", this.arcWedge.centerX(), 150, paint);

            paint.setFakeBoldText(true);
            paint.setTextSize(300.0f);
            canvas.drawText(String.valueOf(finalScore), this.arcWedge.centerX(),
                    this.arcWedge.centerY(), paint);

            paint.setTextSize(100.0f);
            canvas.drawText("play again", this.arcWedge.centerX(), this.arcWedge.centerY() + 150,
                    paint);
        }

        invalidate();
    }

    public boolean onTouch(View view, MotionEvent event){
        if(!gameOver){
            return true;
        } else {
            resetGame();
            return true;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !gameOver) {
            float velX = event.values[0] * sensorMultiplier;
            float velY = event.values[1] * sensorMultiplier;

            this.curBall.move(velX, velY);
        }
    }

    public boolean checkCollision(){

        float xDif = this.curBall.positionX - this.arcWedge.centerX();
        float yDif = this.curBall.positionY - this.arcWedge.centerY();
        float disSquared = (xDif * xDif) + (yDif * yDif);
        boolean collision = disSquared < (this.innerRadius - this.curBall.radius) *
                (this.innerRadius - this.curBall.radius);

        if(!collision) {
            if (this.colorMatched()) {
                score += 1;
                scoreChanged = true;
                this.curBall.reset();
                finalScore = score;
            }
            else{
                score = 0;
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
            mainLoop.addSegment(getRandLoopColor());
            loopSpeed += 1;
            sensorMultiplier += 0.1f;
            scoreChanged = false;
        }
    }

    private void addColorsToLoop(){
        loopColors.add(res.getColor(R.color.gameGreen));
        loopColors.add(res.getColor(R.color.gameOrange));
        loopColors.add(res.getColor(R.color.gameRed));
        loopColors.add(res.getColor(R.color.gameBlue));
        loopColors.add(res.getColor(R.color.gamePink));
        loopColors.add(res.getColor(R.color.gamePurple));
        loopColors.add(res.getColor(R.color.gameYellow));
    }

    private void resetGame(){
        mainLoop.clearLoop();
        int tmp = 0;

        while(tmp != 2) {
            if(mainLoop.addSegment(getRandLoopColor()))
                tmp++;
        }

        this.curBall.reset();
        score = 0;
        finalScore = 0;
        gameOver = false;
        scoreChanged = false;
    }
}
