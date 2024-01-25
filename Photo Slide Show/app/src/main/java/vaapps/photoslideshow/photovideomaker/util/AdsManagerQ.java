package vaapps.photoslideshow.photovideomaker.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import vaapps.photoslideshow.photovideomaker.R;


public class AdsManagerQ {
    //    static final String BanneradID = "ca-app-pub-3940256099942544/6300978111";
    public static String BanneradID;
    public String InterstitialStaticadID;
    public String rewqardedInterstitialadID;
//    public static final String BanneradID = "ca-app-pub-9522824014864088/3481723741";
//    public static final String InterstitialStaticadID = "ca-app-pub-9522824014864088/1468421037";
//    public static final String InterstitialStaticadID = "ca-app-pub-3940256099942544/1033173712";


    // Static fields are shared between all instances.
    private static AdManagerAdRequest adRequestinterstitial;
    private static AdsManagerQ singleton;
    public AdManagerInterstitialAd mAdManagerInterstitialAd;
    public RewardedInterstitialAd mRewardedVideoAd;


    int countforinterstitialrequest = 0;
    int countforinterstitialrequestlimit = 1;
    int countforinterstitialShow = 0;
    static int countforbannerrequest = 0;
    static int countforintersttialrequestSHOWlimit = 0;
    static int countforbannerlimitrequest = 5;
    private static AdManagerAdView adView;

    public AdsManagerQ() {

    }
/*    public AdsManagerQ(Context ctx) {
        this.ctx = ctx;
        //createAd(); //Remove this line!
    }*/

    /***
     * returns an instance of this class. if singleton is null create an instance
     * else return  the current instance
     * @return
     */


    public void refreshAd(Activity activity, FrameLayout frameLayout, RelativeLayout nativeRel) {
//        refresh.setEnabled(false);

        AdLoader.Builder builder = new AdLoader.Builder(frameLayout.getContext(), String.valueOf(R.string.nativeid));

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        nativeRel.setVisibility(View.GONE);
                        boolean isDestroyed = false;
//                        refresh.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = activity.isDestroyed();
                        }
                        if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
//                        if (activity.this.nativeAd != null) {
//                            GenerateActivity.this.nativeAd.destroy();
//                        }
//                        GenerateActivity.this.nativeAd = nativeAd;

                        NativeAdView adView =
                                (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });


    }


    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
//                    refresh.setEnabled(true);
//                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
//            videoStatus.setText("Video status: Ad does not contain a video asset.");
//            refresh.setEnabled(true);
        }
    }


    public static AdsManagerQ getInstance() {


        if (singleton == null) {
            Log.d("7247singletonaccess", "accessed");

            adRequestinterstitial = new AdManagerAdRequest.Builder().build();
            singleton = new AdsManagerQ();
        }

        return singleton;
    }

    public void createInterstitialstaticAd(Context context, String id) {
        Log.d("7247hereeee2", "interstialcheck");

        MobileAds.initialize(context,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus status) {
                        Log.d("7247hereeee2", "interstialcheckinside");

                    }
                });
        // Create an ad.
  /*      Log.d("7247context", ""+context);
        Log.d("7247IDDD", ""+InterstitialStaticadID);
        Log.d("7247adRequestinterstitial", ""+adRequestinterstitial);
        Log.d("7247context", ""+context);*/

//        AdManagerInterstitialAd.load(context,context.getResources().getString(R.string.staticinterstitialid), adRequestinterstitial, new AdManagerInterstitialAdLoadCallback() {
        AdManagerInterstitialAd.load(context, id, adRequestinterstitial, new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                countforinterstitialrequest++;
                Log.d("7247hereeee2", "interstialloaded");
                if (countforinterstitialrequest > countforinterstitialrequestlimit) {
                    countforinterstitialrequest = 0;

                    createInterstitialstaticAd(context, id);

                }
                mAdManagerInterstitialAd = interstitialAd;


                Log.d("7247hereeee2", "" + mAdManagerInterstitialAd);

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mAdManagerInterstitialAd = null;
                Log.d("7247hereeee2", "failedtoload Interstital" + loadAdError);

            }

        });
    }

    public void createVideoInterstitialstaticAd(Context context, String id) {
        Log.d("7247hereeee", "interstialcheck");

        MobileAds.initialize(
                context,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus status) {
                        Log.d("7247hereeee", "interstialcheckinside");

                    }
                });
        // Create an ad.
  /*      Log.d("7247context", ""+context);
        Log.d("7247IDDD", ""+InterstitialStaticadID);
        Log.d("7247adRequestinterstitial", ""+adRequestinterstitial);
        Log.d("7247context", ""+context);*/

