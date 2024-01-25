package vaapps.photoslideshow.photovideomaker.videoplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;

import vaapps.photoslideshow.photovideomaker.MainActivity;
import vaapps.photoslideshow.photovideomaker.R;

import vaapps.photoslideshow.photovideomaker.util.AdManager;
import vaapps.photoslideshow.photovideomaker.util.AdsManagerQ;
import vaapps.photoslideshow.photovideomaker.util.KSUtil;
import vaapps.photoslideshow.photovideomaker.util.MyUtil;
import vaapps.photoslideshow.photovideomaker.util.Render;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoPlayerActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener {

    ImageView btnback;
    VideoView videoView;
    String path;
    MediaController mediaController;
    int vw, vh;
    FrameLayout frame;
    SeekBar seekVideo;
    TextView tvStartVideo, tvEndVideo;
    int duration = 0;
    Handler handler = new Handler();
    ImageView btnPlayVideo;
    boolean isPlay = false;

    ImageView btnShare, btnDelete;
    RelativeLayout main, top;


    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    AdsManagerQ adsManagerQ;
    public AdManagerInterstitialAd mAdManagerInterstitialAd;
    Dialog addialogueLoad;
    int BG_Tex = 0;
    private static final int REQUEST_SELECT_PHOTO = 789;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_video);

        addialogueLoad = new Dialog(VideoPlayerActivity.this);
        AdsManagerQ.getInstance().createInterstitialstaticAd(VideoPlayerActivity.this, getResources().getString(R.string.staticinterstitialid));
        ShowInterstitial();


        setTV();
        main = findViewById(R.id.main);
        top = findViewById(R.id.header);
        btnback = findViewById(R.id.btn_back1);
        videoView = findViewById(R.id.video111);
        frame = findViewById(R.id.frame1);
        seekVideo = findViewById(R.id.videoSeek);
        seekVideo.setOnSeekBarChangeListener(this);
        seekVideo.setEnabled(false);
        Drawable dr = getResources().getDrawable(R.drawable.shipbar_round);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080, true));
        seekVideo.setThumb(d);
        tvStartVideo = findViewById(R.id.tvStartVideo);
        tvEndVideo = findViewById(R.id.tvEndVideo);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                share();
                startActivityes(null,0);
            }
        });

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                delete();
                startActivityes(null,0);
            }
        });


        btnback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });


        mediaController = new MediaController(VideoPlayerActivity.this);
        Bundle extras = getIntent().getExtras();
        path = extras.getString("video_path");

        Log.d("7247checkedddafter", "path"+path);

//        path = getIntent().getExtras().getString("video_path");
//        Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        videoView.setVideoPath(path);
        videoView.seekTo(100);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);

        vw = Integer
                .valueOf(mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        vh = Integer
                .valueOf(mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

        mediaMetadataRetriever.release();

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPlay) {

                    videoView.seekTo(seekVideo.getProgress());
                    videoView.start();
                    handler.postDelayed(seekrunnable, 200);
                    videoView.setVisibility(View.VISIBLE);
                    btnPlayVideo.setVisibility(View.VISIBLE);
                    btnPlayVideo.setImageResource(0);
                    btnPlayVideo.setImageResource(R.drawable.pause2);
                } else {

                    videoView.pause();
                    handler.removeCallbacks(seekrunnable);
                    btnPlayVideo.setVisibility(View.VISIBLE);
                    btnPlayVideo.setImageResource(0);
                    btnPlayVideo.setImageResource(R.drawable.play);

                }
                isPlay = !isPlay;

            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                duration = videoView.getDuration();
                seekVideo.setMax(duration);

                tvStartVideo.setText("00:00");
                try {
                    tvEndVideo.setText("" + formatTimeUnit(duration));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.play);
                btnPlayVideo.setVisibility(View.VISIBLE);
                videoView.seekTo(100);
                seekVideo.setProgress(0);
                tvStartVideo.setText("00:00");
                handler.removeCallbacks(seekrunnable);
                isPlay = !isPlay;

            }
        });
        btnPlayVideo.setOnClickListener(onclickplayvideo);
    }








    public void ShowInterstitial() {

        mAdManagerInterstitialAd = AdsManagerQ.getInstance().getAd();
        Log.i("wwwww", "Interstitial Ad =" + mAdManagerInterstitialAd);
        if (mAdManagerInterstitialAd != null) {
            Log.i("qqqqwhereee","here1st");
            loadingad(VideoPlayerActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addialogueLoad.dismiss();
                    Log.i("qqqqwhereee","here2nd");
                    mAdManagerInterstitialAd.show(VideoPlayerActivity.this);
                    mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.i("wwwww", "Interstitial Ad Dismissed");
                            Log.i("wwwww", "BG_Tex=" + BG_Tex);
                            adsManagerQ.createVideoInterstitialstaticAd(VideoPlayerActivity.this, getResources().getString(R.string.videoInterstitialid));
                        }


                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            // overridePendingTransition(R.anim.slide_down, R.anim.slide_down);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
//                                mInterstitialAd = null;

                            adsManagerQ.createVideoInterstitialstaticAd(VideoPlayerActivity.this, getResources().getString(R.string.videoInterstitialid));
                            mAdManagerInterstitialAd = adsManagerQ.getAd();

                        }

                    });
                }

            }, 700);
        } else {

        }

        //}
    }


    public void loadingad(Context context) {

        try {


            addialogueLoad.setCancelable(false);
            addialogueLoad.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.language_dialogue_background));
            addialogueLoad.getWindow().setDimAmount(0);
            addialogueLoad.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            addialogueLoad.setContentView(R.layout.loadingad);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Set Animation
                    Render render = new Render(context);
                    render.setAnimation(MyUtil.Swing(addialogueLoad.findViewById(R.id.fullview)));
                    render.start();
                }
            }, 100);
            TextView Yes, no;

            addialogueLoad.show();



          /*  int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80); //<-- int width=400;
            int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.45);*/
