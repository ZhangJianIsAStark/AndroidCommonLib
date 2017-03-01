package stark.a.is.zhang.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class SunSetFragment extends Fragment{
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private boolean mSunset = true;
    private AnimatorSet mAnimatorSet = null;

    public static SunSetFragment newInstance() {
        return new SunSetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSunView = v.findViewById(R.id.sun);
        mSkyView = v.findViewById(R.id.sky);

        Resources resources = getResources();

        if (Build.VERSION.SDK_INT >= 23) {
            mBlueSkyColor = resources.getColor(R.color.blue_sky, getActivity().getTheme());
            mSunsetSkyColor = resources.getColor(R.color.sunset_sky, getActivity().getTheme());
            mNightSkyColor = resources.getColor(R.color.night_sky, getActivity().getTheme());
        } else {
            mBlueSkyColor = resources.getColor(R.color.blue_sky);
            mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
            mNightSkyColor = resources.getColor(R.color.night_sky);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
                    if (mAnimatorSet == null || !mAnimatorSet.isStarted()) {
                        startAnimation();
                    } else {
                        if (mAnimatorSet.isPaused()) {
                            mAnimatorSet.resume();
                        } else {
                            mAnimatorSet.pause();
                        }
                    }
                }
            }
        });

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAnimation() {
        startAnimation(mSunset);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAnimation(boolean isSunSet) {
        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.play(getHeightAnimator(isSunSet))
                .with(getInitialSkyAnimator(isSunSet))
                .with(getScaleAnimator(isSunSet))
                .with(getRotateAnimator())
                .before(getLaterSkyAnimator(isSunSet));

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSunset = !mSunset;
                mAnimatorSet = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnimatorSet.start();
    }

//    A way to reverse animation
//    private long mCurrentPlayTime = 0;
//    private float mCurrentHeight = 0;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ObjectAnimator getHeightAnimator(boolean isSunset) {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight() + mSunView.getBottom() - mSunView.getTop();

        final float start = isSunset ? sunYStart : sunYEnd;
        final float end = isSunset ? sunYEnd : sunYStart;

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", start, end)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
//        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mCurrentPlayTime = animation.getCurrentPlayTime();
//                mCurrentHeight = (float)animation.getAnimatedValue();
//            }
//        });
//
//        heightAnimator.addPauseListener(new Animator.AnimatorPauseListener() {
//            @Override
//            public void onAnimationPause(Animator animation) {
//                heightAnimator = ObjectAnimator
//                        .ofFloat(mSunView, "y", mCurrentHeight, start)
//                        .setDuration(mCurrentPlayTime);
//                heightAnimator.setInterpolator(new DecelerateInterpolator());
//
//                mAnimatorSet.cancel();
//
//                heightAnimator.start();
//            }
//
//            @Override
//            public void onAnimationResume(Animator animation) {
//
//            }
//        });

        return heightAnimator;
    }

    private ObjectAnimator getInitialSkyAnimator(boolean isSunset) {
        int start = isSunset ? mBlueSkyColor : mNightSkyColor;
        int end = mSunsetSkyColor;

        return createSkyAnimator(start, end, 3000);
    }

    private ObjectAnimator getLaterSkyAnimator(boolean isSunset) {
        int start = mSunsetSkyColor;
        int end = isSunset ? mNightSkyColor : mBlueSkyColor;

        return createSkyAnimator(start, end, 1500);
    }

    private ObjectAnimator createSkyAnimator(int start, int end, int duration) {
        ObjectAnimator skyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", start, end)
                .setDuration(duration);
        skyAnimator.setEvaluator(new ArgbEvaluator());

        return skyAnimator;
    }

    private ObjectAnimator getScaleAnimator(boolean isSunSet) {
        float start = (float) (isSunSet ? 1 : 1.5);
        float end = (float) (isSunSet ? 1.5 : 1);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", start, end);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", start, end);
        return ObjectAnimator.ofPropertyValuesHolder(mSunView, pvhX, pvhY)
                .setDuration(3000);

// Another way
//        ObjectAnimator scaleAnimator = ObjectAnimator
//                .ofFloat(mSunView, "whatever", start, end)
//                .setDuration(3000);
//        scaleAnimator.setInterpolator(new AccelerateInterpolator());
//
//        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float val = (float)animation.getAnimatedValue();
//                mSunView.setScaleX(val);
//                mSunView.setScaleY(val);
//            }
//        });
//        return scaleAnimator;
    }

    private ObjectAnimator getRotateAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator
                .ofFloat(mSunView, "rotation", 0, 360)
                .setDuration(1000);
        objectAnimator.setRepeatCount(4);
        return objectAnimator;
    }
}