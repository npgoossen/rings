package com.npgoossen.rings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class Loop {

    private Paint paint;

    public final RectF container;
    private final float radius;
    private final float innerRadius;

    private int backgroundColor;

    public List<LoopSegment> segments = new ArrayList<>();

    public int size = 0;

    public Loop(Paint parentPaint, RectF container, float radius,
                float innerRadius, int backgroundColor){
        this.radius = radius;
        this.container = container;
        this.paint = parentPaint;
        this.innerRadius = innerRadius;
        this.backgroundColor = backgroundColor;
    }

    public boolean addSegment(int color){

        int sweepAngle = (int)(360.0 / (segments.size() + 1));

        int curPosition = 0;

        for(LoopSegment seg : this.segments){
            if (seg.color == color)
                return false;
        }

        this.segments.add(new LoopSegment(this.paint, color, sweepAngle,
                0, this.container));

        if(segments.size() != 7) {
            for (LoopSegment seg : this.segments) {
                seg.setPosition(curPosition, sweepAngle);
                curPosition += sweepAngle;
            }
        } else {
            int i;
            for (i = 0; i < segments.size() - 1; i++) {
                segments.get(i).setPosition(curPosition, sweepAngle);
                curPosition += sweepAngle;
            }

            segments.get(segments.size() - 1).setPosition(curPosition, 360 - curPosition);
        }
        this.size++;

        return true;
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < this.segments.size(); i++){
            this.segments.get(i).draw(canvas);
        }
        paint.setColor(this.backgroundColor);
        canvas.drawCircle(this.container.centerX(), this.container.centerY(),
                this.innerRadius, this.paint);
    }

    public void move(float rpm){
        float angle = (float) (rpm * ( 360.0 / 3600.0));
        for(LoopSegment seg : this.segments){
            seg.setPosition((seg.startAngle + angle) % 360.0f, (int)seg.sweepAngle);
        }
    }

    public void clearLoop(){
        segments.clear();
    }



    public String toString(){
        String tmp = "";

        for(LoopSegment seg : segments){
            tmp += seg.toString() + "---";
        }
        return tmp;
    }

}
