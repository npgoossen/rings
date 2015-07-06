package com.npgoossen.rings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
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
    private Bitmap instructionImg = BitmapFactory.decodeResource(res, R.drawable.instructions);

    private int bgColor = res.getColor(R.color.mainBG);

    private float radius;
    private float innerRadius;

    private boolean mainMenu;
    private boolean instructionMenu;

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
        this.instructionImg = Bitmap.createScaledBitmap(this.instructionImg,
                this.windowWidth,
                this.windowHeight,
                true);

        this.mainMenu = true;
        this.instructionMenu = false;
    }

    @Override
    protected void onDraw(Canvas canvas){
        if(mainMenu){
            this.drawMainMenu(canvas);
        } else if(instructionMenu) {
            this.drawInstructionMenu(canvas);
        }
        invalidate();
    }

    public boolean onTouch(View view, MotionEvent event){
        if (mainMenu){
            if(this.mainLoop.container.contains(event.getX(), event.getY())) {
                Intent mainIntent = new Intent(getContext(), GameActivity.class);
                getContext().startActivity(mainIntent);
            } else if((event.getX() > this.windowWidth - 200) && (event.getY() < 200)) {
                this.instructionMenu = true;
                this.mainMenu = false;
                return true;
            }
        } else if (instructionMenu){
            if((event.getX() < (this.windowWidth / 3.0)) && (event.getY() < (this.windowHeight / 4.0))){
                this.mainMenu = true;
                this.instructionMenu = false;
                return true;
            }
        }

        return true;
    }

    private void drawMainMenu(Canvas canvas){
        canvas.drawColor(this.bgColor);
        this.mainLoop.draw(canvas);
        canvas.drawBitmap(this.startEmblem, (float) (windowWidth / 2.0 - innerRadius),
                (float) (windowHeight / 2.0 - innerRadius),
                this.paint);
        this.mainLoop.move(6);

        paint.setTextSize(100.0f);
        paint.setColor(this.loopColors.get(1));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        canvas.drawText("?", this.windowWidth - 70, 100, paint);
    }

    private void drawInstructionMenu(Canvas canvas){
        canvas.drawColor(this.bgColor);
        canvas.drawBitmap(this.instructionImg, 0, 0, this.paint);

    }
}