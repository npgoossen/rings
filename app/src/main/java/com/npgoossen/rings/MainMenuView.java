package com.npgoossen.rings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Point;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class MainMenuView extends View implements View.OnTouchListener {

    Paint paint = new Paint();

    private int windowHeight;
    private int windowWidth;

    private Loop mainLoop;

    private Resources res = getResources();

    private List<Integer> loopColors = new ArrayList<>();
    private Bitmap startEmblem = BitmapFactory.decodeResource(res, R.drawable.start);

    private int bgColor = res.getColor(R.color.mainBG);

    private float radius;
    private float innerRadius;

    public MainMenuView(Context context){
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
        windowHeight = size.y;
        windowWidth = size.x;

        radius = (float) (windowWidth * 7.0 / 16.0);
        innerRadius = (float) (this.windowWidth * 2.0/5.0);

        mainLoop = new Loop(this.paint, new RectF((float)(windowWidth / 2.0 - radius),
                (float)(windowHeight / 2.0 - radius),
                (float)(windowWidth / 2.0 + radius),
                (float)(windowHeight / 2.0 + radius)), radius,
                innerRadius,
                bgColor);

        loopColors.add(res.getColor(R.color.gameGreen));
        loopColors.add(res.getColor(R.color.gameOrange));
        loopColors.add(res.getColor(R.color.gameRed));
        loopColors.add(res.getColor(R.color.gameBlue));

        this.mainLoop.addSegment(loopColors.get(0));
        this.mainLoop.addSegment(loopColors.get(1));
        this.mainLoop.addSegment(loopColors.get(2));
        this.mainLoop.addSegment(loopColors.get(3));

        this.startEmblem = Bitmap.createScaledBitmap(this.startEmblem,
                (int)(this.windowWidth * 4.0/5.0),
                (int)(this.windowWidth * 4.0/5.0),
                true);
    }

    @Override
    protected void onDraw(Canvas canvas){

        canvas.drawColor(this.bgColor);
        this.mainLoop.draw(canvas);
        canvas.drawBitmap(this.startEmblem, (float)(windowWidth / 2.0 - innerRadius),
                (float)(windowHeight / 2.0 - innerRadius),
                this.paint);
        this.mainLoop.move(6);

        invalidate();
    }

    public boolean onTouch(View view, MotionEvent event){
        if (this.mainLoop.container.contains(event.getX(), event.getY())){
            Intent mainIntent = new Intent(getContext(), GameActivity.class);
            getContext().startActivity(mainIntent);
        }

        return true;
    }
}