package com.npgoossen.rings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class LoopSegment {
    public int color;
    private Paint paint;
    public float startAngle = 0;
    public float sweepAngle = 0;
    private RectF container;
    
    public LoopSegment(Paint parentPaint,int segColor, int sweepAngle, int startAngle,
                       RectF container){
        this.paint = parentPaint;
        this.color = segColor;
        this.sweepAngle = sweepAngle;
        this.startAngle = startAngle;
        this.container = container;
    }

    public void setPosition(float startAngle, float sweepAngle){
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }

    public void draw(Canvas canvas){
        this.paint.setColor(this.color);
        canvas.drawArc(this.container, this.startAngle,
                this.sweepAngle, true, this.paint);
    }

    public boolean containsPoint(float x, float y){
        float angle = this.getAngle(x, y);

        if(this.startAngle + this.sweepAngle <= 360){
            return ((angle > this.startAngle) && (angle < (this.startAngle + this.sweepAngle)));
        } else if((angle > this.startAngle) || (angle < (this.startAngle + this.sweepAngle) - 360)){
            return true;
        } else{
            return false;
        }
    }

    private float getAngle(float x, float y){
        float angle;

        if(y > this.container.centerY() && x > this.container.centerX()) {
            angle = (float)((180.0 / Math.PI) * Math.atan((y - this.container.centerY()) / (x - this.container.centerX())));
        } else if(y > this.container.centerY() && x < this.container.centerX()) {
            angle = (float)(90.0 +  (180.0 / Math.PI) * Math.atan((this.container.centerX() - x) / (y - this.container.centerY())));
        } else if(y < this.container.centerY() && x < this.container.centerX()) {
            angle = (float)(180.0 + (180.0 / Math.PI) * Math.atan((this.container.centerY() - y)/(this.container.centerX() - x)));
        } else if(y < this.container.centerY() && x > this.container.centerX()) {
            angle = (float)(270.0 + (180.0 / Math.PI) * Math.atan((x - this.container.centerX())/(this.container.centerY() - y)));
        } else if(y == this.container.centerY() && x > this.container.centerX()){
            angle = 0.0f;
        } else if(y == this.container.centerY() && x < this.container.centerX()){
            angle = 180.0f;
        } else if(y > this.container.centerY() && x == this.container.centerX()){
            angle = 270.0f;
        } else if(y < this.container.centerY() && x == this.container.centerX()){
            angle = 90.0f;
        } else {
            angle = 0.0f;
        }

        return angle;
    }

    public String toString(){
        return String.valueOf(color) + "," + String.valueOf(startAngle) + "," +
                String.valueOf(sweepAngle);
    }
}
