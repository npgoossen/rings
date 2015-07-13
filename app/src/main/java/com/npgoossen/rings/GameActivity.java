package com.npgoossen.rings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.npgoossen.rings.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.internal.view.WindowCallbackWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GameActivity extends Activity{
    private GameView gameView;
    public AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        gameView = new GameView(this);

        adView = new AdView(this);
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.bottom_banner_game));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(layoutParams);

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView);
        layout.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());

        setContentView(layout);
    }

    @Override
    protected void onDestroy(){
        adView.destroy();
        super.onDestroy();
    }

}