//        AdManagerInterstitialAd.load(context,context.getResources().getString(R.string.staticinterstitialid), adRequestinterstitial, new AdManagerInterstitialAdLoadCallback() {
        AdManagerInterstitialAd.load(context, id, adRequestinterstitial, new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                countforinterstitialrequest++;
                Log.d("7247hereeee", "interstialloaded");
                if (countforinterstitialrequest > countforinterstitialrequestlimit) {
                    countforinterstitialrequest = 0;

                    createVideoInterstitialstaticAd(context, id);

                }
                mAdManagerInterstitialAd = interstitialAd;


                Log.d("7247hereeee", "" + mAdManagerInterstitialAd);

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mAdManagerInterstitialAd = null;
                Log.d("7247hereeee", "failedtoload Interstital" + loadAdError);

            }

        });
    }

    public AdManagerInterstitialAd getAd() {

//        Toast.makeText(Application.getControlcontext(), "" + countforinterstitialShow, Toast.LENGTH_SHORT).show();
//        Log.d("7247check", "" + mAdManagerInterstitialAd);

        return mAdManagerInterstitialAd;

    }

    public AdManagerInterstitialAd getAdwithlimit() {

        countforinterstitialShow++;
        Toast.makeText(MyApp.getControlcontext(), "" + countforinterstitialShow, Toast.LENGTH_SHORT).show();
//        Log.d("7247check", "" + mAdManagerInterstitialAd);
        if (countforinterstitialShow == countforintersttialrequestSHOWlimit) {
            countforinterstitialShow = 0;
            return mAdManagerInterstitialAd;
        }

        return null;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static void loadbannerAd(Context context, ShimmerFrameLayout layout, LinearLayout view) {
        // This is a one element array because it needs to be declared final
        // TODO: you should probably load the default value from somewhere because of activity restarts
        adView = new AdManagerAdView(context);
        adView.setAdUnitId(context.getResources().getString(R.string.bannerid));
//        adView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        view.removeAllViews();                               //thisisssss
        view.addView(adView);
//        adView.setAdSizes(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 200));
        adView.setAdSizes(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 350));


        adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                super.onAdOpened();


                // TODO: save currentBannerClick[0] somewhere, see previous TODO comment
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
//                currentBannerClick[0]++;
                layout.setVisibility(View.GONE);

                countforbannerrequest++;
                Log.d("7247check", "loaded banner");
//                Toast.makeText(context, "add is loaded" + countforbannerrequest + "Times", Toast.LENGTH_SHORT).show();

                if (countforbannerrequest > countforbannerlimitrequest) {
                    countforbannerrequest = 0;

//                    adView.setVisibility(View.INVISIBLE);
//                    adView.destroy();
                    loadbannerAd(context, layout, view);

                          /*  AdRequest adRequest2 = new AdRequest.Builder().build();
                    adView.setVisibility(View.VISIBLE);
                    adView.loadAd(adRequest2);*/
                }

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("7247checkfailed", "failed loaded banner" + loadAdError);

            }
        });

        if (countforbannerrequest <= countforbannerlimitrequest) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.INVISIBLE);
        }
    }

    private AdSize getAdSize(FrameLayout view) {
        // Determine the screen width (less decorations) to use for the ad width.
     /*   WindowManager windowManager = contexted.getApplicationContext().getWindowManager();
        Display display =windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();*/
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

//        display.getMetrics(metrics);

        float density = metrics.density;

        float adWidthPixels = view.getWidth();

        // If the ad width isn't known, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = metrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(MyApp.getControlcontext(), adWidth);
    }


    public void loadrewardedAd(Context context, String id) {

/*        PrefManager prefManager = new PrefManager(getApplicationContext());
        mRewardedVideoAd.loadAd(prefManager.getString("ADMIN_REWARDED_ADMOB_ID"),
                new AdRequest.Builder().build());*/
        // Use the test ad unit ID to load an ad.

//        Log.d("check7247", getResources().getString(R.string.rewardedinterstitialad));
//        RewardedInterstitialAd.load(context, context.getResources().getString(R.string.rewardedinterstitialad),
        RewardedInterstitialAd.load(context, id,
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        mRewardedVideoAd = ad;
                        Log.e("check7247", "onAdLoaded");


                        mRewardedVideoAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
						/*	@Override
							public void onAdFailedToShowFullScreenContent(AdError adError) {
								Log.i("check7247", "onAdFailedToShowFullScreenContent");
							}*/

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.i("check7247", "onAdShowedFullScreenContent");
                                mRewardedVideoAd = null;

                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i("check7247", "onAdDismissedFullScreenContent");
                                mRewardedVideoAd = null;

                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e("check7247", "onAdFailedToLoad" + loadAdError.getMessage());
                    }
                });
    }
}