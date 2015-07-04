package com.npgoossen.rings;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    float radius;
    float positionX;
    float positionY;
    final float centerX;
    final float centerY;

    int color;

    private float windowHeight;
    private float windowWidth;

    private Paint paint;

    public Ball(float radius, float posX, float posY,
                Paint paint, int color, float windowHeight, float windowWidth) {
        this.radius = radius;
        this.positionX = posX;
        this.positionY = posY;
        this.centerX = posX;
        this.centerY = posY;
        this.paint = paint;
        this.color = color;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
    }

    public void draw(Canvas canvas){
        paint.setColor(this.color);
        canvas.drawCircle(positionX, positionY, radius, paint);
    }

    public void move(float velocityX, float velocityY){
        this.positionX += (-1.5*velocityX);
        this.positionY += (1.5*velocityY);

        if(this.positionX < 0)
            this.positionX = radius;
        if(this.positionY < 0)
            this.positionY = radius;
        if(this.positionX > this.windowWidth)
            this.positionX = this.windowWidth - radius;
        if(this.positionY > this.windowHeight)
            this.positionY = this.windowHeight - radius;
    }

    public void reset(){
        this.positionY = this.centerY;
        this.positionX = this.centerX;
        this.color = GameView.getRandBallColor();
    }
}