//            addialogueLoad.getWindow().setLayout(width, height);
            if (addialogueLoad.getWindow() != null) {
                addialogueLoad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
                addialogueLoad.getWindow().setDimAmount(0.7f); //0 for no dim to 1 for full dim
            }


        } catch (Exception e) {
//            Log.d("catched", e.getMessage());
//            Log.d("check724788",e.getMessage());
        }

    }




























    public void setTV(){
        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (!AdManager.isloadFbMAXAd) {
            //admob
            AdManager.initAd(VideoPlayerActivity.this);
            AdManager.loadBannerAd(VideoPlayerActivity.this, adContainer);
            AdManager.loadInterAd(VideoPlayerActivity.this);
        } else {
            //MAX + Fb banner Ads
            AdManager.initMAX(VideoPlayerActivity.this);
            AdManager.maxBanner(VideoPlayerActivity.this, adContainer);
            AdManager.maxInterstital(VideoPlayerActivity.this);
        }
    }

    void startActivityes(Intent intent, int reqCode) {
//        if (!AdManager.isloadFbMAXAd) {
//            AdManager.adCounter++;
//           // AdManager.showInterAd(VideoPlayerActivity.this, intent, reqCode);
//        } else {
//            AdManager.adCounter++;
//            AdManager.showMaxInterstitial(VideoPlayerActivity.this, intent, reqCode);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(100);
    }

    @Override
    public void onProgressChanged(SeekBar seekbar, int progress,
                                  boolean fromTouch) {
        if (fromTouch) {
            videoView.seekTo(progress);
            try {
                tvStartVideo.setText("" + formatTimeUnit(progress));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    public static String formatTimeUnit(long millis) throws ParseException {
        String formatted = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(millis)));
        return formatted;
    }

    View.OnClickListener onclickplayvideo = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Log.e("", "play status " + isPlay);

            if (!isPlay) {
                videoView.seekTo(seekVideo.getProgress());
                videoView.start();
                handler.postDelayed(seekrunnable, 200);
                videoView.setVisibility(View.VISIBLE);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.pause2);

            } else {
                videoView.pause();
                handler.removeCallbacks(seekrunnable);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.play);
            }
            isPlay = !isPlay;

        }
    };

    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int curPos = videoView.getCurrentPosition();
                seekVideo.setProgress(curPos);
                try {
                    tvStartVideo.setText("" + formatTimeUnit(curPos));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (curPos == duration) {
                    seekVideo.setProgress(0);
                    tvStartVideo.setText("00:00");
                    handler.removeCallbacks(seekrunnable);
                } else
                    handler.postDelayed(seekrunnable, 200);

            } else {
                seekVideo.setProgress(duration);
                try {
                    tvStartVideo.setText("" + formatTimeUnit(duration));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.removeCallbacks(seekrunnable);
            }
        }
    };


    void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("video/*");
        Uri photoURI = FileProvider.getUriForFile(
                getApplicationContext(),
                getApplicationContext()
                        .getPackageName() + ".provider", new File(path));
        share.putExtra(Intent.EXTRA_STREAM,
                photoURI);
        startActivity(Intent.createChooser(share, "Share via"));
    }

    PopupWindow popupWindow;
    LinearLayout alertLay;
    ImageView yes, no;

    public void delete() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.delete_alert, null);

        alertLay = alertLayout.findViewById(R.id.alertLay);
        yes = alertLayout.findViewById(R.id.yes);
        no = alertLayout.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                new File(path).delete();
                Toast.makeText(VideoPlayerActivity.this, "Deleted Successfully!!!",
                        Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(alertLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //display the popup window
        popupWindow.showAtLocation(main, Gravity.CENTER, 0, 0);
        dialogParam();
    }

    void dialogParam() {
        LinearLayout.LayoutParams paramsDialog = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 840 / 1080,
                getResources().getDisplayMetrics().heightPixels * 415 / 1920);
        alertLay.setLayoutParams(paramsDialog);

        LinearLayout.LayoutParams paramsCamera = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 270 / 1080,
                getResources().getDisplayMetrics().heightPixels * 106 / 1920);
        yes.setLayoutParams(paramsCamera);
        no.setLayoutParams(paramsCamera);
    }


    int FLAG_VIDEO = 21;
    @Override
    public void onBackPressed() {
        AdManager.adCounter++;
        if (AdManager.adCounter == AdManager.adDisplayCounter) {
            if (!AdManager.isloadFbMAXAd) {
               // AdManager.showInterAd(VideoPlayerActivity.this, null, 0);
            } else {
                AdManager.showMaxInterstitial(VideoPlayerActivity.this, null, 0);
            }
        } else {
            super.onBackPressed();
            if (KSUtil.fromAlbum) {
                Intent intent = new Intent();
                setResult(FLAG_VIDEO, intent);
            } else {
                gotoMain();
            }
        }
    }


    public void gotoMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
