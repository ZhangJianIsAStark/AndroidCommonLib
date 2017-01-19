package stark.a.is.zhang.quizgeo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import stark.a.is.zhang.activity.BaseActivity;

public class CheatActivity extends BaseActivity {
    private static final String EXTRA_ANSWER_IS_TRUE =
            "stark.a.is.zhang.quizgeo.answer_is_true";

    private static final String EXTRA_ANSWER_SHOWN =
            "stark.a.is.zhang.quizgeo.answer_shown";

    private boolean mAnswerIsTrue;

    private TextView mAnswerTextView;
    private Button mShowAnswer;

    private static final String KEY_ANSWER_SHOWN = "answerShown";
    private boolean mWasAnswerShown;

    private TextView mSdkVersionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        //Get the information from QuizActivity
        Intent i = getIntent();
        if (i != null) {
            mAnswerIsTrue = i.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cheat);

        //When rotate screen, etc.
        //Recover the value about answerShown
        if (savedInstanceState != null) {
            mWasAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
        }

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        if (mWasAnswerShown) {
            updateAnswerTextView();
        }

        mShowAnswer = (Button) findViewById(R.id.showAnswerButton);
        if (mWasAnswerShown) {
            mShowAnswer.setVisibility(View.INVISIBLE);
        } else {
            mShowAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateAnswerTextView();

                    mWasAnswerShown = true;

                    setAnswerShownResult(true);

                    startAnimation();
                }
            });
        }

        mSdkVersionTextView = (TextView) findViewById(R.id.sdk_version_text_view);
        String api = "API level: " + Build.VERSION.SDK_INT;
        mSdkVersionTextView.setText(api);
    }

    @Override
    protected void prepareData() {
        //When rotate screen, etc.
        //Must also send the result to QuizActivity
        if (mWasAnswerShown) {
            setAnswerShownResult(true);
        }
    }

    //QuizActivity uses this to make a intent to start cheatActivity
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }

    //Set the result about whether cheated or not to QuizActivity
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    //QuizActivity uses this to make sure if user has cheated
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, mWasAnswerShown);
    }

    private void updateAnswerTextView() {
        int resId = mAnswerIsTrue ? R.string.true_button : R.string.false_button;
        mAnswerTextView.setText(resId);
    }

    private void startAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswer.getWidth() / 2;
            int cy = mShowAnswer.getHeight() / 2;
            float radius = mShowAnswer.getWidth() / 2;

            Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswer, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswer.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else {
            mShowAnswer.setVisibility(View.INVISIBLE);
        }
    }
}