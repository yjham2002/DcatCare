package com.cat.dongguk.dcatcare;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.view.Window;
        import android.view.WindowManager;

public class introActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        h = new Handler();
        h.postDelayed(intro, 1500);
    }

    Handler h;

    Runnable intro = new Runnable() {
        public void run()
        {
            Intent i = new Intent(introActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        h.removeCallbacks(intro);
    }
}
