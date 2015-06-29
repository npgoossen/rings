package com.npgoossen.rings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
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


    private final RectF arcWedge;
    private final float radius;
    private final float innerRadius;

    private int windowHeight;
    private int windowWidth;

    private Resources res = getResources();

    public static List<Integer> loopColors = new ArrayList<>();
    private static Loop mainLoop;
    private Ball curBall;

    private Paint paint = new Paint();

    private int bgColor = res.getColor(R.color.mainBG);
    private int loopSpeed = 4;

    private SensorManager gameSensorManager;
    private Sensor motionSensor;

    private int score = 0;

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

        loopColors.add(res.getColor(R.color.gameGreen));
        loopColors.add(res.getColor(R.color.gameOrange));
        loopColors.add(res.getColor(R.color.gameRed));
        loopColors.add(res.getColor(R.color.gameBlue));

        mainLoop.addSegment(loopColors.get(0));
        mainLoop.addSegment(loopColors.get(1));

        this.curBall = new Ball((float)(radius/5.0), arcWedge.centerX(), arcWedge.centerY(),
                this.paint, loopColors.get(0),
                windowHeight, windowWidth);

        gameSensorManager.registerListener(this, motionSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(this.bgColor);
        mainLoop.draw(canvas);
        mainLoop.move(this.loopSpeed);
        this.curBall.draw(canvas);
        this.checkCollision();
        paint.setColor(loopColors.get(0));
        canvas.drawText(String.valueOf(score), this.windowWidth - 100, 100, this.paint);

        invalidate();
    }

    public boolean onTouch(View view, MotionEvent event){

        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float velX = event.values[0];
            float velY = event.values[1];

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
                this.score += 1;
                this.curBall.reset();
            }
            else{
                this.score = 0;
                this.curBall.reset();
//                this.gameOver();
            }
        }

        return true;
    }

    public static int getRandColor(){
        Random tmp = new Random();
        int tmpIndex = Math.abs(tmp.nextInt() % mainLoop.segments.size());

        return mainLoop.segments.get(tmpIndex).color;
    }

    protected boolean colorMatched(){
        for(LoopSegment tmpSeg : mainLoop.segments){
            if(tmpSeg.containsPoint(this.curBall.positionX, this.curBall.positionY)){
                System.out.println("contains point");
                if(tmpSeg.color == this.curBall.color) {
                    System.out.println("Same Color");
                    return true;
                }
            }
        }
        return false;
    }

    private void gameOver(){
        return;
    }
}
