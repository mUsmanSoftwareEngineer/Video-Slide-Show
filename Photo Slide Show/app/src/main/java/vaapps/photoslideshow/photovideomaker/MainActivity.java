package vaapps.photoslideshow.photovideomaker;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;

import vaapps.photoslideshow.photovideomaker.kessiphotopicker.activity.ImagePickerActivity;
import vaapps.photoslideshow.photovideomaker.myvideo.MyVideo;
import vaapps.photoslideshow.photovideomaker.util.AdManager;
import vaapps.photoslideshow.photovideomaker.util.AdsManagerQ;
import vaapps.photoslideshow.photovideomaker.util.AdsUtils;
import vaapps.photoslideshow.photovideomaker.util.Animatee;
import vaapps.photoslideshow.photovideomaker.util.KSUtil;
import vaapps.photoslideshow.photovideomaker.swap.SwapperActivity;
import vaapps.photoslideshow.photovideomaker.util.Render;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ImageView mainBg, icon, moreIV;
    LinearLayout btnLay;
    CardView btnStart, btnAllVideo, rateIV, shareIV, privacyIV;

    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    AdsManagerQ adsManagerQ;
    public AdManagerInterstitialAd mAdManagerInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdsManagerQ.getInstance().createInterstitialstaticAd(MainActivity.this, getResources().getString(R.string.staticinterstitialid));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MainActivity.this.requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }

        layout = findViewById(R.id.adLayout);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        Log.d("7247check", "banner");
        if (AdsUtils.isNetworkAvailabel(MainActivity.this)) {
            Log.i("7247check", "banner");

            AdsManagerQ.loadbannerAd(MainActivity.this, mShimmerViewContainer, layout);


        } else {
            //   layout.setVisibility(View.GONE);
            //  mShimmerViewContainer.setVisibility(View.GONE);
        }




//        mainBg = findViewById(R.id.mainBg);
   /*     Glide.with(this)
                .load(R.drawable.f_bg)
                .into(mainBg);*/
        icon = findViewById(R.id.icon);
        Glide.with(this)
                .load(R.drawable.sp_logo)
                .into(icon);
        btnLay = findViewById(R.id.btnLay);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set Animation
                Render render = new Render(MainActivity.this);
                render.setAnimation(KSUtil.Swing(icon));
                render.start();
            }
        }, 1800);

/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mainBg.setVisibility(View.VISIBLE);
                // Set Animation
                Render render = new Render(MainActivity.this);
                render.setAnimation(KSUtil.InUp(mainBg));
                render.start();
            }
        }, 1200);*/

        init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnLay.setVisibility(View.VISIBLE);
                // Set Animation
                Render render = new Render(MainActivity.this);
                render.setAnimation(KSUtil.In(btnLay));
                render.start();
            }
        }, 1500);


    }

    void init() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        KessiApplication.VIDEO_HEIGHT = displaymetrics.widthPixels;
        KessiApplication.VIDEO_WIDTH = displaymetrics.widthPixels;


        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnAllVideo = findViewById(R.id.btnAllVideo);
        btnAllVideo.setOnClickListener(this);

        rateIV = findViewById(R.id.rateIV);
        rateIV.setOnClickListener(this);

        shareIV = findViewById(R.id.shareIV);
        shareIV.setOnClickListener(this);

        privacyIV = findViewById(R.id.privacyIV);
        privacyIV.setOnClickListener(this);

//        moreIV = findViewById(R.id.moreIV);
//        moreIV.setOnClickListener(this);

        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (!AdManager.isloadFbMAXAd) {
            //admob
            AdManager.initAd(MainActivity.this);
            AdManager.loadBannerAd(MainActivity.this, adContainer);
            AdManager.loadInterAd(MainActivity.this);
        } else {
            //MAX + Fb banner Ads
            AdManager.initMAX(MainActivity.this);
            AdManager.maxBanner(MainActivity.this, adContainer);
            AdManager.maxInterstital(MainActivity.this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                if (!checkPermissions(this, permissionsList)) {
                    ActivityCompat.requestPermissions(this, permissionsList, 21);
                } else {
                    KSUtil.fromAlbum = false;
                    Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                    mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                    mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);
                   // startActivityes(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
                  //  startActivity(mIntent);
                    startActivityForResult(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
                }
                break;

            case R.id.btnAllVideo:
                if (!checkPermissions(this, permissionsList)) {
                    ActivityCompat.requestPermissions(this, permissionsList, 22);
                } else {
                    KSUtil.fromAlbum = true;
                   // startActivityes(new Intent(MainActivity.this, MyVideo.class), 0);
                    startActivityForResult(new Intent(MainActivity.this, MyVideo.class), ImagePickerActivity.PICKER_REQUEST_CODE);
                    Animatee.animateSlideUp(MainActivity.this);

                }
                break;

            case R.id.rateIV:
                rateUs();
                break;

            case R.id.shareIV:
                shareApp();
                break;

            case R.id.privacyIV:
               // startActivityes(new Intent(MainActivity.this, PrivacyActivity.class), 0);
                startActivityForResult(new Intent(MainActivity.this, PrivacyActivity.class), 0);
                break;

          /*  case R.id.moreIV:
                moreApp();
                break;*/

            default:
                break;
        }
    }


    void startActivityes(Intent intent, int reqCode) {
        if (!AdManager.isloadFbMAXAd) {
            AdManager.adCounter++;
         //   AdManager.showInterAd(MainActivity.this, intent, reqCode);
        } else {
            AdManager.adCounter++;
            AdManager.showMaxInterstitial(MainActivity.this, intent, reqCode);
        }
    }

    public void moreApp() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/dev?id=7081479513420377164&hl=en")));
    }

    public void shareApp() {
        String shareBody = "https://play.google.com/store/apps/details?id="
                + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "(This app is for making beautiful video from photos. Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void rateUs() {
        Uri uri = Uri.parse("market://details?id="
                + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getApplicationContext().getPackageName())));
        }
    }


    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21){
            if (checkPermissions(this, permissionsList)){
                KSUtil.fromAlbum = false;
                Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);

                AdManager.adCounter = AdManager.adDisplayCounter;
               // AdManager.showInterAd(MainActivity.this, mIntent,ImagePickerActivity.PICKER_REQUEST_CODE);
            }
        }
        if(requestCode == 22){
            KSUtil.fromAlbum = true;
          //  startActivityes(new Intent(MainActivity.this, MyVideo.class), 0);
            startActivityForResult(new Intent(MainActivity.this, MyVideo.class), 0);
            Animatee.animateSlideUp(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ImagePickerActivity.PICKER_REQUEST_CODE) {
            KSUtil.videoPathList.clear();
            KSUtil.videoPathList = data.getExtras().getStringArrayList(ImagePickerActivity.KEY_DATA_RESULT);//ImagePickerActivity.saveFiles;
            if (KSUtil.videoPathList != null && !KSUtil.videoPathList.isEmpty()) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                    sb.append("Image Path" + (i + 1) + ":" + KSUtil.videoPathList.get(i));
                    sb.append("\n");

                }
                Log.e("Image", sb.toString());

            //    startActivityes(new Intent(MainActivity.this, SwapperActivity.class), 0);
                startActivityForResult(new Intent(MainActivity.this, SwapperActivity.class), 0);
            }
        }
    }
}
