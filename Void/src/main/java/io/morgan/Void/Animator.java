package io.morgan.Void;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by mobrown on 6/9/13.
 */
public class Animator {

    public static int dpsToPixels(View v, int dps) {
        float scale = v.getContext().getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static Animation expand(final View v, int targetHeight) {
        final int startHeight = v.getHeight();
        final int endHeight = targetHeight - startHeight;

        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int)(startHeight + endHeight * interpolatedTime);

                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return a;
    }

    public static Animation collapse(final View v, int targetHeight) {
        final int endHeight = targetHeight;
        final int initialHeight = v.getMeasuredHeight() - targetHeight;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = endHeight + initialHeight - (int)(initialHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return a;
    }
}
