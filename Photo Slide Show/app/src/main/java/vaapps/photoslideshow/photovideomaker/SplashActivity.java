package vaapps.photoslideshow.photovideomaker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vaapps.photoslideshow.photovideomaker.util.KSUtil;
import vaapps.photoslideshow.photovideomaker.util.Render;

public class SplashActivity extends AppCompatActivity {

    ImageView icon, bgIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        icon = findViewById(R.id.icon);
//        bgIV = findViewById(R.id.bgIV);
        Glide.with(this)
                .load(R.drawable.sp_logo)
                .into(icon);

   /*     Glide.with(this)
                .load(R.drawable.splash_bg)
                .into(bgIV);*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set Animation
                Render render = new Render(SplashActivity.this);
                render.setAnimation(KSUtil.InUp(icon));
                render.start();
            }
        }, 200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
