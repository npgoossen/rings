package com.npgoossen.rings;

import com.google.ads.AdSize;
import com.npgoossen.rings.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainMenu extends Activity {
    private MainMenuView mainView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mainView = new MainMenuView(this);

        adView = new AdView(this);
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.bottom_banner_game));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(layoutParams);

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(mainView);
        layout.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());

        setContentView(layout);

    }

    @Override
    public void onBackPressed(){

        if(mainView.instructionMenu) {
            mainView.instructionMenu = false;
            mainView.invalidate();
        }
    }

    @Override
    protected void onDestroy(){
        adView.destroy();
        super.onDestroy();
    }

}
