package vaapps.photoslideshow.photovideomaker.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import vaapps.photoslideshow.photovideomaker.R;

public class MyUtil {

    //selected images path list
    public static ArrayList<String> videoPathList = new ArrayList<>();
    public static ArrayList<Bitmap> savebmp=new ArrayList<Bitmap>();
    public static int counter=0;
    public static int ok=0;
    public static int from=0;
    public static String copied;
/*    public static int touched1=0;
    public static int touched2=0;*/

    //image editor path and position
    public static String imgEditorPath;
    public static int imbEditorPos;


    public static boolean fromAlbum = false;

    public static AnimatorSet Swing(View view){
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator object = ObjectAnimator.ofFloat(view,   "rotation", 0, 10, -10, 6, -6, 3, -3, 0);

        animatorSet.playTogether(object);
        return animatorSet;
    }

    public static AnimatorSet InUp(View view){
        AnimatorSet animatorSet = new AnimatorSet();
        ViewGroup parent = (ViewGroup) view.getParent();
        int distance = parent.getHeight() - view.getTop();

        ObjectAnimator object1 = ObjectAnimator.ofFloat(view,    "alpha", 0, 1);
        ObjectAnimator object2 = ObjectAnimator.ofFloat(view,    "translationY", distance, 0);

        animatorSet.playTogether(object1, object2);
        return animatorSet;
    }

    public static AnimatorSet In(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator object = ObjectAnimator.ofFloat(view, "alpha", 0, 1);

        animatorSet.playTogether(object);
        return animatorSet;
    }

    public static void loadingad(Context context)
    {
        Dialog dialogadload;
        dialogadload = new Dialog(context);
        try {

            dialogadload.setCancelable(false);
            dialogadload.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogadload.getWindow().setDimAmount(0);

            dialogadload.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//            binding.mainlayout.setVisibility(View.GONE);
//            topbarwala.setVisibility(View.VISIBLE);
          /*  templateViewmain.setVisibility(View.GONE);
            uploadDownloadLAyout.setVisibility(View.GONE);*/
            dialogadload.setContentView(R.layout.loadingad);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Set Animation
                    Render render = new Render(context);
                    render.setAnimation(MyUtil.Swing(dialogadload.findViewById(R.id.fullview)));
                    render.start();
                }
            }, 1500);
            TextView Yes, no;

            dialogadload.show();

            dialogadload.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogadload.getWindow().setGravity(Gravity.CENTER);
            dialogadload.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80); //<-- int width=400;
            int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.45);
            dialogadload.getWindow().setLayout(width, height);
            if (dialogadload.getWindow() != null) {
                dialogadload.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
                dialogadload.getWindow().setDimAmount(0.7f); //0 for no dim to 1 for full dim
            }

            dialogadload.dismiss();

        } catch (Exception e) {
//            Log.d("catched", e.getMessage());
        }

    }


}
