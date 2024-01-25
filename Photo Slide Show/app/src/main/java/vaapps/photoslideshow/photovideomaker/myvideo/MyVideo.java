package vaapps.photoslideshow.photovideomaker.myvideo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;

import vaapps.photoslideshow.photovideomaker.R;
import vaapps.photoslideshow.photovideomaker.util.AdManager;
import vaapps.photoslideshow.photovideomaker.util.AdsManagerQ;
import vaapps.photoslideshow.photovideomaker.util.AdsUtils;
import vaapps.photoslideshow.photovideomaker.videoplayer.VideoPlayerActivity;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MyVideo extends AppCompatActivity {

    public static ArrayList<String> videoPath = new ArrayList<String>();

    RecyclerView videoListView;
    MyVideoAdapter videoAdapter;
    int FLAG_VIDEO = 21;
    ImageView backIV;
    RelativeLayout header;


    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout layout;
    AdsManagerQ adsManagerQ;
    public AdManagerInterstitialAd mAdManagerInterstitialAd;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_videos);
        AdsManagerQ.getInstance().createInterstitialstaticAd(MyVideo.this, getResources().getString(R.string.staticinterstitialid));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MyVideo.this.requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }

        layout = findViewById(R.id.adLayout);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        Log.d("7247check", "banner");
        if (AdsUtils.isNetworkAvailabel(MyVideo.this)) {
            Log.i("7247check", "banner");

            AdsManagerQ.loadbannerAd(MyVideo.this, mShimmerViewContainer, layout);


        } else {
            //   layout.setVisibility(View.GONE);
            //  mShimmerViewContainer.setVisibility(View.GONE);
        }










        setTV();

        header = (RelativeLayout) findViewById(R.id.header);
        videoLoader();

        backIV = (ImageView) findViewById(R.id.back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void setTV(){
        LinearLayout adContainer = findViewById(R.id.banner_container);
        if (!AdManager.isloadFbMAXAd) {
            //admob
            AdManager.initAd(MyVideo.this);
            AdManager.loadBannerAd(MyVideo.this, adContainer);
            AdManager.loadInterAd(MyVideo.this);
        } else {
//            MAX + Fb banner Ads
            AdManager.initMAX(MyVideo.this);
            AdManager.maxBanner(MyVideo.this, adContainer);
            AdManager.maxInterstital(MyVideo.this);
        }
    }

    @Override
    public void onBackPressed() {
        AdManager.adCounter++;
        if (AdManager.adCounter == AdManager.adDisplayCounter) {
            if (!AdManager.isloadFbMAXAd) {
               // AdManager.showInterAd(MyVideo.this, null, 0);
            } else {
                AdManager.showMaxInterstitial(MyVideo.this, null, 0);
            }
        } else {
            super.onBackPressed();
        }
    }

    void startActivityes(Intent intent, int reqCode) {
        if (!AdManager.isloadFbMAXAd) {
            AdManager.adCounter++;
     //       AdManager.showInterAd(MyVideo.this, intent,reqCode);
        } else {
            AdManager.adCounter++;
            AdManager.showMaxInterstitial(MyVideo.this, intent,reqCode);
        }
    }


    public void videoLoader() {
        getFromStorage();
        videoListView = (RecyclerView) findViewById(R.id.recyclerView);
        videoAdapter = new MyVideoAdapter(videoPath, MyVideo.this, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(MyVideo.this, VideoPlayerActivity.class);
                intent.putExtra("video_path", videoPath.get(position));
                Log.i("7247checkedddbefore", "path"+videoPath.get(position));
                startActivity(intent);

                //  startActivityes(intent, FLAG_VIDEO);
//                startActivityForResult(new Intent(MyVideo.this, VideoPlayerActivity.class), FLAG_VIDEO);

            }
        });

        videoListView.setLayoutManager(new GridLayoutManager(this, 2));
        videoListView.setItemAnimator(new DefaultItemAnimator());
        videoListView.setAdapter(videoAdapter);

    }

    public void getFromStorage() {
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + getResources().getString(R.string.app_name);
        File file = new File(folder);
        videoPath = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getAbsolutePath().contains(".mp4")) {
                    videoPath.add(listFile[i].getAbsolutePath());
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAG_VIDEO) {
            videoAdapter.notifyDataSetChanged();
            videoLoader();
        }
    }


}
